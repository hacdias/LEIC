#include "util.h"

int isTitleValid (const char* str) {
  int len = strlen(str);
  if (len > 10) return 0;

  for (int i = 0; i < len; i++) {
    if (!isalnum(str[i])) {
      return 0;
    }
  }

  return 1;
}

int max (int x, int y) {
  return x > y ? x : y;
}

int isValidUserID (const char *userID) {
  if (strlen(userID) != 5) return 0;

  for (int i = 0; i < 5; i++) {
    if (!isdigit(userID[i])) {
      return 0;
    }
  }

  return 1;
}
