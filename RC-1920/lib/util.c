#include "util.h"

// isTitleValid checks if a certain title is valid, i.e.,
// it has up to 10 alphanumeric characters. Returns 1 on success.
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

// max gets the max number between two.
int max (int x, int y) {
  return x > y ? x : y;
}

// isValidUserID checks if a certain string is a valid User ID, i.e.,
// it has 5 numeric characters. Returns 1 on success.
int isValidUserID (const char *userID) {
  if (strlen(userID) != 5) return 0;

  for (int i = 0; i < 5; i++) {
    if (!isdigit(userID[i])) {
      return 0;
    }
  }

  return 1;
}
