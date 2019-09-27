#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>

#define LOCALHOST "127.0.0.1"
#define PORT "58035"

typedef struct options {
  char ip[16];
  char port[6];
} Options;

typedef struct udpConn {
  int fd;
  struct addrinfo hints, *res;
} UDPConn;

Options getOptions (int argc, char** argv) {
  Options opts = {LOCALHOST, PORT};
  long opt;

  while ((opt = getopt(argc, argv, "n:p:")) != -1) {
    switch (opt) {
      case 'n':
        strcpy(opts.ip, optarg);
        break;
      case 'p':
        strcpy(opts.port, optarg);
        break;
      default:
        exit(1);
    }
  }

  return opts;
}

int checkCommand (char *cmd, char longForm[256], char shortForm[256]) {
  return strcmp(cmd, longForm) == 0 || strcmp(cmd, shortForm) == 0;
}

// TODO: IMPROVE THIS
char* sendUDP (UDPConn *conn, char* msg) {
  int n = sendto(conn->fd, msg, strlen(msg), 0, conn->res->ai_addr, conn->res->ai_addrlen);
  if (n == -1) {
    return NULL;
  }

  struct sockaddr_in addr;
  socklen_t addrlen = sizeof(addr);

  int size = 3, prevLen = 0, len = 0;
  char *buffer = NULL;

  do {
    size++;
    buffer = realloc(buffer, size);
    prevLen = len;
    len = recvfrom(conn->fd, buffer, size, MSG_PEEK, (struct sockaddr*) &addr, &addrlen);
  } while (len != prevLen);

  buffer = realloc(buffer, len + 1);
  len = recvfrom(conn->fd, buffer, size, 0, (struct sockaddr*) &addr, &addrlen);
  buffer[len] = '\0';
  return buffer;
}

char *registerUser (UDPConn *conn) {
  char msg[11] = "REG ";
  scanf("%s", msg + 4);
  msg[9] = '\n';
  msg[10] = '\0';

  struct sockaddr_in addr;
  char *buffer = sendUDP(conn, msg);

  if (buffer == NULL) {
    return NULL;
  }

  if (strcmp(buffer, "RGR OK\n") == 0) {
    printf("User registred!\n");
  } else {
    printf("Registration failed!\n");
    return NULL;
  }

  msg[9] = '\0';
  char *userID = strdup(msg + 4);
  free(buffer);
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
  printf("%s\n", res);

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


UDPConn *connectUDP (Options opts) {
  UDPConn *udpConn = malloc(sizeof(UDPConn));
  int n;

	memset(&udpConn->hints, 0, sizeof(udpConn->hints));
	udpConn->hints.ai_family = AF_INET;
	udpConn->hints.ai_socktype = SOCK_DGRAM;
	udpConn->hints.ai_flags = AI_NUMERICSERV;

  n = getaddrinfo(opts.ip, opts.port, &udpConn->hints, &udpConn->res);
	if (n != 0) {
    return NULL;
  }

  udpConn->fd = socket(udpConn->res->ai_family, udpConn->res->ai_socktype, udpConn->res->ai_protocol);
	if (udpConn->fd == -1) {
    return NULL;
  }

  return udpConn;
}

void closeUDP (UDPConn* conn) {
  freeaddrinfo(conn->res);
	close(conn->fd);
  free(conn);
}

void clearInput () {
  int c;
  while ((c = getchar()) != '\n' && c != EOF) { }
}

int main(int argc, char** argv) {
  Options opts = getOptions(argc, argv);
  UDPConn* conn = connectUDP(opts);

  if (conn == NULL) {
    return 1;
  }

  char cmd[256];
  char *userID = NULL;
  int currentTopic = -1;

  printf("> ");
  while (scanf("%s", cmd) == 1) {

    if (strcmp(cmd, "exit") == 0) {
      break;
    } else if (checkCommand(cmd, "register", "reg")) {
      userID = registerUser(conn);
    } else if (checkCommand(cmd, "topic_list", "tl")) {
      topicList(conn);
    } else if (checkCommand(cmd, "topic_select", "ts")) {
      currentTopic = topicSelect();
    } else if (checkCommand(cmd, "topic_propose", "tp")) {
      if (userID == NULL) {
        printf("User must be registred first!\n");
      } else {
        topicPropose(conn, userID);
      }
    } else if (checkCommand(cmd, "question_list", "ql")) {
      questionList(conn);
    } else if (checkCommand(cmd, "question_get", "qg")) {
      questionGet();
    } else if (checkCommand(cmd, "question_submit", "qs")) {
      if (userID == NULL) {
        printf("User must be registred first!\n");
      } else {
        questionSubmit();
      }
    } else if (checkCommand(cmd, "answer_submit", "as")) {
      if (userID == NULL) {
        printf("User must be registred first!\n");
      } else {
        answerSubmit();
      }
    } else {
      printf("Invalid command! Go read a book.\n");
    }

    printf("> ");
    clearInput();
  }

  closeUDP(conn);
  return 0;
}