#include <jni.h>
#include <string>
#include <android/log.h>

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, "HIDEX", __VA_ARGS__)
#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif

using namespace std;

extern "C"
void redex(JNIEnv *env, jobject /* this */) {
    LOGD("start redex()");



    LOGD("stop redex()");
}

JNIEnv static *g_env;
jclass static native_class;

//注册
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNINativeMethod methods[] = {
            {"redex", "()V", (void *) redex}
    };

    if (JNI_OK != vm->GetEnv((void **) &g_env, JNI_VERSION_1_6)) {
        return -1;
    }
    LOGD("JNI_OnLoad()");
    native_class = g_env->FindClass("cc/gnaixx/hidex_libs/tools/NativeHelper");
    if (JNI_OK == g_env->RegisterNatives(native_class, methods, NELEM(methods))) {
        LOGD("RegisterNatives() --> success");
        return JNI_VERSION_1_6;
    } else {
        LOGD("RegisterNatives() --> failed");
        return -1;
    }
}

//注销
JNIEXPORT void JNI_OnUnLoad(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnUnLoad()");
    g_env->UnregisterNatives(native_class);
    LOGD("UnregisterNatives()");
}
