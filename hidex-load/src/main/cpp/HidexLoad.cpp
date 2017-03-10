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
char *g_filePath;
char dexPath[256];

jclass BuildVersion;

jclass Context;
jmethodID Context_getFilesDir_mID;
jmethodID Context_getClassLoader_mID;

jclass File;
jmethodID File_getAbsolutePath_mID;

jclass System;
jmethodID System_getProperty_mID;

jclass SystemProperties;
jmethodID SystemProperties_get_mID;

jclass DexFile;
jmethodID DexFile_openDexFileNative_mID;

jclass  ClassLoader;
jclass DexPathList$Element;

jclass Integer;
jmethodID Integer_valueOf_mID;

jclass Long;
jmethodID Long_valueOf_mID;


extern "C"
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    LOGI("Start JNI_OnLoad()");

    JNIEnv *env = NULL;
    jint result = -1;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return result;
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


jobject custOpenDexFile(JNIEnv *env, jclass, jobject ctx, jbyteArray jDexBytes, jint jDexLen) {
    LOGD("start custOpenDexFile()");

    int status = initLoad(env, ctx); //初始化工作
    if(status == FAIL){
        LOGE("Init load env failed !!!");
    }
    jobject cookie;
    jbyte *dexBytes = env->GetByteArrayElements(jDexBytes, NULL);
    int dexLen = (int) jDexLen;
    dexDecode((char *) dexBytes, dexLen); //解密dex文件，
    if(isDalvik){
        cookie = dexLoadDvm(env, (char *) dexBytes, dexLen);
    }else{
        cookie = dexLoadArt(env, ctx, (char *) dexBytes, dexLen);
    }
    env->ReleaseByteArrayElements(jDexBytes, dexBytes, 0);
    remove(dexPath); //删除文件
    return cookie;
}

