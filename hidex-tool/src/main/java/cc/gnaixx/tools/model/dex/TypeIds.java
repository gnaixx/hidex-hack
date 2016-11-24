package cc.gnaixx.tools.model.dex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.tools.Reader;

/**
 * 名称: TypeIds
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class TypeIds {
    class TypeId {
        int descriptorIdx; //对应 Stringids编号

        public TypeId(int idx) {
            this.descriptorIdx = idx;
        }
    }

    TypeId typeIds[];

    public TypeIds(byte[] dexbs, int off, int size) {
        this.typeIds = new TypeId[size];

        Reader reader = new Reader(dexbs, off);
        for (int i = 0; i < size; i++) {
            int idx = reader.getUint();
            TypeId typeId = new TypeId(idx);
            typeIds[i] = typeId;
        }
    }

    public String getString(DexFile dexFile, int id) {
        String data = dexFile.stringIds.getData(typeIds[id].descriptorIdx);
        return "(" + id + ")" + data;
    }

    public JSONArray toJson() {
        JSONArray jsonIds = new JSONArray();

        for (int i = 0; i < typeIds.length; i++) {
            TypeId typeId = typeIds[i];
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("id", i);
            jsonItem.put("descriptor_idx", typeId.descriptorIdx);
            //jsonItem.put("data", new String(typeId.data));

            jsonIds.add(i, jsonItem);
        }
        return jsonIds;
    }
}
