//
// Created by 薛祥清 on 2017/2/20.
//

#ifndef HIDEX_HACK_HIDEX_LOAD_LIB_H
#define HIDEX_HACK_HIDEX_LOAD_LIB_H

#include <jni.h>
#include <android/log.h>

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, "HIDEX_NDK", __VA_ARGS__)
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))


#endif //HIDEX_HACK_HIDEX_LOAD_LIB_H
