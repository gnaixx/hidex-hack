//
// Created by 薛祥清 on 2017/2/21.
//

#ifndef HIDEX_HACK_COMMON_H_H
#define HIDEX_HACK_COMMON_H_H

#include <jni.h>
#include <dlfcn.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdint.h>
#include <string.h>
#include <android/log.h>

//日志
#define TAG "HIDEX-LOAD-NDK"
#define LOGI(...)  __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

//计算 JNINativeMethod 数组大小
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

//success or failed
#define SUCC 1
#define FAIL 0

//数组大小
#define MAX_NAME_LEN 256
#define MAX_BUFFER_LEN 1024

//直接定义小端编码
#define HAVE_LITTLE_ENDIAN

typedef uint8_t u1;
typedef uint16_t u2;
typedef uint32_t u4;
typedef uint64_t u8;
typedef int8_t s1;
typedef int16_t s2;
typedef int32_t s4;
typedef int64_t s8;

union JValue {
#if defined(HAVE_LITTLE_ENDIAN)
    u1 z;
    s1 b;
    u2 c;
    s2 s;
    s4 i;
    s8 j;
    float f;
    double d;
    void *l;
#endif
#if defined(HAVE_BIG_ENDIAN)
    struct {
        u1  _z[3];
        u1  z;
    };
    struct {
        s1  _b[3];
        s1  b;
    };
    struct {
        u2  _c;
        u2  c;
    };
    struct {
        s2  _s;
        s2  s;
    };
    s4      i;
    s8      j;
    float   f;
    double  d;
    void*   l;
#endif
};

struct Object { //这里数据暂时没用
    void *clazz;
    u4 lock;
};
struct ArrayObject : Object {
    u4 length;     //dex 长度
    u8 contents[1];//dex 数据第一个地址
};

typedef struct {
    void *clazz;
    u4 lock;
    u4 length;
    u8 contents[1];
} ArrayObject1;


int dFindClass(JNIEnv *, jclass *, const char *);

char *jstringToChar(JNIEnv *, jstring);

#endif //HIDEX_HACK_COMMON_H_H
