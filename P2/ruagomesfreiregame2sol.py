import random
import math

# LearningAgent to implement
# no knowledge about the environment can be used
# the code should work even with another environment
class LearningAgent:
  # init
  # nS maximum number of states
  # nA maximum number of action per state
  def __init__(self, nS, nA):
    self.qlst = [[0] * nA] * nS

  # Select one action, used when learning  
  # st - is the current state        
  # aa - is the set of possible actions
  # for a given state they are always given in the same order
  # returns
  # a - the index to the action in aa
  def selectactiontolearn(self, st, aa):
    epsilon = 0.2
    if random.uniform(0, 1) < epsilon:
      # explore
      return random.randint(0, len(aa) - 1)
    else:
      # exploit aka selecionar maior Q
      return self.selectactiontoexecute(st, aa)

  # Select one action, used when evaluating
  # st - is the current state        
  # aa - is the set of possible actions
  # for a given state they are always given in the same order
  # returns
  # a - the index to the action in aa
  def selectactiontoexecute(self, st, aa): 
    maxQ = -math.inf
    a = 0
    for i, action in enumerate(aa):
      if max(self.qlst[action]) >= maxQ:
        maxQ = max(self.qlst[action])
        a = i
    return a

  # this function is called after every action
  # ost - original state
  # nst - next state
  # a - the index to the action taken
  # r - reward obtained
  def learn(self, ost, nst, a, r): 
    alpha = 0.1
    gamma = 0.9
    self.qlst[ost][a] = self.qlst[ost][a] + alpha * (r + gamma * max(self.qlst[nst]) - self.qlst[ost][a])
