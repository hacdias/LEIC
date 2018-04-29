#include <stdio.h>
#include "cmds.h"
#include "task.h"

int main () {
  char buffer[MAX_LINE];
  Command cmd;
  Task *head = NULL;

  while ((cmd = getCommand(buffer)) != EXIT) {
    switch(cmd) {
      case ADD:
        head = runAdd(buffer, head);
        break;
      case DURATION:
        runDuration(buffer, head);
        break;
      case DEPEND:
        runDepend(buffer, head);
        break;
      case REMOVE:
        head = runRemove(buffer, head);
        break;
      case PATH:
        runPath(buffer, head);
        break;
      default:
        printf("illegal command\n");
    }
  }

  freeAll(head);
  return 0;
}
