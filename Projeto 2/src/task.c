#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "task.h"

void printTask (Task *t) {
  unsigned long i;
  printf("%ld \"%s\" %ld", t->id, t->desc, t->duration);

  if (t->early != 0 || t->late != 0) {
    if (t->early == t->late) {
      printf(" [%ld CRITICAL]", t->early);
    } else {
      printf(" [%ld %ld]", t->early, t->late);
    }
  }

  if (t->depsCount != 0)
    for (i = 0; i < t->depsCount; i++)
      printf(" %ld", t->deps[i]);

  printf("\n");
}

Task * newTask (unsigned long id, unsigned long duration, char *desc, unsigned long *deps, unsigned long depsCount) {
  int i;
  Task *t = malloc(sizeof(Task));

  t->id = id;
  t->duration = duration;
  t->desc = malloc(sizeof(char) * (strlen(desc)+1));
  t->deps = malloc(sizeof(unsigned long) * depsCount);
  t->early = 0;
  t->late = 0;
  t->next = NULL;
  t->depsCount = depsCount;

  strcpy(t->desc, desc);

  for (i = 0; i < depsCount; i++)
    t->deps[i] = deps[i];

  return t;
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

Task * nextDependant (Task *head, unsigned long id) {
  Task *p;
  unsigned long i;

  for (p = head; p != NULL; p = p->next)
    for (i = 0; i < p->depsCount; i++)
      if (p->deps[i] == id)
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
      return head;
    }
  }

  printf("no such task\n");
  return head;
}

Task * insertTask (Task *head, Task *new) {
  Task *t;
  unsigned long i;

  if (head == NULL)
    return new;

  if (lookupTask(head, new->id) != NULL) {
    freeTask(new);
    printf("id already exists\n");
    return head;
  }

  for (i = 0; i < new->depsCount; i++) {
    if (lookupTask(head, new->deps[i]) == NULL) {
      freeTask(new);
      printf("no such task\n");
      return head;
    }
  }

  for (t = head; t->next != NULL; t = t->next);
  t->next = new;
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

void printTasks (Task *head, unsigned long duration) {
  Task *t;

  for (t = head; t != NULL; t = t->next)
    if (t->duration >= duration)
      printTask(t);
}

unsigned long countTasks (Task *head) {
  unsigned long i;
  Task *t;
  for (t = head, i = 0; t != NULL; t = t->next, i++);
  return i;
}

void tasksPath (Task *head) {

}
