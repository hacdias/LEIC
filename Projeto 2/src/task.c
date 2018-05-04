#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <limits.h>
#include "task.h"
#include "utils.h"

void printTask (Task *t) {
  unsigned long i;
  printf("%ld \"%s\" %ld", t->id, t->desc, t->duration);

  if (t->validPath) {
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

void updateValidPath (Task *head, Bool validPath) {
  for (; head != NULL; head = head->next)
    head->validPath = validPath;
}

void freeTask (Task *t) {
  free(t->deps);
  free(t->desc);
  free(t);
}

void freeAll (Task *head) {
  Task *prev;

  while (head != NULL) {
    prev = head;
    head = head->next;
    freeTask(prev);
  }
}

Task * insertTask (Task *head, ulong id, ulong duration, char *desc, ulong *deps, ulong depsCount) {
  Task *t, *p, *q;
  ulong i;

  for (t = head; t != NULL && t->next != NULL; t = t->next) {
    if (t->id == id) {
      printf("id already exists\n");
      return head;
    }
  }

  if (t != NULL && t->id == id) {
      printf("id already exists\n");
      return head;
    }

  p = malloc(sizeof(Task));
  p->id = id;
  p->duration = duration;
  p->desc = malloc(sizeof(char) * (strlen(desc)+1));
  p->deps = malloc(sizeof(Task *) * depsCount);
  p->early = 0;
  p->late = ULONG_MAX;
  p->next = NULL;
  p->depsCount = depsCount;
  p->validPath = 0;

  strcpy(p->desc, desc);

  for (i = 0; i < depsCount; i++) {
    q = lookupTask(head, deps[i]);

    if (q != NULL) {
      p->deps[i] = q;
    } else {
      freeTask(p);
      printf("no such task\n");
      return head;
    }
  }

  if (head == NULL)
    return p;

  t->next = p;

  if (t->validPath)
    updateValidPath(head, 0);

  return head;
}

Task * nextDependant (Task *head, unsigned long id) {
  Task *p;
  unsigned long i;

  for (p = head; p != NULL; p = p->next)
    for (i = 0; i < p->depsCount; i++)
      if (p->deps[i]->id == id)
        return p;

  return NULL;
}

Task * deleteTask (Task *head, unsigned long id) {
  Task *t, *prev;

  if (nextDependant(head, id) != NULL) {
    printf("task with dependencies\n");
    return head;
  }

  for (t = head, prev = NULL; t != NULL; prev = t, t = t->next) {
    if (t->id == id) {
      if (t == head)
        head = t->next;
      else
        prev->next = t->next;

      freeTask(t);
      if (head->validPath) updateValidPath(head, 0);
      return head;
    }
  }

  printf("no such task\n");
  return head;
}

Task * lookupTask (Task *head, unsigned long id) {
  Task *t;

  for (t = head; t != NULL; t = t->next)
    if (t->id == id)
      return t;

  return NULL;
}

void taskDependencies (Task *head, unsigned long id) {
  Task *t = lookupTask(head, id);
  int i;

  if (t == NULL) {
    printf("no such task\n");
    return;
  }

  printf("%ld:", id);
  i = 0;

  while ((t = nextDependant(head, id)) != NULL) {
    printf(" %ld", t->id);
    i++;
    head = t->next;
  }

  if (i == 0)
    printf(" no dependencies");

  printf("\n");
}

void printTasks (Task *head, unsigned long duration, Bool onlyCritical) {
  Task *t;

  for (t = head; t != NULL; t = t->next)
    if (t->duration >= duration && (!onlyCritical || t->early == t->late))
      printTask(t);
}

unsigned long countTasks (Task *head) {
  unsigned long i;
  Task *t;
  for (t = head, i = 0; t != NULL; t = t->next, i++);
  return i;
}

void calculateEarlyStart (Task *head, Task *t, unsigned long sum) {
  Task *p;

  for (p = nextDependant(head, t->id); p != NULL; p = nextDependant(p->next, t->id)) {
    p->early = max(p->early, sum);
    calculateEarlyStart(head, p, sum+p->duration);
  }
}

void calculateLateStart (Task *head, Task *t, unsigned long sub) {
  Task *p;
  unsigned long i;

  for (i = 0; i < t->depsCount; i++) {
    p = t->deps[i];
    p->late = min(p->late, sub - p->duration);
    calculateLateStart(head, p, sub-p->duration);
  }
}

void tasksPath (Task *head) {
  Task *t;
  unsigned long duration;

  for (t = head; t != NULL; t = t->next) {
    if (t->depsCount != 0)
      continue;

    calculateEarlyStart(head, t, t->duration);
  }

  duration = 0;
  for (t = head; t != NULL; t = t->next)
    duration = max(duration, t->early+t->duration);

  for (t = head; t != NULL; t = t->next)
    if (nextDependant(head, t->id) == NULL) {
      t->late = duration - t->duration;
      calculateLateStart(head, t, t->late);
    }

  updateValidPath(head, 1);
  printTasks(head, 0, 1);
  printf("project duration = %lu\n", duration);
}
