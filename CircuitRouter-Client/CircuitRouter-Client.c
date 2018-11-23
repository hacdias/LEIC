#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/stat.h>
#include <sys/select.h>

#define BUFF_SIZE 1000

int makePipe (const char* name) {
  unlink(name);
  
  if (mkfifo(name, 0777) < 0) {
    return -1;
  }
  
  return open(name, O_RDONLY|O_NONBLOCK);
}

char* getPipeName () {
  char* buff = malloc(sizeof(char) * 30);
  sprintf(buff, "/tmp/SO%d.pipe", rand() % 1000 +1);
  buff = realloc(buff, strlen(buff) + 1);
  return buff;
}

int main (int argc, char** argv) {
  if (argc != 2) {
    printf("usage: client [pathname]\n");
    exit(1);
  }

  char* inPipeName = getPipeName();
  int inPipeNameLength = strlen(inPipeName);
  int inPipe = makePipe(inPipeName);
  if (inPipe < 0) {
    printf("cannot open input pipe\n");
    exit(1);
  }

  int outPipe = open(argv[1], O_WRONLY);
  if (outPipe < 0) {
    printf("cannot open output pipe\n");
    exit(1);
  }

  char* buffer = malloc(sizeof(char) * BUFF_SIZE);

  fd_set input, backup;
  FD_ZERO(&backup);
  FD_ZERO(&input);
  FD_SET(inPipe, &backup);
  FD_SET(STDIN_FILENO, &backup);

  for (;;) {
    input = backup;

    // TODO: dup not working, redirect pipe to stdout
    if (select(inPipe+1, &input, NULL, NULL, NULL) == -1) {
      printf("error while reading\n");
      return -1;
    }

    if (FD_ISSET(inPipe, &input)) {
      read(inPipe, buffer, BUFF_SIZE);
      printf("%s", buffer);
    } else {
      sprintf(buffer, "%s ", inPipeName);
    
      if (fgets(buffer + inPipeNameLength + 1, BUFF_SIZE - inPipeNameLength, stdin) == NULL) {
        return -1;
      }

      write(outPipe, buffer, strlen(buffer));
    }
  }

  close(inPipe);
  close(outPipe);
  free(inPipeName);
  free(buffer);
  return 0;
}
