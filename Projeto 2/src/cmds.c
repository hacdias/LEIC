#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "cmds.h"
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
  printf("illegal arguments\n");
}

void runAdd (char *cmd, TaskList lst) {
  char *desc, *p;
  int n;
  ulong *deps;
  ulong id, duration, depsCount, maxTasks;

  /* reads the id */
  if (sscanf(cmd, "%lu %n", &id, &n) != 1) {
    illegalArg();
    return;
  }

  /* seeks the beginning of the description */
  if ((desc = strchr(cmd + n, '"')) == NULL) {
    illegalArg();
    return;
  }

  /* inscrements the description to avoid the quotation marks */
  desc++;

  /* seeks the end of the description */
  if ((p = strchr(desc, '"')) == NULL) {
    illegalArg();
    return;
  }

  /* changes the last quotation mark to the end of the string
  so 'desc' corresponds to the description. Also, increments
  the p pointer to use in future readings */
  *p = '\0';
  p++;

  /* reads the duration */
  if (sscanf(p, "%lu %n", &duration, &n) != 1) {
    illegalArg();
    return;
  }

  if (duration == 0 || id == 0) {
    illegalArg();
    return;
  }

  p += n;

  maxTasks = countTasks(lst);
  deps = malloc(sizeof(unsigned long) * maxTasks);
  depsCount = 0;

  /* reads the dependencies */
  while (depsCount < maxTasks && sscanf(p, "%lu%n", &deps[depsCount], &n) == 1) {
    depsCount++;
    p += n;
  }

  chop(p, 0);
  if (*p != '\0') {
    free(deps);
    illegalArg();
    return;
  }

  insertTask(lst, id, duration, desc, deps, depsCount);
  free(deps);
}


void runDuration (char *cmd, TaskList lst) {
  ulong duration = 0;
  sscanf(cmd, "%lu", &duration);
  printTasks(lst, duration, false);
}

void runDepend (char *cmd, TaskList lst) {
  ulong id;

  if (sscanf(cmd, "%lu", &id) != 1) {
    illegalArg();
    return;
  }

  taskDependencies(lst, id);
}

void runRemove (char *cmd, TaskList lst) {
  ulong id = 0;

  if (sscanf(cmd, "%lu", &id) == 0) {
    illegalArg();
    return;
  }

  deleteTask(lst, id);
}

void runPath (char *cmd, TaskList lst) {
  tasksPath(lst);
}
