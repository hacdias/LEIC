all:
	gcc -o fibiter -O3 fibiter.c
	gcc -o fibrec -O3 fibrec.c
	javac fibiter.java
	javac fibrec.java
	# dx --dex --no-strict --output=classes.dex . # android
	# jar fib.jar classes.dex # android
	jar -cf fib.jar *.class

fib12:: all
	time python fibiter.py 12
	time python fibrec.py 12
	time java -jar fib.jar fibiter 12
	time java -jar fib.jar fibrec 12
	time ./fibiter 12
	time ./fibrec 12

clean::
	rm -f fibiter fibrec *.class *.jar classes.dex
