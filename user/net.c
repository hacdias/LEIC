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
