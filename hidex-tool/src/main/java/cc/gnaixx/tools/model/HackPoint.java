package cc.gnaixx.tools.model;

/**
 * 名称: HackPoint
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/28
 */

public class HackPoint implements Cloneable {

    public static final int UINT = 0x01;
    public static final int USHORT = 0x02;
    public static final int ULEB128 = 0x03;

    public int type;        //数据类型
    public int offset;      //偏移地址
    public int value;       //原始值

    public HackPoint(int type, int offset, int val) {
        this.type = type;
        this.offset = offset;
        this.value = val;
    }

    @Override
    public HackPoint clone() {
        HackPoint hp = null;
        try {
            hp = (HackPoint) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return hp;
    }
}
