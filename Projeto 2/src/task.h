#ifndef _task_h
#define _task_h

#include "utils.h"

struct task {
  unsigned long id;
  unsigned long duration;
  char *desc;
  unsigned long depsCount;
  unsigned long early, late;
  struct task **deps;
  struct task *next;
  Bool visited;
};

struct taskList {
  struct task *head;
  Bool validPath;
};

typedef struct task* Task;
typedef struct taskList* TaskList;

TaskList newTaskList ();
void insertTask (TaskList lst, ulong id, ulong duration, char *desc, Task *deps, ulong depsCount);
void deleteTask (TaskList lst, ulong id);
Task lookupTask (TaskList lst, ulong id);
void printTasks (TaskList lst, ulong duration, Bool onlyCritical);
void taskDependencies (TaskList lst, ulong id);
void freeAll (TaskList lst);
void tasksPath (TaskList lst);
ulong countTasks (TaskList lst);

#endif
