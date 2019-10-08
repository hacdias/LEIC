import math
import pickle
import time

class Node():
  def __init__(self, parent=None, position=None, transport=None):
    self.position = position
    self.parent = parent
    self.transport = transport
    self.g = 0
    self.h = 0
    self.f = 0

  def __eq__(self, other):
    return self.position == other.position and self.transport == other.transport

class SearchProblem:
  def __init__(self, goal, model, auxheur = []):
    self.goal = goal
    self.model = model
    self.auxheur = auxheur

  def search(self, init, limitexp = 2000, limitdepth = 10, tickets = [math.inf, math.inf, math.inf]):
    opened = [Node(None, init[0], None)]
    closed = []

    while len(opened) > 0:
      curr = opened[0]
      curri = 0

      for i, node in enumerate(opened):
        if node.f < curr.f:
          curr = node
          curri = i

      if curr.transport is not None:
        tickets[curr.transport] -= 1

      opened.pop(curri)
      closed.append(curr)

      if curr.position == self.goal[0]:
        path = []
        while curr is not None:
          path.insert(0, [[curr.transport], [curr.position]])
          curr = curr.parent
        print("PATH to GOAL:", path)
        return path

      for newPos in self.model[curr.position]:
        child = Node(curr, newPos[1], newPos[0])

        if tickets[newPos[0]] -1 < 0:
          continue

        if child in closed:
          continue

        child.g = curr.g + 1
        child.h = self.__distance(child.position, self.goal[0])
        child.f = child.g + child.h

        if not isInListWithG(child, opened):
          opened.append(child)

  def __heuristic (self, pos):
    return map(lambda arg : self.__distance(arg[1], self.goal[arg[0]]), enumerate(pos))

  def __distance (self, src, dst):
    x = self.auxheur[dst - 1][0] - self.auxheur[src - 1][0]
    y = self.auxheur[dst - 1][1] - self.auxheur[src - 1][1]
    return math.hypot(x, y)

def isInListWithG (node, list):
  for n in list:
    if node == n and node.g > n.g:
      return True
  return False
