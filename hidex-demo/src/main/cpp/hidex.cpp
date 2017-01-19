#include <jni.h>
#include <string>
#include <errno.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>

#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG, "HIDEX_NDK", __VA_ARGS__)
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

#define MAGIC_LEN  0x0008
#define CHECKSUM_OFF 0x0008
#define CHECKSUM_LEN 0x0004
#define SIGNATURE_OFF 0x000C
#define SIGNATURE_LEN 0x0014
#define UINT_LEN 0x0004
#define USHORT_LEN = 0x0002
#define MAP_OFF_OFF (MAGIC_LEN + UINT_LEN + SIGNATURE_LEN + UINT_LEN * 5)
#define MAP_ITEM_LEN 0x000C


struct HackPoint {
    uint type;
    uint offset;
    uint value;
};

JNIEnv static *g_env;
jclass static native_class;

using namespace std;

extern "C"
//读取uint类型数据
uint readUint(char* buffer, uint off){
    uint value = 0;
    value += buffer[off];
    value += buffer[off + 1] << 8;
    value += buffer[off + 2] << 16;
    value += buffer[off + 3] << 24;
    return value;
}

//初始化
void initHP(HackPoint* hackPoints, char* hackInfo, uint size){
    for(int i=0; i<size; i++){
        hackPoints[i].type = readUint(hackInfo, i * sizeof(HackPoint));
        hackPoints[i].offset = readUint(hackInfo, i * sizeof(HackPoint) + UINT_LEN);
        hackPoints[i].value = readUint(hackInfo, i * sizeof(HackPoint) + UINT_LEN + UINT_LEN);
        LOGD("HackPoint[%d]{type:%d, offset:%d, vaule:%d}", i, hackPoints[i].type, hackPoints[i].offset, hackPoints[i].value);
    }
}

//写uint数据
void writeUint(char* buffer, uint off, uint value){
    buffer[off] = (char)(value & 0xFF);
    buffer[off + 1] = (char)((value >> 8) & 0xFF);
    buffer[off + 2] = (char)((value >> 16) & 0xFF);
    buffer[off + 3] = (char)((value >> 24) & 0xFF);
}

//写ushort
void writeUshort(char* buffer, uint off, uint value){
    buffer[off] = (char)(value & 0xFF);
    buffer[off + 1] = (char)((value >> 8) & 0xFF);
}

//uint to uleb128
char* uintToUleb128(uint val, uint* uleb128Len){
    char* uleb128 = new char[4];
    uint maxLen = 4;
    uint bk = val;
    uint len = 0;
    for(int i=0; i<maxLen; i++){
        len = i + 1;
        uleb128[i] += (val & 0x7F);
        if(val > (0x7F)) {
            uleb128[i] = (char)(uleb128[i] | (0x01 << 7));
        }
        val = val >> 7;
        if(val <= 0) break;
    }
    *uleb128Len = len;
    return uleb128;
}

//写uleb128
void writeUleb128(char* buffer, uint off, uint value){
    uint uleb128Len;
    char* uleb128 = uintToUleb128(value, &uleb128Len);
    for(int i=0; i<uleb128Len; i++){
        buffer[off + i] = uleb128[i];
    }
}

//修复hackPoint
void recovery(char* target, HackPoint hackPoint){

    switch (hackPoint.type){
        case 0x01: //uint
            writeUint(target, hackPoint.offset, hackPoint.value);
            break;
        case 0x02: //ushort
            writeUshort(target, hackPoint.offset, hackPoint.value);
            break;
        case 0x03: //uleb128
            writeUleb128(target, hackPoint.offset, hackPoint.value);
            break;
    }
}


//解密dex
void recode(char* source, uint sourceLen, char* target, uint* targetLen){
    uint mapOff = readUint(source, MAP_OFF_OFF); //获取map_off
    uint mapSize = readUint(source, mapOff); //获取map_size
    LOGD("map_off:%d, map_size:%d", mapOff, mapSize);

    uint hackInfoOff = mapOff + UINT_LEN + (mapSize * MAP_ITEM_LEN); //定位hackInfo位置
    uint hackInfoLen = sourceLen - hackInfoOff; //hackInfo长度
    char* hackInfo = (char *) malloc(hackInfoLen);
    memcpy(hackInfo, source + hackInfoOff, hackInfoLen); //复制hackInfo

    uint hackPointSize = hackInfoLen / sizeof(HackPoint); //获取hackPoint结构体
    HackPoint* hackPoints = (HackPoint *) malloc(sizeof(HackPoint) * hackPointSize);
    initHP(hackPoints, hackInfo, hackPointSize);

    *targetLen = hackInfoOff;
    memcpy(target, source, *targetLen); //恢复原始长度

    //恢复数据
    for(int i=0; i<hackPointSize; i++){
        recovery(target, hackPoints[i]);
    }


    free(hackInfo);
    free(hackPoints);
}

int redex(JNIEnv* env, jclass clazz, jobject assetManager, jstring jsource, jstring jpath, jstring jtarget) {
    LOGD("Start redex .......");
    const char* csource = env->GetStringUTFChars(jsource, NULL);
    const char* ctarget = env->GetStringUTFChars(jtarget, NULL);
    const char* cpath   = env->GetStringUTFChars(jpath, NULL);

    //############# 读取文件 #############
    if(cpath == NULL || csource == NULL || ctarget == NULL){
        LOGD("path | source | target is NULL");
        return 1;
    }
    LOGD("path:%s, source:%s, target:%s", cpath, csource, ctarget);

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(mgr == NULL){
        LOGD("AAssetManager is NULL");
        return 2;
    }
    AAsset* asset = AAssetManager_open(mgr, csource, AASSET_MODE_UNKNOWN);
    if(asset == NULL){
        LOGD("AAsset is NULL, %s not exist", csource);
        return 3;
    }

    off_t sourceLen = AAsset_getLength(asset);
    char* sourceBuff = (char*)malloc(sizeof(char) * sourceLen);
    if(AAsset_read(asset, sourceBuff, (size_t) sourceLen) < 0){
        LOGD("AAsset read failed");
        return 4;
    }
    AAsset_close(asset);
    LOGD("AAsset read success");

    //############# redex #############
    char* targetBuff = (char *) malloc(sizeof(char) * sourceLen);
    uint targetLen;
    recode(sourceBuff, (uint) sourceLen, targetBuff, &targetLen);

    //############# 写入文件 #############
    string strpath(cpath);
    string strtarget(ctarget);
    strpath += "/" + strtarget;
    FILE* fp = fopen(strpath.c_str(), "w+");
    if (fp == NULL) {
        LOGD("Open %s failed:[error:%d, desc:%s]", strpath.c_str(), errno, strerror(errno));
        return 5;
    }
    fwrite(targetBuff, 1, targetLen, fp);
    LOGD("Write %s/%s success", cpath, ctarget);
    fclose(fp);

    free(sourceBuff);
    free(targetBuff);
    env->ReleaseStringUTFChars(jsource, csource);
    env->ReleaseStringUTFChars(jtarget, ctarget);
    env->ReleaseStringUTFChars(jpath, cpath);
    LOGD("Stop redex ......");
    return 0;
}

//注册
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNINativeMethod methods[] = {
            {"redex", "(Landroid/content/res/AssetManager;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", (void *) redex}
    };

    if (JNI_OK != vm->GetEnv((void **) &g_env, JNI_VERSION_1_6)) {
        return -1;
    }
    LOGD("JNI_OnLoad()");
    native_class = g_env->FindClass("cc/gnaixx/hidex_hack/common/JniBridge");
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
