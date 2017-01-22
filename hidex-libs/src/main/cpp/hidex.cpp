#include <jni.h>
#include <string>
#include <errno.h>
#include <android/log.h>
#include <android/asset_manager.h>
#include <android/asset_manager_jni.h>
#include "sha1.h"

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
#define FILE_SIZE_OFF (MAGIC_LEN + UINT_LEN + SIGNATURE_LEN)
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
        hackPoints[i].type   = readUint(hackInfo, i * sizeof(HackPoint));
        hackPoints[i].offset = readUint(hackInfo, i * sizeof(HackPoint) + UINT_LEN);
        hackPoints[i].value  = readUint(hackInfo, i * sizeof(HackPoint) + UINT_LEN + UINT_LEN);
        LOGD("hackPoint[%d]: {type:%d, offset:%d, vaule:%d}", i, hackPoints[i].type, hackPoints[i].offset, hackPoints[i].value);
    }
}

void write(char* buffer, char* val, uint off, uint valLen){
    for(int i=0; i<valLen; i++){
        buffer[off + i] = val[i];
    }
}

//写uint数据
void writeUint(char* buffer, uint off, uint value){
    buffer[off + 0] = (char)((value >> 0)  & 0xFF);
    buffer[off + 1] = (char)((value >> 8)  & 0xFF);
    buffer[off + 2] = (char)((value >> 16) & 0xFF);
    buffer[off + 3] = (char)((value >> 24) & 0xFF);
}

//写ushort
void writeUshort(char* buffer, uint off, uint value){
    buffer[off + 0] = (char)((value >> 0) & 0xFF);
    buffer[off + 1] = (char)((value >> 8) & 0xFF);
}

//uint to uleb128
void uintToUleb128(uint val, uint8_t *uleb128, uint *uleb128Len) {
    uint maxLen = UINT_LEN;
    uint len = 0;
    for (uint i = 0; i < maxLen; i++) {
        len = i + 1; //最小长度为1
        uleb128[i] = (uint8_t) (val & 0x7F); //写入低7位值
        if (val > (0x7F)) { //判断是下一个字节是否有值
            uleb128[i] |= 0x80;
        }
        val = val >> 7;
        if (val <= 0) break;
    }
    *uleb128Len = len;
}

//写uleb128
void writeUleb128(char* buffer, uint off, uint value){
    uint8_t * uleb128 = (uint8_t *) calloc(UINT_LEN, sizeof(uint8_t));
    uint uleb128Len;
    uintToUleb128(value, uleb128, &uleb128Len);
    for(int i=0; i<uleb128Len; i++){
        buffer[off + i] = uleb128[i];
    }
    free(uleb128);
}

