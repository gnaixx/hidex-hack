package cc.gnaixx.tools.model.dex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.model.Uleb128;
import cc.gnaixx.tools.tools.Reader;

import static cc.gnaixx.tools.model.DexCon.DEF_INT;
import static cc.gnaixx.tools.tools.StreamUtil.getUleb128;
import static cc.gnaixx.tools.tools.StreamUtil.subdex;

/**
 * 名称: StringIds
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class StringIds {

    class StringId {
        int dataOff = DEF_INT;  //字符串偏移位置
        Uleb128 utf16Size;      //字符串长度
        byte data[];            //字符串数据

        public StringId(int dataOff, Uleb128 uleb128, byte[] data) {
            this.dataOff = dataOff;
            this.utf16Size = uleb128;
            this.data = data;
        }
    }

    StringId stringIds[];

    public StringIds(byte[] dexbs, int off, int size) {
        this.stringIds = new StringId[size];

        Reader reader = new Reader(dexbs, off);
        for (int i = 0; i < size; i++) {
            int dataOff = reader.getUint();
            Uleb128 utf16Size = getUleb128(dexbs, dataOff);
            byte[] data = subdex(dexbs, dataOff + 1, utf16Size.getVal());
            StringId stringId = new StringId(dataOff, utf16Size, data);
            stringIds[i] = stringId;
        }
    }

    public String getData(int id) {
        return "(" + id + ")" + new String(stringIds[id].data);
    }

    public JSONArray toJson() {
        JSONArray jsonIds = new JSONArray();
        for (int i = 0; i < stringIds.length; i++) {
            StringId stringId = stringIds[i];
            JSONObject jsonData = new JSONObject();
            jsonData.put("utf16_size", stringId.utf16Size.getVal());
            jsonData.put("data", new String(stringId.data));

            JSONObject jsonItem = new JSONObject();
            jsonItem.put("id", i);
            jsonItem.put("data_off", stringId.dataOff);
            jsonItem.put("data", jsonData);

            jsonIds.add(i, jsonItem);
        }
        return jsonIds;
    }
}
