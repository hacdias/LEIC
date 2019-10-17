#include "net.h"

#define LOCALHOST "127.0.0.1"
#define PORT "58035"

ServerOptions getOptions (int argc, char** argv) {
  ServerOptions opts = {LOCALHOST, PORT};
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

UDPConn *connectUDP (ServerOptions opts) {
  UDPConn *conn = malloc(sizeof(UDPConn));
  int n;

	memset(&conn->hints, 0, sizeof(conn->hints));
	conn->hints.ai_family = AF_INET;
	conn->hints.ai_socktype = SOCK_DGRAM;
	conn->hints.ai_flags = AI_NUMERICSERV;

  n = getaddrinfo(opts.ip, opts.port, &conn->hints, &conn->res);
	if (n != 0) {
    return NULL;
  }

  conn->fd = socket(conn->res->ai_family, conn->res->ai_socktype, conn->res->ai_protocol);
	if (conn->fd == -1) {
    return NULL;
  }

  return conn;
}

UDPConn *listenUDP (ServerOptions opts) {
  UDPConn *conn = malloc(sizeof(UDPConn));
  int n;

	memset(&conn->hints, 0, sizeof(conn->hints));
	conn->hints.ai_family = AF_INET;
	conn->hints.ai_socktype = SOCK_DGRAM;
	conn->hints.ai_flags = AI_PASSIVE|AI_NUMERICSERV;

  n = getaddrinfo(NULL, opts.port, &conn->hints, &conn->res);
	if (n != 0) {
    return NULL;
  }

  conn->fd = socket(conn->res->ai_family, conn->res->ai_socktype, conn->res->ai_protocol);
	if (conn->fd == -1) {
    return NULL;
  }

  n = bind(conn->fd, conn->res->ai_addr, conn->res->ai_addrlen);
  if (n == -1) {
    return NULL;
  }

  return conn;
}

void closeUDP (UDPConn* conn) {
  freeaddrinfo(conn->res);
	close(conn->fd);
  free(conn);
}

int sendUDP (UDPConn *conn, const char* msg, struct sockaddr_in addr) {
  return sendto(conn->fd, msg, strlen(msg), 0, (struct sockaddr*)&addr, sizeof(addr)) != strlen(msg);
}

char* sendWithReplyUDP (UDPConn *conn, char* msg) {
  int tries = 0;

  while (tries < 3) {
    int n = sendto(conn->fd, msg, strlen(msg), 0, conn->res->ai_addr, conn->res->ai_addrlen);
    if (n == -1) {
      return NULL;
    }

    sleep(1);
    char* res = receiveUDP(conn, NULL, NULL);
    if (res == NULL && (errno == EAGAIN || errno == EWOULDBLOCK)) {
      tries++;
      sleep(3);
      res = receiveUDP(conn, NULL, NULL);
    }

    if (res != NULL) {
      return res;
    }
  }

  printf("Cannot contact the server.\n");
  return NULL;
}

char* receiveUDP (UDPConn *conn, struct sockaddr_in* addr, socklen_t* addrlen) {
  int size = 0, len = 0;
  char *buffer = NULL;

  do {
    size += 1024;
    buffer = realloc(buffer, size);
    len = recvfrom(conn->fd, buffer, size - 1, MSG_DONTWAIT|MSG_PEEK, (struct sockaddr*)addr, addrlen);
  } while (len == size - 1);

  if (len < 0) {
    free(buffer);
    return NULL;
  }

  len = recvfrom(conn->fd, buffer, size, MSG_DONTWAIT, (struct sockaddr*)addr, addrlen);
  if (len < 0) {
    free(buffer);
    return NULL;
  }
  buffer[len] = '\0';
  return buffer;
}

TCPConn *connectTCP (ServerOptions opts) {
  TCPConn *conn = malloc(sizeof(TCPConn));
  int n;

	memset(&conn->hints, 0, sizeof(conn->hints));
	conn->hints.ai_family = AF_INET;
	conn->hints.ai_socktype = SOCK_STREAM;
	conn->hints.ai_flags = AI_NUMERICSERV;

  n = getaddrinfo(opts.ip, opts.port, &conn->hints, &conn->res);
	if (n != 0) {
    return NULL;
  }

  conn->fd = socket(conn->res->ai_family, conn->res->ai_socktype, conn->res->ai_protocol);
	if (conn->fd == -1) {
    return NULL;
  }

  n = connect(conn->fd, conn->res->ai_addr, conn->res->ai_addrlen);
  if (n == -1) {
    return NULL;
  }

  return conn;
}

TCPConn *listenTCP (ServerOptions opts) {
  TCPConn *conn = malloc(sizeof(TCPConn));
  int n;

	memset(&conn->hints, 0, sizeof(conn->hints));
	conn->hints.ai_family = AF_INET;
	conn->hints.ai_socktype = SOCK_STREAM;
	conn->hints.ai_flags = AI_PASSIVE|AI_NUMERICSERV;

  n = getaddrinfo(NULL, opts.port, &conn->hints, &conn->res);
	if (n != 0) {
    return NULL;
  }

  conn->fd = socket(conn->res->ai_family, conn->res->ai_socktype, conn->res->ai_protocol);
	if (conn->fd == -1) {
    return NULL;
  }

  n = bind(conn->fd, conn->res->ai_addr, conn->res->ai_addrlen);
  if (n == -1) {
    return NULL;
  }

  if (listen(conn->fd, 5) == -1) {
    return NULL;
  }

  return conn;
}

int readTCP (int socket, char* buffer, int chars) {
  int n = 0;

  while (n != chars) {
    int k = read(socket, buffer + n, chars - n);

    if (k < 0) {
      return k;
    }

    n += k;
  }

  return 0;
}

// readSpaceTCP returns 0 if a space is successfully read,
// 1 if it's not a space, -1 if an error occurred.
int readSpaceTCP (int socket) {
  char c;

  if (readTCP(socket, &c, 1) != 0) {
    return -1;
  }

  return c != ' ';
}

// readSpaceOrNewLineTCP returns 0 if a space is successfully read,
// 1 if it's not a space, -1 if an error occurred.
int readSpaceOrNewLineTCP (int socket) {
  char c;

  if (readTCP(socket, &c, 1) != 0) {
    return -1;
  }

  return !(c == ' ' || c == '\n' || c == '\r');
}

// readWordTCP reads a contiguos word via TCP. It consumes a
// space, \n or \r after the last character.
char* readWordTCP (int socket) {
  int size = 32, offset = 0, n = 0;
  char* buffer = malloc(sizeof(char) * size);

  while ((n = readTCP(socket, buffer + offset, 1)) == 0) {
    if (buffer[offset] == ' ' || buffer[offset] == '\n' || buffer[offset] == '\r') {
      break;
    }

    offset++;
    if (offset >= size - 1) {
      size += 32;
      buffer = realloc(buffer, sizeof(char) * size);
    }
  }

  if (n != 0) {
    return NULL;
  }

  buffer = realloc(buffer, offset + 1);
  buffer[offset] = '\0';
  return buffer;
}

void closeTCP (TCPConn* conn) {
  freeaddrinfo(conn->res);
	close(conn->fd);
  free(conn);
}

int writeTCP(int socket, const char* buffer, int size) {
  int n = 0;

  while (n != size) {
    int k = write(socket, buffer + n, size - n);
    if (k < 0) return -1;
    n += k;
  }

  return 0;
}

// returns -1 on error.
int sendFile (int connFd, char *file, int extension, int sendSize) {
  struct stat st;
  if (stat(file, &st) == -1) {
    return -1;
  }

  if (extension) {
    const char *dot = strrchr(file, '.');
    if (!dot || dot == file) {
      return -1;
    }

    if (writeTCP(connFd, dot + 1, strlen(dot + 1)) != 0 || writeTCP(connFd, " ", 1) != 0) {
      return -1;
    }
  }

  if (sendSize) {
    char size[256];
    sprintf(size, "%ld ", st.st_size);
    if (writeTCP(connFd, size, strlen(size)) != 0) {
      return -1;
    }
  }

  FILE *fpointer = fopen(file, "r");
  if (fpointer == NULL) {
    return -1;
  }

  unsigned char fileData[10000];
  size_t nbytes = 0;
  while ((nbytes = fread(fileData, sizeof(unsigned char), 10000, fpointer)) > 0) {
    if (writeTCP(connFd, fileData, nbytes) != 0) {
      break;
    }

    bzero(fileData, 10000);
  }

  return 0;
}

// reads a message like this: [ext] size data
int readAndSave (int socket, const char* basename, int isImg, int isServer) {
  char tmp[256], filename[256];
  int readCode;

  if (isImg) {
    if (readTCP(socket, tmp, 3) != 0 || readSpaceTCP(socket) != 0) {
      return -1;
    }

    tmp[3] = '\0';

    if (isServer) {
      sprintf(filename, "%s/img_ext", basename);

      FILE *fp = fopen(filename, "w");
      if (fp == NULL) return -1;

      fwrite(tmp , sizeof(char), 3, fp);
      fclose(fp);
      sprintf(filename, "%s/img", basename);
    } else {
      sprintf(filename, "%s.%s", basename, tmp);
    }
  } else {
    sprintf(filename, "%s", basename);
  }

  FILE *fp = fopen(filename, "w");
  if (fp == NULL) return -1;

  long int size = readPositiveNumber(socket);
  if (size < 0) return -1;

  int toRead = 256;
  do {
    if (size < toRead) toRead = size;
    readCode = read(socket, tmp, toRead);
    if (readCode < 0) break;
    size -= readCode;
    fwrite(tmp , sizeof(char), readCode, fp);
  } while (size > 0);

  fclose(fp);
  return 0;
}

int readTextAndImage (int socket, const char *basename, int isServer) {
  char filename[256];

  if (!isServer) {
    char *userID = readWordTCP(socket);
    if (userID == NULL) return -1;
    if (strlen(userID) != 5) { free(userID); return -1; }

    sprintf(filename, "%s_user.txt", basename);

    FILE *fp = fopen(filename, "w");
    if (fp == NULL || fputs(userID, fp) == EOF || fclose(fp) == EOF) {
      free(userID);
      return -1;
    }
    free(userID);
  }

  if (isServer) {
    sprintf(filename, "%s/data", basename);
  } else {
    sprintf(filename, "%s.txt", basename);
  }

  if (readAndSave(socket, filename, 0, isServer) != 0) {
    return -1;
  }

  if (readSpaceTCP(socket) != 0) return -1;
  char *hasImage = readWordTCP(socket);
  if (hasImage == NULL) return -1;
  if (strlen(hasImage) != 1) { free(hasImage); return -1; }

  if (hasImage[0] == '1') {
    free(hasImage);
    if (readAndSave(socket, basename, 1, isServer) != 0) return -1;
    if (readSpaceOrNewLineTCP(socket) != 0) return -1;
  }

  return 0;
}

long int readPositiveNumber (int socket) {
  char *string = readWordTCP(socket);
  if (string == NULL) return -1;

  char *ptr;
  long int answers = strtol(string, &ptr, 10);
  free(string);

  if (*ptr != '\0') return -1;
  return answers;
}
