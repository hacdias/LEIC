#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include "task.h"
#include "utils.h"

TaskList newTaskList () {
  TaskList lst = malloc(sizeof(struct taskList));
  lst->head = NULL;
  lst->validPath = false;
  return lst;
}

void freeTask (Task t) {
  free(t->deps);
  free(t->desc);
  free(t);
}

void resetTimes (TaskList lst) {
  Task t;
  lst->validPath = false;
  for (t = lst->head; t != NULL; t = t->next) {
    t->early = 0;
    t->late = ULONG_MAX;
  }
}

void insertTask (TaskList lst, ulong id, ulong duration, char *desc, Task *deps, ulong depsCount) {
  Task t, p;

  for (t = lst->head; t != NULL && t->next != NULL; t = t->next) {
    if (t->id == id) {
      printf("id already exists\n");
      return;
    }
  }

  if (t != NULL && t->id == id) {
    printf("id already exists\n");
    return;
  }

  p = malloc(sizeof(struct task));
  p->id = id;
  p->duration = duration;
  p->desc = malloc(sizeof(char) * (strlen(desc)+1));
  p->deps = deps;
  p->early = 0;
  p->late = ULONG_MAX;
  p->next = NULL;
  p->depsCount = depsCount;

  strcpy(p->desc, desc);

  if (lst->head == NULL)
    lst->head = p;
  else
    t->next = p;

  if (lst->validPath)
    resetTimes(lst);
}

Task nextDependant (Task head, ulong id) {
  Task p;
  ulong i;

  for (p = head; p != NULL; p = p->next)
    for (i = 0; i < p->depsCount; i++)
      if (p->deps[i]->id == id)
        return p;

  return NULL;
}

void deleteTask (TaskList lst, ulong id) {
  Task t, prev;

  if (nextDependant(lst->head, id) != NULL) {
    printf("task with dependencies\n");
    return;
  }

  for (t = lst->head, prev = NULL; t != NULL; prev = t, t = t->next) {
    if (t->id == id) {
      if (t == lst->head)
        lst->head = t->next;
      else
        prev->next = t->next;

      freeTask(t);

      if (lst->validPath)
        resetTimes(lst);

      return;
    }
  }

  printf("no such task\n");
}

Task lookupTask (TaskList lst, ulong id) {
  Task t;

  for (t = lst->head; t != NULL; t = t->next)
    if (t->id == id)
      return t;

  return NULL;
}

void printTask (Task t, Bool validPath) {
  ulong i;
  printf("%ld \"%s\" %ld", t->id, t->desc, t->duration);

  if (validPath) {
    if (t->early == t->late) {
      printf(" [%ld CRITICAL]", t->early);
    } else {
      printf(" [%ld %ld]", t->early, t->late);
    }
  }

  for (i = 0; i < t->depsCount; i++)
    printf(" %ld", t->deps[i]->id);

  printf("\n");
}

void printTasks (TaskList lst, ulong duration, Bool onlyCritical) {
  Task t;

  for (t = lst->head; t != NULL; t = t->next)
    if (t->duration >= duration && (!onlyCritical || t->early == t->late))
      printTask(t, lst->validPath);
}

void taskDependencies (TaskList lst, ulong id) {
  Task t, h;
  ulong i;

  t = lookupTask(lst, id);
  h = lst->head;

  if (t == NULL) {
    printf("no such task\n");
    return;
  }

  printf("%ld:", id);
  i = 0;

  while ((t = nextDependant(h, id)) != NULL) {
    printf(" %ld", t->id);
    i++;
    h = t->next;
  }

  if (i == 0)
    printf(" no dependencies");

  printf("\n");
}

void freeAll (TaskList lst) {
  Task curr, prev;
  curr = lst->head;

  while (curr != NULL) {
    prev = curr;
    curr = curr->next;
    freeTask(prev);
  }

  free(lst);
}

void calculateEarlyStart (TaskList lst, Task t, ulong sum) {
  Task p;

  for (p = nextDependant(lst->head, t->id); p != NULL; p = nextDependant(p->next, t->id)) {
    if (sum > p->early) {
      p->early = sum;
      calculateEarlyStart(lst, p, sum+p->duration);
    }
  }
}

void calculateLateStart (TaskList lst, Task t, ulong sub) {
  Task p;
  ulong i, h;

  for (i = 0; i < t->depsCount; i++) {
    p = t->deps[i];
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

  for (t = lst->head; t != NULL; t = t->next) {
    if (t->depsCount != 0)
      continue;

    calculateEarlyStart(lst, t, t->duration);
  }

  duration = 0;

  for (t = lst->head; t != NULL; t = t->next)
    duration = max(duration, t->early+t->duration);

  for (t = lst->head; t != NULL; t = t->next)
    if (nextDependant(lst->head, t->id) == NULL) {
      t->late = duration - t->duration;
      calculateLateStart(lst, t, t->late);
    }

  lst->validPath = true;

  printTasks(lst, 0, true);
  printf("project duration = %lu\n", duration);
}

ulong countTasks (TaskList lst) {
  ulong i;
  Task t;
  for (t = lst->head, i = 0; t != NULL; t = t->next, i++);
  return i;
}
