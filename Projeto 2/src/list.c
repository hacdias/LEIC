#include "list.h"

DLL newDLL (int (*compare)(const void*, const void*), void (*free)(void *a)) {
  DLL lst = malloc(sizeof(struct dll));
  lst->compare = compare;
  lst->free = free;
  lst->count = 0;
  lst->head = NULL;
  lst->last = NULL;
  return lst;
}

DLLnode newElement (void* item) {
  DLLnode x = malloc(sizeof(struct dllnode));
  x->item = item;
  x->next = NULL;
  x->prev = NULL;
  return x;
}

DLLnode DLLinsertBegin (DLL lst, void* item) {
  DLLnode x = newElement(item);
  x->next = lst->head;

  if (x->next != NULL)
    x->next->prev = x;

  lst->head = x;

  if (lst->last == NULL)
    lst->last = x;

  lst->count++;
  return x;
}

DLLnode DLLinsertEnd (DLL lst, void* item) {
  DLLnode x = newElement(item);

  if (lst->head == NULL) {
    lst->head = x;
  } else {
    lst->last->next = x;
    x->prev = lst->last;
  }

  lst->count++;
  lst->last = x;
  return x;
}

DLLnode DLLlookup (DLL lst, void *item) {
  DLLnode t;

  for (t = lst->head; t != NULL; t = t->next)
    if (lst->compare(t, item) == 0)
      return t;
  
  return NULL;
}

void DLLdelete (DLL lst, void* item) {
  DLLnode t, prev;

  for (t = lst->head, prev = NULL; t != NULL; prev = t, t = t->next) {
    if (lst->compare(t->item, item) == 0) {
      if (prev != NULL)
        prev->next = t->next;

      if (t->next != NULL)
        t->next->prev = prev;

      if (t == lst->head)
        lst->head = t->next;

      if (t == lst->last)
        lst->last = prev;

      lst->count--;
      lst->free(t);
      return;
    }
  }
}

void DLLvisit (DLL lst, void (*visit)(const void *)) {
  DLLnode n;

  for (n = lst->head; n != NULL; n = n->next)
    visit(n->item);
}

void DLLvisitInverse (DLL lst, void (*visit)(const void *)) {
  DLLnode n;

  for (n = lst->last; n != NULL; n = n->prev)
    visit(n->item);
}

void DLLfree (DLL *lst) {
  /* DLLnode h, p = NULL;

  for (h = (*lst)->head; h != NULL; h = h->next) {
    /*(*lst)->free(p->item);
    free(p);
    p = h;
  }

  (*lst)->free(p->item);
  free(p);
  free(*lst); */
} 
