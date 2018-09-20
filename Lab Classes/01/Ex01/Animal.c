#include "Animal.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

struct animal {
  char _name[16];
  int _age;
  int _weight;
};

Animal newAnimal(const char name[16], int age, int weight) {
  Animal a = malloc(sizeof(struct animal));
  if (a != NULL) {
    a->_age = age;
    a->_weight = weight;
    strcpy(a->_name, name);
  }
  return a;
}

void destroyAnimal(Animal a) {
  if (a != NULL) {
    free(a);
  }
}

int equalsAnimal(Animal a, Animal b) {
  if (a == NULL || b == NULL) return 0;
  return a->_age == b->_age &&
    a->_weight == b->_weight &&
    strcmp(a->_name, b->_name) == 0;
}

char* getAnimalName(Animal a) {
  return a->_name;
}

int getAnimalAge(Animal a) {
  return a->_age;
}

int getAnimalWeight(Animal a) {
  return a->_weight;
}

void printAnimal(Animal a) {
  printf("=== ANIMAL ===\n");
  printf("Name: %s\n", getAnimalName(a));
  printf("Age: %d\n", getAnimalAge(a));
  printf("Weight: %d\n", getAnimalWeight(a));
}
