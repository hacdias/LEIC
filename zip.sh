#!/bin/bash

rm proj.zip
zip proj.zip **/*.c **/*.h **/Makefile results/* Makefile doTest.sh
