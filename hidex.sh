#!/usr/bin/env bash

#####################################
#   编译环境监测
#param1 输出路径
#####################################
function check_env(){
    #jdk
    hash java 2>/dev/null || { echo >&2 "java not installed.  Aborting."; exit 1;}
    #dx
    hash dx 2>/dev/null || { echo >&2 "dx not installed.  Aborting."; exit 1;}
    #output
    if [ ! -d $1 ]
    then
        mkdir $1
    fi
}

#####################################
#   编译module
#param1 编译module名
#param2 生成文件名
#param3 存放路径
#####################################
function build_module(){
    echo -e "\nBuild $1 default release"
    ./gradlew -q -p $1 assembleRelease
    if [ -f  $1/build/intermediates/bundles/release/classes.jar ]
    then
        echo "Copy classes.jar to $3/$2"
        cp $1/build/intermediates/bundles/release/classes.jar $3/$2
    fi
    if [ -f $1/build/libs/$1.jar ]
    then
        echo "Copy $1.jar to $3/$2"
        cp -r $1/build/libs/ $3/
    fi
}

#####################################
#   jar to dex
#param1 输入文件
#param2 生成文件名
#param3 存放路径
#####################################
function jar2dex(){
    echo -e "\nTransition $3/$1 to $3/$2"
    dx --dex --output=$3/$2 $3/$1
}

#####################################
#   dex to jar
#param1 输入文件
#param2 生成文件名
#param3 存放路径
#####################################
function dex2jar(){
    if [ -f $3/$1 ]
    then
        echo -e "\nTransition $3/$1 to $3/$2"
        d2j-dex2jar.sh -f $3/$1 -o $3/$2
    else
        echo -e "\nDex $3/$1 not exist"
    fi
}

#####################################
#   dex to jar
#param1 操作
#param2 输入文件名
#param3 工具名
#param4 目标目录
#param5 配置文件
#####################################
function hack_dex(){
    echo -e "\n$1 $2 to $1-$2"
    java -jar $4/$3 $1 $4/$2 $4/$5
}

#####################################
#    copy dex to hidex-demo
#####################################
function copy_dex(){
    echo -e "\nCopy dex to hidex-demo"
    assets_dir="hidex-demo/src/main/assets"
    if [[ ! -d ${assets_dir} ]]
    then
        mkdir -p ${assets_dir}
    fi
    rm ${assets_dir}/*.dex
    cp -r output/*.dex ${assets_dir}
    rm ${assets_dir}/redex-**.dex
}

#####################################
#   清空编译
#####################################
function clean_output(){
    echo -e "\nClean module and output"
    ./gradlew -q clean
    rm output/*.dex
    rm output/*.jar
}


echo "################## start ##################"
#module
module_demo="hidex-demo"
module_libs="hidex-libs"
module_samp="hidex-samp"
module_tool="hidex-tool"
#source file
source_samp_jar="samp.jar"
source_samp_dex="samp.dex"
target_tool_jar="hidex-tool.jar"
#hidex file
hidex_samp_dex="hidex-samp.dex"
redex_samp_dex="redex-hidex-samp.dex"
#reverse file
reverse_source_samp_jar="reverse-samp.jar"
reverse_hidex_samp_jar="reverse-hidex-samp.jar"
reverse_redex_samp_jar="reverse-redex-hidex-samp.jar"
#action
action_hidex="hidex"
action_redex="redex"
#config
hidex_conf="hidex.conf"

#output
output_dir="output"
#环境检测
check_env ${output_dir}

case $1 in
    clean)
        clean_output
    ;;
    build)
        build_module ${module_samp} ${source_samp_jar} ${output_dir}
        build_module ${module_tool} ${target_tool_jar} ${output_dir}
        jar2dex ${source_samp_jar}  ${source_samp_dex} ${output_dir}
    ;;
    hidex)
        hack_dex ${action_hidex} ${source_samp_dex} ${target_tool_jar} ${output_dir} ${hidex_conf}
    ;;
    redex)
        hack_dex ${action_redex} ${hidex_samp_dex} ${target_tool_jar} ${output_dir}
    ;;
    d2j)
        dex2jar ${source_samp_dex} ${reverse_source_samp_jar} ${output_dir}
        dex2jar ${hidex_samp_dex} ${reverse_hidex_samp_jar} ${output_dir}
        dex2jar ${redex_samp_dex} ${reverse_redex_samp_jar} ${output_dir}
    ;;
    copy)
        copy_dex
    ;;
    *)
        echo "Usage:xxx {clean|build|hidex|redex|d2j|copy}"
    ;;
esac
echo -e "\n##################  end  ##################"