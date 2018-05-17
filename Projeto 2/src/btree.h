#ifndef _btree_h
#define _btree_h

#include <stdlib.h>
#include <stdio.h>

typedef struct leaf* Leaf;
typedef struct btree* BTree;

typedef int (*BTreeCompFn)(void*, void*);
typedef void* (*BTreeKeyFn)(void *);
typedef void (*BTreeFreeFn)(void *);

struct btree {
  BTreeCompFn compare;
  BTreeKeyFn key;
  BTreeFreeFn free;
  Leaf head;
};

/**
 * newBTree - creates a new binary tree
 * @compare - the comparison function between element' keys.
 * @key - the function to get the key.
 * @free - the function to free to each element.
 * @returns - a binary tree.
 */
BTree newBTree (BTreeCompFn, BTreeKeyFn, BTreeFreeFn);

/**
 * countLeafs - counts the number of leafs recursively
 * calling itself for each left and right node.
 * @node - a node.
 * @returns - the leafs count.
 */
int countLeafs (BTree);

/**
 * searchTree - looks up on the tree the element
 * identifiable by the key.
 * @t - the binary tree.
 * @v - the key to search for.
 * @returns - the pointer to the element.
 */
void* searchTree (BTree, void* key);

/**
 * insertLeaf - inserts an item in the tree.
 * @t - the binary tree.
 * @item - the item.
 */
void insertLeaf (BTree, void* item);

/**
 * deleteLeaf - removes an element from the tree.
 * @t - the binary tree.
 * @v - the key.
 */
void deleteLeaf (BTree, void* key);

/**
 * traverseTree - traverses a tree and calls a function to every
 * element.
 * @node - a node.
 * @visit - the function to call to each element.
 */
void traverseTree (BTree, void (*visit)(void*));

/**
 * freeTree - frees an entire tree.
 * @b - a pointer to a BTree.
 */
void freeTree (BTree);

#endif
