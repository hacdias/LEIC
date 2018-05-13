#ifndef _btree_h
#define _btree_h

#include <stdlib.h>
#include <stdio.h>

#include "task.h"

typedef struct node* Node;

/**
 * initNode - initializes a node.
 * @node - a pointer to a node.
 */
void initNode (Node* node);

/**
 * countNodes - counts the number of nodes.
 * @node - a node.
 */
int countNodes (Node node);

/**
 * searchTree - searches the node tree to find a certain node.
 * @node - a node.
 * @key - the key to search for.
 */
Item searchTree (Node node, Key key);

/**
 * insertNode - inserts an item in the tree.
 * @node - the pointer to a node.
 * @item - the item.
 */
void insertNode (Node* node, Item item);

/**
 * deleteNode - deletes an element from a tree.
 * @node - the pointer to a node.
 * @key - key of the element to delete.
 */
void deleteNode (Node* node, Key key);

/**
 * traverseTree - trverses a tree and calls a function to every
 * element.
 * @node - a node.
 * @visit - the function to call to each element.
 */
void traverseTree (Node node, void (*visit)(Item));

/**
 * freeNode - frees the Node.
 * @node - a pointer to a node.
 */
void freeNode (Node* node);

#endif