//修复hackPoint
void recoverHP(char *target, HackPoint hackPoint){
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

// byte to hex
void binToHex(uint8_t *buff, int buffLen, uint8_t* hex) {
    for (int i = 0; i < buffLen; i++) {
        uint8_t hex1, hex2;
        uint val = buff[i];
        uint v1 = val / 16;
        uint v2 = val % 16;
        if (v1 >= 0 && v1 <= 9) {
            hex1 = (uint8_t) (0x30 + v1);
        } else{
            hex1 = (uint8_t) (0x37 + v1);
        }
        if (v2 >= 0 && v2 <= 9) {
            hex2 = (uint8_t) (0x30 + v2);
        } else{
            hex2 = (uint8_t) (0x37 + v2);
        }
        hex[i * 2] = hex1;
        hex[i * 2 + 1] = hex2;
    }
    hex[buffLen * 2] = '\0';
}

//int to hex Little Endian
void intToHex(uint val, uint8_t* hex){
    uint8_t bin[UINT_LEN] = {
            (uint8_t) ((val >> 24) & 0xFF),
            (uint8_t) ((val >> 16) & 0xFF),
            (uint8_t) ((val >> 8) & 0xFF),
            (uint8_t) ((val >> 0) & 0xFF),
    };
    binToHex(bin, UINT_LEN, hex);
}


//计算checksum adler32
uint adler32(char *buff, uint off, uint len) {
    const int MOD_ADLER = 65521;
    uint a = 1, b = 0;
    uint index;
    for (index = 0; index < len; ++index) {
        a = (a + buff[index + off]) % MOD_ADLER;
        b = (b + a) % MOD_ADLER;
    }
    return (b << 16) | a;
}

//计算signature sha1
void sha1(uint8_t* source, uint off, uint len, uint8_t* hash){
    uint8_t * buff = (uint8_t *) calloc(len, sizeof(uint8_t));
    memcpy(buff, source + off, len);
    SHA1_CTX ctx;
    SHA1Init(&ctx);
    SHA1Update(&ctx, buff, len);
    SHA1Final(hash, &ctx);
    free(buff);
}

//修复header
void recoverHeader(char *target, uint targetLen) {
    //修复文件长度
    writeUint(target, FILE_SIZE_OFF, targetLen);
    LOGD("Recover fileSize: {file_size:%d}", targetLen);

    //修复signature
    uint8_t signature[SIGNATURE_LEN];
    sha1((uint8_t *) target, (SIGNATURE_OFF + SIGNATURE_LEN), (targetLen - SIGNATURE_OFF - SIGNATURE_LEN), signature);
    write(target, (char *) signature, SIGNATURE_OFF, SIGNATURE_LEN);
    uint8_t * hex = (uint8_t *) calloc(SIGNATURE_LEN * 2 + 1, sizeof(uint8_t));//打印日志
    binToHex(signature, SIGNATURE_LEN, hex);
    LOGD("Recover signature: {signature:%s}", hex);
    free(hex);

    //修复checksum
    uint checksum = adler32(target, (CHECKSUM_OFF + CHECKSUM_LEN), (targetLen - CHECKSUM_OFF - CHECKSUM_LEN));
    writeUint(target, CHECKSUM_OFF, checksum);
    uint8_t * checksumHex = (uint8_t *) calloc(UINT_LEN * 2 + 1, sizeof(uint8_t));//打印日志
    intToHex(checksum, checksumHex);
    LOGD("Recover checksum: {integer:%d, checksum:%s}", checksum, checksumHex);
    free(checksumHex);
}


//解密dex
void recode(char* source, uint sourceLen, char* target, uint* targetLen){
    uint mapOff = readUint(source, MAP_OFF_OFF); //获取map_off
    uint mapSize = readUint(source, mapOff); //获取map_size
    LOGD("mapInfo: {map_off:%d, map_size:%d}", mapOff, mapSize);

    uint hackInfoOff = mapOff + UINT_LEN + (mapSize * MAP_ITEM_LEN); //定位hackInfo位置
    uint hackInfoLen = sourceLen - hackInfoOff; //hackInfo长度
    char* hackInfo = (char *) calloc(hackInfoLen, sizeof(char));
    memcpy(hackInfo, source + hackInfoOff, hackInfoLen); //复制hackInfo
    LOGD("hackInfo: {hackInfo_off:%d, hackInfo_len}", hackInfoOff, hackInfoLen);

    uint hackPointSize = hackInfoLen / sizeof(HackPoint); //获取hackPoint结构体
    HackPoint* hackPoints = (HackPoint *) calloc(hackPointSize, sizeof(HackPoint));
    initHP(hackPoints, hackInfo, hackPointSize); //将hockInfo 转化为结构体

    *targetLen = hackInfoOff;
    memcpy(target, source, *targetLen); //恢复原始长度

    //恢复数据
    for(int i=0; i<hackPointSize; i++){
        recoverHP(target, hackPoints[i]);
    }
    LOGD("Recover HackPoint success");

    //修复hearder
    recoverHeader(target, *targetLen);

    free(hackInfo);
    free(hackPoints);
}

//写文件
int fwrite(char* buff, uint buffLen, const  char* path, const char* fileName){
    char filePath[128];
    sprintf(filePath, "%s/%s", path, fileName);
    FILE* fp = fopen(filePath, "w+");
    if (fp == NULL) {
        LOGD("Open %s failed:[error:%d, desc:%s]", filePath, errno, strerror(errno));
        return -1;
    }
    fwrite(buff, sizeof(char), buffLen, fp);
    LOGD("Write %s success", filePath);
    fclose(fp);
    return 0;
}

int redexFromAssets(JNIEnv* env, jclass clazz, jobject assetManager, jstring jsourceName, jstring jtargetPath, jstring jtargetName) {
    LOGD("Start redex .......");
    const char* csourceName = env->GetStringUTFChars(jsourceName, NULL);
    const char* ctargetName = env->GetStringUTFChars(jtargetName, NULL);
    const char* ctargetPath  = env->GetStringUTFChars(jtargetPath, NULL);

    if(ctargetPath == NULL || csourceName == NULL || ctargetName == NULL){
        LOGD("path | source | target is NULL");
        return 1;
    }
    LOGD("sourceName:%s, targetPath:%s, targetName:%s", csourceName, ctargetPath, ctargetName);

    //############# 读取文件 #############
    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);
    if(mgr == NULL){
        LOGD("AAssetManager is NULL");
        return 2;
    }
    AAsset* asset = AAssetManager_open(mgr, csourceName, AASSET_MODE_UNKNOWN);
    if(asset == NULL){
        LOGD("AAsset is NULL, %s not exist", csourceName);
        return 3;
    }

    off_t sourceLen = AAsset_getLength(asset);
    char* sourceBuff = (char*)calloc((size_t) sourceLen, sizeof(char));
    if(AAsset_read(asset, sourceBuff, (size_t) sourceLen) < 0){
        LOGD("AAsset read failed");
        return 4;
    }
    AAsset_close(asset);
    LOGD("AAsset read success");

    //############# redex #############
    char* targetBuff = (char *) calloc((size_t) sourceLen, sizeof(char));
    uint targetLen;
    recode(sourceBuff, (uint) sourceLen, targetBuff, &targetLen);

    //############# 写入文件 #############
    if(fwrite(targetBuff, targetLen, ctargetPath, ctargetName) != 0){
        return 5;
    }

    free(sourceBuff);
    free(targetBuff);
    env->ReleaseStringUTFChars(jsourceName, csourceName);
    env->ReleaseStringUTFChars(jtargetPath, ctargetName);
    env->ReleaseStringUTFChars(jtargetName, ctargetPath);
    LOGD("Stop redex ......");
    return 0;
}

