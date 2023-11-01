#!/bin/sh

# default
# assumes script inside test folder and `og` in ../
testfolder="$(dirname $0)"

lightred="\033[31m"
lightgreen="\033[32;m"
red="\033[31;1m"
green="\033[32;1m"
reset="\033[0m"

if ! [ -d results ]
then
  mkdir results
fi

total=0
succs=0
n_tests=$(ls -1 $testfolder/expected/*.out | wc -l)
for file in $testfolder/*.og
do
  input=$file
  raw=$(basename $input | cut -f 1 -d'.')
  asm=$testfolder/$raw.asm
  o=$testfolder/$raw.o
  exe=$testfolder/$raw.exe
  expected=$testfolder/expected/$raw.out
  out=results/$raw.outhyp
  if ! [ -r $input ]
  then
    printf '%b' "$0: cannot read file "$input"${red}aborting$reset\n"
    exit 1
  fi
  if ! [ -r $expected ]
  then
    printf '%b' "$0: cannot read file $expected${red}aborting$reset\n"
    exit 1
  fi

  total=$((total+1))

  ../og $input && yasm -felf32 $asm && ld -m elf_i386 -o $exe $o -lrts && $exe > $out

  go=0
  if [ -f $out ]
  then
	diff -b $expected $out &> /dev/null
	if [ $? -eq 0 ]
	then
	  go=1
	fi
  fi

  if [ $go -eq 1 ]
  then
    printf '%b' "Test $raw -- ${green}OK$reset\n"
	succs=$((succs+1))
  else
    printf '%b' "Test $raw -- ${red}FAILED\n"
    printf '%b' "$reset $lightred"
    diff -b $expected $out
    printf '%b' "$reset"
  fi
  if [ -f $asm ]
  then
    rm $asm
  fi
  if [ -f $o ]
  then
    rm $o
  fi
  if [ -f $exe ]
  then
    rm $exe
  fi
done

if [ $succs -eq $n_tests ]
then
  printf '%b' "Score$green $succs/$n_tests\n"
else
  printf '%b' "Score$red $succs/$n_tests\n"
fi

printf '%b' "$reset"
echo "$0: finished"