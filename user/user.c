#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/stat.h>
#include "cmds.h"
#include "../lib/net.h"
#include "../lib/dirs.h"

int errorHappened = 0;

char *registerUser (UDPConn *conn) {
  char msg[11] = "REG ";
  scanf("%s", msg + 4);
  msg[9] = '\n';
  msg[10] = '\0';

  char *buffer = sendWithReplyUDP(conn, msg);

  if (buffer == NULL) {
    errorHappened = 1;
    return NULL;
  }

  int ok = strcmp(buffer, "RGR OK\n") == 0;

  if (ok) {
    printf("User registred!\n");
  } else {
    printf("Registration failed!\n");
  }

  free(buffer);

  if (!ok) {
    return NULL;
  }

  msg[9] = '\0';
  char *userID = strdup(msg + 4);
  return userID;
}

typedef struct stringArray {
  int count;
  char **names;
} StringArray;

void freeStringArray (StringArray* arr) {
  if (arr == NULL) return;

  for (int i = 0; i < arr->count; i++) {
    free(arr->names[i]);
  }

  free(arr->names);
  free(arr);
}

StringArray* topicList (UDPConn *conn) {
  char* buffer = sendWithReplyUDP(conn, "LTP\n");
  if (buffer == NULL) {
    errorHappened = 1;
    return NULL;
  }

  StringArray *topics = malloc(sizeof(StringArray));
  if (topics == NULL) {
    errorHappened = 1;
    return NULL;
  }

  int pos = 0;
  sscanf(buffer, "LTR %d%n", &topics->count, &pos);
  pos++;

  char *spaceToken = strtok(buffer + pos, " :\n");
  printf("Available Topics:\n");

  topics->names = malloc(topics->count * sizeof(char *));
  if (topics == NULL) {
    errorHappened = 1;
    return NULL;
  }

  for (int i = 0; i < topics->count; i++) {
    topics->names[i] = strdup(spaceToken);
    printf("%d. '%s'", i + 1, topics->names[i]);
    spaceToken = strtok(NULL, " :\n");
    printf(" (proposed by %s)\n", spaceToken);
    spaceToken = strtok(NULL, " :\n");
  }

  free(buffer);
  return topics;
}

char* topicSelect (StringArray *topics) {
  if (topics == NULL) {
    printf("You must get the topics list first!\n");
    return NULL;
  }

  int num;
  scanf("%d", &num);

  if (num <= 0 || num > topics->count) {
    printf("Invalid topic number!\n");
    return NULL;
  }

  printf("Topic %s selected!\n", topics->names[num - 1]);
  return topics->names[num - 1];
}

void topicPropose (UDPConn *conn, char* userID) {
  int step = 0;
  char msg[256] = "PTP ";
  strcpy(msg + 4, userID);
  msg[9] = ' ';
  scanf("%s%n", msg + 10, &step);
  msg[10 + step - 1] = '\n';
  msg[10 + step] = '\0';
  char* res = sendWithReplyUDP(conn, msg);
  if (res == NULL) {
    errorHappened = 1;
    return;
  }

  if (strcmp(res, "PTR OK\n") == 0) {
    printf("Topic proposed successfully!\n");
  } else if (strcmp(res, "PTR DUP\n") == 0) {
    printf("Topic proposed already exists!\n");
  } else if (strcmp(res, "PTR FUL\n") == 0) {
    printf("Topic list is full!\n");
  } else {
    printf("Could not propose topic.\n");
  }

  free(res);
}

StringArray* questionList (UDPConn *conn, char *topic) {
  if (topic == NULL) {
    printf("You must pick a topic first!\n");
    return NULL;
  }

  StringArray *questions = malloc(sizeof(StringArray));
  if (questions == NULL) {
    errorHappened = 1;
    return NULL;
  }

  char msg[256];
  sprintf(msg, "LQU %s\n", topic);
  char *buffer = sendWithReplyUDP(conn, msg);
  if (buffer == NULL) {
    free(questions);
    errorHappened = 1;
    return NULL;
  }

  int pos = 0;
  sscanf(buffer, "LQR %d%n", &questions->count, &pos);
  pos++;

  questions->names = malloc(questions->count * sizeof(char *));
  if (questions->names == NULL) {
    free(questions);
    free(buffer);
    errorHappened = 1;
    return NULL;
  }

  char *spaceToken = strtok(buffer + pos, " :\n");
  printf("Available Questions for %s:\n", topic);

  for (int i = 0; i < questions->count; i++) {
    questions->names[i] = strdup(spaceToken);
    printf("%d. '%s'", i + 1, questions->names[i]);
    spaceToken = strtok(NULL, " :\n");
    printf(" (asked by %s", spaceToken);
    spaceToken = strtok(NULL, " :\n");
    printf(" with %s answers)\n", spaceToken);
    spaceToken = strtok(NULL, " :\n");
  }

  free(buffer);
  return questions;
}

