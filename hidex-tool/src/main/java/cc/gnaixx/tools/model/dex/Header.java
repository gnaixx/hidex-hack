package cc.gnaixx.tools.model.dex;


import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.tools.Encrypt;
import cc.gnaixx.tools.tools.Reader;

import static cc.gnaixx.tools.model.DexCon.DEF_INT;

/**
 * 名称: header
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class Header {
    public static final int MAGIC_LEN       = 8;
    public static final int SIGNATURE_LEN   = 20;

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


    public Header(byte[] dexHeader) {
        Reader reader = new Reader(0);
        this.magic = reader.subdex(dexHeader, MAGIC_LEN);
        this.checksum = reader.getUint(dexHeader);
        this.signature = reader.subdex(dexHeader,SIGNATURE_LEN);
        this.fileSize = reader.getUint(dexHeader);
        this.headerSize = reader.getUint(dexHeader);
        this.endianTag = reader.getUint(dexHeader);
        this.linkSize = reader.getUint(dexHeader);
        this.linkOff = reader.getUint(dexHeader);
        this.mapOff = reader.getUint(dexHeader);
        this.stringIdsSize = reader.getUint(dexHeader);
        this.stringIdsOff = reader.getUint(dexHeader);
        this.typeIdsSize = reader.getUint(dexHeader);
        this.typeIdsOff = reader.getUint(dexHeader);
        this.protoIdsSize = reader.getUint(dexHeader);
        this.protoIdsOff = reader.getUint(dexHeader);
        this.fieldIdsSize = reader.getUint(dexHeader);
        this.fieldIdsOff = reader.getUint(dexHeader);
        this.methodIdsSize = reader.getUint(dexHeader);
        this.methodIdsOff = reader.getUint(dexHeader);
        this.classDefsSize = reader.getUint(dexHeader);
        this.classDefsOff = reader.getUint(dexHeader);
        this.dataSize = reader.getUint(dexHeader);
        this.dataOff = reader.getUint(dexHeader);
    }

    public JSONObject toJson(){
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("magic", Encrypt.binToStr(this.magic));
        jsonHeader.put("checksum", Encrypt.intToHex(this.checksum));
        jsonHeader.put("signature", Encrypt.binToHex(this.signature));
        jsonHeader.put("file_size", this.fileSize);
        jsonHeader.put("header_size", this.headerSize);
        jsonHeader.put("endian_tag", Encrypt.intToHex(this.endianTag));
        jsonHeader.put("link_size", this.linkSize);
        jsonHeader.put("link_off", this.linkOff);
        jsonHeader.put("map_off", this.mapOff);
        jsonHeader.put("string_ids_size", this.stringIdsSize);
        jsonHeader.put("string_ids_off", this.stringIdsOff);
        jsonHeader.put("type_ids_size", this.typeIdsSize);
        jsonHeader.put("type_ids_off", this.typeIdsOff);
        jsonHeader.put("proto_ids_size", this.protoIdsSize);
        jsonHeader.put("proto_ids_off", this.protoIdsOff);
        jsonHeader.put("field_ids_size", this.fieldIdsSize);
        jsonHeader.put("field_ids_off", this.fieldIdsOff);
        jsonHeader.put("method_ids_size", this.methodIdsSize);
        jsonHeader.put("method_ids_off", this.methodIdsOff);
        jsonHeader.put("class_defs_size", this.classDefsSize);
        jsonHeader.put("class_defs_off", this.classDefsOff);
        jsonHeader.put("data_size", this.dataSize);
        jsonHeader.put("data_off", this.dataOff);
        return jsonHeader;
    }
}
