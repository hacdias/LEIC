#ifndef __DIRS_H__
#define __DIRS_H__

#include <errno.h>
#include <dirent.h>
#include <sys/stat.h>
#include <errno.h>

int mkdirIfNotExists (const char *name);

#endif
