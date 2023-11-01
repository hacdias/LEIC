#include "dirs.h"

// mkdirIfNotExists creates a directory if it does not
// exist already. Returns 0 on success.
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

// dirExists returns 1 if a certain directory exists.
int dirExists (const char *name) {
  DIR* dir = opendir(name);
  if (dir) {
    closedir(dir);
    return 1;
  } else if (ENOENT == errno) {
    return 0;
  } else {
    return -1;
  }
}
