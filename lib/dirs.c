#include "dirs.h"

int mkdirIfNotExists (const char *name) {
  DIR* dir = opendir(name);
  if (dir) {
    closedir(dir);
    return 0;
  } else if (ENOENT == errno) {
    return mkdir(name, S_IRWXU);
  } else {
    return -1;
  }
}