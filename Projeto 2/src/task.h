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

/**
 * newTask - creates a new task from some info.
 * @id - the task id.
 * @duration - the task duration.
 * @desc - the task description.
 * @returns - a task.
 */
Task newTask (ulong id, ulong duration, char *desc);

/**
 * taskDeps - prints the task dependants in the format:
 *  <id>: [no dependencies|dependants...]
 * @t - a task.
 */
void taskDeps (Task t);

/**
 * addDependency - adds a depedency to a task.
 * @t - a task.
 * @dep - a dependency.
 */
void addDependency (Task t, Task dep);

/**
 * removeDependency - removes a depedency from a task.
 * @t - a task.
 * @dep - a dependency.
 */
void removeDependency (Task t, Task dep);

/**
 * addDependant - adds a dependant to a task.
 * @t - a task.
 * @dep - a dependant.
 */
void addDependant (Task t, Task dependant);

/**
 * removeDependant - removes a dependant from a task.
 * @t - a task.
 * @dep - a dependant.
 */
void removeDependant (Task t, Task dependant);

/**
 * freeTask - frees a task.
 * @a - a task.
 */
void freeTask (void *a);

/**
 * newTaskList - creates a new task list.
 * @returns - a task list.
 */
TaskList newTaskList ();

/**
 * insertTask - inserts a task on a task list.
 * @lst - a task list.
 * @t - a task.
 */
void insertTask (TaskList lst, Task task);

/**
 * deleteTask - removes a task from a task list.
 * @lst - a task list.
 * @task - the task to add.
 */
void deleteTask (TaskList lst, Task task);

/**
 * lookupTask - finds a task on a task list using
 * the binary tree search.
 * @lst - a task list.
 * @id - a task id.
 * @returns - a task.
 */
Task lookupTask (TaskList lst, ulong id);

/**
 * printTasks - prints the tasks from a task list.
 * @lst - a task list.
 * @duration - a positive duration to filter the printed tasks.
 * @onlyCritical - only prints the critical tasks.
 */
void printTasks (TaskList lst, ulong duration, bool onlyCritical);

/**
 * tasksPath - calculates the tasks path.
 * @lst - a task list.
 */
void tasksPath (TaskList lst);

/**
 * freeAll - frees every node from the task list
 * and their tasks.
 * @lst - a task list.
 */
void freeAll (TaskList lst);

#endif
