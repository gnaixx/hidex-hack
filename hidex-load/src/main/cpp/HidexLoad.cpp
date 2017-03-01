//
// Created by 薛祥清 on 2017/2/20.
//

#include <jni.h>
#include "HidexLoad.h"


static JNINativeMethod gMethods[] = {
        {"custOpenDexFile", "(Landroid/content/Context;[BI)Ljava/lang/Object;", (void *) custOpenDexFile}
};

void (*openDexFile)(const u4 *args, union JValue *pResult) = NULL;
int isDalvik;
int sdkVersion;
char * gFilePath;

jclass BuildVersion;
jclass System;
jmethodID System_getProperty_mID;
jclass SystemProperties;
jmethodID SystemProperties_get_mID;
jclass DexFile;
jmethodID DexFile_openDexFileNative_mID;

extern "C"
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("Start JNI_OnLoad()");

    JNIEnv *env = NULL;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return result;
    }

    int status = initLoad(env); //初始化工作
    if(status == FAIL){
        LOGE("Init load env failed !!!");
    }

    jclass clazz = env->FindClass("cc/gnaixx/hidex_load/tool/NativeTool");
    if (clazz == NULL) {
        LOGE("Find %s failed !!!", "cc/gnaixx/hidex_load/tool/NativeTool");
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, NELEM(gMethods)) != JNI_OK) {
        LOGE("Register natives failed !!!");
    }
    return JNI_VERSION_1_6;
}

int initLoad(JNIEnv *env) {
    LOGI("Start initLoad()");

    if (!dFindClass(env, &BuildVersion, "android/os/Build$VERSION")) {
        return FAIL;
    }
    jfieldID fieldID = env->GetStaticFieldID(BuildVersion, "SDK_INT", "I");
    sdkVersion = env->GetStaticIntField(BuildVersion, fieldID);
    LOGI("sdk version:%d", sdkVersion);

    if (!dFindClass(env, &DexFile, "dalvik/system/DexFile")) {
        return FAIL;
    }
    if(sdkVersion > 19 && sdkVersion <= 22){
        //long openDexFileNative(String sourceName, String outputName, int flags);
        DexFile_openDexFileNative_mID = env->GetStaticMethodID(DexFile, "openDexFile", "(Ljava/lang/String;Ljava/lang/String;I)J");
    }else{
        //Object openDexFileNative(String sourceName, String outputName, int flags);
        DexFile_openDexFileNative_mID = env->GetStaticMethodID(DexFile, "openDexFile", "(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/Object;");
    }

    //判断是否为dalvik
    LOGI("Start System");
    if(!dFindClass(env, &System, "java/lang/System")){
        return FAIL;
    }
    System_getProperty_mID = env->GetStaticMethodID(System, "getProperty", "(Ljava/lang/String;)Ljava/lang/String;");

    LOGI("Start SystemProperties");
    if (!dFindClass(env, &SystemProperties, "android/os/SystemProperties")) {
        return FAIL;
    }
    SystemProperties_get_mID = env->GetStaticMethodID(SystemProperties, "get", "(Ljava/lang/String;)Ljava/lang/String;");

    //获取 vm 类型
    jstring vmNameKey = env->NewStringUTF("java.vm.name");
    jstring jvmName = (jstring) env->CallStaticObjectMethod(System, System_getProperty_mID, vmNameKey);
    const char * vmName= env->GetStringUTFChars(jvmName, NULL);
    LOGI("vmName:%s", vmName);
    env->ReleaseStringUTFChars(jvmName, vmName);

    // persist.sys.dalvik.vm.lib
    // persist.sys.dalvik.vm.lib.2
    jstring vmLibKey1 = env->NewStringUTF("persist.sys.dalvik.vm.lib");
    jstring vmLibKey2 = env->NewStringUTF("persist.sys.dalvik.vm.lib.2");
    jstring jvmLib = (jstring) env->CallStaticObjectMethod(SystemProperties, SystemProperties_get_mID, vmLibKey1);
    const char *vmLib = env->GetStringUTFChars(jvmLib, NULL);
    if(strcmp(vmLib, "") == 0){
        jvmLib = (jstring) env->CallStaticObjectMethod(SystemProperties, SystemProperties_get_mID, vmLibKey2);
        vmLib = env->GetStringUTFChars(jvmLib, NULL);
    }
    LOGI("vmLib:%s", vmLib);

    //获取 vm 版本
    jstring vmVersionKey = env->NewStringUTF("java.vm.version");
    jstring jvmVersion = (jstring) env->CallStaticObjectMethod(System, System_getProperty_mID, vmVersionKey);
    const char *vmVersion = env->GetStringUTFChars(jvmVersion, NULL);
    double vmVersionInt = atof(vmVersion);
    if (vmVersionInt > 2) {
        isDalvik = 0;
    } else {
        isDalvik = 1;
    }
    LOGI("vmVersion:%s, vmVersionInt:%f, isDalvik:%d", vmVersion, vmVersionInt, isDalvik);
    env->ReleaseStringUTFChars(jvmVersion, vmVersion);
    env->ReleaseStringUTFChars(jvmLib, vmLib);
    return SUCC;
}

