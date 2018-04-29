#ifndef _utils_h
#define _utils_h

#include "task.h"

#define MAX_LINE 9000

typedef enum {
  ADD = 0,
  DURATION = 1,
  DEPEND = 2,
  REMOVE = 3,
  PATH = 4,
  EXIT = 5
} Command;

Command getCommand (char *buffer);
Task * runAdd (char *cmd, Task *head);
void runDuration (char *cmd, Task *head);
void runDepend (char *cmd, Task *head);
Task * runRemove (char *cmd, Task *head);
void runPath (char *cmd, Task *head);

#endif
