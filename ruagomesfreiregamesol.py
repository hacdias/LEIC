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
    return self.position == other.position

class SearchProblem:
  def __init__(self, goal, model, auxheur = []):
    self.goal = goal
    self.model = model
    self.auxheur = auxheur

  def search(self, init, limitexp = 2000, limitdepth = 10, tickets = [math.inf, math.inf, math.inf]):
    startNode = Node(None, init[0])
    startNode.g = startNode.h = startNode.f = 0
    endNode = Node(None, self.goal[0])
    endNode.h = endNode.h = endNode.f = 0

    openList = []
    closedList = []

    openList.append(startNode)
    
    while len(openList) > 0:
        currentNode = openList[0]
        currentIndex = 0

        for index, item in enumerate(openList):
            if item.f < currentNode.f:
                currentNode = item
                currentIndex = index

        openList.pop(currentIndex)
        closedList.append(currentNode)

        # Found the goal
        if currentNode == endNode:
            path = []
            current = currentNode
            while current is not None:
                path.append([[current.transport], [current.position]])
                current = current.parent
            print(path)
            return path[::-1] # Return reversed path

        for newPosition in self.model[currentNode.position]:
          child = Node(currentNode, newPosition[1], newPosition[0])

          if child in closedList:
            continue

          child.g = currentNode.g + self.__distance(child.position, currentNode.position)
          child.h = self.__distance(child.position, endNode.position)
          child.f = child.g + child.h

          shouldContinue = False
          for openNode in openList:
            if child == openNode and child.g > openNode.g:
              shouldContinue = True
              break

          if shouldContinue:
            continue

          openList.append(child)

  def __heuristic (self, pos):
    return map(lambda arg : self.__distance(arg[1], self.goal[arg[0]]), enumerate(pos))

  def __distance (self, src, dst):
    x = self.auxheur[dst - 1][0] - self.auxheur[src - 1][0]
    y = self.auxheur[dst - 1][1] - self.auxheur[src - 1][1]
    return math.sqrt(x ** 2 + y ** 2)
