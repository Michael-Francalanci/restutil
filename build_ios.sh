#!/bin/bash

./gradlew clean install
cd example/iosjava
./gradlew clean && ./gradlew j2objcBuild
cd ..
cd ..
