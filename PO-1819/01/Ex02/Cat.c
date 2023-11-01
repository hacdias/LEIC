#include "Cat.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

struct cat {
  char _name[16];
  int _age;
  int _weight;
  int _purrLevel;
  int _fluffiness;
};

Cat newCat(const char name[16], int age, int weight, int purrLevel, int fluffiness) {
  Cat a = malloc(sizeof(struct cat));
  if (a != NULL) {
    a->_age = age;
    a->_weight = weight;
    a->_purrLevel = purrLevel;
    a->_fluffiness = fluffiness;
    strcpy(a->_name, name);
  }
  return a;
}

void destroyCat(Cat a) {
  if (a != NULL) {
    free(a);
  }
}

int equalsCat(Cat a, Cat b) {
  if (a == NULL || b == NULL) return 0;
  return a->_age == b->_age &&
    a->_weight == b->_weight &&
    a->_fluffiness == b->_fluffiness &&
    a->_purrLevel == b->_purrLevel &&
    strcmp(a->_name, b->_name) == 0;
}

char* getCatName(Cat a) {
  return a->_name;
}

int getCatAge(Cat a) {
  return a->_age;
}

int getCatWeight(Cat a) {
  return a->_weight;
}

int getCatPurrLevel(Cat c) { return c->_purrLevel; }
int getCatFluffiness(Cat c) { return c->_fluffiness; }

void printCat(Cat a) {
  printf("=== CAT ===\n");
  printf("Name: %s\n", getCatName(a));
  printf("Age: %d\n", getCatAge(a));
  printf("Weight: %d\n", getCatWeight(a));
  printf("Purr Level: %d\n", getCatPurrLevel(a));
  printf("Fluffiness: %d\n", getCatFluffiness(a));
}
