#include "task.h"

int compareTasks (const void *a, const void *b) {
  Task t1 = (Task)a;
  Task t2 = (Task)b;

  if (t1->id < t2->id)
    return -1;
  else if (t1->id == t2->id)
    return 0;
  else
    return 1;
}

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
Task newTask (ulong id, ulong duration, char *desc, DLL deps) {
  Task p = malloc(sizeof(struct task));

  /* given info */
  p->id = id;
  p->duration = duration;
  p->desc = malloc(sizeof(char) * (strlen(desc)+1));
  p->dependencies = deps;
  strcpy(p->desc, desc);

  /* defaults */
  p->early = 0;
  p->late = ULONG_MAX;;
  p->dependants = newDLL(compareTasks, null);

  return p;
}

/**
 * freeTask - frees a task.
 * @t - a task.
 */
void freeTask (void *a) {
  Task t = (Task)a;
  DLLfree(&t->dependants);
  DLLfree(&t->dependencies);
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

void print (const void *a) {
  printf(" %ld", ((Task)a)->id);
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
  printf("%ld \"%s\" %ld", t->id, t->desc, t->duration);

  if (validPath) {
    if (t->early == t->late) {
      printf(" [%ld CRITICAL]", t->early);
    } else {
      printf(" [%ld %ld]", t->early, t->late);
    }
  }

  DLLvisit(t->dependencies, print);
  printf("\n");
}

/**
 * taskDeps - prints the task dependants in the format:
 *  <id>: [no dependencies|dependants...]
 * @t - a task.
 */
void taskDeps (Task t) {
  printf("%ld:", t->id);

  if (t->dependants->count == 0)
    printf(" no dependencies");

  DLLvisitInverse(t->dependants, print);
  printf("\n");
}

/**
 * calculateEarlyStart - calculates the early start
 * of a task.
 * @t - a task.
 */
void calculateEarlyStart (Task t) {
  DLLnode dp;
  Task p;

  for (dp = t->dependants->head; dp != NULL; dp = dp->next) {
    p = (Task)dp->item;

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
  DLLnode n;
  Task p;

  if (t->dependants->count == 0)
    t->late = duration - t->duration;

  for (n = t->dependencies->head; n != NULL; n = n->next) {
    p = (Task)n->item;

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
  DLLinsertBegin(t->dependants, dependant);
}

/**
 * removeDependant - removes a dependant from a task.
 * @t - a task.
 * @dependant - the task that depends on @t.
 */
void removeDependant (Task t, Task dependant) {
  DLLdelete(t->dependants, dependant);
}
