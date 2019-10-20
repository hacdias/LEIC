import math
import pickle
import time
import itertools
from functools import reduce

def heuristic (auxheur, src, dst):
  sum = 0
  for (s, d) in zip(src, dst):
    x = auxheur[d - 1][0] - auxheur[s - 1][0]
    y = auxheur[d - 1][1] - auxheur[s - 1][1]
    sum += x ** 2 + y ** 2
  return sum

class State():
  def __init__(self, path, tickets, goal, auxheur):
    self.path = path
    self.tickets = tickets
    self.depth = len(path)
    self.heuristic = heuristic(auxheur, path[-1][1], goal)

  def __gt__(self, other):
    return self.depth + self.heuristic > other.depth + other.heuristic

  def __ge__(self, other):
    return self.depth + self.heuristic >= other.depth + other.heuristic

  def __lt__(self, other):
    return self.depth + self.heuristic < other.depth + other.heuristic

  def __le__(self, other):
    return self.depth + self.heuristic <= other.depth + other.heuristic

  #def __eq__(self, other):
    #return self.tickets == other.tickets and self.depth == other.depth

  def isValid(self):
    return all(x >= 0 for x in self.tickets)

  def isGoal(self, goal):
    # IF 5, check any order
    return self.path[-1][1] == goal

  def expand(self, pos, goal, auxheur):
    newPath = self.path.copy()
    newPath.append(pos)

    newTickets = self.tickets.copy()
    for t in pos[0]:
      newTickets[t] -= 1
    return State(newPath, newTickets, goal, auxheur)

class SearchProblem:
  def __init__(self, goal, model, auxheur = []):
    self.goal = goal
    self.model = model
    self.auxheur = auxheur

  def search(self, init, limitexp = 2000, limitdepth = 10, tickets = [math.inf, math.inf, math.inf], anyorder=False):
    opened = [State([[[], init.copy()]], tickets.copy(), self.goal, self.auxheur)]
    openedTwo = [init.copy()]
    closed = []
    closedTwo = []

    while len(opened) > 0:
      opened.sort()
      curr = opened.pop(0)
      openedTwo.remove(curr.path[-1][1])
      closed.append(curr)
      closedTwo.append(curr.path[-1][1])

      print(curr.heuristic + curr.depth)

      #print(curr.path[-1])

      limitexp -= 1

      if curr.isGoal(self.goal) or limitexp == 0:
        return curr.path

      for tup in itertools.product(*list(self.model[x] for x in curr.path[-1][1])):
        # print(tup)
        if not allDifferent(tup):
          continue

        nextPos = [[], []]
        for (trans, pos) in tup:
          nextPos[0].append(trans)
          nextPos[1].append(pos)

        move = curr.expand(nextPos, self.goal, self.auxheur)

        if not move.isValid():
          continue

        if move.depth > limitdepth:
          continue

        if move.path[-1][1] in closedTwo:
          continue

        #if move.path[-1][1] in openedTwo:
        #  continue

        opened.append(move)
        openedTwo.append(move.path[-1][1])

def allDifferent (tup):
  for i, l1 in enumerate(tup):
    for j, l2 in enumerate(tup):
      if i is not j and l1[1] is l2[1]:
        return False
  return True
