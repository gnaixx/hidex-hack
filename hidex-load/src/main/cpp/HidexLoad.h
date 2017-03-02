//
// Created by 薛祥清 on 2017/2/20.
//

#ifndef HIDEX_HACK_HIDEX_LOAD_LIB_H
#define HIDEX_HACK_HIDEX_LOAD_LIB_H

#include "Common.h"
#include "HidexLoad.h"


#define Gingerbread   10 //2.3
#define ICS           14 //4.0
#define JellyBean     18 //4.3
#define KitKat        19 //4.4
#define Lollipop_0    21 //5.0
#define Lollipop_1    22 //5.1
#define Marshmallow   23 //6.0

#define DEX_NAME "hidex-load.dex"

jint JNI_OnLoad(JavaVM *, void *); //注册函数

jobject custOpenDexFile(JNIEnv *, jclass, jobject, jbyteArray, jint);

int initLoad(JNIEnv *);

void initLoadOfPath(JNIEnv *, jobject);

jobject dexLoadDvm(JNIEnv *, char *, int);

jobject dexLoadArt(JNIEnv *, jobject, char *, int);

int lookup(JNINativeMethod *, const char *, const char *, void (**)(const u4 *, union JValue *));

void gotOpenDexFile();

void writeDex(char *, int);

void dexDecode(char *, int);

#endif //HIDEX_HACK_HIDEX_LOAD_LIB_H
