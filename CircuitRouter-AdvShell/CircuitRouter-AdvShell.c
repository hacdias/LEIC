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

typedef struct pinfo {
  pid_t pid;
  int state;
} pinfo_t;

// Function used to store infromation about a process in pinfo_t struct
pinfo_t* makepinfo (pid_t pid, int state) {
  pinfo_t* p = malloc(sizeof(pinfo_t));
  p->pid = pid;
  p->state = state;
  return p;
}

void free_everything (char** argVector, char* buffer, list_t* list) {
  free(argVector);
  free(buffer);

  list_iter_t it;
  list_iter_reset(&it, list);

  while (list_iter_hasNext(&it, list)) {
    free((pinfo_t*)list_iter_next(&it, list));
  }

  list_free(list);
}

int main (int argc, char** argv) {
  if (argc > 2) {
    printf("Please only provide the maximum of processes allowed\n");
    return 1;
  }

  char **argVector = malloc(sizeof(char*) * 3);
  char *buffer = malloc(sizeof(char) * 256);
  int maxChildren = argc == 1 ? 0 : atoi(argv[1]);
  int children = 0, state, args;
  pid_t pid;
  // Inicialization of a list to store pid numbers and exit state
  list_t* list = list_alloc(NULL);

  while (TRUE) {
    args = readLineArguments(argVector, 3, buffer, 256);
    
    // User wishes to run SeqSolver
    if (args == 2 && !strcmp(argVector[0], "run")) {
      // Reached limit of processes available
      if (maxChildren && children == maxChildren) {
        // Wait until one child process finishes
        pid = wait(&state);
        list_insert(list, makepinfo(pid, state));
        children--;
      }

      // Creation of a child process
      pid = fork();
      
      // Child process
      if (pid == (pid_t)0) {
        execl(BINARY, BINARY, argVector[1], (char*)NULL);
        return 1;
      // Parent process (Main process)
      } else if (maxChildren) {
        children++;
      }
    // User wishes to exit program
    } else if (args < 0 || (args == 1 && !strcmp(argVector[0], "exit"))) {
      // Wait for processes still running
      while (children--) {
        pid = wait(&state);
        // Insert information about the process that finished in list
        list_insert(list, makepinfo(pid, state));
      }

      list_iter_t it;
      list_iter_reset(&it, list);

      // Iterate over the list and print information about the processes
      while (list_iter_hasNext(&it, list)) {
        pinfo_t* pinfo = (pinfo_t*)list_iter_next(&it, list);
        printf("CHILD EXITED (PID=%i; return %s)\n", pinfo->pid,
          WIFEXITED(pinfo->state) && WEXITSTATUS(pinfo->state) == 0 ? "OK" : "NOK");
      }

      printf("END.\n");
      free_everything(argVector, buffer, list);
      return 0;
    } else {
      printf("Invalid Command\n");
    }
  }

  return 0;
}
