package cc.gnaixx.hidex_libs.inter;

/**
 * 名称: Entrance
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/29
 */

public interface Entrance {

    byte[] encrypt(byte[] data, byte[] key);

    byte[] decrypt(byte[] data, byte[] key);

    String md5(String data);

    String getStaticFields();
}