jobject custOpenDexFile(JNIEnv *env, jclass, jobject ctx, jbyteArray jDexBytes, jint jDexLen) {
    LOGD("start custOpenDexFile()");

    jobject cookie;
    jbyte *dexBytes = env->GetByteArrayElements(jDexBytes, NULL);
    int dexLen = (int) jDexLen;
    if(isDalvik){
        cookie = (jobject) dexLoadDvm(env, (char *) dexBytes, dexLen);
    }else{
        cookie = dexLoadArt(env, ctx, (char *) dexBytes, dexLen);
    }
    env->ReleaseByteArrayElements(jDexBytes, dexBytes, 0);
    return cookie;
}

int dexLoadDvm(JNIEnv *env, char * dexBytes, int dexLen){
    LOGD("start dexLoadDvm()");
    gotOpenDexFile(); //查找openDexFile函数

    /*结构体内最大成员对齐 sizeof(ArrayObject) = 24*/
    char *fileContent = (char *) malloc(sizeof(ArrayObject) + dexLen); //所有指针为4字节
    ArrayObject *fileContentObj = (ArrayObject *) fileContent;
    fileContentObj->length = (u4) dexLen;
    memcpy(fileContentObj->contents, dexBytes, dexLen);
    //memcpy(fileContent + 16, dexBytes, dexLen); //因为按照最大字节对齐 4+4, 4+()
    LOGD("dexLen:%d", fileContentObj->length);

    //u4 args[] = {(u4) fileContentObj};
    u4 *args = (u4 *) &fileContentObj; // args => fileContent
    union JValue pResult;
    int cookie;
    if (openDexFile != NULL) {
        openDexFile(args, &pResult);
    } else {
        cookie = -1;
    }
    cookie = (u8) pResult.l;
    LOGD("openDexFile cookie:%d", cookie);
    free(fileContent);
    return cookie;
}

void gotOpenDexFile() {
    LOGI("Start gotOpenDexFile()");

    void *dldvm = dlopen("libdvm.so", RTLD_LAZY); //获取libdvm句柄
    JNINativeMethod *natvieMethods = (JNINativeMethod *) dlsym(dldvm, "dvm_dalvik_system_DexFile"); //获取注册函数
    int isExist = lookup(natvieMethods, "openDexFile", "([B)I", &openDexFile); //查找是否包含 openDexFile 函数
    if (isExist == 1) {
        LOGD("openDexFile method found !!!");
    } else {
        LOGD("openDexFile method does not found !!!");
    }
}

int lookup(JNINativeMethod *methods, const char *name, const char *signature, void (**fnPtr)(const u4 *, union JValue *)) {
    LOGI("Start gotOpenDexFile()");

    if (methods == NULL) {
        LOGD("%s is NULL", "dvm_dalvik_system_DexFile");
        return 0;
    }
    int i = 0;
    while (methods[i].name != NULL) {
        if (strcmp(name, methods[i].name) == 0 && strcmp(signature, methods[i].signature) == 0) { //比较函数名 函数签名
            *fnPtr = (void (*)(const u4 *, union JValue *)) methods[i].fnPtr;
            LOGD("lookup index:%d, name:%s", i, name);
            return 1;
        }
        i++;
    }
}

jobject dexLoadArt(JNIEnv *env, jobject ctx, char* dexBytes, int dexLen){
    LOGI("Start dexLoadArt()");

    //getFilesDir
    jclass Context = env->GetObjectClass(ctx);
    jmethodID getFilesDir_mID = env->GetMethodID(Context, "getFilesDir", "()Ljava/io/File;");
    jobject fileObj = env->CallObjectMethod(ctx, getFilesDir_mID); //获取/data/data/packageName/files
    jclass File = env->GetObjectClass(fileObj);
    jmethodID File_getAbsolutePath_mID = env->GetMethodID(File, "getAbsolutePath", "()Ljava/lang/String;");
    jstring jAbsolutePath = (jstring) env->CallObjectMethod(fileObj, File_getAbsolutePath_mID);
    gFilePath = jstringToChar(env, jAbsolutePath);
    LOGD("global files path: %s", gFilePath);

    char dexPath[256];
    sprintf(dexPath, "%s/%s", gFilePath, "hidex_load.dex");
    FILE *fp;
    fp = fopen(dexPath, "w");
    fwrite(dexBytes, dexLen, 1, fp);
    fclose(fp);
    LOGD("Write dex file success");

    jstring jdexPath = env->NewStringUTF(dexPath);
    jobject cookie;
    if(sdkVersion > 19 && sdkVersion <= 22){
        cookie = (jobject) env->CallStaticLongMethod(DexFile, DexFile_openDexFileNative_mID, jdexPath, 0, 0);
    }else{
        cookie = (jobject) env->CallStaticObjectMethod(DexFile, DexFile_openDexFileNative_mID, jdexPath, 0, 0);
    }
    return cookie;
}





