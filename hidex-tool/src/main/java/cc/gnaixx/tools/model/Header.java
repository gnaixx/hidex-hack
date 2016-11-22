package cc.gnaixx.tools.model;

import static cc.gnaixx.tools.model.DexCon.DEF_INT;

/**
 * 名称: header
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class Header {

    byte[]  magic           = new byte[8];
    int     checksum        = DEF_INT;
    byte[]  signature       = new byte[20];
    int     fileSize        = DEF_INT;
    int     headerSize      = DEF_INT;
    int     endianTag       = DEF_INT;
    int     linkSize        = DEF_INT;
    int     linkOff         = DEF_INT;
    int     mapOff          = DEF_INT;
    int     stringIdsSize   = DEF_INT;
    int     stringIdsOff    = DEF_INT;
    int     typeIdsSize     = DEF_INT;
    int     typeIdsOff      = DEF_INT;
    int     protoIdsSize    = DEF_INT;
    int     protoIdsOff     = DEF_INT;
    int     fieldIdsSize    = DEF_INT;
    int     fieldIdsOff     = DEF_INT;
    int     methodIdsSize   = DEF_INT;
    int     methodIdsOff    = DEF_INT;
    int     classDefsSize   = DEF_INT;
    int     classDefsOff    = DEF_INT;
    int     dataSize        = DEF_INT;
    int     dataOff         = DEF_INT;


    public Header() {
        this.magic = new byte[8];
        this.signature = new byte[20];
    }
}
