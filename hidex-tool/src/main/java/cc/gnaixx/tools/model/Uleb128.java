package cc.gnaixx.tools.model;

/**
 * 名称: Uleb128
 * 描述: 只用来编码32bits的整型数
 *
 * @author xiangqing.xue
 * @date 2016/11/23
 */

public class Uleb128 {
    byte[] realVal;
    int val;

    public Uleb128(byte[] realVal, int val){
        this.realVal = realVal;
        this.val = val;
    }

    public int getSize(){
        return this.realVal.length;
    }

    public int getVal(){
        return this.val;
    }

    public byte[] getRealVal(){
        return this.realVal;
    }
}
