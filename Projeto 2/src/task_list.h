#ifndef _task_list_h
#define _task_list_h

#include "btree.h"
#include "task.h"

struct taskList {
  Node head;
  ulong count;
  Bool validPath;
  Task first;
  Task last;
};

typedef struct taskList* TaskList;

TaskList newTaskList ();
void insertTask (TaskList, Task);
void deleteTask (TaskList, Task);
Task lookupTask (TaskList, ulong id);
void printTasks (TaskList, ulong duration, Bool onlyCritical);
void taskDependencies (TaskList, ulong id);
void freeAll (TaskList);
void tasksPath (TaskList);

#endif
