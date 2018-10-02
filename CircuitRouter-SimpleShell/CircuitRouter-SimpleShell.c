#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <unistd.h>
#include "../lib/list.h"
#include "../lib/types.h"
#include "../lib/commandlinereader.h"

#define BINARY "../CircuitRouter-SeqSolver/CircuitRouter-SeqSolver"

int getMaxChildren (int argc, char **argv) {
  if (argc == 1) {
    return 0;
  }

  return atoi(argv[1]);
}

typedef struct pinfo {
  pid_t pid;
  int state;
} pinfo_t;

pinfo_t* makepinfo (pid_t pid, int state) {
  pinfo_t* p = malloc(sizeof(pinfo_t));
  p->pid = pid;
  p->state = state;
  return p;
}

int main (int argc, char** argv) {
  char **argVector = malloc(sizeof(char) * 256 * 3);
  char *buffer = malloc(sizeof(char) * 256);
  int maxChildren = getMaxChildren(argc, argv);
  int children = 0, state, args;
  pid_t pid;
  list_t* list = list_alloc(NULL);

  while (TRUE) {
    args = readLineArguments(argVector, 3, buffer, 256);

    if (args == 2 && !strcmp(argVector[0], "run")) {
      if (maxChildren && children == maxChildren) {
        pid = wait(&state);
        list_insert(list, makepinfo(pid, state));
        children--;
      }

      pid = fork();

      if (pid == (pid_t)0) {
        execl(BINARY, BINARY, argVector[1], (char*)NULL);
        return 0;
      } else if (maxChildren) {
        children++;
      }
    } else if (args == 1 && !strcmp(argVector[0], "exit")) {
      while ((pid = wait(&state)) != -1) {
        list_insert(list, makepinfo(pid, state));
      }

      list_iter_t it;
      list_iter_reset(&it, list);

      while (list_iter_hasNext(&it, list)) {
        pinfo_t* pinfo = (pinfo_t*)list_iter_next(&it, list);
        printf("CHILD EXITED (PID=%i; return %s)\n", pinfo->pid, pinfo->state ? "NOK" : "OK");
      }

      printf("END.\n");
      list_free(list);
      return 0;
    } else {
      printf("Invalid Command\n");
      return 1;
    }
  }

  return 0;
}
