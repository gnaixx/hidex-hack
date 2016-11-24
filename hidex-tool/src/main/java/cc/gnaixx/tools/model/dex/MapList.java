package cc.gnaixx.tools.model.dex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.gnaixx.tools.tools.Reader;

/**
 * 名称: MapList
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/23
 */

public class MapList {

    static Map<Integer, String> ITEM_TYPE = new HashMap<>();

    static {
        ITEM_TYPE.put(0x0000, "TYPE_HEADER_ITEM");
        ITEM_TYPE.put(0x0001, "TYPE_STRING_ID_ITEM");
        ITEM_TYPE.put(0x0002, "TYPE_TYPE_ID_ITEM");
        ITEM_TYPE.put(0x0003, "TYPE_PROTO_ID_ITEM");
        ITEM_TYPE.put(0x0004, "TYPE_FIELD_ID_ITEM");
        ITEM_TYPE.put(0x0005, "TYPE_METHOD_ID_ITEM");
        ITEM_TYPE.put(0x0006, "TYPE_CLASS_DEF_ITEM");
        ITEM_TYPE.put(0x1000, "TYPE_MAP_LIST");
        ITEM_TYPE.put(0x1001, "TYPE_TYPE_LIST");
        ITEM_TYPE.put(0x1002, "TYPE_ANNOTATION_SET_REF");
        ITEM_TYPE.put(0x1003, "TYPE_ANNOTATION_SET_ITE");
        ITEM_TYPE.put(0x2000, "TYPE_CLASS_DATA_ITEM");
        ITEM_TYPE.put(0x2001, "TYPE_CODE_ITEM");
        ITEM_TYPE.put(0x2002, "TYPE_STRING_DATA_ITEM");
        ITEM_TYPE.put(0x2003, "TYPE_DEBUG_INFO_ITEM");
        ITEM_TYPE.put(0x2004, "TYPE_ANNOTATION_ITEM");
        ITEM_TYPE.put(0x2005, "TYPE_ENCODED_ARRAY_ITEM");
        ITEM_TYPE.put(0x2006, "TYPE_ANNOTATIONS_DIRECT");
    }

    class MapItem {
        char type;      //类型
        char unused;    //对其方式
        int size;       //大小
        int offset;     //偏移

        public MapItem(char type, char unused, int size, int offset) {
            this.type = type;
            this.unused = unused;
            this.size = size;
            this.offset = offset;
        }
    }

    int mapSize;
    MapItem mapItems[];

    public MapList(byte[] dexbs, int off) {
        Reader reader = new Reader(dexbs, off);
        mapSize = reader.getUint();
        mapItems = new MapItem[mapSize];

        for (int i = 0; i < mapSize; i++) {
            char type = reader.getUshort();
            char unused = reader.getUshort();
            int size = reader.getUint();
            int offset = reader.getUint();

            MapItem item = new MapItem(type, unused, size, offset);
            mapItems[i] = item;
        }
    }

    public JSONObject toJson() {
        JSONObject jsonMaps = new JSONObject();
        jsonMaps.put("size", this.mapSize);

        JSONArray jsonItems = new JSONArray();
        for (int i = 0; i < mapSize; i++) {
            MapItem item = mapItems[i];
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("type", ITEM_TYPE.get((int)item.type));
            jsonItem.put("unused", (int)item.unused);
            jsonItem.put("size", item.size);
            jsonItem.put("offset", item.offset);
            jsonItems.add(i, jsonItem);
        }

        jsonMaps.put("list", jsonItems);
        return jsonMaps;
    }
}
