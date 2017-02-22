//
// Created by 薛祥清 on 2017/2/20.
//

#include <jni.h>
#include "HidexLoad.h"


JNIEnv static *g_env;
jclass static native_class;
JNINativeMethod *natvieMethods = NULL;
void (*openDexFile)(const u4* args, union JValue* pResult) = NULL;

extern  "C"
//注册
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    gotOpenDexFile(); //获取openDexFile地址

    JNINativeMethod methods[] = {
            {"custOpenDexFile", "([BI)I", (void *) custOpenDexFile}
    };

    if (JNI_OK != vm->GetEnv((void **) &g_env, JNI_VERSION_1_6)) {
        return -1;
    }
    LOGD("JNI_OnLoad()");
    native_class = g_env->FindClass("cc/gnaixx/hidex_load/tool/NativeTool");
    if (JNI_OK == g_env->RegisterNatives(native_class, methods, NELEM(methods))) {
        LOGD("Register success");
        return JNI_VERSION_1_6;
    } else {
        LOGD("Register failed");
        return -1;
    }
}

JNIEXPORT jint custOpenDexFile(JNIEnv *env, jobject clazz, jbyteArray jDexBytes, jint jDexLen){
    LOGD("start custOpenDexFile");

    u1 *dexBytes = (u1 *) env->GetByteArrayElements(jDexBytes, NULL);//
    int dexLen = (int) jDexLen;

    /*结构体内最大成员对齐 sizeof(ArrayObject) = 24*/
    char *fileContent = (char *) malloc(sizeof(ArrayObject) + dexLen); //所有指针为4字节
    ArrayObject *fileContentObj = (ArrayObject *) fileContent;
    fileContentObj->length =  dexLen;
    memcpy(fileContentObj->contents, dexBytes, dexLen);
    //memcpy(fileContent + 16, dexBytes, dexLen); //因为按照最大字节对齐 4+4, 4+()
    LOGD("dexLen:%d", fileContentObj->length);

    //u4 args[] = {(u4) fileContentObj};
    u4* args = (u4*) &fileContentObj; // args => fileContent
    union JValue pResult;
    int cookie;
    if(openDexFile != NULL){
        openDexFile(args, &pResult);
    }else{
        cookie = -1;
    }
    cookie = (u8) pResult.l;
    LOGD("openDexFile cookie:%d", cookie);
    free(fileContent);
    env->ReleaseByteArrayElements(jDexBytes, (jbyte *) dexBytes, 0);//
    return cookie;
}

//查找openDexFile
void gotOpenDexFile(){
    void *dldvm = dlopen(LIB_DVM, RTLD_LAZY); //获取libvm句柄
    natvieMethods = (JNINativeMethod *) dlsym(dldvm, JNI_NATIVE_METHOD); //获取注册函数
    int isExist = lookup(natvieMethods, METHOD_NAME, METHOD_SIGNATURE, &openDexFile);
    if(isExist == 1){
        LOGD("openDexFile method found !!!");
    }else{
        LOGD("openDexFile method does not found !!!");
    }
}

//查找是否包含 openDexFile 函数
int lookup(JNINativeMethod *methods,
           const char *name,
           const char *signature,
           void (**fnPtr)(const u4*, union JValue*)){

    if(methods == NULL){
        LOGD("%s is NULL", JNI_NATIVE_METHOD);
        return 0;
    }
    int i = 0;
    while(methods[i].name != NULL){
        if(strcmp(name, methods[i].name)==0 && strcmp(signature, methods[i].signature)==0){ //比较函数名 函数签名
            *fnPtr = (void (*)(const u4 *, union JValue *)) methods[i].fnPtr;
            LOGD("lookup index:%d, name:%s", i, name);
            return 1;
        }
        i++;
    }
}

//注销
JNIEXPORT void JNI_OnUnLoad(JavaVM *vm, void *reserved) {
    LOGD("JNI_OnUnLoad()");
    g_env->UnregisterNatives(native_class);
    LOGD("UnregisterNatives()");
}

