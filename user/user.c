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

  TCPConn* conn = connectTCP(opts);

  if (write(conn->fd, msg, strlen(msg)) == -1) {
    printf("I'm sad :(");
    closeTCP(conn);
    return;
  }


  char buffer[1024];

  while (read(conn->fd, buffer, 256) != -1) {
    printf("%s\n", buffer);
  }

  printf("I'm TCPing'");

  closeTCP(conn);

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
        break;
      case TopicList:
        topics = topicList(conn);
        break;
      case TopicSelect:
        currentTopic = topicSelect(topics);
        break;
      case TopicPropose:
        if (hasUserId(userID)) {
          topicPropose(conn, userID);
        }
        break;
      case QuestionList:
        questions = questionList(conn, currentTopic);
        break;
      case QuestionGet:
        questionGet(opts, currentTopic, questions);
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
