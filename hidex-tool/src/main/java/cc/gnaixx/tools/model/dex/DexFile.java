package cc.gnaixx.tools.model.dex;



import com.alibaba.fastjson.JSONObject;

import static cc.gnaixx.tools.tools.Reader.subdex;


/**
 * 名称: DexFile
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */
public class DexFile {
    public static final int HEADER_LEN = 0x70;

    private Header header;
    private StringIds stringIds;
    private TypeIds typeIds;
    private ProtoIds protoIds;
    private FieldIds fieldIds;
    private MethodIds methodIds;

    public DexFile(){

    }


    //reader dex
    public void read(byte[] dexbs){
        //read header
        byte[] headerbs = subdex(dexbs, 0, HEADER_LEN);
        header = new Header(headerbs);

        //read string_ids
        stringIds = new StringIds(dexbs, header.stringIdsOff, header.stringIdsSize);

        //read type_ids
        typeIds = new TypeIds(dexbs, header.typeIdsOff, header.typeIdsSize);

        //read proto_ids
        protoIds = new ProtoIds(dexbs, header.protoIdsOff, header.protoIdsSize);

        //read field_ids
        fieldIds = new FieldIds(dexbs, header.fieldIdsOff, header.fieldIdsSize);

        //read method_ids
        methodIds = new MethodIds(dexbs, header.methodIdsOff, header.methodIdsSize);
    }

    public void write(){

    }

    public String toJsonStr(){
        JSONObject jsonDex = new JSONObject();
        jsonDex.put("header", header.toJson());
        jsonDex.put("string_ids", stringIds.toJson());
        jsonDex.put("type_ids", typeIds.toJson());
        jsonDex.put("proto_ids", protoIds.toJson());
        jsonDex.put("field_ids", fieldIds.toJson());
        jsonDex.put("method_ids", methodIds.toJson());

        return jsonDex.toJSONString();
    }
}
