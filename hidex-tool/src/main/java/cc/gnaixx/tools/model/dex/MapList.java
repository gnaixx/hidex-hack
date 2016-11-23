package cc.gnaixx.tools.model.dex;

import cc.gnaixx.tools.tools.Reader;

/**
 * 名称: MapList
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/23
 */

public class MapList {
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
        Reader reader = new Reader(off);
        mapSize = reader.getUint(dexbs);
        mapItems = new MapItem[mapSize];

        for (int i = 0; i < mapSize; i++) {
            char type = reader.getUshort(dexbs);
            char unused = reader.getUshort(dexbs);
            int size = reader.getUint(dexbs);
            int offset = reader.getUint(dexbs);
        }

    }

}
