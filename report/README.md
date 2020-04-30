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

Let's define some operations `a priori`:

```
merge(tsA, tsB):
  for each entry i in tsA
    if tsA[i] > tsB[i]
      tsB[i] = tsA[i]
```

### Get Operations

( TODO and include our modification )

### Add Operations

When a client wants to add a new Camera or Observation, it generates a [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier) _id_ to uniquely identify the request. Then, it sends the `id`, the `data` (Camera or Observations) and the `previous` timestamp that was stored in the client. When the request `req` arrives in the server, the replica checks decides whether of not to discard the request. A request is discarded if:

- The `id` is present on the executed operations list; or
- The `id` is present on any log record.

If the request is accepted, then we follow the algorithm:

1. Update the replica timestamp by incrementing the _i_\th entry, where `i` is the current instance number starting on 0.
2. Create a unique `timestamp` to represent the operation from now on. This timestamp is created by duplicating `previous` and replacing the _i_\th entry with the previously calculated value.
3. Creates a new log record with the new `timestamp`, the current instance number, the `id` and the data to add.
4. Returns the new timestamp to the client.
5. Checks if the operation can be executed immediately by checking if `previous` <= `valueTimestamp`. If possible, execute `merge(timestamp, valueTimestamp)`.

Otherwise, wait.

### Gossip Operations (Sync)

The gossip operations are required to ensure all replicas end up receiving all the information, eventually. By default, we send these messages every 30 seconds, which can be customized. On a gossip request, a replica _i_ sends to the replica _j_:

- The instance number _i_;
- The `sourceTimestamp`, which is the replica timestamp of replica _i_;
- The logs records we estimate the replica _j_ does not have:
    - TODO: explain
  
When the replica _j_ receives the gossip message from the replica _i_, it then proceeds as follows:

1. Updates the entry _i_ in the timestamp table with the `replicaTimestamp`;
2. For each log record `r`:
    - Checks if `r.id` is on the executed operations list. If so, discards.
    - Checks if `r.timestamp` > `replicaTimestamp`. If not, discards.
    - Adds the record to its own record log.
3. Updates `replicaTimestamp`, by executing `merge(sourceTimestamp, replicaTimestamp)`.
4. Goes through the log and executes all stable operations.
5. Finally, cleans up the log, freeing some space, by comparing every timestamp on the table of timestamps with each record's timestamp. Being `c` the number of the instance where the record was initially created, if the every timestamp's _c_ entry >= record's timestamp _c_ entry, then it means the record can be safely removed from the log since all replicas already received that information.

## Implementation Specifics

_(Descrição de opções de implementação, incluindo otimizações e melhorias introduzidas)_

## Final Remarks

_(Algo mais a dizer?)_
