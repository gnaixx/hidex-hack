package cc.gnaixx.tools.model.dex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.tools.Reader;

/**
 * 名称: MethodIds
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class MethodIds {
    class MethodId {
        char classIdx; //所属类 type_ids 的index
        char protoIdx;  //类型 proto_ids 的index
        int nameIdx;  //名称 string_ids 的index

        public MethodId(char classIdx, char protoIdx, int nameIdx) {
            this.classIdx = classIdx;
            this.protoIdx = protoIdx;
            this.nameIdx = nameIdx;
        }
    }

    MethodId methodIds[];

    public MethodIds(byte[] dexbs, int off, int size) {
        methodIds = new MethodId[size];

        Reader reader = new Reader(off);
        for (int i = 0; i < size; i++) {
            char classIdx = reader.getUshort(dexbs);
            char protoIdx = reader.getUshort(dexbs);
            int nameidx = reader.getUint(dexbs);
            MethodId methodId = new MethodId(classIdx, protoIdx, nameidx);
            methodIds[i] = methodId;
        }
    }


    public JSONArray toJson(){
        JSONArray jsonIds = new JSONArray();

        for(int i = 0; i< methodIds.length; i++){
            MethodId fieldId = methodIds[i];
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("class_idx", (int)fieldId.classIdx);
            jsonItem.put("proto_idx", (int)fieldId.protoIdx);
            jsonItem.put("name_idx", fieldId.nameIdx);
            jsonIds.add(i, jsonItem);
        }
        return jsonIds;
    }

}
