# Sauron

Distributed Systems 2019-2020, 2nd semester project


## Authors

**Group A46**

### Code identification

In all the source files (including POMs), please replace __A46__ with your group identifier.  
The group identifier is composed by Campus - A (Alameda) or T (Tagus) - and number - always with two digits.  
This change is important for code dependency management, to make sure that your code runs using the correct components and not someone else's.

### Team members

| Number | Name              | User                             | Email                                      |
| -------|-------------------|----------------------------------| ------------------------------------------ |
| 89455  | Henrique Dias     | <https://github.com/hacdias>     | <mailto:henrique.dias@tecnico.ulisboa.pt>  |
| 89466  | Isabel Soares     | <https://github.com/isabelSoares>| <mailto:isabel.r.soares@tecnico.ulisboa.pt>|
| 89535  | Rodrigo Sousa     | <https://github.com/Sousa99>     | <mailto:rodrigo.b.sousa@tecnico.ulisboa.pt>|

### Task leaders

| Task set | To-Do                         | Leader              |
| ---------|-------------------------------| --------------------|
| core     | protocol buffers, silo-client | _(whole team)_      |
| T1       | cam_join, cam_info, eye       | _Isabel Soares_     |
| T2       | report, spotter               | _Henrique Dias_     |
| T3       | track, trackMatch, trace      | _Rodrigo Sousa_     |
| T4       | test T1                       | _Henrique Dias_     |
| T5       | test T2                       | _Rodrigo Sousa_     |
| T6       | test T3                       | _Isabel Soares_     |


## Getting Started

The overall system is composed of multiple modules.
The main server is the _silo_.
The clients are the _eye_ and _spotter_.

See the [project statement](https://github.com/tecnico-distsys/Sauron/blob/master/README.md) for a full description of the domain and the system.

### Prerequisites

Java Developer Kit 11 is required running on Linux, Windows or Mac.
Maven 3 is also required.

To confirm that you have them installed, open a terminal and type:

```
javac -version

mvn -version
```

### Installing

To compile and install all modules:

```
mvn clean install -DskipTests
```

The integration tests are skipped because they require the servers to be running.


## Built With

* [Maven](https://maven.apache.org/) - Build Tool and Dependency Management
* [gRPC](https://grpc.io/) - RPC framework


## Versioning

We use [SemVer](http://semver.org/) for versioning. 
