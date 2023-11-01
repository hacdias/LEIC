# A013, Henrique Dias - 89455, Tiago Gonçalves - 89547
import math
import pickle
import time
import itertools

def heuristic_aux (heur, src, dst):
  m = 0
  for (s, d) in zip(src, dst):
    m = max(m, heur[d][s])
  return m

def heuristic(heur, src, dst_lst):
  maxs = []
  for dst in dst_lst:
    maxs.append(heuristic_aux(heur, src, dst))
  return min(maxs)

class State():
  def __init__(self, path, tickets, goals, heur):
    self.path = path
    self.tickets = tickets
    self.depth = len(path)
    self.heuristic = heuristic(heur, path[-1][1], goals)

  def __gt__(self, other):
    return self.depth + self.heuristic > other.depth + other.heuristic

  def __ge__(self, other):
    return self.depth + self.heuristic >= other.depth + other.heuristic

  def __lt__(self, other):
    return self.depth + self.heuristic < other.depth + other.heuristic

  def __le__(self, other):
    return self.depth + self.heuristic <= other.depth + other.heuristic

  def isValid(self):
    return all(x >= 0 for x in self.tickets)

  def isGoal(self, goals):
    for goal in goals:
      if self.path[-1][1] == goal:
        return True
    return False

  def expand(self, pos, goals, heur):
    newPath = self.path.copy()
    newPath.append(pos)

    newTickets = self.tickets.copy()
    for t in pos[0]:
      newTickets[t] -= 1
    return State(newPath, newTickets, goals, heur)

class SearchProblem:
  def __init__(self, goal, model, auxheur = []):
    self.goal = goal
    self.model = model
    self.auxheur = auxheur
    self.bfs()

  def bfs(self):
    heur = {}

    for goal in self.goal:
      heur[goal] = {goal: 0}
      queue = [(goal, 0)]
      while queue:
        vertex, cost = queue.pop(0)
        for node in self.model[vertex]:
          if node[1] not in heur[goal]:
            heur[goal][node[1]] = cost + 1
            queue.append((node[1], cost + 1))
    self.heur = heur

  def search(self, init, limitexp = 2000, limitdepth = 10, tickets = [math.inf, math.inf, math.inf], anyorder=False):
    goals = list(list(i) for i in itertools.permutations(self.goal)) if anyorder else [self.goal]
    opened = [State([[[], init.copy()]], tickets.copy(), goals, self.heur)]
    closed = []

    while len(opened) > 0:
      opened.sort()
      curr = opened.pop(0)
      closed.append(curr)

      limitexp -= 1

      if curr.isGoal(goals) or limitexp == 0:
        return curr.path

      for tup in itertools.product(*list(self.model[x] for x in curr.path[-1][1])):
        if not allDifferent(tup):
          continue

        nextPos = [[], []]
        for (trans, pos) in tup:
          nextPos[0].append(trans)
          nextPos[1].append(pos)

        move = curr.expand(nextPos, goals, self.heur)

        if not move.isValid():
          continue

        if move.depth > limitdepth:
          continue

        opened = [move] + opened

def allDifferent (tup):
  for i, l1 in enumerate(tup):
    for j, l2 in enumerate(tup):
      if i is not j and l1[1] is l2[1]:
        return False
  return True
