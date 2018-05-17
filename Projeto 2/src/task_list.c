#include "task_list.h"

struct taskList {
  Node head;
  bool validPath;
  DLL dll;
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
  lst->dll = newDLL(compareTasks, null);

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
  DLLnode n;

  for (n = t->dependencies->head; n != NULL; n = n->next)
    addDependant((Task)n->item, t);

  DLLinsertEnd(lst->dll, t);
  insertNode(&lst->head, t);

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
  DLLnode n;

  for (n = t->dependencies->head; n != NULL; n = n->next)
    removeDependant((Task)n->item, t);

  DLLdelete(lst->dll, t);
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
  DLLnode n;
  Task t;

  for (n = lst->dll->head; n != NULL; n = n->next) {
    t = (Task)n->item;
    if (t->duration >= duration && (!onlyCritical || t->early == t->late))
      printTask(t, lst->validPath);
  }
}

/**
 * tasksPath - calculates the tasks path.
 * @lst - a task list.
 */
void tasksPath (TaskList lst) {
  DLLnode n;
  Task t;
  ulong duration = 0;

  for (n = lst->dll->head; n != NULL; n = n->next) {
    t = (Task)n->item;
    calculateEarlyStart(t);
    duration = max(duration, t->early+t->duration);
  }

  for (n = lst->dll->last; n != NULL; n = n->prev) {
    t = (Task)n->item;
    calculateLateStart(t, duration);
  }

  lst->validPath = true;
  printTasks(lst, 0, true);
  printf("project duration = %lu\n", duration);
}
