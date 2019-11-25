import random

# LearningAgent to implement
# no knowledge about the environment can be used
# the code should work even with another environment
class LearningAgent:

        # init
        # nS maximum number of states
        # nA maximum number of action per state
        def __init__(self,nS,nA):

                # define this function
                self.nS = nS
                self.nA = nA
                tempLst = [0]*nA
                self.qlst = [tempLst]*nS
                # define this function
              
        
        # Select one action, used when learning  
        # st - is the current state        
        # aa - is the set of possible actions
        # for a given state they are always given in the same order
        # returns
        # a - the index to the action in aa
        def selectactiontolearn(self,st,aa):
        ##Selecionamos randomly mas pelo que eu percebi temos de ter em atenção exploration e exploitation que está no livro. Ou seja não é só randomly
                # define this function
                # print("select one action to learn better")

                a = 0
                # define this function
                a = random.randint(0, len(aa) - 1)
                return a

        # Select one action, used when evaluating
        # st - is the current state        
        # aa - is the set of possible actions
        # for a given state they are always given in the same order
        # returns
        # a - the index to the action in aa
        def selectactiontoexecute(self,st,aa): 
        ##Selecionamos o que tiver maior Q
                # define this function
                a = 0
                # print("select one action to see if I learned")
                
                maxQ = self.qlst[st][aa[0]]
                for i in range(0, len(aa) - 1):
                        if self.qlst[st][aa[i]] >= maxQ:
                                maxQ = aa[i]
                                a = i
                return a


        # this function is called after every action
        # ost - original state
        # nst - next state
        # a - the index to the action taken
        # r - reward obtained
        def learn(self,ost,nst,a,r): 
        ##Faz-se aqui o Q-Learning
                # define this function
                #print("learn something from this data")
                alfa = 0.1
                gamma = 0.9
                self.qlst[ost][a] = self.qlst[ost][a] + alfa * (r + gamma * max(self.qlst[nst]) - self.qlst[ost][a])
                return
