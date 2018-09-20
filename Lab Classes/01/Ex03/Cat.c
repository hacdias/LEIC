#include "../Ex01/Animal.h"
#include "Cat.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

struct cat {
  Animal animal;
  int _purrLevel;
  int _fluffiness;
};

Cat newCat(const char name[16], int age, int weight, int purrLevel, int fluffiness) {
  Cat c = malloc(sizeof(struct cat));
  if (c != NULL) {
    c->animal = newAnimal(name, age, weight);
    c->_purrLevel = purrLevel;
    c->_fluffiness = fluffiness;
  }
  return c;
}

void destroyCat(Cat c) {
  if (c != NULL) {
    destroyAnimal(c->animal);
    free(c);
  }
}

int equalsCat(Cat a, Cat b) {
  if (a == NULL || b == NULL) return 0;
  return equalsAnimal(a->animal, b->animal) &&
    a->_fluffiness == b->_fluffiness &&
    a->_purrLevel == b->_purrLevel;
}

char* getCatName(Cat a) {
  return getAnimalAge(a->animal);
}

int getCatAge(Cat a) {
  return getAnimalAge(a->animal);
}

int getCatWeight(Cat a) {
  return getAnimalWeight(a->animal);
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
