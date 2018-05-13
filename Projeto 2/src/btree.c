#include "btree.h"

struct node {
  Item item;
  Node l, r;
  int height;
};

/**
 * newNode - creates a new node based on an Item.
 * @item - the item the node contains.
 * @l - the left node.
 * @r - the right node.
 * @returns - the head of the node.
 */
Node newNode (Item item, Node l, Node r) {
  Node x = malloc(sizeof(struct node));

  x->item = item;
  x->l = l;
  x->r = r;
  x->height = 1;

  return x;
}

/**
 * height - returns the height of a node.
 * @h - a node.
 * @returns - the height.
 */
int height (Node h) {
  if (h == NULL)
    return 0;

  return h->height;
}

/**
 * rotL - rotates a node to the left.
 * @h - a node
 * @returns - a node.
 */
Node rotL (Node h) {
  Node x = h->r;
  h->r = x->l;
  x->l = h;
  return x;
}

/**
 * rotR - rotates a node to the right.
 * @h - a node
 * @returns - a node.
 */
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

/**
 * rotLR - rotates a node to the left and right.
 * @h - a node
 * @returns - a node.
 */
Node rotLR (Node h) {
  if (h == NULL)
    return h;

  h->l = rotL(h->l);
  return rotR(h);
}

/**
 * rotRL - rotates a node to the right and left.
 * @h - a node
 * @returns - a node.
 */
Node rotRL (Node h) {
  if (h == NULL)
    return h;

  h->r = rotR(h->r);
  return rotL(h);
}

/**
 * balanceFactor - calculates the balance factor using
 * the height of the child nodes.
 * @h - a node
 * @returns - the balance factor.
 */
int balanceFactor (Node h) {
  if (h == NULL)
    return 0;

  return height(h->l) - height(h->r);
}

/**
 * AVLbalance - balances the tree.
 * @h - a node.
 * @returns - a node.
 */
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

/**
 * maxNode - gets the max node of a tree.
 * @h - a node.
 * @returns - a node.
 */
Node maxNode (Node h) {
  if (h == NULL || h->r == NULL)
    return h;
  else
    return maxNode(h->r);
}

/**
 * initNode - initializes a node by assigning it to NULL.
 * @node - a pointer to a node.
 */
void initNode (Node *head) {
  *head = NULL;
}

/**
 * countNodes - counts the number of nodes recursively
 * calling itself for each left and right node.
 * @node - a node.
 */
int countNodes (Node h) {
  if (h == NULL)
    return 0;
  else
    return countNodes(h->r) + countNodes(h->l) + 1;
}

/**
 * searchTree - searches the node tree recursively to find a certain node.
 * @node - a node.
 * @key - the key to search for.
 */
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

/**
 * insertR - inserts an item in the tree recursively
 * and then balances the tree.
 * @node - the pointer to a node.
 * @item - the item.
 * @returns - a node.
 */
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

/**
 * insertNode - inserts an item in the tree.
 * @node - the pointer to a node.
 * @item - the item.
 */
void insertNode (Node *head, Item item) {
  *head = insertR(*head, item);
}

/**
 * deleteR - deletes an element from a tree recursively.
 * @node - the pointer to a node.
 * @key - key of the element to delete.
 * @returns - a node.
 */
Node deleteR(Node h, Key k) {
  Node aux;
  Item x;

  if (h == NULL)
    return h;
  else if (less(k, key(h->item)))
    h->l = deleteR(h->l,k);
  else if (less(key(h->item), k))
    h->r = deleteR(h->r,k);
  else {
    if (h->l !=NULL && h->r !=NULL) {
      aux = maxNode(h->l);
      x = h->item;
      h->item = aux->item;
      aux->item = x;
      h->l= deleteR(h->l, key(aux->item));
    } else {
      aux=h;
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

/**
 * deleteNode - deletes an element from a tree.
 * @node - the pointer to a node.
 * @key - key of the element to delete.
 */
void deleteNode (Node *head, Key k){
  *head = deleteR(*head, k);
}

/**
 * traverseR - traverses a tree recursively from the smallest
 * element to the biggest one by going down to the most down left
 * leaf and going upwards.
 * @h - the head of the node.
 * @visit - the function to apply to every element.
 */
void traverseR(Node h, void (*visit)(Item)) {
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
void traverseTree (Node head, void (*visit)(Item)) {
  traverseR(head, visit);
}

/**
 * freeR - frees the Node. THis is an helper function
 * to free every node from the tree using the delete
 * function.
 * @node - a a node.
 * @returns - a node.
 */
Node freeR (Node h) {
  if (h == NULL)
    return h;

  h->l = freeR(h->l);
  h->r = freeR(h->r);

  return deleteR(h, key(h->item));
}

/**
 * freeNode - frees the Node.
 * @node - a pointer to a node.
 */
void freeNode (Node *head) {
  *head = freeR(*head);
}
