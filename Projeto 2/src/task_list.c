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

ulong calculateEarlyStart (TaskList lst) {
  ulong i, duration = 0;
  Task p, t;

  for (t = lst->first; t != NULL; t = t->next) {
    for (i = 0; i < t->dependantsCount; i++) {
      p = t->dependants[i];

      if (t->early + t->duration > p->early)
        p->early = t->early + t->duration;
    }

    duration = max(duration, t->early+t->duration);
  }

  return duration;
}

void calculateLateStart (TaskList lst, ulong duration) {
  ulong i;
  Task p, t;

  for (t = lst->last; t != NULL; t = t->prev) {
    if (t->dependantsCount == 0)
      t->late = duration - t->duration;

    for (i = 0; i < t->dependenciesCount; i++) {
      p = t->dependencies[i];

      if (t->late - p->duration < p->late)
        p->late = t->late - p->duration;
    }
  }
}

void tasksPath (TaskList lst) {
  ulong duration = calculateEarlyStart(lst);
  calculateLateStart(lst, duration);

  lst->validPath = true;
  printTasks(lst, 0, true);
  printf("project duration = %lu\n", duration);
}
