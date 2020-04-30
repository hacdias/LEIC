# Sauron Project Report

Distributed Systems 2019/2020, Second Semester

## Authors

**Group A46**

| Number | Name              | User                             | Email                                      |
| -------|-------------------|----------------------------------| ------------------------------------------ |
| 89455  | Henrique Dias     | <https://github.com/hacdias>     | <mailto:henrique.dias@tecnico.ulisboa.pt>  |
| 89466  | Isabel Soares     | <https://github.com/isabelSoares>| <mailto:isabel.r.soares@tecnico.ulisboa.pt>|
| 89535  | Rodrigo Sousa     | <https://github.com/Sousa99>     | <mailto:rodrigo.b.sousa@tecnico.ulisboa.pt>|

![Henrique Dias](henrique.png) ![Isabel Soares](isabel.png) ![Rodrigo Sousa](rodrigo.png)

## First Part Improvements

- [Add tests for invalid camera names](https://github.com/tecnico-distsys/A46-Sauron/commit/d0cd7d18ceae2f04ab60c559351ddf7535217451)
- [Synchronize shared variables](https://github.com/tecnico-distsys/A46-Sauron/commit/9bbd41ce74a592654d572a0b59faeb222d213a06)
- [Accept camera with same specifications](https://github.com/tecnico-distsys/A46-Sauron/commit/02b35fdd471efb15a3194e2333990e55f34a845f)

## Fault Model

_(que faltas são toleradas, que faltas não são toleradas)_

## Solution

_(Figura da solução de tolerância a faltas)_

_(Breve explicação da solução, suportada pela figura anterior)_

## Replication Protocol

For our replication protocol between replicas, we decided to use a modified [_gossip_ protocol](https://en.wikipedia.org/wiki/Gossip_protocol). Each replica has its own Replica Manager (RM), which handles all the data operations done in each Replica. Each RM contains:

- _Value_: the value that is stored on each replica, which, in this case, are the cameras and the observations.
- _Value Timestamp_: a timestamp representing the operations done by each replica to achieve the current value on the current replica.
- _Log_: all the update operations the replica has accepted so far. It may contain already processed operations that are yet required to be propagated to other replicas.
- _Replica Timestamp_: represents all the operations the current replica has accepted, i.e., that were placed in the log.
- _Executed Operations_: a list with the unique IDs of the executed operations on the current replica.
- _Timestamp Table_: a list with the the known replica timestamps from other replicas.

### Get Operations

### Add Operations

### Message Exchanging

_(Explicação do protocolo)_

_(descrição das trocas de mensagens)_

## Implementation Specifics

_(Descrição de opções de implementação, incluindo otimizações e melhorias introduzidas)_

## Final Remarks

_(Algo mais a dizer?)_
