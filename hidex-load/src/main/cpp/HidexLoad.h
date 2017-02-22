//
// Created by 薛祥清 on 2017/2/20.
//

#ifndef HIDEX_HACK_HIDEX_LOAD_LIB_H
#define HIDEX_HACK_HIDEX_LOAD_LIB_H

#include <jni.h>
#include <android/log.h>
#include <dlfcn.h>
#include "Common.h"

#define LIB_DVM "libdvm.so"
#define LIB_ART "libart.so"
#define JNI_NATIVE_METHOD "dvm_dalvik_system_DexFile"
#define METHOD_NAME "openDexFile"
#define METHOD_SIGNATURE "([B)I"

jint custOpenDexFile(JNIEnv*, jobject, jbyteArray, jint);

int lookup(JNINativeMethod *, const char *, const char *, void (**)(const u4*, union JValue*));

void gotOpenDexFile();



#endif //HIDEX_HACK_HIDEX_LOAD_LIB_H