int redexFromFile(JNIEnv* env, jclass clazz, jstring jsourcePath, jstring jsourceName, jstring jtargetPath, jstring jtargetName) {
    LOGD("Start redex .......");
    const char* csourceName = env->GetStringUTFChars(jsourceName, NULL);
    const char* csourcePath = env->GetStringUTFChars(jsourcePath, NULL);
    const char* ctargetName = env->GetStringUTFChars(jtargetName, NULL);
    const char* ctargetPath  = env->GetStringUTFChars(jtargetPath, NULL);

    if(csourcePath == NULL || csourceName == NULL || ctargetPath == NULL || ctargetName == NULL){
        LOGD("path | source | target is NULL");
        return 1;
    }
    LOGD("sourcePath:%s, sourceName:%s, targetPath:%s, targetName:%s", csourcePath, csourceName, ctargetPath, ctargetName);

    //############# 读取文件 #############
    char filePath[128];
    sprintf(filePath, "%s/%s", csourcePath, csourceName);
    FILE *fp = fopen(filePath, "r");
    if(fp == NULL){
        LOGD("%s not exist", filePath);
        return 1;
    }
    fseek(fp, 0L, SEEK_END);//移到文件末尾读取文件长度
    long sourceLen = ftell(fp);
    fseek(fp, 0L, SEEK_SET);//移到文件开始
    char * sourceBuff = (char *) calloc(sizeof(char), (size_t) sourceLen);
    fread(sourceBuff, sizeof(char), (size_t) sourceLen, fp);
    fclose(fp);

    //############# redex #############
    char* targetBuff = (char *) calloc((size_t) sourceLen, sizeof(char));
    uint targetLen;
    recode(sourceBuff, (uint) sourceLen, targetBuff, &targetLen);

    //############# 写入文件 #############
    if(fwrite(targetBuff, targetLen, ctargetPath, ctargetName) != 0){
        return 2;
    }

    free(sourceBuff);
    free(targetBuff);
    env->ReleaseStringUTFChars(jsourceName, csourceName);
    env->ReleaseStringUTFChars(jtargetPath, ctargetName);
    env->ReleaseStringUTFChars(jtargetName, ctargetPath);
    LOGD("Stop redex ......");
    return 0;
}


//注册
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNINativeMethod methods[] = {
            {"redexFromAssets", "(Landroid/content/res/AssetManager;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", (void *) redexFromAssets},
            {"redexFromFile", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I", (void *) redexFromFile}
    };

    if (JNI_OK != vm->GetEnv((void **) &g_env, JNI_VERSION_1_6)) {
        return -1;
    }
    LOGD("JNI_OnLoad()");
    native_class = g_env->FindClass("cc/gnaixx/hidex_libs/common/JniBridge");
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
