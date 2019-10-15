#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <errno.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/stat.h>
#include <errno.h>
#include "../lib/net.h"
#include "../lib/dirs.h"

#define STORAGE "TOPICS"

int max (int x, int y) {
  return x > y ? x : y;
}

int isValidUserID (const char *userID) {
  if (strlen(userID) != 5) return 0;

  for (int i = 0; i < 5; i++) {
    if (userID[i] < '0' && userID[i] > '9') {
      return 0;
    }
  }

  return 1;
}

int sendUserDataAndImage (int socket, const char* dir) {
  char filename[1024];
  sprintf(filename, "%s/user", dir);
  sendFile(socket, filename, 0, 0);
  write(socket, " ", 1);

  sprintf(filename, "%s/data", dir);
  sendFile(socket, filename, 0, 1);

  sprintf(filename, "%s/img_ext", dir);

  struct stat st;
  if (stat(filename, &st) == -1) {
    write(socket, " 0", 2);
  } else {
    write(socket, " 1 ", 3);
    sendFile(socket, filename, 0, 0);
    write(socket, " ", 1);
    sprintf(filename, "%s/img", dir);
    sendFile(socket, filename, 0, 1);
  }

  return 0;
}
int numOfDirectories (const char* name) {
  if (!dirExists(name)) return -1;

  int count = 0;
  DIR *d = opendir(name);
  struct dirent *dir;

  while ((dir = readdir(d)) != NULL) {
    if (dir->d_name[0] == '.'
      || dir->d_type != DT_DIR) {
      continue;
    }

    count++;
  }

  return count;
}

int handleGqu (int socket) {
  char* topic = readTCP(socket);
  if (topic == NULL) return -1;
  char* question = readTCP(socket);
  if (question == NULL) return -1;

  char dirName[258];
  sprintf(dirName, "%s/%s/%s", STORAGE, topic, question);

  int exists = dirExists(dirName);

  if (exists == 1) {
    write(socket, "QGR ", 4);

    sendUserDataAndImage(socket, dirName);

    int answersCount = numOfDirectories(dirName);
    if (answersCount == -1) {
      printf("ERRO");
      // TODO;
    }

    char count[3];
    write(socket, " ", 1);

    if (answersCount >= 10) {
      write(socket, "10", 2);
    } else if (answersCount >= 1) {
      sprintf(count, "%d", answersCount);
      write(socket, count, 1);
    } else {
      write(socket, "0\n", 2);
      free(topic);
      free(question);
      return 0;
    }

    int firstAns = answersCount;

    if (answersCount <= 10) {
      firstAns = 1;
    } else {
      firstAns = answersCount - 9;
    }

    for (; firstAns <= answersCount; firstAns++) {
      write(socket, " ", 1);
      sprintf(dirName, "%s/%s/%s/%d", STORAGE, topic, question, firstAns);
      sendUserDataAndImage(socket, dirName);
    }

    write(socket, "\n", 1);
    free(topic);
    free(question);
    return 0;
  } else if (exists == 0) {
    free(topic);
    free(question);
    return write(socket, "QGR EOF\n", 8);
  }

  free(topic);
  free(question);
  return write(socket, "QGR ERR\n", 8);
}

int handleQus (int socket) {
  char* userID = readTCP(socket);
  if (userID == NULL) return -1;
  if (!isValidUserID(userID)) {
    free(userID);
    return write(socket, "QUR NOK\n", 8) != 8;
  }

  char *topic = readTCP(socket);
  if (topic == NULL) {
    free(userID);
    return -1;
  }

  char *question = readTCP(socket);
  if (question == NULL) {
    free(topic);
    free(userID);
    return -1;
  }

  char dirName[256];
  sprintf(dirName, "%s/%s", STORAGE, topic);
  if (dirExists(dirName) != 1) {
    free(userID);
    free(topic);
    free(question);

    return write(socket, "QUR NOK\n", 8) != 8;
  }

  sprintf(dirName, "%s/%s/%s", STORAGE, topic, question);
  if (dirExists(dirName) == 1) {
    free(userID);
    free(topic);
    free(question);

    return write(socket, "QUR DUP\n", 8) != 8;
  }

  int success = readTextAndImage(socket, dirName, 1);

  free(userID);
  free(topic);
  free(question);
  return success;
}