char* questionGet (ServerOptions opts, char* topic, StringArray *questions) {
  if (questions == NULL) {
    printf("You must get the questions first!\n");
    return NULL;
  }

  int num;
  scanf("%d", &num);

  if (num <= 0 || num > questions->count) {
    printf("Invalid question number!\n");
    return NULL;
  }

  if (mkdirIfNotExists(topic) == -1) {
    printf("Cannot create '%s' directory.\n", topic);
    return NULL;
  }

  char msg[1024];
  sprintf(msg, "GQU %s %s\n", topic, questions->names[num - 1]);

  TCPConn* conn = connectTCP(opts);

  if (write(conn->fd, msg, strlen(msg)) == -1) {
    errorHappened = 1;
    closeTCP(conn);
    return NULL;
  }

  char buffer[256], filename[256];

  if (read(conn->fd, buffer, 3) != 3) {
    errorHappened = 1;
    closeTCP(conn);
    return NULL;
  }

  // TODO: check errs
  buffer[3] = '\0';
  if (strcmp(buffer, "QGR")) {
    printf("ERR\n"); closeTCP(conn); return NULL;
  }

  sprintf(filename, "%s/%s", topic, questions->names[num - 1]);

  if (read(conn->fd, buffer, 1) != 1 ||
    readTextAndImage(conn->fd, filename, 0) == -1 ||
    read(conn->fd, buffer, 2) != 2) {
    errorHappened = 1;
    closeTCP(conn);
    return NULL;
  }

  int answers = 0;

  if (buffer[1] == '1') {
    if (read(conn->fd, buffer, 1) != 1) {
      errorHappened = 1;
      closeTCP(conn);
      return NULL;
    }
    answers = 10;
  } else {
    buffer[2] = '\0';
    answers = atoi(buffer);
  }

  if (answers != 0) {
    if (read(conn->fd, buffer, 1) != 1) {
      errorHappened = 1;
      closeTCP(conn);
      return NULL;
    }

    for (; answers > 0; answers--) {
      if (read(conn->fd, buffer, 3) != 3) {
        errorHappened = 1;
        closeTCP(conn);
        return NULL;
      }
      buffer[2] = '\0';

      sprintf(filename, "%s/%s_%d", topic, questions->names[num - 1], answers);

      if (readTextAndImage(conn->fd, filename, 0) == -1) {
        errorHappened = 1;
        closeTCP(conn);
        return NULL;
      }
      if (read(conn->fd, buffer, 1) != 1) {
        errorHappened = 1;
        closeTCP(conn);
        return NULL;
      }
    }
  }

  printf("Question and answers downloaded to %s\n", questions->names[num - 1]);
  closeTCP(conn);
  return questions->names[num - 1];
}

void questionSubmit (ServerOptions opts, char *userID, char *topic) {
  if (topic == NULL) {
    printf("You must get the topics list first!\n");
    return;
  }

  char str[1024];

  if (fgets (str, 1024, stdin) == NULL) {
    printf("Cannot read.\n");
    return;
  }

  char *question = strtok(str+1, " \n");
  char *txtFile = strtok(NULL, " \n");
  char *imgFile = strtok(NULL, " \n");

  if (question == NULL || txtFile == NULL) {
    printf("A question and a text file are required!\n");
    return;
  }

  if (access(txtFile, F_OK) == -1 || (imgFile != NULL && access(imgFile, F_OK) == -1)) {
    printf("Text or image file does not exist!\n");
    return;
  }

  TCPConn* conn = connectTCP(opts);

  if (write(conn->fd, "QUS ", 4) != 4 ||
    write(conn->fd, userID, 5) != 5 ||
    write(conn->fd, " ", 1) != 1 ||
    write(conn->fd, topic, strlen(topic)) != strlen(topic) ||
    write(conn->fd, " ", 1) != 1 ||
    write(conn->fd, question, strlen(question)) != strlen(question) ||
    write(conn->fd, " ", 1) != 1) {
    errorHappened = 1;
    closeTCP(conn);
    return;
  }

  if (sendFile(conn->fd, txtFile, 0, 1) == -1) {
    printf("Cannot send file properly!\n");
    errorHappened = 1;
    closeTCP(conn);
    return;
  }

  if (imgFile != NULL) {
    if (write(conn->fd, " 1 ", 3) != 3 ||
      sendFile(conn->fd, imgFile, 1, 1) == -1) {
      printf("Cannot send image properly!\n");
      errorHappened = 1;
      closeTCP(conn);
      return;
    }
  } else {
    if (write(conn->fd, " 0", 2) != 2) {
      errorHappened = 1;
      closeTCP(conn);
      return;
    }
  }
  char buffer[8];

  if (write(conn->fd, "\n", 1) != 1) {
    errorHappened = 1;
    closeTCP(conn);
    return;
  }

  if (strcmp(buffer, "QUR OK\n") == 0) {
    printf("Question submited successfully!\n");
  } else if (strcmp(buffer, "QUR DUP\n") == 0) {
    printf("Question submited already exists!\n");
  } else if (strcmp(buffer, "QUR FUL\n") == 0) {
    printf("Question list is full!\n");
  } else {
    printf("Could not submit question.\n");
  }

  closeTCP(conn);
}

