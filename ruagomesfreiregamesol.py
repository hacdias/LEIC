import math
import pickle
import time
import itertools

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
      avg = average(curr)

      for i, lst in enumerate(opened):
        if average(lst) < avg:
          curr = lst
          curri = i

      for node in curr:
        if node.transport is not None:
          tickets[node.transport] -= 1

      opened.pop(curri)
      closed.append(curr)

      isGoal = list(self.goal[i] == node.position for i, node in enumerate(curr))

      if all(isGoal):
        """
        path = []
        while curr is not None:
          path.insert(0, [[curr.transport], [curr.position]])
          curr = curr.parent
        print("PATH to GOAL:", path)
        return path
        """
        print("IM AT THE END")
        return []

      if any(isGoal):
        continue

      for tup in itertools.product(*list(self.model[node.position] for node in curr)):
        tk = tickets.copy()
        for move in tup:
          tk[move[0]] -= 1

        if any(x < 0 for x in tk):
          continue


        move = list(Node(curr[i], pos, trans) for i, (trans, pos) in enumerate(tup))

        if move in closed:
          continue

        for (c, n, g) in zip(curr, move, self.goal):
          n.g = c.g + 1
          n.h = self.__distance(n.position, g)
          n.f = n.g + n.h

        print(list(n.f for n in move))
  
      
      """
      for newPos in self.model[curr.position]:
        child = Node(curr, newPos[1], newPos[0])
        if child in closed:
          continue

        if not isInListWithG(child, opened):
          opened.append(child)
      """

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

def average (list):
  sum = 0
  for node in list:
    sum = sum + node.f
  return sum / len(list)
