#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
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
  int pid, state;
  int actual_children = 0;

  while ((args = readLineArguments(argVector, 3, buffer, 256)) != -1) {
    if (args == 2 && !strcmp(argVector[0], "run")) {

      pid = fork();
      actual_children ++;

      if (pid == 0) {
        execl(BINARY, BINARY, argVector[1], (char*)NULL);
        /* code */
        exit(0);
      } else {
        printf("CHILD BEGAN (PID=%d)\n", pid);
        // MAIN PROCESS
        if (actual_children == maxChildren && maxChildren != -1) {
          pid = wait(&state);
          actual_children--;
        }      
      }
    } else if ((args == 1 && !strcmp(argVector[0], "exit"))) {
      while (actual_children > 0 && (pid = wait(&state))) {
        printf("CHILD EXITED (PID=%i; return %s)\n", pid, state ? "NOK" : "OK");
        actual_children--;
      }
      printf("END.\n");
      exit(0);

    } else {
      printf("Invalid Command\n");
    }
  }

  return 0;
}
