#ifndef _btree_h
#define _btree_h

#include <stdlib.h>
#include <stdio.h>

#include "task.h"

typedef struct node* Node;

void initNode (Node*);
int countNodes (Node);
Item searchTree (Node, Key);
void insertNode (Node*, Item);
void deleteNode (Node*, Key);
void traverseTree (Node, void (*visit)(Item));
void freeNode (Node*);

#endif