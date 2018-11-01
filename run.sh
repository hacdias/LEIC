#!/bin/bash

java -Dimport=./sth-app/people.import \
  -cp ./sth-app/sth-app.jar:./sth-core/sth-core.jar:/usr/share/java/po-uuilib.jar \
  sth.app.App
