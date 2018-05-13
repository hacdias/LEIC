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

typedef struct task* Task;

struct task {
  ulong id;
  char *desc;
  ulong duration;
  ulong early, late;
  ulong dependenciesCount, dependantsCount;
  Task *dependencies, *dependants;
  Task next, prev;
};

Task newTask (ulong id, ulong duration, char *desc, Task *deps, ulong depsCount);
void freeTask (Task t);
void printTask (Task t, bool validPath);
void taskDeps (Task t);
void resetTime (Task t);
void addDependant (Task t, Task dependant);
void removeDependant (Task t, Task dependant);

#endif
