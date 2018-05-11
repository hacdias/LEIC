#ifndef _task_list_h
#define _task_list_h

#include "btree.h"
#include "task.h"

struct taskList {
  Node head;
  ulong count;
  Task first, last;
  bool validPath;
};

typedef struct taskList* TaskList;

TaskList newTaskList ();
void insertTask (TaskList, Task);
void deleteTask (TaskList, Task);
Task lookupTask (TaskList, ulong id);
void printTasks (TaskList, ulong duration, bool onlyCritical);
void taskDependencies (TaskList, ulong id);
void freeAll (TaskList);
void tasksPath (TaskList);

#endif
