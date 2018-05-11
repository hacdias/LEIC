#include "task.h"

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
  p->dependants = NULL;
  p->dependantsCount = 0;
  strcpy(p->desc, desc);
  p->next = NULL;
  p->prev = NULL;

  return p;
}

void freeTask (Task t) {
  free(t->dependants);
  free(t->dependencies);
  free(t->desc);
  free(t);
}

void resetTime (Task t) {
  t->early = 0;
  t->late = ULONG_MAX;
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

  for (i = 0; i < t->dependenciesCount; i++)
    printf(" %ld", t->dependencies[i]->id);

  printf("\n");
}

void taskDeps (Task t) {
  ulong i;
  printf("%ld:", t->id);

  for (i = 0; i < t->dependantsCount; i++) {
    printf(" %ld", t->dependants[i]->id);
  }

  if (i == 0)
    printf(" no dependencies");

  printf("\n");
}

void addDependant (Task t, Task dependant) {
  t->dependantsCount++;
  t->dependants = realloc(t->dependants, sizeof(Task) * t->dependantsCount);
  t->dependants[t->dependantsCount - 1] = dependant;
}

void removeDependant (Task t, Task dependant) {
  ulong i = 0;

  while (t->dependants[i] != NULL && t->dependants[i] != dependant)
    i++;

  for (i = i + 1; i < t->dependantsCount; i++)
    t->dependants[i-1] = t->dependants[i];

  t->dependantsCount--;
  t->dependants = realloc(t->dependants, sizeof(Task) * t->dependantsCount);
}