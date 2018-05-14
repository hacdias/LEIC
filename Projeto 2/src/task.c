#include "task.h"

struct depsList {
  Task task;
  struct depsList* next;
};

/**
 * newTask - allocates the memory for a new task and sets the default values
 * for each field and fills all the other fields wiht the given information.
 * @id - the task id.
 * @duration - the task duration.
 * @desc - the task description.
 * @deps - the task dependencies.
 * @depsCount - the number of dependencies.
 * @returns - a task.
 */
Task newTask (ulong id, ulong duration, char *desc, Task *deps, ulong depsCount) {
  Task p = malloc(sizeof(struct task));

  /* given info */
  p->id = id;
  p->duration = duration;
  p->desc = malloc(sizeof(char) * (strlen(desc)+1));
  p->dependencies = deps;
  p->dependenciesCount = depsCount;
  strcpy(p->desc, desc);

  /* defaults */
  p->early = 0;
  p->late = ULONG_MAX;;
  p->dependants = NULL;
  p->dependantsCount = 0;
  p->next = NULL;
  p->prev = NULL;

  return p;
}

/**
 * freeTask - frees a task.
 * @t - a task.
 */
void freeTask (Task t) {
  struct depsList *h, *p = NULL;

  for (h = t->dependants; h != NULL; h = h->next) {
    free(p);
    p = h;
  }

  free(p);
  free(t->dependencies);
  free(t->desc);
  free(t);
}

/**
 * resetTime - resets the early and late starts of a single task.
 * @t - a task.
 */
void resetTime (Task t) {
  t->early = 0;
  t->late = ULONG_MAX;
}

/**
 * printTask - prints a task. If the validPath is set to false
 * it will have the following format:
 *
 *  <id> "<description>" <duration> [dependencies...]
 *
 * otherwise, if validPath is true:
 *
 *  <id> "<description>" <duration> [<earlyStart> <lateStart>] [dependencies...]
 *
 * also, if the early start is the same as the late start:
 *
 *  <id> "<description>" <duration> [<earlyStart> CRITICAL] [dependencies...]
 *
 * @t - the task.
 * @validPath - indicates if the early and late start values are correct.
 */
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

/**
 * taskDeps - prints the task dependants in the format:
 *  <id>: [no dependencies|dependants...]
 * @t - a task.
 */
void taskDeps (Task t) {
  ulong i, *ids;
  struct depsList *h;

  printf("%ld:", t->id);

  if (t->dependantsCount == 0)
    printf(" no dependencies");

  ids = malloc(sizeof(ulong) * t->dependantsCount);
  i = t->dependantsCount - 1;

  for (h = t->dependants; h != NULL; h = h->next)
    ids[i--] = h->task->id;


  for (i = 0; i < t->dependantsCount; i++)
    printf(" %ld", ids[i]);

  free(ids);
  printf("\n");
}

/**
 * calculateEarlyStart - calculates the early start
 * of a task.
 * @t - a task.
 */
void calculateEarlyStart (Task t) {
  struct depsList* dp;
  Task p;

  for (dp = t->dependants; dp != NULL; dp = dp->next) {
    p = dp->task;

    if (t->early + t->duration > p->early)
      p->early = t->early + t->duration;
  }
}

/**
 * calculateLateStart - calculates the late start
 * of a task.
 * @t - a task.
 * @duration - the project duration.
 */
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

/**
 * addDependant - adds a dependant to a task.
 * @t - a task.
 * @dependant - the task that depends on @t.
 */
void addDependant (Task t, Task dependant) {
  struct depsList *dep = malloc(sizeof(struct depsList));
  dep->task = dependant;
  dep->next = NULL;

  if (t->dependantsCount)
    dep->next = t->dependants;

  t->dependants = dep;
  t->dependantsCount++;
}

/**
 * removeDependant - removes a dependant from a task.
 * @t - a task.
 * @dependant - the task that depends on @t.
 */
void removeDependant (Task t, Task dependant) {
  struct depsList *h, *p;

  p = t->dependants;
  for (h = p; h != NULL && h->task != dependant; h = h->next)
    p = h;

  if (h == t->dependants)
    t->dependants = h->next;

  if (p != NULL)
    p->next = h->next;

  free(h);
  t->dependantsCount--;
}
