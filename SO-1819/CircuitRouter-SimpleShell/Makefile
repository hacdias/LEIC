# Makefile, versao 1
# Sistemas Operativos, DEI/IST/ULisboa 2018-19

SOURCES = CircuitRouter-SimpleShell.c
SOURCES+= ../lib/commandlinereader.c ../lib/list.o
OBJS = $(SOURCES:%.c=%.o)
CC   = gcc
CFLAGS =-Wall -std=gnu99 -I../
LDFLAGS=-lm
TARGET = CircuitRouter-SimpleShell

all: $(TARGET)

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) $^ -o $(TARGET) $(LDFLAGS) 

CircuitRouter-SimpleShell.o: CircuitRouter-SimpleShell.c ../lib/commandlinereader.h ../lib/list.h
../lib/commandlinereader.o: ../lib/commandlinereader.c ../lib/commandlinereader.h
../lib/list.o: ../lib/list.c ../lib/list.h

$(OBJS):
	$(CC) $(CFLAGS) -c -o $@ $<

clean:
	@echo Cleaning...
	rm -f $(OBJS) $(TARGET)
