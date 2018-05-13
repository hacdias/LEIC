#include "task_list.h"

TaskList newTaskList () {
  TaskList lst = malloc(sizeof(struct taskList));

  initNode(&lst->head);
  lst->validPath = false;
  lst->count = 0;
  lst->first = NULL;
  lst->last = NULL;

  return lst;
}

void resetTimes (TaskList lst) {
  lst->validPath = false;
  traverseTree(lst->head, resetTime);
}

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

void freeAll (TaskList lst) {
  freeNode(&lst->head);
  free(lst);
}

Task lookupTask (TaskList lst, ulong id) {
  return searchTree(lst->head, id);
}

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

void printTasks (TaskList lst, ulong duration, bool onlyCritical) {
  Task t;

  for (t = lst->first; t != NULL; t = t->next)
    if (t->duration >= duration && (!onlyCritical || t->early == t->late))
      printTask(t, lst->validPath);
}

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
