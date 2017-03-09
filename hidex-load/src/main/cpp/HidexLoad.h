//
// Created by 薛祥清 on 2017/2/20.
//

#ifndef HIDEX_HACK_HIDEX_LOAD_LIB_H
#define HIDEX_HACK_HIDEX_LOAD_LIB_H

#include "Common.h"
#include "HidexLoad.h"


#define ANDROID_2_1   7  //2.1
#define ANDROID_2_2   8  //2.2
#define ANDROID_2_3   9  //2.3
#define ANDROID_2_3_3 10 //2.3.3
#define ANDROID_3_0   11 //3.0
#define ANDROID_3_1   12 //3.1
#define ANDROID_3_2   13 //3.2
#define ANDROID_4_0   14 //4.0
#define ANDROID_4_0_3 15 //4.0.3
#define ANDROID_4_1   16 //4.1
#define ANDROID_4_2   17 //4.2
#define ANDROID_4_3   18 //4.3
#define ANDROID_4_4   19 //4.4
#define ANDROID_5_0   21 //5.0
#define ANDROID_5_1   22 //5.1
#define ANDROID_6_0   23 //6.0
#define ANDROID_7_0   24 //7.0
#define ANDROID_7_1   25 //7.1

#define DEX_NAME "hidex-load.dex"

jint JNI_OnLoad(JavaVM *, void *); //注册函数

jobject custOpenDexFile(JNIEnv *, jclass, jobject, jbyteArray, jint);

int initLoad(JNIEnv *, jobject);

jobject dexLoadDvm(JNIEnv *, char *, int);

jobject dexLoadArt(JNIEnv *, jobject, char *, int);

int lookup(JNINativeMethod *, const char *, const char *, void (**)(const u4 *, union JValue *));

void gotOpenDexFile();

void writeDex(char *, int);

void dexDecode(char *, int);

#endif //HIDEX_HACK_HIDEX_LOAD_LIB_H
