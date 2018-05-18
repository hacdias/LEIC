#include "btree.h"

struct leaf {
  void * item;
  Leaf l, r;
  int height;
};

/**
 * newBTree - creates a new binary tree
 * @compare - the comparison function between element' keys.
 * @key - the function to get the key.
 * @free - the function to free to each element.
 * @returns - a binary tree.
 */
BTree newBTree (BTreeCompFn compare, BTreeKeyFn key, BTreeFreeFn free) {
  BTree b = malloc(sizeof(struct btree));
  b->compare = compare;
  b->free = free;
  b->key = key;
  b->head = NULL;
  return b;
}

/**
 * newLeaf - creates a new leaf based on an item.
 * @item - the item the leaf contains.
 * @l - the left leaf.
 * @r - the right leaf.
 * @returns - the head of the leaf.
 */
Leaf newLeaf (void* item, Leaf l, Leaf r) {
  Leaf x = malloc(sizeof(struct leaf));

  x->item = item;
  x->l = l;
  x->r = r;
  x->height = 1;

  return x;
}

/**
 * height - returns the height of a leaf.
 * @h - a leaf.
 * @returns - the height.
 */
int height (Leaf h) {
  if (h == NULL)
    return 0;

  return h->height;
}

/**
 * rotL - rotates a leaf to the left.
 * @h - a leaf
 * @returns - a leaf.
 */
Leaf rotL (Leaf h) {
  Leaf x = h->r;
  h->r = x->l;
  x->l = h;
  return x;
}

/**
 * rotR - rotates a leaf to the right.
 * @h - a leaf
 * @returns - a leaf.
 */
Leaf rotR (Leaf h) {
  Leaf x;
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

/**
 * rotLR - rotates a leaf to the left and right.
 * @h - a leaf
 * @returns - a leaf.
 */
Leaf rotLR (Leaf h) {
  if (h == NULL)
    return h;

  h->l = rotL(h->l);
  return rotR(h);
}

/**
 * rotRL - rotates a leaf to the right and left.
 * @h - a leaf
 * @returns - a leaf.
 */
Leaf rotRL (Leaf h) {
  if (h == NULL)
    return h;

  h->r = rotR(h->r);
  return rotL(h);
}

/**
 * balanceFactor - calculates the balance factor using
 * the height of the child leafs.
 * @h - a leafs.
 * @returns - the balance factor.
 */
int balanceFactor (Leaf h) {
  if (h == NULL)
    return 0;

  return height(h->l) - height(h->r);
}

/**
 * AVLbalance - balances the tree.
 * @h - a leaf.
 * @returns - a leaf.
 */
Leaf AVLbalance (Leaf h) {
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

/**
 * maxNode - gets the max leaf of a tree.
 * @h - a leaf.
 * @returns - a leaf.
 */
Leaf maxNode (Leaf h) {
  if (h == NULL || h->r == NULL)
    return h;
  else
    return maxNode(h->r);
}

/**
 * countLeafsAux - counts the nuber of leafs in a tree
 * recursively.
 * @l - the root leaf.
 * @returns - the leaf count.
 */
int countLeafsAux (Leaf l) {
  if (l == NULL)
    return 0;
  else
    return countLeafsAux(l->r) + countLeafsAux(l->l) + 1;
}

/**
 * countLeafs - counts the number of leafs recursively
 * calling itself for each left and right node.
 * @node - a node.
 * @returns - the leaf count.
 */
int countLeafs (BTree b) {
  return countLeafsAux(b->head);
}

/**
 * searchR - looks up on the tree the element
 * identifiable by the key, recursively.
 * @b - the binary tree.
 * @h - the head where we're searching.
 * @v - the key to search for.
 * @returns - the pointer to the element.
 */
void* searchR (BTree b, Leaf h, void* v) {
  if (h == NULL)
    return NULL;

  switch (b->compare(v, b->key(h->item))) {
    case -1:
      return searchR(b, h->l, v);
    case 0:
      return h->item;
    default:
      return searchR(b, h->r, v);
  }
}

/**
 * searchTree - looks up on the tree the element
 * identifiable by the key.
 * @t - the binary tree.
 * @v - the key to search for.
 * @returns - the pointer to the element.
 */
void* searchTree (BTree t, void* v) {
  return searchR(t, t->head, v);
}

/**
 * insertR - inserts an item in the tree.
 * @t - the binary tree.
 * @h - the head leaf.
 * @item - the item.
 * @returns - the new head leaf.
 */
Leaf insertR (BTree t, Leaf h, void* item) {
  if (h == NULL)
    return newLeaf(item, NULL, NULL);
  if (t->compare(t->key(item), t->key(h->item)) == -1)
    h->l = insertR(t, h->l, item);
  else
    h->r = insertR(t, h->r, item);

  h = AVLbalance(h);
  return h;
}

/**
 * insertLeaf - inserts an item in the tree.
 * @t - the binary tree.
 * @item - the item.
 */
void insertLeaf (BTree t, void* item) {
  t->head = insertR(t, t->head, item);
}

/**
 * deleteLeaf - removes an element from the tree,
 * recursively.
 * @t - the binary tree.
 * @h - the head leaf.
 * @v - the key.
 * @returns - the new head leaf.
 */
Leaf deleteR (BTree t, Leaf h, void* k) {
  Leaf aux;
  void* x;

  if (h == NULL)
    return h;

  switch (t->compare(k, t->key(h->item))) {
    case -1:
      h->l = deleteR(t, h->l, k);
      break;
    case 1:
      h->r = deleteR(t, h->r, k);
      break;
    default:
      if (h->l !=NULL && h->r !=NULL) {
        aux = maxNode(h->l);
        x = h->item;
        h->item = aux->item;
        aux->item = x;
        h->l= deleteR(t, h->l, t->key(aux->item));
      } else {
        aux=h;
        if (h->l == NULL && h->r == NULL)
          h = NULL;
        else if (h->l == NULL)
          h = h->r;
        else
          h = h->l;

        t->free(aux->item);
        free(aux);
      }
  }

  h = AVLbalance(h);
  return h;
}

/**
 * deleteLeaf - removes an element from the tree.
 * @t - the binary tree.
 * @v - the key.
 */
void deleteLeaf (BTree t, void* v) {
  t->head = deleteR(t, t->head, v);
}

/**
 * traverseR - traverses a tree recursively from the smallest
 * element to the biggest one by going down to the most down left
 * leaf and going upwards.
 * @h - the head of the node.
 * @visit - the function to apply to every element.
 */
void traverseR( Leaf h, void (*visit)(void *)) {
  if (h == NULL)
    return;

  traverseR(h->l, visit);
  visit(h->item);
  traverseR(h->r, visit);
}

/**
 * traverseTree - traverses a tree and calls a function to every
 * element.
 * @node - a node.
 * @visit - the function to call to each element.
 */
void traverseTree (BTree t, void (*visit)(void*)) {
  traverseR(t->head, visit);
}

/**
 * freeR - frees a leaf. THis is an helper function
 * to free every node from the tree using the delete
 * function.
 * @node - a a node.
 * @returns - a node.
 */
Leaf freeR (BTree t, Leaf h) {
  if (h == NULL)
    return h;

  h->l = freeR(t, h->l);
  h->r = freeR(t, h->r);

  return deleteR(t, h, t->key(h->item));
}

/**
 * freeTree - frees an entire tree.
 * @b - a pointer to a BTree.
 */
void freeTree (BTree b) {
  b->head = freeR(b, b->head);
  free(b);
}
