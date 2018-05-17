#include "task.h"

struct taskList {
  bool validPath;
  BTree tree;
  DLL dll;
};

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


void print (const void *a) {
  printf(" %ld", ((Task)a)->id);
}

void printTask (Task t, bool validPath) {
  printf("%ld \"%s\" %ld", t->id, t->desc, t->duration);

  if (validPath) {
    if (t->early == t->late) {
      printf(" [%ld CRITICAL]", t->early);
    } else {
      printf(" [%ld %ld]", t->early, t->late);
    }
  }

  visitList(t->dependencies, print);
  printf("\n");
}

void taskDeps (Task t) {
  printf("%ld:", t->id);

  if (t->dependants->count == 0)
    printf(" no dependencies");

  visitInverseList(t->dependants, print);
  printf("\n");
}

void calculateEarlyStart (Task t) {
  DLLnode dp;
  Task p;

  for (dp = t->dependants->head; dp != NULL; dp = dp->next) {
    p = (Task)dp->item;

    if (t->early + t->duration > p->early)
      p->early = t->early + t->duration;
  }
}

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

void addDependant (Task t, Task dependant) {
  insertBeginList(t->dependants, dependant);
}

void removeDependant (Task t, Task dependant) {
  deleteElementList(t->dependants, dependant);
}


void* key (void *a) {
  return &(((Task)(((DLLnode)a)->item))->id);
}

int compFns(void* a, void* b) {
  ulong* k1 = (ulong*)a;
  ulong* k2 = (ulong*)b;

  if (*k1 < *k2)
    return -1;
  else if (*k1 == *k2)
    return 0;
  else
    return 1;
}

void freeTask (void *a) {
  Task t = (Task)a;
  freeList(t->dependencies);
  freeList(t->dependants);
  free(t->desc);
  free(t);
}

void freeAll (TaskList lst) {
  freeTree(lst->tree);
  freeList(lst->dll);
  free(lst);
}

TaskList newTaskList () {
  TaskList lst = malloc(sizeof(struct taskList));

  lst->validPath = false;
  lst->dll = newDLL(compareTasks, freeTask);
  lst->tree = newBTree(compFns, key, null);

  return lst;
}

void resetTime (const void *t) {
  ((Task)t)->early = 0;
  ((Task)t)->late = ULONG_MAX;
}

void resetTimes (TaskList lst) {
  lst->validPath = false;
  visitList(lst->dll, resetTime);
}

void insertTask (TaskList lst, Task t) {
  DLLnode n;

  for (n = t->dependencies->head; n != NULL; n = n->next)
    addDependant((Task)n->item, t);

  insertLeaf(lst->tree, insertEndList(lst->dll, t));

  if (lst->validPath)
    resetTimes(lst);
}



Task lookupTask (TaskList lst, ulong id) {
  DLLnode n = (DLLnode)searchTree(lst->tree, &id);

  if (n == NULL)
    return NULL;
  else
    return (Task)n->item;
}

void deleteTask (TaskList lst, Task t) {
  DLLnode n;

  for (n = t->dependencies->head; n != NULL; n = n->next)
    removeDependant((Task)n->item, t);

  deleteLeaf(lst->tree, &t->id);
  deleteElementList(lst->dll, t);

  if (lst->validPath)
    resetTimes(lst);
}

void printTasks (TaskList lst, ulong duration, bool onlyCritical) {
  DLLnode n;
  Task t;

  for (n = lst->dll->head; n != NULL; n = n->next) {
    t = (Task)n->item;
    if (t->duration >= duration && (!onlyCritical || t->early == t->late))
      printTask(t, lst->validPath);
  }
}


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
