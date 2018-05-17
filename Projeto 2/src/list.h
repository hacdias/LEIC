#ifndef _list_h
#define _list_h

#include <stdlib.h>

typedef struct dll* DLL;
typedef struct dllnode* DLLnode;

struct dll {
  int (*compare)(const void*, const void*);
  void (*free)(void *a);
  int count;
  DLLnode head, tail;
};

struct dllnode {
 void* item;
 DLLnode prev, next;
};

/**
 * newDLL - creates a new Double Linked List (DLL).
 * @compare - the comparison function between elements.
 * @free - the function to free each element.
 * @returns - a new DDL.
 */
DLL newDLL (int (*compare)(const void*, const void*), void (*free)(void *a));

/**
 * insertBeginList - inserts a new item at the beginning of the list.
 * @lst - a DDL.
 * @item - the pointer to the item.
 * @returns - the inserted DDLnode.
 */
DLLnode insertBeginList (DLL lst, void* item);

/**
 * insertEndList - inserts a new item at the end of the list.
 * @lst - a DDL.
 * @item - the pointer to the item.
 * @returns - the inserted DDLnode.
 */
DLLnode insertEndList (DLL lst, void* item);

/**
 * lookupList - searches for an item.
 * @lst - a DDL.
 * @item - the pointer to the item.
 * @returns - the DDLnode that corresponds to @item.
 */
DLLnode lookupList (DLL lst, void *item);

/**
 * freeList - frees the list.
 * @lst - a DDL.
 */
void freeList (DLL lst);

/**
 * visitList - visits the list from the head to the tail.
 * @lst - a DDL.
 * @visit - the function to apply to every element.
 */
void visitList (DLL lst, void (*visit)(const void *));

/**
 * visitInverseList - visits the list from the tail to the head.
 * @lst - a DDL.
 * @visit - the function to apply to every element.
 */
void visitInverseList (DLL lst, void (*visit)(const void *));

/**
 * deleteElementList - deletes an ite from a list.
 * @lst - a DDL.
 * @item - the pointer to the item.
 */
void deleteElementList (DLL lst, void* item);

#endif
