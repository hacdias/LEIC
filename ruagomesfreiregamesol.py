import math
import pickle
import time
import itertools
from functools import reduce

class Node():
  def __init__(self, parent=None, position=None, transport=None, depth = 0):
    self.position = position
    self.parent = parent
    self.transport = transport
    self.g = 0
    self.h = 0
    self.f = 0
    self.depth = depth

  def __eq__(self, other):
    return self.position == other.position and self.transport == other.transport

class SearchProblem:
  def __init__(self, goal, model, auxheur = []):
    self.goal = goal
    self.model = model
    self.auxheur = auxheur

  def search(self, init, limitexp = 2000, limitdepth = 10, tickets = [math.inf, math.inf, math.inf], anyorder=False):
    opened = [list(Node(None, x, None) for x in init)]
    closed = []

    while len(opened) > 0:
      curr = opened[0]
      curri = 0
      avg = heurAvgF(curr)
    
      for i, lst in enumerate(opened):
        newAvg = heurAvgF(lst)
        if newAvg < avg:
          avg = newAvg
          curr = lst
          curri = i

      #for node in curr:
        #if node.transport is not None:
          #tickets[node.transport] -= 1

      opened.pop(curri)
      closed.append(curr)

      if all(list(self.goal[i] == node.position for i, node in enumerate(curr))):
        path = []
        while curr[0] is not None:
          path.insert(0, [list(n.transport for n in curr), list(n.position for n in curr)])
          curr = list(n.parent for n in curr)
        print(path)
        return path

      for tup in itertools.product(*list(self.model[node.position] for node in curr)):
        if not allDifferent(tup):
          continue

        #tk = tickets.copy()
        #for move in tup:
        #  tk[move[0]] -= 1
        #if any(x < 0 for x in tk):
        #  continue

        move = list(Node(curr[i], pos, trans, depth = curr[i].depth + 1) for i, (trans, pos) in enumerate(tup))

        if isInList(move, closed):
          continue

        for (c, n, g) in zip(curr, move, self.goal):
          n.g = c.g + 1
          n.h = self.__distance(n.position, g)
          n.f = n.g + n.h

        if not isInList(move, opened):
          opened.append(move)

  def __heuristic (self, pos):
    return map(lambda arg : self.__distance(arg[1], self.goal[arg[0]]), enumerate(pos))

  def __distance (self, src, dst):
    x = self.auxheur[dst - 1][0] - self.auxheur[src - 1][0]
    y = self.auxheur[dst - 1][1] - self.auxheur[src - 1][1]
    return math.hypot(x, y)

def isInList (move, list):
  for l in list:
    allEqual = True
    for i, m in enumerate(l):
      if m.position != move[i].position:
        allEqual = False
        break
    if allEqual:
      return True
  return False

def heurAvgF (list):
  return reduce(lambda a, b: a + b.f, list, 0)

def allDifferent (tup):
  for i, l1 in enumerate(tup):
    for j, l2 in enumerate(tup):
      if i is not j and l1[1] is l2[1]:
        return False
  return True
    