# Sauron Tests

Open a Terminal window:

```shell
cd /path/to/A46-Sauron
mvn clean install -DskipTests
cd silo-server
mvn compile exec:java
```

In another window:

```shell
cd /path/to/A46-Sauron
mvn clean install
```

And now you can see the tests running!

---

For more information about how each application works (eye, spotter),
please take a look at their READMEs!


---


```
mvn compile exec:java -Dinstance=1 -DnumberServers=1
```