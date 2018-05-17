#include "task.h"

struct taskList {
  bool validPath;
  BTree tree;
  DLL dll;
};

/**
 * compareTasks - compares two tasks by ID.
 *    -1 if A is before B
 *     0 if A is equivalent to B
 *     1 if A is after B
 * @a - task 1.
 * @b - task 2.
 * @returns - the result of the comparison.
 */
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
 * null - do nothing.
 * @n - anything.
 */
void null (void* n) {}

/**
 * newTask - creates a new task from some info.
 * @id - the task id.
 * @duration - the task duration.
 * @desc - the task description.
 * @returns - a task.
 */
Task newTask (ulong id, ulong duration, char *desc) {
  Task p = malloc(sizeof(struct task));

  /* given info */
  p->id = id;
  p->duration = duration;
  p->desc = malloc(sizeof(char) * (strlen(desc)+1));
  strcpy(p->desc, desc);

  /* defaults */
  p->early = 0;
  p->late = ULONG_MAX;;
  p->dependants = newDLL(compareTasks, null);
  p->dependencies = newDLL(compareTasks, null);

  return p;
}

/**
 * printWithSpace - prints a task id with a space before.
 * @t - a task.
 */
void printWithSpace (const void *t) {
  printf(" %ld", ((Task)t)->id);
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

  visitList(t->dependencies, printWithSpace);
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

  visitInverseList(t->dependants, printWithSpace);
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
 * addDependency - adds a depedency to a task.
 * @t - a task.
 * @dep - a dependency.
 */
void addDependency (Task t, Task dep) {
  insertEndList(t->dependencies, dep);
}

/**
 * removeDependency - removes a depedency from a task.
 * @t - a task.
 * @dep - a dependency.
 */
void removeDependency (Task t, Task dep) {
  deleteElementList(t->dependencies, dep);
}

/**
 * addDependant - adds a dependant to a task.
 * @t - a task.
 * @dep - a dependant.
 */
void addDependant (Task t, Task dependant) {
  insertBeginList(t->dependants, dependant);
}

/**
 * removeDependant - removes a dependant from a task.
 * @t - a task.
 * @dep - a dependant.
 */
void removeDependant (Task t, Task dependant) {
  deleteElementList(t->dependants, dependant);
}

/**
 * freeTask - frees a task.
 * @a - a task.
 */
void freeTask (void *a) {
  Task t = (Task)a;
  freeList(t->dependencies);
  freeList(t->dependants);
  free(t->desc);
  free(t);
}

/**
 * resetTime - resets a task early and late starts.
 * It receives a pointer to void to be compatible with
 * DLL and BTree's visit functions.
 */
void resetTime (const void *t) {
  ((Task)t)->early = 0;
  ((Task)t)->late = ULONG_MAX;
}

/**
 * compareTaskIDs - compares two tasks' IDs.
 * @a - pointer to task 1.
 * @b - pointer to task 2.
 * @returns -
 *    -1 if a < b
 *     0 if a = b
 *     1 if a > b
 */
int compareTaskIDs (void* a, void* b) {
  ulong* k1 = (ulong*)a;
  ulong* k2 = (ulong*)b;

  if (*k1 < *k2)
    return -1;
  else if (*k1 == *k2)
    return 0;
  else
    return 1;
}

/**
 * getTaskID - retrieves a task ID from a double linked list node.
 * @a - pointer to a DLL node.
 * @returns - a pointer to the ID.
 */
void* getTaskID (void *a) {
  return &(((Task)(((DLLnode)a)->item))->id);
}


/**
 * newTaskList - creates a new task list.
 * @returns - a task list.
 */
TaskList newTaskList () {
  TaskList lst = malloc(sizeof(struct taskList));

  lst->validPath = false;
  lst->dll = newDLL(compareTasks, freeTask);
  lst->tree = newBTree(compareTaskIDs, getTaskID, null);

  return lst;
}

/**
 * resetTimes - reset the early and late starts from an
 * entire task list.
 * @lst - a task list.
 */
void resetTimes (TaskList lst) {
  lst->validPath = false;
  visitList(lst->dll, resetTime);
}

/**
 * insertTask - inserts a task on a task list.
 * @lst - a task list.
 * @t - a task.
 */
void insertTask (TaskList lst, Task t) {
  DLLnode n;

  for (n = t->dependencies->head; n != NULL; n = n->next)
    addDependant((Task)n->item, t);

  insertLeaf(lst->tree, insertEndList(lst->dll, t));

  if (lst->validPath)
    resetTimes(lst);
}

/**
 * deleteTask - removes a task from a task list.
 * @lst - a task list.
 * @task - the task to add.
 */
void deleteTask (TaskList lst, Task t) {
  DLLnode n;

  for (n = t->dependencies->head; n != NULL; n = n->next)
    removeDependant((Task)n->item, t);

  deleteLeaf(lst->tree, &t->id);
  deleteElementList(lst->dll, t);

  if (lst->validPath)
    resetTimes(lst);
}

/**
 * lookupTask - finds a task on a task list using
 * the binary tree search.
 * @lst - a task list.
 * @id - a task id.
 * @returns - a task.
 */
Task lookupTask (TaskList lst, ulong id) {
  DLLnode n = (DLLnode)searchTree(lst->tree, &id);

  if (n == NULL)
    return NULL;
  else
    return (Task)n->item;
}

/**
 * printTasks - prints the tasks from a task list.
 * @lst - a task list.
 * @duration - a positive duration to filter the printed tasks.
 * @onlyCritical - only prints the critical tasks.
 */
void printTasks (TaskList lst, ulong duration, bool onlyCritical) {
  DLLnode n;
  Task t;

  for (n = lst->dll->head; n != NULL; n = n->next) {
    t = (Task)n->item;
    if (t->duration >= duration && (!onlyCritical || t->early == t->late))
      printTask(t, lst->validPath);
  }
}

/**
 * tasksPath - calculates the tasks path.
 * @lst - a task list.
 */
void tasksPath (TaskList lst) {
  DLLnode n;
  Task t;
  ulong duration = 0;

  for (n = lst->dll->head; n != NULL; n = n->next) {
    t = (Task)n->item;
    calculateEarlyStart(t);
    duration = max(duration, t->early+t->duration);
  }

  for (n = lst->dll->tail; n != NULL; n = n->prev) {
    t = (Task)n->item;
    calculateLateStart(t, duration);
  }

  lst->validPath = true;
  printTasks(lst, 0, true);
  printf("project duration = %lu\n", duration);
}

/**
 * freeAll - frees every node from the task list
 * and their tasks.
 * @lst - a task list.
 */
void freeAll (TaskList lst) {
  freeTree(lst->tree);
  freeList(lst->dll);
  free(lst);
}
