#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
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

void topicList (UDPConn *conn) {
  char* buffer = sendUDP(conn, "LTP\n");
  int topics = 0, pos = 0;
  sscanf(buffer, "LTR %d%n", &topics, &pos);
  pos++;

  char *spaceToken = strtok(buffer + pos, " :\n");
  printf("Available Topics:\n");

  for (int i = 0; i < topics; i++) {
    printf("%d. '%s'", i + 1, spaceToken);
    spaceToken = strtok(NULL, " :\n");
    printf(" (proposed by %s)\n", spaceToken);
    spaceToken = strtok(NULL, " :\n");
  }

  free(buffer);
}

int topicSelect () {
  int num;
  scanf("%d", &num);
  printf("Topic %d selected!\n", num);
  return num;
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
  } else {
    printf("Could not propose topic.\n");
  }

  free(res);
}

void questionList (UDPConn *conn) {
  printf("I do nothing yet. Get away!");
}

void questionGet () {
  printf("I do nothing yet. Get away!");
}

void questionSubmit () {
  printf("I do nothing yet. Get away!");
}

void answerSubmit () {
  printf("I do nothing yet. Get away!");
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
  int currentTopic = -1;
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
        break;
      case TopicList:
        topicList(conn);
        break;
      case TopicSelect:
       currentTopic = topicSelect();
       break;
      case TopicPropose:
        if (hasUserId(userID)) {
          topicPropose(conn, userID);
        }
        break;
      case QuestionList:
        questionList(conn);
        break;
      case QuestionGet:
        questionGet();
        break;
      case QuestionSubmit:
        if (hasUserId(userID)) {
          questionSubmit();
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

    clearInput();
  } while (!exit);

  closeUDP(conn);
  return 0;
}