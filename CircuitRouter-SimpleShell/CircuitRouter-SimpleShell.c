#include <stdio.h>
#include <stdlib.h>
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
    switch (args) {
      case 2:
        execl(BINARY, BINARY, argVector[1], (char*)NULL);

        /* code */
        break;
      case 1:
        printf("exit");
        break;
      default:
        printf("CHUPA. NAo existes\n");
    }
  }

  return 0;
}
