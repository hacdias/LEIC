#include "list.h"

/**
 * newDLL - creates a new Double Linked List (DLL).
 * @compare - the comparison function between elements.
 * @free - the function to free each element.
 * @returns - a new DDL.
 */
DLL newDLL (int (*compare)(const void*, const void*), void (*free)(void *a)) {
  DLL lst = malloc(sizeof(struct dll));
  lst->compare = compare;
  lst->free = free;
  lst->count = 0;
  lst->head = NULL;
  lst->tail = NULL;
  return lst;
}

/**
 * newElement - creates a new DLL element.
 * @item - the pointer to the item.
 * @returns - a DDLnode.
 */
DLLnode newElement (void* item) {
  DLLnode x = malloc(sizeof(struct dllnode));
  x->item = item;
  x->next = NULL;
  x->prev = NULL;
  return x;
}

/**
 * insertBeginList - inserts a new item at the beginning of the list.
 * @lst - a DDL.
 * @item - the pointer to the item.
 * @returns - the inserted DDLnode.
 */
DLLnode insertBeginList (DLL lst, void* item) {
  DLLnode x = newElement(item);
  x->next = lst->head;

  if (x->next != NULL)
    x->next->prev = x;

  lst->head = x;

  if (lst->tail == NULL)
    lst->tail = x;

  lst->count++;
  return x;
}

/**
 * insertEndList - inserts a new item at the end of the list.
 * @lst - a DDL.
 * @item - the pointer to the item.
 * @returns - the inserted DDLnode.
 */
DLLnode insertEndList (DLL lst, void* item) {
  DLLnode x = newElement(item);

  if (lst->head == NULL) {
    lst->head = x;
  } else {
    lst->tail->next = x;
    x->prev = lst->tail;
  }

  lst->count++;
  lst->tail = x;
  return x;
}

/**
 * lookupList - searches for an item.
 * @lst - a DDL.
 * @item - the pointer to the item.
 * @returns - the DDLnode that corresponds to @item.
 */
DLLnode lookupList (DLL lst, void *item) {
  DLLnode t;

  for (t = lst->head; t != NULL; t = t->next)
    if (lst->compare(t, item) == 0)
      return t;

  return NULL;
}

/**
 * deleteElementList - deletes an ite from a list.
 * @lst - a DDL.
 * @item - the pointer to the item.
 */
void deleteElementList (DLL lst, void* item) {
  DLLnode t, prev;

  for (t = lst->head, prev = NULL; t != NULL; prev = t, t = t->next) {
    if (lst->compare(t->item, item) == 0) {
      if (prev != NULL)
        prev->next = t->next;

      if (t->next != NULL)
        t->next->prev = prev;

      if (t == lst->head)
        lst->head = t->next;

      if (t == lst->tail)
        lst->tail = prev;

      lst->count--;
      lst->free(t->item);
      free(t);
      return;
    }
  }
}

/**
 * visitList - visits the list from the head to the tail.
 * @lst - a DDL.
 * @visit - the function to apply to every element.
 */
void visitList (DLL lst, void (*visit)(const void *)) {
  DLLnode n;

  for (n = lst->head; n != NULL; n = n->next)
    visit(n->item);
}

/**
 * visitInverseList - visits the list from the tail to the head.
 * @lst - a DDL.
 * @visit - the function to apply to every element.
 */
void visitInverseList (DLL lst, void (*visit)(const void *)) {
  DLLnode n;

  for (n = lst->tail; n != NULL; n = n->prev)
    visit(n->item);
}

/**
 * freeList - frees the list.
 * @lst - a DDL.
 */
void freeList (DLL lst) {
  DLLnode n, p;

  for (n = lst->head; n != NULL; n = n->next)
    lst->free(n->item);

  for (n = lst->head, p = NULL; n != NULL; p = n, n = n->next)
    free(p);

  free(p);
  free(lst);
}
