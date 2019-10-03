#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/stat.h>
#include "cmds.h"
#include "net.h"

char *registerUser (UDPConn *conn) {
  char msg[11] = "REG ";
  scanf("%s", msg + 4);
  msg[9] = '\n';
  msg[10] = '\0';

  char *buffer = sendUDP(conn, msg);

  if (buffer == NULL) {
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

StringArray* topicList (UDPConn *conn) {
  char* buffer = sendUDP(conn, "LTP\n");
  StringArray *topics = malloc(sizeof(StringArray));
  int pos = 0;
  sscanf(buffer, "LTR %d%n", &topics->count, &pos);
  pos++;

  char *spaceToken = strtok(buffer + pos, " :\n");
  printf("Available Topics:\n");

  topics->names = malloc(topics->count * sizeof(char *));

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
  char* res = sendUDP(conn, msg);

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
  char msg[256];
  sprintf(msg, "LQU %s\n", topic);
  char *buffer = sendUDP(conn, msg);

  int pos = 0;
  sscanf(buffer, "LQR %d%n", &questions->count, &pos);
  pos++;

  questions->names = malloc(questions->count * sizeof(char *));

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

  return questions;
}

int readAndSave (int socket, int isImg, char* filename) {
  char tmp[256];
  int readCode;

  if (isImg) {
    read(socket, tmp, 4);
    tmp[3] = '\0';
    strcat(filename, ".");
    strcat(filename, tmp);
  }

  FILE *fp = fopen(filename, "w");
  int size = 0;
  int i = 0;

  while ((readCode = read(socket, tmp + i, 1)) == 1) {
    if (tmp[i] == ' ') {
      tmp[i] = '\0';
      size = atoi(tmp);
      break;
    }
    i++;
  }

  int toRead = 256;
  do {
    if (size < toRead) toRead = size;
    readCode = read(socket, tmp, toRead);
    if (readCode <= 0) break;
    size -= readCode;
    fwrite (tmp , sizeof(char), readCode, fp);
  } while (size > 0);

  fclose(fp);
  return 0;
}

int readTextAndImage (int socket, char *basename) {
  char buffer[256], filename[256];

  if (read(socket, buffer, 6) != 6) {
    return -1;
  }

  buffer[6] = '\0';

  strcpy(filename, basename);
  strcat(filename, "/user.txt");

  FILE *fp = fopen(filename, "w");
  if (fputs(buffer + 1, fp) == EOF || fclose(fp) == EOF) {
    return -1;
  }

  strcpy(filename, basename);
  strcat(filename, "/question.txt");

  readAndSave(socket, 0, filename);

  if (read(socket, buffer, 2) != 2) {
    return -1;
  }

  if (buffer[1] == '1') {
    if (read(socket, buffer, 1) != 1) {
      return -1;
    }

    strcpy(filename, basename);
    strcat(filename, "/image");
    readAndSave(socket, 1, filename);
  }

  return 0;
}

void questionGet (ServerOptions opts, char* topic, StringArray *questions) {
  if (questions == NULL) {
    printf("You must get the questions first!\n");
    return;
  }


  int num;
  scanf("%d", &num);

  if (num <= 0 || num > questions->count) {
    printf("Invalid question number!\n");
    return;
  }

  char msg[1024];
  sprintf(msg, "GQU %s %s\n", topic, questions->names[num - 1]);

  if (mkdir(questions->names[num - 1], S_IRWXU) == -1) {
    printf("Cannot create dir\n");
    return;
  }
  TCPConn* conn = connectTCP(opts);

  if (write(conn->fd, msg, strlen(msg)) == -1) {
    printf("An error has occurred.\n");
    closeTCP(conn);
    return;
  }

  char buffer[256], filename[256];

  if (read(conn->fd, buffer, 3) != 3) {
    printf("ERR\n"); closeTCP(conn); return;
  }

  // TODO: check errs
  buffer[3] = '\0';
  if (strcmp(buffer, "QGR")) {
    printf("ERR\n"); closeTCP(conn); return;
  }

  read(conn->fd, buffer, 1);
  readTextAndImage(conn->fd, questions->names[num - 1]);
  read(conn->fd, buffer, 1);

  read(conn->fd, buffer, 1);
  int answers = 0;

  if (buffer[0] == '1') {
    read(conn->fd, buffer, 1);
    answers = 10;
  } else {
    buffer[1] = '\0';
    answers = atoi(buffer);
  }

  if (answers != 0) {
    read(conn->fd, buffer, 1);

    for (; answers > 0; answers--) {
      strcpy(filename, questions->names[num - 1]);
      strcat(filename, "/");
      read(conn->fd, buffer, 3);
      buffer[2] = '\0';

      strcat(filename, buffer);
      mkdir(filename, S_IRWXU);
      
      readTextAndImage(conn->fd, filename);
      read(conn->fd, buffer, 1);
    }
  }

  printf("Question and answers downloaded to %s\n", questions->names[num - 1]);
  closeTCP(conn);
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

  // TODO: check for errors
  write(conn->fd, "QUS ", 4);
  write(conn->fd, userID, 5);
  write(conn->fd, " ", 1);
  write(conn->fd, topic, strlen(topic));
  write(conn->fd, " ", 1);
  write(conn->fd, question, strlen(question));
  write(conn->fd, " ", 1);

  if (sendFile(conn->fd, txtFile, 0) == -1) {
    printf("Cannot send file properly!\n");
    return;
  }

  if (imgFile != NULL) {
    write(conn->fd, "1 ", 2);
    if (sendFile(conn->fd, imgFile, 1) == -1) {
      printf("Cannot send image properly!\n");
      return;
    }
  } else {
    write(conn->fd, "0", 1);
  }
  printf("meda\n");

  write(conn->fd, "\n", 1);

  char buffer[1024];

  while (read(conn->fd, buffer, 1024) > 0) {
  printf("%s\n", buffer);
  }


  closeTCP(conn);
}

void answerSubmit () {
  printf("I do nothing yet. Get away!\n");
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
        questionGet(opts, currentTopic, questions);
        clearInput();
        break;
      case QuestionSubmit:
        if (hasUserId(userID)) {
          questionSubmit(opts, userID, currentTopic);
        }
        break;
      case AnswerSubmit:
        if (hasUserId(userID)) {
          answerSubmit();
        }
        break;
      default:
        printf("Invalid command! Go read a book.\n");
    }
  } while (!exit);

  closeUDP(conn);
  return 0;
}
