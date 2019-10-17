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

char *topicSelectLong (StringArray *topics) {
  if (topics == NULL) {
    printf("You must get the topics list first!\n");
    return NULL;
  }

  char topic[11];
  scanf("%s", topic);

  for (int i = 0; i < topics->count; i++) {
    if (strcmp(topics->names[i], topic) == 0) {
      printf("Topic %s selected!\n", topics->names[i]);
      return topics->names[i];
    }
  }


  printf("404: Topic not found!\n");
  return NULL;
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

char* errorCloseAndReturnTCP (TCPConn *conn) {
  errorHappened = 1;
  closeTCP(conn);
  return NULL;
}

char* questionGet (ServerOptions opts, char* topic, StringArray *questions, int isNum) {
  if (questions == NULL) {
    printf("You must get the questions first!\n");
    return NULL;
  }

  int num;

  if (isNum) {
    scanf("%d", &num);
  } else {
    char question[11];
    num = -1;
    scanf("%s", question);

    for (int i = 0; i < questions->count; i++) {
      if (strcmp(questions->names[i], question) == 0) {
        num = i + 1;
      }
    }
  }

  if (num <= 0 || num > questions->count) {
    printf("Invalid question!\n");
    return NULL;
  }

  if (mkdirIfNotExists(topic) == -1) {
    printf("Cannot create '%s' directory.\n", topic);
    return NULL;
  }

  char msg[1024];
  sprintf(msg, "GQU %s %s\n", topic, questions->names[num - 1]);

  TCPConn* conn = connectTCP(opts);

  if (writeTCP(conn->fd, msg, strlen(msg)) != 0) return errorCloseAndReturnTCP(conn);

  char filename[256];

  char *code = readWordTCP(conn->fd);
  if (code == NULL) return errorCloseAndReturnTCP(conn);

  if (strcmp(code, "QGR") != 0) {
    free(code);
    return errorCloseAndReturnTCP(conn);
  }

  free(code);

  sprintf(filename, "%s/%s", topic, questions->names[num - 1]);
  if (readTextAndImage(conn->fd, filename, 0) == -1) {
    return errorCloseAndReturnTCP(conn);
  }

  long int answers = readPositiveNumber(conn->fd);
  if (answers < 0) return errorCloseAndReturnTCP(conn);

  for (; answers > 0; answers--) {
    long int number = readPositiveNumber(conn->fd);
    if (number < 0) return errorCloseAndReturnTCP(conn);

    sprintf(filename, "%s/%s_%ld", topic, questions->names[num - 1], number);

    if (readTextAndImage(conn->fd, filename, 0) == -1)
      return errorCloseAndReturnTCP(conn);
  }

  printf("Question and answers downloaded to %s\n", questions->names[num - 1]);
  closeTCP(conn);
  return questions->names[num - 1];
}

char* questionSubmit (ServerOptions opts, char *userID, char *topic) {
  if (topic == NULL) {
    printf("You must get the topics list first!\n");
    return NULL;
  }

  char str[1024];

  if (fgets (str, 1024, stdin) == NULL) {
    printf("Cannot read.\n");
    return NULL;
  }

  char *question = strtok(str+1, " \n");
  char *txtFile = strtok(NULL, " \n");
  char *imgFile = strtok(NULL, " \n");

  if (question == NULL || txtFile == NULL) {
    printf("A question and a text file are required!\n");
    return NULL;
  }

  if (access(txtFile, F_OK) == -1 || (imgFile != NULL && access(imgFile, F_OK) == -1)) {
    printf("Text or image file does not exist!\n");
    return NULL;
  }

  TCPConn* conn = connectTCP(opts);

  if (writeTCP(conn->fd, "QUS ", 4) != 0 ||
    writeTCP(conn->fd, userID, 5) != 0 ||
    writeTCP(conn->fd, " ", 1) != 0 ||
    writeTCP(conn->fd, topic, strlen(topic)) != 0||
    writeTCP(conn->fd, " ", 1) != 0 ||
    writeTCP(conn->fd, question, strlen(question)) != 0 ||
    writeTCP(conn->fd, " ", 1) != 0) {
      return errorCloseAndReturnTCP(conn);
  }

  if (sendFile(conn->fd, txtFile, 0, 1) == -1) {
    printf("Cannot send file properly!\n");
    return errorCloseAndReturnTCP(conn);
  }

  if (imgFile != NULL) {
    if (writeTCP(conn->fd, " 1 ", 3) != 0 ||
      sendFile(conn->fd, imgFile, 1, 1) == -1) {
      printf("Cannot send image properly!\n");
      return errorCloseAndReturnTCP(conn);
    }
  } else {
    if (writeTCP(conn->fd, " 0", 2) != 0) {
      return errorCloseAndReturnTCP(conn);
    }
  }

  if (writeTCP(conn->fd, "\n", 1) != 0) {
    return errorCloseAndReturnTCP(conn);
  }

  char *code = readWordTCP(conn->fd);
  if (code == NULL) return errorCloseAndReturnTCP(conn);
  if (strcmp(code, "QUR")) {
    free(code);
    return errorCloseAndReturnTCP(conn);
  }

  free(code);

  char *subcode = readWordTCP(conn->fd);
  if (subcode == NULL) return errorCloseAndReturnTCP(conn);

  if (strcmp(subcode, "OK") == 0) {
    printf("Question submited successfully!\n");
  } else if (strcmp(subcode, "NOK") == 0) {
    printf("Could not submit question.\n");
  } else if (strcmp(subcode, "DUP") == 0) {
    printf("Question submited already exists!\n");
  } else if (strcmp(subcode, "FUL") == 0) {
    printf("Question list is full!\n");
  } else {
    return errorCloseAndReturnTCP(conn);
  }

  closeTCP(conn);
  return NULL;
}

char* answerSubmit (ServerOptions opts, char *userID, char *topic, char *question) {
  if (topic == NULL) {
    printf("You must pick a topic first!\n");
    return NULL;
  }

  if (question == NULL) {
    printf("You must pick a question first!\n");
    return NULL;
  }

  char str[1024];

  if (fgets(str, 1024, stdin) == NULL) {
    errorHappened = 1;
    return NULL;
  }

  char *txtFile = strtok(str+1, " \n");
  char *imgFile = strtok(NULL, " \n");

  if (txtFile == NULL) {
    printf("At least a text file is required!\n");
    return NULL;
  }

  if (access(txtFile, F_OK) == -1 || (imgFile != NULL && access(imgFile, F_OK) == -1)) {
    printf("Text or image file does not exist!\n");
    return NULL;
  }

  TCPConn* conn = connectTCP(opts);

  if (writeTCP(conn->fd, "ANS ", 4) != 0 ||
    writeTCP(conn->fd, userID, 5) != 0 ||
    writeTCP(conn->fd, " ", 1) != 0 ||
    writeTCP(conn->fd, topic, strlen(topic)) != 0 ||
    writeTCP(conn->fd, " ", 1) != 0 ||
    writeTCP(conn->fd, question, strlen(question)) != 0 ||
    writeTCP(conn->fd, " ", 1) != 0) {
    return errorCloseAndReturnTCP(conn);
  }

  if (sendFile(conn->fd, txtFile, 0, 1) == -1) {
    printf("Cannot send file properly!\n");
    return errorCloseAndReturnTCP(conn);
  }

  if (imgFile != NULL) {
    if (writeTCP(conn->fd, " 1 ", 3) != 0 ||
      sendFile(conn->fd, imgFile, 1, 1) == -1) {
      printf("Cannot send image properly!\n");
      return errorCloseAndReturnTCP(conn);
    }
  } else {
    if (writeTCP(conn->fd, " 0", 2) != 0) {
      return errorCloseAndReturnTCP(conn);
    }
  }

  if (writeTCP(conn->fd, "\n", 1) != 0) {
    return errorCloseAndReturnTCP(conn);
  }

  char *code = readWordTCP(conn->fd);
  if (code == NULL) return errorCloseAndReturnTCP(conn);
  if (strcmp(code, "ANR")) {
    free(code);
    return errorCloseAndReturnTCP(conn);
  }

  free(code);

  char *subcode = readWordTCP(conn->fd);
  if (subcode == NULL) return errorCloseAndReturnTCP(conn);

  if (strcmp(subcode, "OK") == 0) {
    printf("Answer submited successfully!\n");
  } else if (strcmp(subcode, "NOK") == 0) {
    printf("Could not submit answer.\n");
  } else if (strcmp(subcode, "FUL") == 0) {
    printf("Answer list is full!\n");
  } else {
    return errorCloseAndReturnTCP(conn);
  }

  closeTCP(conn);
  return NULL;
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

void help () {
  printf("register/reg <userID>topic_list/tl\n");
  printf("topic_select <topic name>\n");
  printf("ts <topic_number>\n");
  printf("topic_propose/tp <topic>\n");
  printf("question_list/ql\n");
  printf("question_get <question name>\n");
  printf("qg <question_number>\n");
  printf("answer_submit/as <text_file [image_file]>\n");
  printf("question_submit/qs <question_name> <text_file [image_file]>\n");
  printf("exit\n");
}

int main(int argc, char** argv) {
  ServerOptions opts = getOptions(argc, argv);
  UDPConn* conn = connectUDP(opts);

  if (conn == NULL) {
    return 1;
  }

  char cmd[1024];
  char *userID = NULL;
  StringArray *topics = NULL;
  StringArray *questions = NULL;
  char *currentTopic = NULL;
  char *currentQuestion = NULL;
  int exit = 0;

  printf("\nYou are connected to %s:%s. Type '?' for help.\n\n", opts.ip, opts.port);

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
        if (userID != NULL) {
          printf("User %s is already registred!\n", userID);
        } else {
          userID = registerUser(conn);
        }
        clearInput();
        break;
      case TopicList:
        topics = topicList(conn);
        clearInput();
        break;
      case TopicSelectShort:
        currentTopic = topicSelect(topics);
        clearInput();
        break;
      case TopicSelectLong:
        currentTopic = topicSelectLong(topics);
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
      case QuestionGetShort:
        currentQuestion = questionGet(opts, currentTopic, questions, 1);
        clearInput();
        break;
      case QuestionGetLong:
        currentQuestion = questionGet(opts, currentTopic, questions, 0);
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
      case Help:
        help();
        break;
      default:
        printf("Invalid command!\n");
        clearInput();
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
