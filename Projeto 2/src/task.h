#ifndef _task_h
#define _task_h

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include "utils.h"
#include "list.h"
#include "btree.h"

#define MAX_DESC 7998

typedef struct task* Task;
typedef struct taskList* TaskList;

struct task {
  ulong id;
  char *desc;
  ulong duration;
  ulong early, late;
  DLL dependencies, dependants;
};

int compareTasks (const void *a, const void *b);

Task newTask (ulong id, ulong duration, char *desc, DLL deps);

void taskDeps (Task t);

TaskList newTaskList ();

void insertTask (TaskList lst, Task task);

void deleteTask (TaskList lst, Task task);

Task lookupTask (TaskList lst, ulong id);

void printTasks (TaskList lst, ulong duration, bool onlyCritical);

void freeAll (TaskList lst);

void tasksPath (TaskList lst);

#endif
