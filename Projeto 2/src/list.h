#ifndef _list_h
#define _list_h

#include <stdlib.h>

typedef struct dll* DLL;
typedef struct dllnode* DLLnode;

struct dll {
  int (*compare)(const void*, const void*);
  void (*free)(void *a);
  int count;
  DLLnode head, last;
};

struct dllnode {
 void* item;
 DLLnode prev, next;
};

DLL newDLL (int (*compare)(const void*, const void*), void (*free)(void *a));
DLLnode DLLinsertBegin (DLL lst, void* item);
DLLnode DLLinsertEnd (DLL lst, void* item);
DLLnode DLLlookup (DLL lst, void *item);
void DLLfree (DLL *lst);
void DLLvisit (DLL lst, void (*visit)(const void *));
void DLLvisitInverse (DLL lst, void (*visit)(const void *));
void DLLdelete (DLL lst, void* item);

#endif
