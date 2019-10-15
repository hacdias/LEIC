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
  return sendto(conn->fd, msg, strlen(msg), 0, (struct sockaddr*)&addr, sizeof(addr));
}

char* sendWithReplyUDP (UDPConn *conn, char* msg) {
  int n = sendto(conn->fd, msg, strlen(msg), 0, conn->res->ai_addr, conn->res->ai_addrlen);
  if (n == -1) {
    return NULL;
  }

  return receiveUDP(conn, NULL, NULL);
}

char* receiveUDP (UDPConn *conn, struct sockaddr_in* addr, socklen_t* addrlen) {
  int size = 0, len = 0;
  char *buffer = NULL;

  do {
    size += 1024;
    buffer = realloc(buffer, size);
    len = recvfrom(conn->fd, buffer, size - 1, MSG_PEEK, (struct sockaddr*)addr, addrlen);
  } while (len > size && len > 0);

  if (len < 0) {
    free(buffer);
    return NULL;
  }

  len = recvfrom(conn->fd, buffer, size, 0, (struct sockaddr*)addr, addrlen);
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

char* readTCP (int socket) {
  int size = 32;
  char* buffer = malloc(sizeof(char) * size);
  int offset = 0, n;

  while ((n = read(socket, buffer + offset, 1)) == 1 && buffer[offset] != ' ' && buffer[offset] != '\n' && buffer[offset] != '\r') {
    offset++;

    if (offset >= size) {
      size += 32;
      buffer = realloc(buffer, size);
    }
  }

  if (n == -1) {
    return NULL;
  }

  buffer = realloc(buffer, offset);
  buffer[offset] = '\0';
  return buffer;
}

void closeTCP (TCPConn* conn) {
  freeaddrinfo(conn->res);
	close(conn->fd);
  free(conn);
}

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

    if (write(connFd, dot + 1, strlen(dot + 1)) == -1) {
      return -1;
    }

    write(connFd, " ", 1);
  }

  if (sendSize) {
    char size[256];
    sprintf(size, "%lld ", st.st_size);
    if (write(connFd, size, strlen(size)) == -1) {
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
    if (write(connFd, fileData, nbytes) < 0) {
      break;
    }

    bzero(fileData, 10000);
  }

  return 0;
}

int readAndSave (int socket, const char* basename, int isImg, int isServer) {
  char tmp[256], filename[256];
  int readCode;

  if (isImg) {
    read(socket, tmp, 4);
    tmp[3] = '\0';

    if (isServer) {
      sprintf(filename, "%s/img_ext", basename);

      FILE *fp = fopen(filename, "w");
      fwrite (tmp , sizeof(char), 3, fp);
      fclose(fp);

      sprintf(filename, "%s/img", basename);
    } else {
      sprintf(filename, "%s.%s", basename, tmp);
    }
  } else {
    sprintf(filename, "%s", basename);
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

int readTextAndImage (int socket, const char *basename, int isServer) {
  char buffer[256], filename[256];

  if (read(socket, buffer, 6) != 6) {
    return -1;
  }

  buffer[5] = '\0';

  if (isServer) {
    sprintf(filename, "%s/user", basename);
  } else {
    sprintf(filename, "%s_user.txt", basename);
  }

  FILE *fp = fopen(filename, "w");
  if (fputs(buffer, fp) == EOF || fclose(fp) == EOF) {
    return -1;
  }

  if (isServer) {
    sprintf(filename, "%s/data", basename);
  } else {
    sprintf(filename, "%s.txt", basename);
  }

  readAndSave(socket, filename, 0, isServer);

  if (read(socket, buffer, 2) != 2) {
    return -1;
  }

  if (buffer[1] == '1') {
    if (read(socket, buffer, 1) != 1) {
      return -1;
    }

    readAndSave(socket, basename, 1, isServer);
  }

  return 0;
}
