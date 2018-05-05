#include <stdio.h>
#include <stdlib.h>
#include "cmds.h"
#include "task.h"

int main () {
  char buffer[MAX_LINE];
  Command cmd;
  TaskList lst = newTaskList();

  while ((cmd = getCommand(buffer)) != EXIT) {
    switch(cmd) {
      case ADD:
        runAdd(buffer, lst);
        break;
      case DURATION:
        runDuration(buffer, lst);
        break;
      case DEPEND:
        runDepend(buffer, lst);
        break;
      case REMOVE:
        runRemove(buffer, lst);
        break;
      case PATH:
        runPath(buffer, lst);
        break;
      default:
        printf("illegal command\n");
    }
  }

  freeAll(lst);
  return 0;
}
