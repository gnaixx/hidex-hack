//
// Created by 薛祥清 on 2017/3/1.
//

#include "Common.h"

int dFindClass(JNIEnv *env, jclass *ptr, const char *className) {
    LOGI("Start dFindClass : %s", className);
    jobject globalRef;
    jclass clazz = env->FindClass(className);
    if (clazz != NULL) {
        globalRef = env->NewGlobalRef(clazz); //创建全局变量
        *ptr = (jclass) globalRef;
        return SUCC;
    } else {
        LOGE("Find %s failed !!!", className);
        return FAIL;
    }
}

char *jstringToChar(JNIEnv *env, jstring jstr) {
    const char *cstr = env->GetStringUTFChars(jstr, NULL);
    int len = strlen(cstr);
    char *result = (char *) calloc(len + 1, sizeof(char));
    strcpy(result, cstr);
    env->ReleaseStringUTFChars(jstr, cstr);
    return result;
}