int initLoad(JNIEnv *env, jobject ctx){
    LOGD("start initLoadOfPath()");
    //Build$VERSION
    if (!dFindClass(env, &BuildVersion, "android/os/Build$VERSION")) {
        return FAIL;
    }
    jfieldID fieldID = env->GetStaticFieldID(BuildVersion, "SDK_INT", "I");
    sdkVersion = env->GetStaticIntField(BuildVersion, fieldID);
    LOGI("sdk version:%d", sdkVersion);

    //Integer
    if(!dFindClass(env, &Integer, "java/lang/Integer")){
        return FAIL;
    }
    Integer_valueOf_mID = env->GetStaticMethodID(Integer, "valueOf", "(I)Ljava/lang/Integer;");

    //Long
    if(!dFindClass(env, &Long, "java/lang/Long")){
        return FAIL;
    }
    Long_valueOf_mID = env->GetStaticMethodID(Long, "valueOf", "(J)Ljava/lang/Long;");

    //SystemPropreties
    if (!dFindClass(env, &SystemProperties, "android/os/SystemProperties")) {
        return FAIL;
    }
    SystemProperties_get_mID = env->GetStaticMethodID(SystemProperties, "get", "(Ljava/lang/String;)Ljava/lang/String;");
    //System
    if(!dFindClass(env, &System, "java/lang/System")){
        return FAIL;
    }
    System_getProperty_mID = env->GetStaticMethodID(System, "getProperty", "(Ljava/lang/String;)Ljava/lang/String;");

    //ClassLoader
    if(!dFindClass(env, &ClassLoader, "java/lang/ClassLoader")){
        return FAIL;
    }

    //DexPathList.Element
    if(!dFindClass(env, &DexPathList$Element, "dalvik/system/DexPathList$Element")){
        return FAIL;
    }

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
    env->ReleaseStringUTFChars(jvmLib, vmLib);

    //获取 vm 版本
    jstring vmVersionKey = env->NewStringUTF("java.vm.version");
    jstring jvmVersion = (jstring) env->CallStaticObjectMethod(System, System_getProperty_mID, vmVersionKey);
    const char *vmVersion = env->GetStringUTFChars(jvmVersion, NULL);
    double vmVersionInt = atof(vmVersion);
    LOGI("vmVersion:%s, vmVersionInt:%f, isDalvik:%d", vmVersion, vmVersionInt, isDalvik);
    env->ReleaseStringUTFChars(jvmVersion, vmVersion);
    //判断是art or dvm
    if (vmVersionInt > 2) {
        isDalvik = 0;
    } else {
        isDalvik = 1;
    }

    //DexFile
    if (!dFindClass(env, &DexFile, "dalvik/system/DexFile")) {
        return FAIL;
    }
    if(sdkVersion <= ANDROID_2_3_3){
        //2.3以下系统 int openDexFileNative(String sourceName, String outputName, int flags);
        DexFile_openDexFileNative_mID = env->GetStaticMethodID(DexFile, "openDexFile", "(Ljava/lang/String;Ljava/lang/String;I)I");
    }else if(sdkVersion > ANDROID_3_2 && sdkVersion <= ANDROID_4_4){
        //4.0-4.4系统 存在 int openDexFile(byte[], int);
        //art模式只有int openDexFile(String, String, int);
        if(isDalvik == 0){
            DexFile_openDexFileNative_mID = env->GetStaticMethodID(DexFile, "openDexFile", "(Ljava/lang/String;Ljava/lang/String;I)I");
        }
    } else if(sdkVersion > ANDROID_4_4 && sdkVersion <= ANDROID_5_1){
        //5.0-5.1系统 long openDexFileNative(String sourceName, String outputName, int flags);
        DexFile_openDexFileNative_mID = env->GetStaticMethodID(DexFile, "openDexFile", "(Ljava/lang/String;Ljava/lang/String;I)J");
    }else if(sdkVersion >= ANDROID_5_1 && sdkVersion<= ANDROID_6_0){
        //6.0系统 Object openDexFileNative(String sourceName, String outputName, int flags);
        DexFile_openDexFileNative_mID = env->GetStaticMethodID(DexFile, "openDexFile", "(Ljava/lang/String;Ljava/lang/String;I)Ljava/lang/Object;");
    }else if(sdkVersion > ANDROID_6_0){
        //7.0系统 Object openDexFileNative(String sourceName, String outputName, int flags, ClassLoader loader, DexPathList.Element[] elements);
        DexFile_openDexFileNative_mID = env->GetStaticMethodID(DexFile, "openDexFile", "(Ljava/lang/String;Ljava/lang/String;ILjava/lang/ClassLoader;dalvik/system/DexPathList$Element;)Ljava/lang/Object;");
    }

    //获取getFilesDir路径
    Context = env->GetObjectClass(ctx);
    Context_getFilesDir_mID = env->GetMethodID(Context, "getFilesDir", "()Ljava/io/File;");
    jobject fileObj = env->CallObjectMethod(ctx, Context_getFilesDir_mID); //获取/data/data/packageName/files
    File = env->GetObjectClass(fileObj);
    File_getAbsolutePath_mID = env->GetMethodID(File, "getAbsolutePath", "()Ljava/lang/String;");
    jstring jAbsolutePath = (jstring) env->CallObjectMethod(fileObj, File_getAbsolutePath_mID);
    g_filePath = jstringToChar(env, jAbsolutePath);
    LOGD("global files path: %s", g_filePath);

    return SUCC;
}

jobject dexLoadDvm(JNIEnv *env, char * dexBytes, int dexLen){
    LOGD("start dexLoadDvm()");
    //dvm 2.1 - 4.4
    if(sdkVersion <= ANDROID_2_3_3){ //2.3以下系统
        writeDex(dexBytes, dexLen);
        jstring jdexPath = env->NewStringUTF(dexPath);
        jlong cookieOfInt = env->CallStaticLongMethod(DexFile, DexFile_openDexFileNative_mID, jdexPath, 0, 0);
        jobject cookie = env->CallStaticObjectMethod(Long, Long_valueOf_mID, cookieOfInt);
        return cookie;
    }else {
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
        int cookieOfInt;
        if (openDexFile != NULL) {
            openDexFile(args, &pResult);
        } else {
            cookieOfInt = -1;
        }
        cookieOfInt = (u8) pResult.l;
        LOGD("openDexFile cookie:%d", cookieOfInt);
        free(fileContent);
        jobject cookie = env->CallStaticObjectMethod(Integer, Integer_valueOf_mID, cookieOfInt);
        return cookie;
    }
}

