package cc.gnaixx.tools.model;

/**
 * 名称: Uleb128
 * 描述: 只用来编码32bits的整型数
 *
 * @author xiangqing.xue
 * @date 2016/11/23
 */

public class Uleb128 {
    byte[] origValue;
    int intValue;
    int length;

    public Uleb128(byte[] origValue, int intValue, int length){
        this.origValue = origValue;
        this.intValue = intValue;
        this.length = length;
    }

    public int getLength(){
        return this.length;
    }

    public int getIntValue(){
        return this.intValue;
    }

    public byte[] getOrigValue(){
        return this.origValue;
    }
}
