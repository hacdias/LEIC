#include "cmds.h"

/**
 * chop - removes the first n characters from a string
 * and the blank spaces afterwards.
 * @str - the string.
 * @n - the number of characters to chop.
 */
void chop (char *str, int n) {
  int len;

  len = strlen(str);
  if (n > len)
    return;

  for (n++; (str[n] == ' ' || str[n] == '\t') && n < len; n++);
  memmove(str, str+n, len - n + 1);
}

/**
 * scanUlong - scans an unsigned long from a string.
 * @str - the input string.
 * @lu - the place to save the number.
 * @n - the number of read characters. Can be NULL.
 * @returns - true if the read was successful. False otherwise.
 */
bool scanUlong (char *str, ulong *lu, int *n) {
  int i, l;

  for (i = 0; str[i] == ' '; i++);
  l = i;
  for (; isdigit(str[i]); i++);

  if (i == l)
    return false;

  if (n == NULL)
    i = sscanf(str, "%lu", lu);
  else
    i = sscanf(str, "%lu%n", lu, n);

  return (i == 1);
}

/**
 * getline - reads a line from a stream until a newline character
 * is reached or the end of the file is reached.
 * @f - the stream.
 * @returns - the pointer to a string with the line.
 */
char * getline (FILE *f) {
  size_t size = 0;
  size_t len = 0;
  size_t last = 0;
  char * buf = NULL;

  do {
    size += BUFFER_SIZE;
    buf = realloc(buf, size);
    fgets(buf+len, size - last - 1, f);
    len = strlen(buf);
    last = len - 1;
  } while (!feof(f) && buf[last] != '\n');

  buf = realloc(buf, strlen(buf) + 1);
  return buf;
}

/**
 * initBuffer - initializes a buffer.
 * @buffer - the pointer to a buffer.
 */
void initBuffer (char **buffer) {
  *buffer = NULL;
}

/**
 * freeBuffer - frees a buffer.
 * @buffer - the pointer to a buffer.
 */
void freeBuffer (char **buffer) {
  free(*buffer);
}

/**
 * getCommand - gets the command to run from a buffer.
 * @buffer - the pointer to a buffer.
 * @returns - the command to run.
 */
Command getCommand (char **buffer) {
  freeBuffer(buffer);
  *buffer = getline(stdin);

  if (strstr(*buffer, "add") == *buffer) {
    chop(*buffer, 3);
    return ADD;
  } else if (strstr(*buffer, "duration") == *buffer) {
    chop(*buffer, 8);
    return DURATION;
  } else if (strstr(*buffer, "depend") == *buffer) {
    chop(*buffer, 6);
    return DEPEND;
  } else if (strstr(*buffer, "remove") == *buffer) {
    chop(*buffer, 6);
    return REMOVE;
  } else if (strstr(*buffer, "path") == *buffer) {
    chop(*buffer, 4);
    return PATH;
  } else if (strstr(*buffer, "exit") == *buffer) {
    return EXIT;
  }

  return -1;
}

/**
 * illegalArg - prints the illegal arguments message.
 */
void illegalArg () {
  printf("illegal arguments\n");
}

void runAdd (char *cmd, TaskList lst) {
  Task d, *deps;
  char *desc, *p;
  int n;
  ulong id, duration, depsCount, maxTasks, tmp;

  /* reads the id */
  if (scanUlong(cmd, &id, &n) != 1) {
    illegalArg();
    return;
  }

  if (lookupTask(lst, id) != NULL) {
    printf("id already exists\n");
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

  if (p - desc > MAX_DESC) {
    illegalArg();
    return;
  }

  /* changes the last quotation mark to the end of the string
  so 'desc' corresponds to the description. Also, increments
  the p pointer to use in future readings */
  *p = '\0';
  p++;

  /* reads the duration */
  if (scanUlong(p, &duration, &n) != 1) {
    illegalArg();
    return;
  }

  p += n;

  if (duration == 0 || id == 0) {
    illegalArg();
    return;
  }

  maxTasks = lst->count;
  deps = malloc(sizeof(Task) * maxTasks);
  depsCount = 0;

  /* reads the dependencies */
  while (*p != '\n') {
    if (scanUlong(p, &tmp, &n) != 1) {
      illegalArg();
      free(deps);
      return;    
    }

    d = lookupTask(lst, tmp);

    if (d == NULL) {
      printf("no such task\n");
      free(deps);
      return;      
    }

    deps[depsCount++] = d;
    p += n;
  }

  if (depsCount < maxTasks)
    deps = realloc(deps, sizeof(Task) * depsCount);
  
  d = newTask(id, duration, desc, deps, depsCount);
  insertTask(lst, d);
}

void runDuration (char *cmd, TaskList lst) {
  ulong duration = 0;

  if (cmd[0] != '\n' && cmd[0] == '-') {
    illegalArg();
    return;
  }

  scanUlong(cmd, &duration, NULL);
  printTasks(lst, duration, false);
}

void runDepend (char *cmd, TaskList lst) {
  ulong id;
  Task t;

  if (scanUlong(cmd, &id, NULL) != 1) {
    illegalArg();
    return;
  }

  t = lookupTask(lst, id);

  if (t == NULL)
    printf("no such task\n");
  else
    taskDeps(t);
}

void runRemove (char *cmd, TaskList lst) {
  ulong id = 0;
  Task t;

  if (scanUlong(cmd, &id, NULL) == 0) {
    illegalArg();
    return;
  }

  t = lookupTask(lst, id);

  if (t == NULL) {
    printf("no such task\n");
    return;
  }

  if (t->dependantsCount) {
    printf("task with dependencies\n");
    return;
  }

  deleteTask(lst, t);
}

void runPath (char *cmd, TaskList lst) {
  tasksPath(lst);
}