jobject dexLoadArt(JNIEnv *env, jobject ctx, char *dexBytes, int dexLen) {
    LOGI("Start dexLoadArt()");
    //art 从4.4开始
    writeDex(dexBytes, dexLen);
    jstring jdexPath = env->NewStringUTF(dexPath);

    jobject cookie = NULL;
    if(sdkVersion <= ANDROID_4_4){
        //4.4系统 int openDexFile(String, String, int);
        jint cookieOfInt = env->CallStaticIntMethod(DexFile, DexFile_openDexFileNative_mID, jdexPath, NULL, 0);
        cookie = env->CallStaticObjectMethod(Integer, Integer_valueOf_mID, cookieOfInt);
    } else if(sdkVersion > ANDROID_4_4 && sdkVersion <= ANDROID_5_1){
        //5.0-5.1系统 long openDexFile(String, String, int);
        jlong cookieOfLong = env->CallStaticLongMethod(DexFile, DexFile_openDexFileNative_mID, jdexPath, NULL, 0);
        cookie = env->CallStaticObjectMethod(Long, Long_valueOf_mID, cookieOfLong);
    } else if(sdkVersion > ANDROID_5_1 && sdkVersion <= ANDROID_6_0){
        //6.0系统 Object openDexFile(String, String, int);
        cookie = env->CallStaticObjectMethod(DexFile, DexFile_openDexFileNative_mID, jdexPath, NULL, 0);
    } else if(sdkVersion > ANDROID_6_0){
        //7.0以上系统 Object openDexFile(String, String, int, ClassLoader, DexPathList.Element[]);
        Context = env->GetObjectClass(ctx);
        Context_getClassLoader_mID = env->GetMethodID(Context, "getClassLoader", "()Ljava/lang/ClassLoader;");
        jobject loaderObj = env->CallObjectMethod(ctx, Context_getClassLoader_mID);
        cookie = env->CallStaticObjectMethod(DexFile, DexFile_openDexFileNative_mID, jdexPath, NULL, 0, loaderObj, 0);
    }
    return cookie;
}

void gotOpenDexFile() {
    LOGI("Start gotOpenDexFile()");

    void *dldvm = dlopen("libdvm.so", RTLD_LAZY); //获取libdvm句柄
    JNINativeMethod *natvieMethods = (JNINativeMethod *) dlsym(dldvm, "dvm_dalvik_system_DexFile"); //获取注册函数
    int isExist = lookup(natvieMethods, "openDexFile", "([B)I", &openDexFile); //查找是否包含 openDexFile 函数
    if (isExist == 1) {
        LOGD("openDexFile(byte[], int) method found !!!");
    } else {
        LOGD("openDexFile(byte[], int) method does not found !!!");
    }
}

int lookup(JNINativeMethod *methods, const char *name, const char *signature, void (**fnPtr)(const u4 *, union JValue *)) {
    LOGI("Start lookup()");

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

void writeDex(char *dexBytes, int dexLen){
    LOGI("Start writeDex()");

    sprintf(dexPath, "%s/%s", g_filePath, DEX_NAME);
    FILE *fp = fopen(dexPath, "w");
    fwrite(dexBytes, dexLen, 1, fp);
    fclose(fp);
    LOGD("Write dex file success");
}

void dexDecode(char *dexBytes, int dexLen){
    //这里不做处理
    for(int i=0; i<dexLen; i++){
        dexBytes[i] = dexBytes[i];
    }
}







