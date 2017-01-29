#!/usr/bin/env bash

cd bantamweight
apt-get update
apt-get --assume-yes install openjdk-8-jdk
./gradlew clean test

