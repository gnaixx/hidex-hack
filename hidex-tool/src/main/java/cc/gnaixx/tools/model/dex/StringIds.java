package cc.gnaixx.tools.model.dex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.model.Uleb128;
import cc.gnaixx.tools.util.Reader;

import static cc.gnaixx.tools.util.BufferUtil.getUleb128;
import static cc.gnaixx.tools.util.BufferUtil.getUtf8;
import static cc.gnaixx.tools.util.BufferUtil.subdex;

/**
 * 名称: StringIds
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class StringIds {

    class StringId {
        int dataOff;            //字符串偏移位置
        Uleb128 utf16Size;      //字符串长度
        byte data[];            //字符串数据

        public StringId(int dataOff, Uleb128 uleb128, byte[] data) {
            this.dataOff = dataOff;
            this.utf16Size = uleb128;
            this.data = data;
        }
    }

    StringId stringIds[];

    public StringIds(byte[] dexBuff, int off, int size) {
        this.stringIds = new StringId[size];

        Reader reader = new Reader(dexBuff, off);
        for (int i = 0; i < size; i++) {
            int dataOff = reader.readUint();
            Uleb128 utf16Size = getUleb128(dexBuff, dataOff);
            byte[] data = getUtf8(dexBuff, dataOff + utf16Size.getLength(), utf16Size.getIntValue()); //读取utf-8 类型数据
            StringId stringId = new StringId(dataOff, utf16Size, data);
            stringIds[i] = stringId;
        }
    }

    public String getData(int id) {
        //return "(" + id + ")" + new String(stringIds[id].data);
        return new String(stringIds[id].data);
    }

    public JSONArray toJson() {
        JSONArray jsonIds = new JSONArray();
        for (int i = 0; i < stringIds.length; i++) {
            StringId stringId = stringIds[i];
            JSONObject jsonData = new JSONObject();
            jsonData.put("utf16_size", stringId.utf16Size.getIntValue());
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
