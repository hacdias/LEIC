#ifndef _commands_h
#define _commands_h

#include <string.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include "task_list.h"

typedef enum {
  ADD = 0,
  DURATION = 1,
  DEPEND = 2,
  REMOVE = 3,
  PATH = 4,
  EXIT = 5
} Command;

Command getCommand (char **buffer);
void runAdd (char *cmd, TaskList lst);
void runDuration (char *cmd, TaskList lst);
void runDepend (char *cmd, TaskList lst);
void runRemove (char *cmd, TaskList lst);
void runPath (char *cmd, TaskList lst);

#endif