void answerSubmit (ServerOptions opts, char *userID, char *topic, char *question) {
  if (topic == NULL) {
    printf("You must pick a topic first!\n");
    return;
  }

  if (question == NULL) {
    printf("You must pick a question first!\n");
    return;
  }

  printf("%s\n", question);
  char str[1024];

  if (fgets (str, 1024, stdin) == NULL) {
    errorHappened = 1;
    return;
  }

  char *txtFile = strtok(str+1, " \n");
  char *imgFile = strtok(NULL, " \n");

  if (txtFile == NULL) {
    printf("At least a text file is required!\n");
    return;
  }

  if (access(txtFile, F_OK) == -1 || (imgFile != NULL && access(imgFile, F_OK) == -1)) {
    printf("Text or image file does not exist!\n");
    return;
  }

  TCPConn* conn = connectTCP(opts);

  if (write(conn->fd, "ANS ", 4) != 4 ||
    write(conn->fd, userID, 5) != 5 ||
    write(conn->fd, " ", 1) != 1 ||
    write(conn->fd, topic, strlen(topic)) != strlen(topic) ||
    write(conn->fd, " ", 1) != 1 ||
    write(conn->fd, question, strlen(question)) != strlen(question) ||
    write(conn->fd, " ", 1) != 1) {
    errorHappened = 1;
    closeTCP(conn);
    return;
  }

  if (sendFile(conn->fd, txtFile, 0, 1) == -1) {
    printf("Cannot send file properly!\n");
    errorHappened = 1;
    closeTCP(conn);
    return;
  }

  if (imgFile != NULL) {
    if (write(conn->fd, " 1 ", 3) != 3 || sendFile(conn->fd, imgFile, 1, 1) == -1) {
      printf("Cannot send image properly!\n");
      errorHappened = 1;
      closeTCP(conn);
      return;
    }
  } else {
    if (write(conn->fd, " 0", 2) != 2) {
      errorHappened = 1;
      closeTCP(conn);
      return;
    }
  }

  char buffer[8];

  if (write(conn->fd, "\n", 1) != 1 ||
    read(conn->fd, buffer, 8) != 8) {
    errorHappened = 1;
    closeTCP(conn);
    return;
  }

  if (strcmp(buffer, "ANR OK\n") == 0) {
    printf("Answer submited successfully!\n");
  } else if (strcmp(buffer, "ANR FUL\n") == 0) {
    printf("Answer list is full!\n");
  } else {
    printf("Could not submit answer.\n");
  }

  closeTCP(conn);
}

void clearInput () {
  int c;
  while ((c = getchar()) != '\n' && c != EOF) { }
}

int hasUserId (char* userID) {
  if (userID == NULL) {
    printf("User must be registred first!\n");
    return 0;
  }

  return 1;
}

int main(int argc, char** argv) {
  ServerOptions opts = getOptions(argc, argv);
  UDPConn* conn = connectUDP(opts);

  if (conn == NULL) {
    return 1;
  }

  char cmd[256];
  char *userID = NULL;
  StringArray *topics = NULL;
  StringArray *questions = NULL;
  char *currentTopic = NULL;
  char *currentQuestion = NULL;
  int exit = 0;

  do {
    printf("> ");

    if (scanf("%s", cmd) != 1) {
      break;
    }

    switch (getCommand(cmd)) {
      case Exit:
        exit = 1;
        break;
      case Register:
        userID = registerUser(conn);
        clearInput();
        break;
      case TopicList:
        topics = topicList(conn);
        clearInput();
        break;
      case TopicSelect:
        currentTopic = topicSelect(topics);
        clearInput();
        break;
      case TopicPropose:
        if (hasUserId(userID)) {
          topicPropose(conn, userID);
          clearInput();
        }
        break;
      case QuestionList:
        questions = questionList(conn, currentTopic);
        clearInput();
        break;
      case QuestionGet:
        currentQuestion = questionGet(opts, currentTopic, questions);
        clearInput();
        break;
      case QuestionSubmit:
        if (hasUserId(userID)) {
          questionSubmit(opts, userID, currentTopic);
        }
        break;
      case AnswerSubmit:
        if (hasUserId(userID)) {
          answerSubmit(opts, userID, currentTopic, currentQuestion);
        }
        break;
      default:
        printf("Invalid command!\n");
    }

    if (errorHappened == 1) {
      printf("An error has happened while processing your request.\n");
      break;
    }
  } while (!exit);

  free(userID);
  freeStringArray(topics);
  freeStringArray(questions);
  // Do not free currentTopic not currentQuestion
  // because they're pointers to places in topics or questions,
  // respectively.
  closeUDP(conn);
  return errorHappened;
}