int handleAns (int socket) {
  return 0;
}

void handleTCP (TCPConn *conn) {
  int fd, n;
  struct sockaddr_in clientAddr;
  socklen_t len = sizeof(clientAddr);

  if ((fd = accept(conn->fd,(struct sockaddr*)&clientAddr, &len)) == -1) {
    return;
  }

  char* cmd = readTCP(fd);
  if (cmd == NULL) {
    printf("Merda aconeceu;");
    return;
  }

  if (!strcmp(cmd, "GQU")) {
    n = handleGqu(fd);
  } else if (!strcmp(cmd, "QUS")) {
    n = handleQus(fd);
  } else if (!strcmp(cmd, "ANS")) {
    n = handleAns(fd);
  } else {
    write(fd, "ERR\n", 4);
  }

  // TODO: ver n

  free(cmd);
  printf("TCP/IP %s %s\n", inet_ntoa(clientAddr.sin_addr), cmd);
  close(fd);
}

int handleReg (UDPConn *conn, struct sockaddr_in addr, char *buffer) {
  if (buffer[9] != '\n') {
    return sendUDP(conn, "RGR NOK\n", addr);
  }

  buffer[9] = '\0';

  if (!isValidUserID(buffer + 4)) {
    return sendUDP(conn, "RGR NOK\n", addr);
  }

  return sendUDP(conn, "RGR OK\n", addr);
}

int handleLtp (UDPConn *conn, struct sockaddr_in addr, char *buffer) {
  DIR *d = opendir(STORAGE);
  struct dirent *dir;
  int i = 0;

  if (d) {
    char str[1024];
    bzero(str, sizeof(str));

    while ((dir = readdir(d)) != NULL) {
      if (dir->d_name[0] == '.') {
        continue;
      }

      char filename[256];
      sprintf(filename, "%s/%s/user", STORAGE, dir->d_name);

      FILE *fp = fopen(filename, "r");
      if (fp == NULL) {
        closedir(d);
        return -1;
      }

      char userID[6];
      userID[5] = '\0';

      if (fread(userID, sizeof(char), 5, fp) == -1) {
        closedir(d);
        fclose(fp);
        return -1;
      }

      strcat(str, " ");
      strcat(str, dir->d_name);
      strcat(str, ":");
      strcat(str, userID);

      fclose(fp);
      i++;
    }

    closedir(d);

    char buffer[1024];
    sprintf(buffer, "LTR %d%s\n", i, str);
    return sendUDP(conn, buffer, addr);
  } else {
    return -1;
  }
}

int handlePtp (UDPConn *conn, struct sockaddr_in addr, char *buffer) {
  buffer = buffer + 4;
  char dirName[1024];
  char userID[5];

  for (int i = 0; i < 5; i++) {
    if (buffer[i] < '0' || buffer[i] > '9') {
      return sendUDP(conn, "PTR NOK\n", addr);
    }

    userID[i] = buffer[i];
  }

  buffer = buffer + 6;
  buffer[strlen(buffer) - 1] = '\0';

  sprintf(dirName, "%s/%s", STORAGE, buffer);
  DIR *dir = opendir(dirName);
  if (dir) {
    closedir(dir);
    return sendUDP(conn, "PTR DUP\n", addr);
  } else if (ENOENT == errno) {
    if (mkdir(dirName, S_IRWXU) == -1) {
      printf("Error creating directory %s\n", dirName);
      return sendUDP(conn, "PTR NOK\n", addr);
    } else {
      strcat(dirName, "/");
      strcat(dirName, "user");

      FILE *fp = fopen(dirName, "w");
      printf("%s-%s\n", userID, dirName);
      fwrite(userID, sizeof(char), sizeof(userID), fp);
      fclose(fp);
      return sendUDP(conn, "PTR OK\n", addr);
    }
  } else {
    return -1;
  }

  // TODO: Directory full needs to be implemented!

  return 0;
}

