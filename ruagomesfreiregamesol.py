import math
import pickle
import time

class SearchProblem:
  def __init__(self, goal, model, auxheur = []):
    self.goal = goal
    self.model = model
    self.auxheur = auxheur

  def search(self, init, limitexp = 2000, limitdepth = 10, tickets = [math.inf, math.inf, math.inf]):
    path = self.aux(init[0], self.goal[0], init[0], path = [[[], init]])
    print(path)
    return path

  def aux(self, init, goal, current, visited = [], path = []):
    visited.append(current)
    if current == goal:
      return path

    for x in self.model[current]:
      if x[1] not in visited:
        sis = path.copy()
        sis.append([
          [x[0]],
          [x[1]]
        ])
        sol = self.aux(init, goal, x[1], visited = visited.copy(), path = sis)
        if sol != None:
          return sol
      else:
        return None
