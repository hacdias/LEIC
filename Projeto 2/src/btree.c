#include "btree.h"

struct node {
  Item item;
  Node l, r;
  int height;
};

Node newNode (Item item, Node l, Node r) {
  Node x = malloc(sizeof(struct node));

  x->item = item;
  x->l = l;
  x->r = r;
  x->height = 1;

  return x;
}

int height (Node h) {
  if (h == NULL)
    return 0;

  return h->height;
}

Node rotL (Node h) {
  Node x = h->r;
  h->r = x->l;
  x->l = h;
  return x;
}

Node rotR (Node h) {
  Node x;
  int heightLeft, heightRight;

  x = h->l;
  h->l = x->r;
  x->r = h;

  heightLeft = height(h->l);
  heightRight = height(h->r);

  if (heightLeft > heightRight)
    h->height = heightLeft + 1;
  else
    h->height = heightRight + 1;

  heightLeft = height(x->l);
  heightRight = height(x->r);

  if (heightLeft > heightRight)
    x->height = heightLeft + 1;
  else
    x->height = heightRight + 1;

  return x;
}

Node rotLR (Node h) {
  if (h == NULL)
    return h;

  h->l = rotL(h->l);
  return rotR(h);
}

Node rotRL (Node h) {
  if (h == NULL)
    return h;

  h->r = rotR(h->r);
  return rotL(h);
}

int balanceFactor (Node h) {
  if (h == NULL)
    return 0;

  return height(h->l) - height(h->r);
}

Node AVLbalance (Node h) {
  int balance, heightLeft, heightRight;

  if (h == NULL)
    return h;

  balance = balanceFactor(h);

  if (balance > 1) {
    if (balanceFactor(h->l) >= 0)
      h = rotR(h);
    else
      h = rotLR(h);
  } else if (balance < -1) {
    if (balanceFactor(h->r) <= 0)
      h = rotL(h);
    else
      h = rotRL(h);
  } else {
    heightLeft = height(h->l);
    heightRight = height(h->r);

    if (heightLeft > heightRight)
      h->height = heightLeft + 1;
    else
      h->height = heightRight + 1;
  }

  return h;
}

Node insertR (Node h, Item item) {
  if (h == NULL)
    return newNode(item, NULL, NULL);
  if (less(key(item), key(h->item)))
    h->l = insertR(h->l, item);
  else
    h->r = insertR(h->r, item);

  h = AVLbalance(h);
  return h;
}

Node maxNode (Node h) {
  if (h == NULL || h->r == NULL)
    return h;
  else
    return maxNode(h->r);
}

Node deleteR(Node h, Key k) {
  if (h == NULL)
    return h;
  else if (less(k, key(h->item)))
    h->l = deleteR(h->l,k);
  else if (less(key(h->item), k))
    h->r = deleteR(h->r,k);
  else {
    if (h->l !=NULL && h->r !=NULL) {
      Node aux = maxNode(h->l);
      {
        Item x;
        x = h->item;
        h->item = aux->item;
        aux->item = x;
      }
      h->l= deleteR(h->l, key(aux->item));
    } else {
      Node aux=h;
      if (h->l == NULL && h->r == NULL)
        h = NULL;
      else if (h->l == NULL)
        h = h->r;
      else
        h = h->l;

      deleteItem(aux->item);
      free(aux);
    }
  }

  h = AVLbalance(h);
  return h;
}

void sortR(Node h, void (*visit)(Item)) {
  if (h == NULL)
    return;

  sortR(h->l, visit);
  visit(h->item);
  sortR(h->r, visit);
}

Node freeR (Node h) {
  if (h == NULL)
    return h;

  h->l = freeR(h->l);
  h->r = freeR(h->r);

  return deleteR(h, key(h->item));
}

void initNode (Node *head) {
  *head = NULL;
}

int countNodes (Node h) {
  if (h == NULL)
    return 0;
  else
    return countNodes(h->r) + countNodes(h->l) + 1;
}

Item searchTree (Node h, Key v) {
  if (h == NULL)
    return NULL;
  if (eq(v, key(h->item)))
    return h->item;
  if (less(v, key(h->item)))
    return searchTree(h->l, v);
  else
    return searchTree(h->r, v);
}

void insertNode (Node *head, Item item) {
  *head = insertR(*head, item);
}

void deleteNode (Node *head, Key k){
  *head = deleteR(*head, k);
}

void traverseTree (Node head, void (*visit)(Item)) {
  sortR(head, visit);
}

void freeNode (Node *head) {
  *head = freeR(*head);
}
