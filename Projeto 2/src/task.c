#include "task.h"

struct depsList {
  Task task;
  struct depsList* next;
};

Task newTask (ulong id, ulong duration, char *desc, Task *deps, ulong depsCount) {
  Task p;

  p = malloc(sizeof(struct task));
  p->id = id;
  p->duration = duration;
  p->desc = malloc(sizeof(char) * (strlen(desc)+1));
  p->early = 0;
  p->late = ULONG_MAX;;
  p->dependencies = deps;
  p->dependenciesCount = depsCount;
  p->firstDependant = NULL;
  p->lastDependant = NULL;
  p->dependantsCount = 0;
  strcpy(p->desc, desc);
  p->next = NULL;
  p->prev = NULL;

  return p;
}

void freeTask (Task t) {
  struct depsList *h, *p = NULL;

  for (h = t->firstDependant; h != NULL; h = h->next) {
    free(p);
    p = h;
  }

  free(p);
  free(t->dependencies);
  free(t->desc);
  free(t);
}

void resetTime (Task t) {
  t->early = 0;
  t->late = ULONG_MAX;
}

void printTask (Task t, bool validPath) {
  ulong i;
  printf("%ld \"%s\" %ld", t->id, t->desc, t->duration);

  if (validPath) {
    if (t->early == t->late) {
      printf(" [%ld CRITICAL]", t->early);
    } else {
      printf(" [%ld %ld]", t->early, t->late);
    }
  }

  for (i = 0; i < t->dependenciesCount; i++)
    printf(" %ld", t->dependencies[i]->id);

  printf("\n");
}

void taskDeps (Task t) {
  struct depsList* h;
  printf("%ld:", t->id);

  for (h = t->firstDependant; h != NULL; h = h->next) {
    printf(" %ld", h->task->id);
  }

  if (t->firstDependant == NULL)
    printf(" no dependencies");

  printf("\n");
}

void calculateEarlyStart (Task t) {
  struct depsList* dp;
  Task p;

  for (dp = t->firstDependant; dp != NULL; dp = dp->next) {
    p = dp->task;

    if (t->early + t->duration > p->early)
      p->early = t->early + t->duration;
  }
}

void calculateLateStart (Task t, ulong duration) {
  ulong i;
  Task p;

  if (t->dependantsCount == 0)
    t->late = duration - t->duration;

  for (i = 0; i < t->dependenciesCount; i++) {
    p = t->dependencies[i];

    if (t->late - p->duration < p->late)
      p->late = t->late - p->duration;
  }
}

void addDependant (Task t, Task dependant) {
  struct depsList *dep = malloc(sizeof(struct depsList));
  dep->task = dependant;
  dep->next = NULL;

  if (t->dependantsCount == 0)
    t->firstDependant = dep;
  else
    t->lastDependant->next = dep;

  t->lastDependant = dep;
  t->dependantsCount++;
}

void removeDependant (Task t, Task dependant) {
  struct depsList *h, *p;

  p = t->firstDependant;
  for (h = p; h != NULL && h->task != dependant; h = h->next)
    p = h;

  if (h == t->firstDependant)
    t->firstDependant = h->next; 

  if (h == t->lastDependant)
    t->lastDependant = p;

  if (p != NULL)
    p->next = h->next;

  free(h);
  t->dependantsCount--;
}
