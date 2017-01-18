#include <jni.h>
#include <string>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, "HIDEX", __VA_ARGS__)
#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif


JNIEnv static *g_env;
jclass static native_class;

using namespace std;

extern "C"
int redex(JNIEnv* env, jclass clazz, jobject assetManager, jstring jname, jstring jpath) {
    LOGD("start redex()");
    const char* cname = env->GetStringUTFChars(jname, NULL);
    const char* cpath = env->GetStringUTFChars(jpath, NULL);

    if(cpath == NULL || cname == NULL){
        LOGD("path or name is NULL");
        return 1;
    }
    LOGD("path:%s, name:%s", cpath, cname);

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(mgr == NULL){
        LOGD("AAssetManager is NULL");
        return 2;
    }
    AAsset* asset = AAssetManager_open(mgr, cname, AASSET_MODE_UNKNOWN);
    if(asset == NULL){
        LOGD("AAsset is NULL");
        return 3;
    }
    long filelen = AAsset_getLength(asset);
    char* buffer = (char*)malloc(sizeof(char) * filelen);
    if(AAsset_read(asset, buffer, filelen) < 0){
        LOGD("AAsset read failed");
        return 4;
    }
    AAsset_close(asset);
    LOGD("AAsset read success");



    env->ReleaseStringUTFChars(jname, cname);
    env->ReleaseStringUTFChars(jpath, cpath);
    LOGD("stop redex()");
    return 0;
}

//注册
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNINativeMethod methods[] = {
            {"redex", "(Landroid/content/res/AssetManager;Ljava/lang/String;Ljava/lang/String;)I", (void *) redex}
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
