#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "utils.h"
#include "task.h"

void chop (char *str, int n) {
  int len;

  len = strlen(str);
  if (n > len)
    return;

  for (n++; str[n] == ' ' && n < len; n++);
  memmove(str, str+n, len - n + 1);
}

Command getCommand (char *buffer) {
  fgets(buffer, MAX_LINE, stdin);

  if (strstr(buffer, "add") == buffer) {
    chop(buffer, 3);
    return ADD;
  } else if (strstr(buffer, "duration") == buffer) {
    chop(buffer, 8);
    return DURATION;
  } else if (strstr(buffer, "depend") == buffer) {
    chop(buffer, 6);
    return DEPEND;
  } else if (strstr(buffer, "remove") == buffer) {
    chop(buffer, 6);
    return REMOVE;
  } else if (strstr(buffer, "path") == buffer) {
    chop(buffer, 4);
    return PATH;
  } else if (strstr(buffer, "exit") == buffer) {
    return EXIT;
  }

  return -1;
}

void illegalArg () {
  printf("illegal arg\n");
}

Task * runAdd (char *cmd, Task *head) {
  char *desc, *p;
  int n;
  unsigned long *deps;
  unsigned long id, duration, depsCount, maxTasks;
  Task *t;

  /* reads the id */
  if (sscanf(cmd, "%lu %n", &id, &n) != 1) {
    illegalArg();
    return head;
  }

  /* seeks the beginning of the description */
  if ((desc = strchr(cmd + n, '"')) == NULL) {
    illegalArg();
    return head;
  }

  /* inscrements the description to avoid the quotation marks */
  desc++;

  /* seeks the end of the description */
  if ((p = strchr(desc, '"')) == NULL) {
    illegalArg();
    return head;
  }

  /* changes the last quotation mark to the end of the string
  so 'desc' corresponds to the description. Also, increments
  the p pointer to use in future readings */
  *p = '\0';
  p++;

  /* reads the duration */
  if (sscanf(p, "%lu %n", &duration, &n) != 1) {
    illegalArg();
    return head;
  }

  p += n;

  maxTasks = countTasks(head);
  deps = malloc(sizeof(unsigned long) * maxTasks);
  depsCount = 0;

  /* reads the dependencies */
  while (sscanf(p, "%lu%n", &deps[depsCount], &n) == 1 && depsCount < maxTasks) {
    depsCount++;
    p += n;
  }

  t = newTask(id, duration, desc, deps, depsCount);
  free(deps);
  return insertTask(head, t);
}


void runDuration (char *cmd, Task *head) {
  unsigned long duration = 0;
  sscanf(cmd, "%lu", &duration);
  printTasks(head, duration);
}

void runDepend (char *cmd, Task *head) {
  unsigned long id;

  if (sscanf(cmd, "%lu", &id) != 1) {
    illegalArg();
    return;
  }

  taskDependencies(head, id);
}

Task * runRemove (char *cmd, Task *head) {
  unsigned long id = 0;

  if (sscanf(cmd, "%lu", &id) == 0) {
    illegalArg();
    return head;
  }

  return deleteTask(head, id);
}

void runPath (char *cmd, Task *head) {
  tasksPath(head);
}
