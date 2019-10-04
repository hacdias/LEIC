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

char* sendUDP (UDPConn *conn, char* msg) {
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

void closeTCP (TCPConn* conn) {
  freeaddrinfo(conn->res);
	close(conn->fd);
  free(conn);
}

int sendFile (int connFd, char *file, int extension) {
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

    printf("%s %lu\n", dot + 1, strlen(dot + 1));

    write(connFd, " ", 1);
  }

  char size[256];
  sprintf(size, "%lld ", st.st_size);
  if (write(connFd, size, strlen(size)) == -1) {
    return -1;
  }

  printf("%s\n", size);

  FILE *fpointer = fopen(file, "r");
  if (fpointer == NULL) {
    return -1;
  }

  unsigned char fileData[10000];
  size_t nbytes = 0;
  while ((nbytes = fread(fileData, sizeof(unsigned char), 10000, fpointer)) > 0) {
    if (write(connFd, fileData, nbytes) < 0) {
      printf("%lu\n", nbytes);
      break;
    }

    bzero(fileData, 10000);
  }

  // TODO: is this working?
  printf("PIPI\n");
  printf("CACA\n");
  fclose(fpointer);
  return 0;
}

