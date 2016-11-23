package cc.gnaixx.tools.model.dex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.tools.Reader;

/**
 * 名称: FieldIds
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class FieldIds {
    class FieldId {
        char classIdx; //所属类 type_ids 的index
        char typeIdx;  //类型 type_ids 的index
        int nameIdx;  //名称 string_ids 的index

        public FieldId(char classIdx, char typeIdx, int nameIdx) {
            this.classIdx = classIdx;
            this.typeIdx = typeIdx;
            this.nameIdx = nameIdx;
        }
    }

    FieldId fieldIds[];

    public FieldIds(byte[] dexbs, int off, int size) {
        fieldIds = new FieldId[size];

        Reader reader = new Reader(off);
        for (int i = 0; i < size; i++) {
            char classIdx = reader.getUshort(dexbs);
            char typeIdx = reader.getUshort(dexbs);
            int nameidx = reader.getUint(dexbs);
            FieldId fieldId = new FieldId(classIdx, typeIdx, nameidx);
            fieldIds[i] = fieldId;
        }
    }


    public JSONArray toJson(){
        JSONArray jsonIds = new JSONArray();

        for(int i = 0; i< fieldIds.length; i++){
            FieldId fieldId = fieldIds[i];
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("class_idx", (int)fieldId.classIdx);
            jsonItem.put("type_idx", (int)fieldId.typeIdx);
            jsonItem.put("name_idx", fieldId.nameIdx);
            jsonIds.add(i, jsonItem);
        }
        return jsonIds;
    }

}
