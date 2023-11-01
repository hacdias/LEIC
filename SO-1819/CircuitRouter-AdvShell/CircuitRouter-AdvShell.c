#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/select.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <signal.h>
#include <errno.h>
#include <unistd.h>
#include <time.h>
#include "../lib/list.h"
#include "../lib/types.h"

#define BUFF_SIZE 1000
#define BINARY "../CircuitRouter-SeqSolver/CircuitRouter-SeqSolver"

list_t* pinfoList;
int children = 0;

typedef struct pinfo {
  pid_t   pid;
  struct timespec start;
  struct timespec end;
  int state;
} pinfo_t;

// Function used to store infromation about a process in pinfo_t struct
pinfo_t* makepinfo (pid_t pid, int state) {
  pinfo_t* p = malloc(sizeof(pinfo_t));
  p->pid = pid;
  p->state = state;
  if (clock_gettime(CLOCK_MONOTONIC, &p->start) < 0) {
    perror("Error happened.\n");
    exit(errno);
  }
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

int makePipe (char *name) {
  unlink(name);

  if (mkfifo(name, 0777) < 0) {
    return -1;
  }

  return open(name, O_RDWR|O_NONBLOCK);
}

int readLineArguments(char **argVector, int vectorSize, char *buffer) {
  int numTokens = 0;
  char *s = " \r\n\t";

  int i;

  char *token;

  if (argVector == NULL || buffer == NULL || vectorSize <= 0)
     return 0;

  /* get the first token */
  token = strtok(buffer, s);

  /* walk through other tokens */
  while( numTokens < vectorSize && token != NULL ) {
    /* return error if there are more tokens than expected */
    if (numTokens == vectorSize-1) return -1;

    argVector[numTokens] = token;
    numTokens ++;

    token = strtok(NULL, s);
  }

  for (i = numTokens; i<vectorSize; i++) {
    argVector[i] = NULL;
  }

  return numTokens;
}

char* getPipeName (const char* s) {
  char* name = malloc(strlen(s) + 6);
  if (name == NULL) return NULL;
  strcpy(name, s);
  if (strcat(name, ".pipe") == NULL) return NULL;
  return name;
}

void sigchildHandler (int signal) {
  int state;
  int pid;
  struct timespec time_noted;
  
  while ((pid = waitpid(-1, &state, WNOHANG)) > 0) {
    if (clock_gettime(CLOCK_MONOTONIC, &time_noted) < 0) {
      perror("Error happened.");
      exit(errno);
    }

    children--;
    list_iter_t it;
    list_iter_reset(&it, pinfoList);

    // Iterate over the list and print information about the processes
    while (list_iter_hasNext(&it, pinfoList)) {
      pinfo_t* pinfo = (pinfo_t*)list_iter_next(&it, pinfoList);
      if (pinfo->pid == pid) {
        pinfo->state = state;
        pinfo->end = time_noted;
        break;
      }
    }
  }
}

int main (int argc, char** argv) {
  struct sigaction sa;

  sa.sa_handler = &sigchildHandler;
  // Restart the system call
  sa.sa_flags = SA_RESTART|SA_NOCLDSTOP;
  // Block other signals during handler
  sigfillset(&sa.sa_mask);
  
  if (sigaction(SIGCHLD, &sa, NULL) == -1) {
    perror("Something went wrong, couldn't handle signal\n");
  }

  if (argc > 2) {
    perror("Please only provide the maximum of processes allowed\n");
    return 1;
  }

  char* pipeName = getPipeName(argv[0]);
  if (pipeName == NULL) {
    perror("could not create pipe name");
    return 1;
  }

  int pipe = makePipe(pipeName);
  if (pipe < 0) {
    perror("could not open pipe\n");
    return errno;
  }

  fd_set input, backup;
  FD_ZERO(&backup);
  FD_ZERO(&input);
  FD_SET(pipe, &backup);
  FD_SET(STDIN_FILENO, &backup);

  char **argVector = malloc(sizeof(char*) * 4);
  char *buffer = malloc(sizeof(char) * BUFF_SIZE);
  int maxChildren = argc == 1 ? 0 : atoi(argv[1]);
  int args;
  pid_t pid;

  pinfoList = list_alloc(NULL);

  while (TRUE) {
    input = backup;

    if (select(pipe+1, &input, NULL, NULL, NULL) == -1) {
      if (errno != EINTR) {
        perror("error while reading\n");
        return 1;
      }

      continue;
    }

    char* outPipeName = NULL;
    int isFromPipe = FD_ISSET(pipe, &input);
    int n = read(isFromPipe ? pipe : STDIN_FILENO, buffer, BUFF_SIZE);
    buffer[n] = '\0';

    if (n == 0) {
      // EOF, forces exit
      args = -1;
    } else if (n < 0) {
      return 1;
    } else {
      args = readLineArguments(argVector, isFromPipe ? 4 : 3, buffer);
    }

    if (isFromPipe) {
      outPipeName = argVector[0];
      args--;
      for (int i = 0; i < args; i++) argVector[i] = argVector[i+1];
    }

    // User wishes to run SeqSolver
    if (args == 2 && !strcmp(argVector[0], "run")) {
      // Reached limit of processes available
      while (maxChildren && children >= maxChildren) pause();

      // Creation of a child process
      pid = fork();

      // Child process
      if (pid == (pid_t)0) {
        if (isFromPipe) {
          execl(BINARY, BINARY, argVector[1], outPipeName, (char*)NULL);
        } else {
          execl(BINARY, BINARY, argVector[1], (char*)NULL);
        }

        return 1;
      // Parent process (Main process)
      } else {
        children++;
        list_insert(pinfoList, makepinfo(pid, -1));
      }
    // User wishes to exit program
    } else if (args < 0 || (args == 1 && !isFromPipe && !strcmp(argVector[0], "exit"))) {
      // wait for sigchlds...
      while (children != 0) pause();

      list_iter_t it;
      list_iter_reset(&it, pinfoList);

      // Iterate over the list and print information about the processes
      while (list_iter_hasNext(&it, pinfoList)) {
        pinfo_t* pinfo = (pinfo_t*)list_iter_next(&it, pinfoList);
        printf("CHILD EXITED (PID=%i; return %s; %ld s)\n", pinfo->pid,
          WIFEXITED(pinfo->state) && WEXITSTATUS(pinfo->state) == 0 ? "OK" : "NOK",
          pinfo->end.tv_sec - pinfo->start.tv_sec);
      }

      printf("END.\n");
      close(pipe);
      free_everything(argVector, buffer, pinfoList);
      return 0;
    } else if (isFromPipe) {
      int outFd = open(outPipeName, O_WRONLY);
      write(outFd, "Command not supported\n", 22);
      close(outFd);
    } else {
      printf("Invalid Command\n");
    }
  }

  return 0;
}
