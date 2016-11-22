#!/usr/bin/env bash

#build jar
./gradlew -a -p hidex-samp assembleRelease

rm samp.*
#cp jar
cp ./hidex-samp/build/intermediates/bundles/release/classes.jar ./output/samp.jar
cd ./output/
dx --dex --output=samp.dex samp.jar
adb push samp.dex /data/local/tmp/
cd ..