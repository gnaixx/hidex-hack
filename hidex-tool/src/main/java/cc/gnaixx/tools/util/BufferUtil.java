package cc.gnaixx.tools.util;


import java.nio.ByteBuffer;

import cc.gnaixx.tools.model.Uleb128;

import static cc.gnaixx.tools.model.DexCon.UINT_LEN;

/**
 * 名称: StreamUtil
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class BufferUtil {

    public static byte[] subdex(byte[] stream, int offset, int len) {
        byte[] sub = new byte[len];
        for (int i = 0; i < len; i++) {
            sub[i] = stream[i + offset];
        }
        return sub;
    }

    public static int getUint(byte[] stream, int offset) {
        int value = 0;
        for (int i = 0; i < UINT_LEN; i++) {
            int seg = stream[offset + i];
            if (seg < 0) {
                seg = 256 + seg;
            }
            value += seg << (8 * i);
        }
        offset += UINT_LEN; //int 四个字节
        return value;
    }

    //读取utf-8 字符
    public static byte[] getUtf8(byte[] stream, int offset, int len) {
        byte subBuffer[] = new byte[len * 3];
        int size = 0;
        for (int i = 0; i < len; i++) {
            if((stream[offset] & 0x80) == 0x00){ //第一位为0 ASCII 码
                subBuffer[size] = stream[offset];
                offset ++;
                size ++;
            }else{
                int j;
                for(j=0; j<8; j++){
                    byte high = (byte) (stream[offset] & (0xFF << (7-j)));
                    byte flag = (byte) (-0x80 >> j);
                    if(high == flag){ //比较高位是否为1
                        subBuffer[size] = stream[offset + j];
                        size ++;
                    }else{
                        break;
                    }
                }
                offset += j;
            }
        }
        return subdex(subBuffer, 0, size);
    }

    public static Uleb128 getUleb128(byte[] stream, int offset) {
        int value = 0;
        int length = 0;
        byte origValue[] = new byte[4];
        boolean flag;
        do {
            flag = false;
            byte seg = stream[offset];
            if ((seg & 0x80) == 0x80) { //第一位为1
                flag = true;
            }
            seg = (byte) (seg & 0x7F);
            value += seg << (7 * length);
            origValue[length] = stream[offset];
            length++;
            offset++;
        } while (flag);
        origValue = BufferUtil.subdex(origValue, 0, length);//去掉空字节
        return new Uleb128(origValue, value, length);
    }

    public static byte[] replace(byte[] source, byte[] replacement, int offset, int len) {
        for (int i = 0; i < len; i++) {
            source[i + offset] = replacement[i];
        }
        return source;
    }

    public static byte[] append(byte[] source, byte[] element, int len) {
        ByteBuffer bb = ByteBuffer.allocate(source.length + element.length);
        bb.put(source);
        bb.put(element);
        return bb.array();
    }
}
