#ifndef _CAT_H_
#define _CAT_H_

typedef struct cat* Cat;

Cat newCat(const char name[16], int age, int weight, int purrLevel, int fluffiness);
void destroyCat(Cat a);

int equalsCat(Cat c1, Cat c2);
char* getCatName(Cat c);
int getCatAge(Cat c);
int getCatWeight(Cat c);
int getCatPurrLevel(Cat c);
int getCatFluffiness(Cat c);
void printCat(Cat c);

#endif
