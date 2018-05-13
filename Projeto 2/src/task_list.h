#ifndef _task_list_h
#define _task_list_h

#include "btree.h"
#include "task.h"

typedef struct taskList* TaskList;

/**
 * newTaskList - creates a new task list.
 * @returns - a task list.
 */
TaskList newTaskList ();

/**
 * insertTask - adds a task to a task list.
 * @lst - a task list.
 * @task - the task to add.
 */
void insertTask (TaskList lst, Task task);

/**
 * deleteTask - removes a task from a task list.
 * @lst - a task list.
 * @task - the task to add.
 */
void deleteTask (TaskList lst, Task task);

/**
 * lookupTask - searches for a task in a list.
 * @lst - a task list.
 * @task - the task to add.
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
 * freeAll - frees a task list.
 * @lst - a task list.
 */
void freeAll (TaskList lst);

/**
 * tasksPath - calculates the tasks path.
 * @lst - a task list.
 */
void tasksPath (TaskList lst);

/**
 * tasksCount - the number of tasks on a list.
 * @lst - a task list.
 * @returns - the number of tasks.
 */
ulong tasksCount (TaskList lst);

#endif
