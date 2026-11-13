#!/bin/sh
command -v gradle >/dev/null || { echo "Install JDK 21 and Gradle 8.8+ first"; exit 1; }
gradle build && echo "Done: build/libs/koenigsegg-cars-2.0.0.jar"
