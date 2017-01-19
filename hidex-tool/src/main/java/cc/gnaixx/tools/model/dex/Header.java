package cc.gnaixx.tools.model.dex;


import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.util.Reader;
import cc.gnaixx.tools.util.Writer;

import static cc.gnaixx.tools.model.DexCon.MAGIC_LEN;
import static cc.gnaixx.tools.model.DexCon.SIGNATURE_LEN;
import static cc.gnaixx.tools.util.Trans.binToHex;
import static cc.gnaixx.tools.util.Trans.binToStr;
import static cc.gnaixx.tools.util.Trans.intToHex;

/**
 * 名称: header
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class Header {

    public byte[]  magic           = new byte[MAGIC_LEN];
    public int     checksum;
    public byte[]  signature       = new byte[SIGNATURE_LEN];
    public int     fileSize;
    public int     headerSize;
    public int     endianTag;
    public int     linkSize;
    public int     linkOff;
    public int     mapOff;
    public int     stringIdsSize;
    public int     stringIdsOff;
    public int     typeIdsSize;
    public int     typeIdsOff;
    public int     protoIdsSize;
    public int     protoIdsOff;
    public int     fieldIdsSize;
    public int     fieldIdsOff;
    public int     methodIdsSize;
    public int     methodIdsOff;
    public int     classDefsSize;
    public int     classDefsOff;
    public int     dataSize;
    public int     dataOff;


    public Header(byte[] headerBuff) {
        Reader reader = new Reader(headerBuff, 0);
        this.magic = reader.subdex(MAGIC_LEN);
        this.checksum = reader.readUint();
        this.signature = reader.subdex(SIGNATURE_LEN);
        this.fileSize = reader.readUint();
        this.headerSize = reader.readUint();
        this.endianTag = reader.readUint();
        this.linkSize = reader.readUint();
        this.linkOff = reader.readUint();
        this.mapOff = reader.readUint();
        this.stringIdsSize = reader.readUint();
        this.stringIdsOff = reader.readUint();
        this.typeIdsSize = reader.readUint();
        this.typeIdsOff = reader.readUint();
        this.protoIdsSize = reader.readUint();
        this.protoIdsOff = reader.readUint();
        this.fieldIdsSize = reader.readUint();
        this.fieldIdsOff = reader.readUint();
        this.methodIdsSize = reader.readUint();
        this.methodIdsOff = reader.readUint();
        this.classDefsSize = reader.readUint();
        this.classDefsOff = reader.readUint();
        this.dataSize = reader.readUint();
        this.dataOff = reader.readUint();
    }

    public void write(byte[] dexBuff){
        Writer writer = new Writer(dexBuff, 0);
        writer.replace(magic, MAGIC_LEN);
        writer.writeUint(checksum);
        writer.replace(signature, SIGNATURE_LEN);
        writer.writeUint(fileSize);
        writer.writeUint(headerSize);
        writer.writeUint(endianTag);
        writer.writeUint(linkSize);
        writer.writeUint(linkOff);
        writer.writeUint(mapOff);
        writer.writeUint(stringIdsSize);
        writer.writeUint(stringIdsOff);
        writer.writeUint(typeIdsSize);
        writer.writeUint(typeIdsOff);
        writer.writeUint(protoIdsSize);
        writer.writeUint(protoIdsOff);
        writer.writeUint(fieldIdsSize);
        writer.writeUint(fieldIdsOff);
        writer.writeUint(methodIdsSize);
        writer.writeUint(methodIdsOff);
        writer.writeUint(classDefsSize);
        writer.writeUint(classDefsOff);
        writer.writeUint(dataSize);
        writer.writeUint(dataOff);
    }

    public JSONObject toJson(){
        JSONObject jsonHeader = new JSONObject();
        jsonHeader.put("magic", binToStr(this.magic));
        jsonHeader.put("checksum", intToHex(this.checksum));
        jsonHeader.put("signature", binToHex(this.signature));
        jsonHeader.put("file_size", this.fileSize);
        jsonHeader.put("header_size", this.headerSize);
        jsonHeader.put("endian_tag", intToHex(this.endianTag));
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
