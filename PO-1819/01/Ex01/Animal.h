#ifndef _ANIMAL_H_
#define _ANIMAL_H_

typedef struct animal* Animal;

Animal newAnimal(const char name[16], int age, int weight);
void destroyAnimal(Animal a);

int equalsAnimal(Animal a, Animal b);
char* getAnimalName(Animal a);
int getAnimalAge(Animal a);
int getAnimalWeight(Animal a);
void printAnimal(Animal a);

#endif
