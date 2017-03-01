//
// Created by 薛祥清 on 2017/2/20.
//

#ifndef HIDEX_HACK_HIDEX_LOAD_LIB_H
#define HIDEX_HACK_HIDEX_LOAD_LIB_H

#include "Common.h"
#include "HidexLoad.h"

jint JNI_OnLoad(JavaVM *, void *); //注册函数

int initLoad(JNIEnv *);

jobject custOpenDexFile(JNIEnv*, jclass, jobject, jbyteArray, jint);

int dexLoadDvm(JNIEnv *, char *, int);

jobject dexLoadArt(JNIEnv*, jobject, char *, int);

int lookup(JNINativeMethod *, const char *, const char *, void (**)(const u4*, union JValue*));

void gotOpenDexFile();

#endif //HIDEX_HACK_HIDEX_LOAD_LIB_H