int handleLqu (UDPConn *conn, struct sockaddr_in addr, char *buffer){
  buffer = buffer + 4;
  DIR *d;
  struct dirent *dir;
  int i = 0;
  char * topic = strdup(buffer);
  topic[strlen(topic) - 1] = '\0';

  if (!strcmp(topic, "")) {
    return sendUDP(conn, "ERR\n", addr);
  }

  char dirNameTopic[2048];
  sprintf(dirNameTopic, "%s/%s", STORAGE, topic);
  d = opendir(dirNameTopic);

  if (d) {
    char str[2048];
    bzero(str, sizeof(str));

    while ((dir = readdir(d)) != NULL) {
      if (strchr(dir->d_name, '.') != NULL) {
        continue;
      }

      if (dir->d_type != DT_DIR) {
        continue;
      }

      int nAnswers = 0;
      char questionDirName[1024];
      sprintf(questionDirName, "%s/%s", dirNameTopic, dir->d_name);
      DIR *dQuestion = opendir(questionDirName);
      struct dirent *subDir;
      while ((subDir = readdir(dQuestion)) != NULL) {
        if (strchr(subDir->d_name, '.') != NULL) {
          continue;
        }

        if (subDir->d_type == DT_DIR) {
          nAnswers += 1;
        }
      }
      closedir(dQuestion);

      char filename[256];
      sprintf(filename, "%s/%s/%s/user", STORAGE, topic, dir->d_name);

      FILE *fp = fopen(filename, "r");
      if (fp == NULL) {
        closedir(d);
        return -1;
      }

      char userID[6];
      userID[5] = '\0';

      if (fread(userID, sizeof(char), 5, fp) == -1) {
        closedir(d);
        fclose(fp);
        return -1;
      }

      char nAnswersString[32];
      sprintf(nAnswersString, "%d", nAnswers);
      strcat(str, " ");
      strcat(str, dir->d_name);
      strcat(str, ":");
      strcat(str, userID);
      strcat(str, ":");
      strcat(str, nAnswersString);

      fclose(fp);
      i++;
    }

    closedir(d);

    char message[1024];
    sprintf(message, "LQR %d%s\n", i, str);
    return sendUDP(conn, message, addr);
  } else {
    return -1;
  }
}

void handleUDP (UDPConn* conn) {
  struct sockaddr_in clientAddr;
  socklen_t len = sizeof(clientAddr);
  int n = 0;

  char* buffer = receiveUDP(conn, &clientAddr, &len);
  if (buffer == NULL) {
    printf("Could not read message!\n");
    return;
  }

  printf("UDP/IP %s", inet_ntoa(clientAddr.sin_addr));
  if (buffer[3] != ' ' && buffer[3] != '\n') {
    sendUDP(conn, "ERR\n", clientAddr);
    free(buffer);
    return;
  }

  buffer[3] = '\0';
  printf(" %s\n", buffer);

  if (!strcmp(buffer, "REG")) {
    n = handleReg(conn, clientAddr, buffer);
  } else if (!strcmp(buffer, "LTP")) {
    n = handleLtp(conn, clientAddr, buffer);
  } else if (!strcmp(buffer, "PTP")) {
    n = handlePtp(conn, clientAddr, buffer);
  } else if (!strcmp(buffer, "LQU")) {
    n = handleLqu(conn, clientAddr, buffer);
  } else {
    sendUDP(conn, "ERR\n", clientAddr);
  }

  if (n == -1) {
    printf("An error occurred while processing a %s request.\n", buffer);
  }

  free(buffer);
}

int main(int argc, char** argv) {
  fd_set desc;
  int maxDesc, ready;
  ServerOptions opts = getOptions(argc, argv);
  UDPConn *udpConn = listenUDP(opts);
  TCPConn *tcpConn = listenTCP(opts);

  if (mkdirIfNotExists(STORAGE) == -1) {
    printf("Cannot create directory\n");
    return 1;
  }

  if (udpConn == NULL || tcpConn == NULL) {
    printf("Cannot listen.\n");
    return 1;
  }

  printf("Listening for UDP and TCP on port %s\n", opts.port);

  FD_ZERO(&desc);
  maxDesc = max(udpConn->fd, tcpConn->fd) + 1;

  sleep(1);

  while (1) {
    FD_SET(udpConn->fd, &desc);
    FD_SET(tcpConn->fd, &desc);

    ready = select(maxDesc, &desc, NULL, NULL, NULL);

    if (FD_ISSET(tcpConn->fd, &desc)) handleTCP(tcpConn);
    if (FD_ISSET(udpConn->fd, &desc)) handleUDP(udpConn);
  }

  closeUDP(udpConn);
  closeTCP(tcpConn);
  return 0;
}
