#!/usr/bin/env bash
function build_dex(){
    echo "build and dx dex"
    ./gradlew -a -p hidex-samp assembleRelease
    rm ./output/sampl.*
    cp ./hidex-samp/build/intermediates/bundles/release/classes.jar ./output/sampl.jar
    #cp jar
    cd output
    dx --dex --output=sampl.dex sampl.jar
    cd ..
}

function hack_dex(){
    echo "hack dex"
    ./gradlew -a -p hidex-tool release
    java -jar hidex-tool/build/libs/hidex-tool.jar
}
function push_dex(){
    echo "push sampl.dex"
    cd output
    adb push sampl.dex /data/local/tmp/
    cd ..
}

function reverse_dex(){
    echo "dex to jar"
    cd output
    d2j-dex2jar.sh -f sampl.dex
    d2j-dex2jar.sh -f hidex.dex
    cd ..
}

function copy_dex(){
    echo "copy dex"
    assets_dir="hidex-demo/src/main/assets"
    if [[ ! -d ${assets_dir} ]]
    then
        mkdir -p ${assets_dir}
    fi
    rm ${assets_dir}/*.dex
    cp -r output/*.dex ${assets_dir}
}


if [[ $1 == 1 ]]
then
    push_dex
elif [[ $1 == 2 ]]
then
    reverse_dex
elif [[ $1 == 3 ]]
then
    copy_dex
else
    build_dex
    hack_dex
    reverse_dex
    copy_dex
fi