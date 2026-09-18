#!/bin/sh
cd "$(dirname "$0")"
mkdir -p out
javac -d out src/*.java || exit 1
java -cp out StayNestApp
