import math
import pickle
import time
import itertools
from functools import reduce

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

      opened.pop(curri)
      closed.append(curr)

      if curr[0].g > limitdepth:
        continue

      limitexp -= 1

      if all(list(self.goal[i] == node.position for i, node in enumerate(curr))) or limitexp == 0:
        path = []
        while curr[0] is not None:
          path.insert(0, [list(n.transport for n in curr), list(n.position for n in curr)])
          curr = list(n.parent for n in curr)
        return path

      for node in curr:
        if node.transport is not None:
          tickets[node.transport] -= 1

      deadend = True
      for tup in itertools.product(*list(self.model[node.position] for node in curr)):
        if not allDifferent(tup):
          continue

        move = list(Node(curr[i], pos, trans) for i, (trans, pos) in enumerate(tup))

        if not ticketsInLimits(move, tickets):
          continue

        if isInList(move, closed):
          continue

        for (c, n, g) in zip(curr, move, self.goal):
          n.g = c.g + 1
          n.h = self.__distance(n.position, g)
          n.f = n.g + n.h

        # SHOULD BE ALL? If so, put before calculations!
        if isInList(move, opened):
          continue

        deadend = False
        opened.append(move)

      if deadend:
        for node in curr:
          tickets[node.transport] += 1

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

def ticketsInLimits (move, tickets):
  tk = [0, 0, 0]
  curr = move
  while curr[0] is not None:
    for node in curr:
      if node.transport is not None:
        tk[node.transport] += 1
    curr = list(n.parent for n in curr)
  return all(list(x <= tickets[i] for i, x in enumerate(tk)))
