#ifndef _commands_h
#define _commands_h

#include <string.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include "task.h"
#include "utils.h"

#define BUFFER_SIZE 100

typedef enum {
  ADD = 0,
  DURATION = 1,
  DEPEND = 2,
  REMOVE = 3,
  PATH = 4,
  EXIT = 5
} Command;

/**
 * initBuffer - initializes a buffer.
 * @buffer - the pointer to a buffer.
 */
void initBuffer (char **buffer);

/**
 * freeBuffer - frees a buffer.
 * @buffer - the pointer to a buffer.
 */
void freeBuffer (char **buffer);

/**
 * getCommand - gets the command to run from a buffer.
 * @buffer - the pointer to a buffer.
 * @returns - the command to run.
 */
Command getCommand (char **buffer);

/**
 * runAdd - runs the ADD command.
 * @cmd - the buffer with the input.
 * @lst - the task list.
 */
void runAdd (char *cmd, TaskList lst);

/**
 * runDuration - runs the DURATION command.
 * @cmd - the buffer with the input.
 * @lst - the task list.
 */
void runDuration (char *cmd, TaskList lst);

/**
 * runDepend - runs the DEPEND command.
 * @cmd - the buffer with the input.
 * @lst - the task list.
 */
void runDepend (char *cmd, TaskList lst);

/**
 * runRemove - runs the REMOVE command.
 * @cmd - the buffer with the input.
 * @lst - the task list.
 */
void runRemove (char *cmd, TaskList lst);

/**
 * runPath - runs the PATH command.
 * @cmd - the buffer with the input.
 * @lst - the task list.
 */
void runPath (char *cmd, TaskList lst);

#endif
