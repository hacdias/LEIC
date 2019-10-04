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

#define STORAGE "TOPICS"

int max (int x, int y) {
  return x > y ? x : y;
}

void handleTCP () {

}

int mkdirIfNotExists (const char *name) {
  DIR* dir = opendir(name);
  if (dir) {
    closedir(dir);
    return 0;
  } else if (ENOENT == errno) {
    return mkdir(name, S_IRWXU);
  } else {
    return -1;
  }
}

int handleReg (UDPConn *conn, struct sockaddr_in addr, char *buffer) {
  int notOk = 0;

  for (int i = 4; i < 9; i++) {
    if (buffer[i] < '0' && buffer[i] > '9') {
      notOk = 1; break;
    }
  }

  if (buffer[9] != '\n' || notOk) {
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
      if (!strcmp(dir->d_name, ".") || !strcmp(dir->d_name, "..")) {
        continue;
      }

      char filename[256];
      sprintf(filename, "%s/%s/%s_UID.txt", STORAGE, dir->d_name, dir->d_name);

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
      strcat(dirName, buffer);
      strcat(dirName, "_UID.txt");

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
    printf("Not working bitch.\n");
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

    if (FD_ISSET(tcpConn->fd, &desc)) handleTCP();
    if (FD_ISSET(udpConn->fd, &desc)) handleUDP(udpConn);
  }

  closeUDP(udpConn);
  closeTCP(tcpConn);
  return 0;
}
