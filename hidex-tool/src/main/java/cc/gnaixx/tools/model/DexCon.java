package cc.gnaixx.tools.model;

/**
 * 名称: DexCon
 * 描述: 常量
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class DexCon {
    public static final int MAGIC_LEN = 0x0008;

    public static final int CHECKSUM_OFF = 0x0008;
    public static final int CHECKSUM_LEN = 0x0004;

    public static final int SIGNATURE_OFF = 0x000C;
    public static final int SIGNATURE_LEN = 0x0014;

    public static final int UINT_LEN = 0x0004;
    public static final int USHORT_LEN = 0x0002;


    public static final int MAP_OFF_OFF = MAGIC_LEN
                                        + UINT_LEN
                                        + SIGNATURE_LEN
                                        + UINT_LEN * 5;
    public static final int MAP_ITEM_LEN = 0x000C;

}
