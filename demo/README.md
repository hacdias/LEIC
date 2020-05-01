# Sauron Demo & Usage

The Sauron server requires a [ZooKeeper](https://zookeeper.apache.org/) instance to be running. We assume it is running on the local machine, on the default port. For further customization, please check [this file](../silo-server/pom.xml).

Before proceeding to any of the following sections, you should make sure the project is compiled and up to date, by running:

```shell
cd /path/to/A46-Sauron
git pull
mvn clean install -DskipTests
```

## Table of Contents

- [Server Usage](#server-usage)
- [Spotter Usage](#spotter-usage)
- [Eye Usage](#eye-usage)
- [Observing Gossips](#observing-gossips)
- [Run Tests](#run-tests)

## Server Usage

To run a _n_ number of replicas, you will need to first install the dependencies and make sure everything compiles. Then, navigate to `silo-server` and create a directory `data` where each of your replicas will be storing their state. This will allow us to shut down the replicas and restart them without losing consistency between them. You can read more on the [report](../report/README.md) for why we made this decision.

The data files are automatically created inside the `data` directory for each replica in a `.data` file. Replica 1 will store on `data/1.data`, 2 on `data/2.data` and so on. This is a small automation we did on [`pom.xml`](../silo-server/pom.xml). However, each replica could be run on distinct machines with their own data file, specified by the `-DstorageFile` flag.

Now, for each _i_ replica of the _n_ replicas, navigate `silo-server` and open a terminal window there and run, replacing _i_ and _n_  by the corresponding numbers:

```shell
mvn compile exec:java -Dinstance=i -DnumberServers=n
````

## Spotter Usage

The Spotter is the client used to fetch observations from cars and people. It can be located on the `spotter` directory. To run it, please navigate to `spotter` directory, and run:

```shell
./target/appassembler/bin/spotter zookeeperHost zookeeperPort [instanceNumber]
```

The `instanceNumber` is optional and should only be used if you really need to connect to a specific replica instance. By default, we connect to a random available replica.

Inside Spotter, you can run a few commands, such as:

- `spot type identifier` - fetch last observation
-	`trail type identifier` - fetch observations history

You can also type `help` for more information and commands.

## Eye Usage

The Eye is the client used to track cars and people. It can be located on the `eye` directory. To run it, please navigate to `eye` directory, and run:

```shell
./target/appassembler/bin/eye zookeeperHost zookeeperPort cameraName cameraLatitude cameraLongitude [instanceNumber]
```

You should replace each argument by its real value. The `instanceNumber` is optional and should only be used if you really need to connect to a specific replica instance. By default, we connect to a random available replica.

Inside Eye, you can enter the observations to add. An example would be:

```shell
car,AA11BB
person,12345
car,BB66CC
```

After inserting the observations, you should press Enter twice to send them to the connected replica.

## Observing _Gossips_

During execution, you will be able to observe the gossips between each replica on the command line. We provide an enormous amount of logs. Here is a simple example of two replicas running, and an Eye sending them information:

Logs from **replica 1**:

```shell
...
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager receiveGossip
INFO: Processing gossips...
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager receiveGossip
INFO: Operation added to log: 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager receiveGossip
INFO: Operation added to log: 89910600-5478-4cb9-87a5-26431dfe566c
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager execute
INFO: Will execute (add) 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager executeAux
INFO: Camera added to the stable value: Camera(camera)@(90.0,90.0)
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager execute
INFO: Executed (add) 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager execute
INFO: Will execute (add) 89910600-5478-4cb9-87a5-26431dfe566c
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager executeAux
INFO: Observation added to the stable value: Observation(car,AA11BB)@2020-05-01T16:42:37.826364 by camera
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager executeAux
INFO: Observation added to the stable value: Observation(person,12345)@2020-05-01T16:42:37.826876 by camera
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager executeAux
INFO: Observation added to the stable value: Observation(car,BB66CC)@2020-05-01T16:42:37.826917 by camera
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager execute
INFO: Executed (add) 89910600-5478-4cb9-87a5-26431dfe566c
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager cleanupLog
INFO: Cleaning up logs...
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager cleanupLog
INFO: Removed log: 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager cleanupLog
INFO: Removed log: 89910600-5478-4cb9-87a5-26431dfe566c
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager receiveGossip
INFO: Gossip processed.
...
```

Logs from **replica 2**:

```shell
...
INFO: Adding camera to log: 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
May 01, 2020 4:42:12 PM pt.tecnico.sauron.silo.domain.ReplicaManager execute
INFO: Will execute (add) 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
May 01, 2020 4:42:12 PM pt.tecnico.sauron.silo.domain.ReplicaManager executeAux
INFO: Camera added to the stable value: Camera(camera)@(90.0,90.0)
May 01, 2020 4:42:12 PM pt.tecnico.sauron.silo.domain.ReplicaManager execute
INFO: Executed (add) 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
May 01, 2020 4:42:15 PM pt.tecnico.sauron.silo.domain.ReplicaManager add
INFO: Adding observations to log89910600-5478-4cb9-87a5-26431dfe566c
May 01, 2020 4:42:15 PM pt.tecnico.sauron.silo.domain.ReplicaManager execute
INFO: Will execute (add) 89910600-5478-4cb9-87a5-26431dfe566c
May 01, 2020 4:42:15 PM pt.tecnico.sauron.silo.domain.ReplicaManager executeAux
INFO: Observation added to the stable value: Observation(car,AA11BB)@2020-05-01T16:42:15.228810 by camera
May 01, 2020 4:42:15 PM pt.tecnico.sauron.silo.domain.ReplicaManager executeAux
INFO: Observation added to the stable value: Observation(person,12345)@2020-05-01T16:42:15.229293 by camera
May 01, 2020 4:42:15 PM pt.tecnico.sauron.silo.domain.ReplicaManager executeAux
INFO: Observation added to the stable value: Observation(car,BB66CC)@2020-05-01T16:42:15.229336 by camera
May 01, 2020 4:42:15 PM pt.tecnico.sauron.silo.domain.ReplicaManager execute
INFO: Executed (add) 89910600-5478-4cb9-87a5-26431dfe566c
May 01, 2020 4:42:29 PM pt.tecnico.sauron.silo.domain.ReplicaManager receiveGossip
INFO: Processing gossips...
May 01, 2020 4:42:29 PM pt.tecnico.sauron.silo.domain.ReplicaManager receiveGossip
INFO: Gossip was empty... how dare you?
May 01, 2020 4:42:29 PM pt.tecnico.sauron.silo.domain.ReplicaManager cleanupLog
INFO: Cleaning up logs...
May 01, 2020 4:42:29 PM pt.tecnico.sauron.silo.domain.ReplicaManager receiveGossip
INFO: Gossip processed.
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.ReplicaManager lambda$update$0
INFO: UPDATE THREAD
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.Replica gossip
INFO: Gossipping to 0
May 01, 2020 4:42:37 PM pt.tecnico.sauron.silo.domain.Replica gossip
INFO: Gossiped to instance 0
...
```

> **Note**: even though the user (you!) assigns a 1-based replica identifier, internally we're using a 0-based replica identifier to ease our calculations. For example, the replica 1 will self-identify as replica 0, the replica 2 will self-identify as replica 1, and so on.

By default, gossips will be sent every 30 seconds, hence the delay you may see in the logs. The key keywords to look out on these logs are "Gossiping to n" (started gossiping), "Gossiped to instance 0" (finished gossiping), "Processing gossips..." (when receiving gossips) and "Gossip processed." (finished gossiping). Between the last two, you can see the operations that were executed.

For example, in this case, the eye connected randomly to the instance 2 (self-identified as 1) to which we sent our requests (one camera join and one report with three observations) that were received as follows (simplified logs):

```shell
INFO: Will execute (add) 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
INFO: Camera added to the stable value: Camera(camera)@(90.0,90.0)
INFO: Executed (add) 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
INFO: Adding observations to log 89910600-5478-4cb9-87a5-26431dfe566c
INFO: Will execute (add) 89910600-5478-4cb9-87a5-26431dfe566c
INFO: Observation added to the stable value: Observation(car,AA11BB)@2020-05-01T16:42:15.228810 by camera
INFO: Observation added to the stable value: Observation(person,12345)@2020-05-01T16:42:15.229293 by camera
INFO: Observation added to the stable value: Observation(car,BB66CC)@2020-05-01T16:42:15.229336 by camera
INFO: Executed (add) 89910600-5478-4cb9-87a5-26431dfe566c
```

The first camera join operation has the id `41d88c85-1510-4d7e-a5f0-6d5bf2100e94`, while the report operation has the id `89910600-5478-4cb9-87a5-26431dfe566c`. Then, when instance 2 gossips to instance 1, we can see it being added to the instance as the operations are new to the replica (simplified logs):

```shell
INFO: Processing gossips...
INFO: Operation added to log: 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
INFO: Operation added to log: 89910600-5478-4cb9-87a5-26431dfe566c
INFO: Will execute (add) 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
INFO: Camera added to the stable value: Camera(camera)@(90.0,90.0)
INFO: Executed (add) 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
INFO: Will execute (add) 89910600-5478-4cb9-87a5-26431dfe566c
INFO: Observation added to the stable value: Observation(car,AA11BB)@2020-05-01T16:42:37.826364 by camera
INFO: Observation added to the stable value: Observation(person,12345)@2020-05-01T16:42:37.826876 by camera
INFO: Observation added to the stable value: Observation(car,BB66CC)@2020-05-01T16:42:37.826917 by camera
INFO: Executed (add) 89910600-5478-4cb9-87a5-26431dfe566c
INFO: Cleaning up logs...
INFO: Removed log: 41d88c85-1510-4d7e-a5f0-6d5bf2100e94
INFO: Removed log: 89910600-5478-4cb9-87a5-26431dfe566c
```

Here, we see that when the instance 1 received both operations ("Operation added...") identified by the ids above and then they are executed as soon as possible. You can also visualize that we cleanup the logs, which is something we advise you to take a look at in our [report](../report/README.md).

## Run Tests

Open a terminal window and execute the following commands:

```shell
cd /path/to/A46-Sauron
mvn clean install -DskipTests
cd silo-server
mvn compile exec:java -Dinstance=1 -DnumberServers=1 -DstorageFile="ignore"
```

Now, you have successfully started one Sauron replica that will work solo.

> **Note**: The flag `-DstorageFile="ignore"` is used to avoid using persistent memory and only use in-memory data. This is useful for tests to avoid recreating data files and/or loading previous information.

In another window:

```shell
cd /path/to/A46-Sauron
mvn clean install
```

And now you can see the tests running. Just wait for them to run successfully.
