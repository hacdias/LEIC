#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include "../lib/commandlinereader.h"

#define BINARY "../CircuitRouter-SeqSolver/CircuitRouter-SeqSolver"

int getMaxChildren (int argc, char **argv) {
  if (argc == 1) {
    return -1;
  }

  return atoi(argv[1]);
}

int main (int argc, char** argv) {
  int maxChildren = getMaxChildren(argc, argv);
  char **argVector = malloc(sizeof(char) * 256 * 3);
  char *buffer = malloc(sizeof(char) * 256);
  int args = 0;

  printf("%d\n", maxChildren);

  while ((args = readLineArguments(argVector, 3, buffer, 256)) != -1) {
    
    if (args == 2 && !strcmp(argVector[0], "run")) {

      execl(BINARY, BINARY, argVector[1], (char*)NULL);
      /* code */

    }

    else if ((args == 1 && !strcmp(argVector[0], "exit"))) {
      printf("exit\n");
    }

    else {
      printf("Invalid Command\n");
    }
  }

  return 0;
}
