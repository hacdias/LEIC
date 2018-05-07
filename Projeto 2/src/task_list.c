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
  Task p;
  ulong i;

  for (i = 0; i < t->dependenciesCount; i++) {
    p = t->dependencies[i];
    p->dependantsCount++;
    p->dependants = realloc(p->dependants, sizeof(Task) * p->dependantsCount);
    p->dependants[p->dependantsCount - 1] = t;
  }

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
  ulong i, j;
  Task p;

  for (i = 0; i < t->dependenciesCount; i++) {
    p = t->dependencies[i];
    for (j = 0; p->dependants[j] != NULL && p->dependants[j] != t; j++);
    for (j = j+1; j < p->dependantsCount; j++) {
      p->dependants[j-1] = p->dependants[j];
    }

    p->dependantsCount--;
    p->dependants = realloc(p->dependants, sizeof(Task) * p->dependantsCount);
  }

  if (t->prev != NULL)
    t->prev->next = t->next;

  deleteNode(&lst->head, t->id);

  if (lst->validPath)
    resetTimes(lst);
}

void printTasks (TaskList lst, ulong duration, Bool onlyCritical) {
  Task t;

  for (t = lst->first; t != NULL; t = t->next)
    if (t->duration >= duration && (!onlyCritical || t->early == t->late))
      printTask(t, lst->validPath);
}

void calculateEarlyStart (TaskList lst, Task t, ulong sum) {
  Task p;
  ulong i;

  for (i = 0; i < t->dependantsCount; i++) {
    p = t->dependants[i];

    if (sum > p->early) {
      p->early = sum;
      calculateEarlyStart(lst, p, sum+p->duration);
    }
  }
}

void calculateLateStart (TaskList lst, Task t, ulong sub) {
  Task p;
  ulong i, h;

  for (i = 0; i < t->dependenciesCount; i++) {
    p = t->dependencies[i];
    h = sub - p->duration;

    if (h < p->late) {
      p->late = h;
      calculateLateStart(lst, p, h);
    }
  }
}

void tasksPath (TaskList lst) {
  Task t;
  ulong duration;

  for (t = lst->first; t != NULL; t = t->next)
    if (t->dependenciesCount == 0)
      calculateEarlyStart(lst, t, t->duration);

  duration = 0;

  for (t = lst->first; t != NULL; t = t->next)
    duration = max(duration, t->early+t->duration);

  for (t = lst->first; t != NULL; t = t->next)
    if (t->dependantsCount == 0) {
      t->late = duration - t->duration;
      calculateLateStart(lst, t, t->late);
    }

  lst->validPath = true;

  printTasks(lst, 0, true);
  printf("project duration = %lu\n", duration);
}
