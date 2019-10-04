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

void handleReg (int socket, struct sockaddr_in addr, char buffer[1024]) {
  int notOk = 0;

  for (int i = 4; i < 9; i++) {
    if (buffer[i] < '0' && buffer[i] > '9') {
      notOk = 1; break;
    }
  }

  if (buffer[9] != '\n' || notOk) {
    sendto(socket, "RGR NOK\n", 8, 0, (struct sockaddr*)&addr, sizeof(addr));
  } else {
    sendto(socket, "RGR OK\n", 7, 0, (struct sockaddr*)&addr, sizeof(addr));
  }
}

void handleLtp (int socket, struct sockaddr_in addr, char buffer[1024]) {
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
        // TODO: handle
        continue;
      }

      char userID[6];
      userID[5] = '\0';

      if (fread(userID, sizeof(char), 5, fp) == -1) {
        // TODO: handle
        printf("err while reading\n"); continue;
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
    sendto(socket, buffer, strlen(buffer), 0, (struct sockaddr*)&addr, sizeof(addr));
  } else {
    // TODO: do sth
  }
}

void handlePtp (int socket, struct sockaddr_in addr, char buffer[1024]) {
  buffer = buffer + 4;
  char dirName[1024];
  char userID[5];
  DIR* dir;

  for (int i = 0; i < 5; i++) {
    if (buffer[i] < '0' && buffer[i] > '9') {
      sendto(socket, "PTR NOK\n", 8, 0, (struct sockaddr*)&addr, sizeof(addr));
      return;
    }

    userID[i] = buffer[i];
  }

  buffer = buffer + 6;
  buffer[strlen(buffer) - 1] = '\0';
  
  sprintf(dirName, "%s/%s", STORAGE, buffer);
  dir = opendir(dirName);
  if (dir) {
    sendto(socket, "PTR DUP\n", 8, 0, (struct sockaddr*)&addr, sizeof(addr));
    closedir(dir);
  } else if (ENOENT == errno) {
    if (mkdir(dirName, S_IRWXU) == -1) {
      printf("Error creating directory %s\n", dirName);
      sendto(socket, "PTR NOK\n", 8, 0, (struct sockaddr*)&addr, sizeof(addr));
    } else {
      strcat(dirName, "/");
      strcat(dirName, buffer);
      strcat(dirName, "_UID.txt");

      FILE *fp = fopen(dirName, "w");
      printf("%s-%s\n", userID, dirName);
      fwrite(userID, sizeof(char), sizeof(userID), fp);
      fclose(fp);
      sendto(socket, "PTR OK\n", 7, 0, (struct sockaddr*)&addr, sizeof(addr));
    }
  } else {
    // TODO: do something
  }

  // TODO: Directory full needs to be implemented!
}

void handleUDP (int socket) {
  struct sockaddr_in clientAddr;
  socklen_t len = sizeof(clientAddr);
  char buffer[1024];
  bzero(buffer, sizeof(buffer));

  int n = recvfrom(socket, buffer, sizeof(buffer), 0, (struct sockaddr*)&clientAddr, &len);
  if (n == -1) {
    printf("An error has occurred!\n");
    return;
  }

  printf("UDP conn from %s", inet_ntoa(clientAddr.sin_addr));
  if (buffer[3] != ' ' && buffer[3] != '\n') {
    sendto(socket, "ERR\n", 4, 0, (struct sockaddr*)&clientAddr, sizeof(clientAddr));
    return;
  }

  buffer[3] = '\0';
  printf(" of type %s\n", buffer);

  if (!strcmp(buffer, "REG")) {
    handleReg(socket, clientAddr, buffer);
  } else if (!strcmp(buffer, "LTP")) {
    handleLtp(socket, clientAddr, buffer);
  } else if (!strcmp(buffer, "PTP")) {
    handlePtp(socket, clientAddr, buffer);
  } else if (!strcmp(buffer, "LQU")) {
    
  } else {
    sendto(socket, "ERR\n", 4, 0, (struct sockaddr*)&clientAddr, sizeof(clientAddr));
    return;
  }
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
    if (FD_ISSET(udpConn->fd, &desc)) handleUDP(udpConn->fd);
  }

  closeUDP(udpConn);
  closeTCP(tcpConn);
  return 0;
}
