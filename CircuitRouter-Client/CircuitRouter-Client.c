#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>

int main (int argc, char** argv) {
  if (argc != 2) {
    printf("usage: client [pathname]\n");
    exit(1);
  }

  int pipe = open(argv[1], O_WRONLY);
  if (pipe < 0) {
    printf("cannot open pipe\n");
    exit(1);
  }

  for (;;) {
    
  }

  return 0;
}
