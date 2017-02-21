//
// Created by 薛祥清 on 2017/2/20.
//

#include "hidex-load-lib.h"


JNIEnv static *g_env;
jclass static native_class;
JNINativeMethod *dvm_dalvik_system_DexFile;

extern "C"

jint custOpenDexFile(JNIEnv *env, jobject, jbyteArray jDexBytes, jint jDexLen){

}

//注册
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    searchMethodFromSo();

    JNINativeMethod methods[] = {
            {"custOpenDexFile", "([BI)I", (void *) custOpenDexFile}
    };

    if (JNI_OK != vm->GetEnv((void **) &g_env, JNI_VERSION_1_6)) {
        return -1;
    }
    LOGD("JNI_OnLoad()");
    native_class = g_env->FindClass("cc/gnaixx/dexloader/NativeTool");
    if (JNI_OK == g_env->RegisterNatives(native_class, methods, NELEM(methods))) {
        LOGD("Register success");
        return JNI_VERSION_1_6;
    } else {
        LOGD("Register failed");
        return -1;
    }
}

//注销
JNIEXPORT void JNI_OnUnLoad(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnUnLoad()");
    g_env->UnregisterNatives(native_class);
    LOGD("UnregisterNatives()");
}