#ifndef _task_h
#define _task_h

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include "utils.h"

#define key(a) (a != NULL ? a->id : 0)
#define less(a,b) ((a) < (b))
#define eq(a,b) ((a) == (b))
#define deleteItem freeTask
#define Item Task
#define Key ulong
#define MAX_DESC 7998
#define NULLitem NULL

typedef struct task* Task;

struct depsList;

struct task {
  ulong id;
  char *desc;
  ulong duration;
  ulong early, late;
  ulong dependenciesCount, dependantsCount;
  Task *dependencies;
  Task next, prev;
  struct depsList *firstDependant, *lastDependant;
};

/**
 * newTask - creates a new task from some info.
 * @id - the task id.
 * @duration - the task duration.
 * @desc - the task description.
 * @deps - the task dependencies.
 * @depsCount - the number of dependencies.
 * @returns - a task.
 */
Task newTask (ulong id, ulong duration, char *desc, Task *deps, ulong depsCount);

/**
 * freeTask - frees a task.
 * @t - a task.
 */
void freeTask (Task t);

/**
 * printTask - prints a task.
 * @t - the task.
 * @validPath - indicates if the early and late start values are correct.
 */
void printTask (Task t, bool validPath);

/**
 * taskDeps - prints the task dependants.
 * @t - a task.
 */
void taskDeps (Task t);

/**
 * resetTime - resets the early and late starts of a single task.
 * @t - a task.
 */
void resetTime (Task t);

/**
 * addDependant - adds a dependant to a task.
 * @t - a task.
 * @dependant - the task that depends on @t.
 */
void addDependant (Task t, Task dependant);

/**
 * removeDependant - removes a dependant from a task.
 * @t - a task.
 * @dependant - the task that depends on @t.
 */
void removeDependant (Task t, Task dependant);

/**
 * calculateEarlyStart - calculates the early start
 * of a task.
 * @t - a task.
 */
void calculateEarlyStart (Task t);

/**
 * calculateLateStart - calculates the late start
 * of a task.
 * @t - a task.
 * @duration - the project duration.
 */
void calculateLateStart (Task t, ulong duration);

#endif
