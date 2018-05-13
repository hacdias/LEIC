#include "task_list.h"

struct taskList {
  Node head;
  ulong count;
  Task first, last;
  bool validPath;
};

/**
 * newTaskList - allocates memory for a new task list,
 * initializes a binary tree node and sets the default
 * values for all variables.
 * @returns - a task list.
 */
TaskList newTaskList () {
  TaskList lst = malloc(sizeof(struct taskList));

  initNode(&lst->head);
  lst->validPath = false;
  lst->count = 0;
  lst->first = NULL;
  lst->last = NULL;

  return lst;
}

/**
 * resetTimes - resets the early start and late start of
 * every element on a task list.
 * @lst - a task list.
 */
void resetTimes (TaskList lst) {
  lst->validPath = false;
  traverseTree(lst->head, resetTime);
}

/**
 * insertTask - inserts a task on a task list.
 * @lst - a task list.
 * @t - a task.
 */
void insertTask (TaskList lst, Task t) {
  ulong i;

  for (i = 0; i < t->dependenciesCount; i++)
    addDependant(t->dependencies[i], t);

  if (lst->first == NULL) {
    lst->first = t;
    lst->last = t;
  } else {
    t->prev = lst->last;
    lst->last->next = t;
    lst->last = t;
  }

  insertNode(&lst->head, t);
  lst->count++;

  if (lst->validPath)
    resetTimes(lst);
}

/**
 * freeAll - frees every node from the task list
 * and their tasks.
 * @lst - a task list.
 */
void freeAll (TaskList lst) {
  freeNode(&lst->head);
  free(lst);
}

/**
 * lookupTask - finds a task on a task list using
 * the binary tree search.
 * @lst - a task list.
 * @id - a task id.
 * @returns - a task.
 */
Task lookupTask (TaskList lst, ulong id) {
  return searchTree(lst->head, id);
}

/**
 * deleteTask - removes a task from a task list.
 * @lst - a task list.
 * @task - the task to add.
 */
void deleteTask (TaskList lst, Task t) {
  ulong i;

  for (i = 0; i < t->dependenciesCount; i++)
    removeDependant(t->dependencies[i], t);

  if (lst->first == t)
    lst->first = t->next;

  if (t->prev != NULL)
    t->prev->next = t->next;

  if (t->next != NULL)
    t->next->prev = t->prev;

  if (lst->last == t)
    lst->last = t->prev;

  deleteNode(&lst->head, t->id);

  if (lst->validPath)
    resetTimes(lst);
}

/**
 * printTasks - prints the tasks from a task list.
 * @lst - a task list.
 * @duration - a positive duration to filter the printed tasks.
 * @onlyCritical - only prints the critical tasks.
 */
void printTasks (TaskList lst, ulong duration, bool onlyCritical) {
  Task t;

  for (t = lst->first; t != NULL; t = t->next)
    if (t->duration >= duration && (!onlyCritical || t->early == t->late))
      printTask(t, lst->validPath);
}

/**
 * tasksPath - calculates the tasks path.
 * @lst - a task list.
 */
void tasksPath (TaskList lst) {
  Task t;
  ulong duration = 0;

  for (t = lst->first; t != NULL; t = t->next) {
    calculateEarlyStart(t);
    duration = max(duration, t->early+t->duration);
  }

  for (t = lst->last; t != NULL; t = t->prev) {
    calculateLateStart(t, duration);
  }

  lst->validPath = true;
  printTasks(lst, 0, true);
  printf("project duration = %lu\n", duration);
}

/**
 * tasksCount - the number of tasks on a list.
 * @lst - a task list.
 * @returns - the number of tasks.
 */
ulong tasksCount (TaskList lst) {
  return lst->count;
}
