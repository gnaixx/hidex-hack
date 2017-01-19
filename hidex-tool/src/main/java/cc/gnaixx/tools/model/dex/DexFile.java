package cc.gnaixx.tools.model.dex;



import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.model.dex.cladef.ClassDefs;

import static cc.gnaixx.tools.util.BufferUtil.subdex;


/**
 * 名称: DexFile
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */
public class DexFile {
    public static final int HEADER_LEN = 0x70;

    public Header header;
    public StringIds stringIds;
    public TypeIds typeIds;
    public ProtoIds protoIds;
    public FieldIds fieldIds;
    public MethodIds methodIds;
    public ClassDefs classDefs;
    public MapList mapList;

    //reader dex
    public void read(byte[] dexBuff){
        //read header
        byte[] headerbs = subdex(dexBuff, 0, HEADER_LEN);
        header = new Header(headerbs);

        //read string_ids
        stringIds = new StringIds(dexBuff, header.stringIdsOff, header.stringIdsSize);

        //read type_ids
        typeIds = new TypeIds(dexBuff, header.typeIdsOff, header.typeIdsSize);

        //read proto_ids
        protoIds = new ProtoIds(dexBuff, header.protoIdsOff, header.protoIdsSize);

        //read field_ids
        fieldIds = new FieldIds(dexBuff, header.fieldIdsOff, header.fieldIdsSize);

        //read method_ids
        methodIds = new MethodIds(dexBuff, header.methodIdsOff, header.methodIdsSize);

        //read class_defs
        classDefs = new ClassDefs(dexBuff, header.classDefsOff, header.classDefsSize);

        //read map_list
        mapList = new MapList(dexBuff, header.mapOff);
    }

    public void write(){

    }

    public String toJsonStr(){
        JSONObject jsonDex = new JSONObject();
        jsonDex.put("header", header.toJson());
        //jsonDex.put("string_ids", stringIds.toJson());
        //jsonDex.put("type_ids", typeIds.toJson());
        //jsonDex.put("proto_ids", protoIds.toJson());
        //jsonDex.put("field_ids", fieldIds.toJson(this));
        //jsonDex.put("method_ids", methodIds.toJson());
        jsonDex.put("class_def", classDefs.toJson(this));
        //jsonDex.put("map_list", mapList.toJson());
        return jsonDex.toJSONString();
    }
}
