package cc.gnaixx.tools.model.dex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.tools.Reader;

import static cc.gnaixx.tools.tools.Trans.charToInt;

/**
 * 名称: ProtoIds
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class ProtoIds {
    class ProtoId {
        class Parameters {
            int size;       //参数个数
            char typeIdxs[]; //参数列表 type_idx

            public Parameters(int size, char[] typeIdxs) {
                this.size = size;
                this.typeIdxs = typeIdxs;
            }

            public JSONObject toJson(){
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("size", this.size);
                jsonParam.put("list", charToInt(this.typeIdxs));
                return jsonParam;
            }
        }

        int shortyIdx;              //StringIds编号
        int returnTypeIdx;          //typeIds编号
        int parametersOff;          //参数偏移位置
        Parameters parameters;

        public ProtoId(int shortyIdx, int returnTypeIdx, int parametersOff, int paramSize, char[] paramTyps) {
            this.shortyIdx = shortyIdx;
            this.returnTypeIdx = returnTypeIdx;
            this.parametersOff = parametersOff;
            this.parameters = new Parameters(paramSize, paramTyps);
        }

        public ProtoId(int shortyIdx, int returnTypeIdx, int parametersOff) {
            this.shortyIdx = shortyIdx;
            this.returnTypeIdx = returnTypeIdx;
            this.parametersOff = parametersOff;
        }

    }

    ProtoId protoIds[];

    public ProtoIds(byte[] dexBuff, int off, int size) {
        this.protoIds = new ProtoId[size];
        Reader reader = new Reader(dexBuff, off);

        for (int i = 0; i < size; i++) {
            int shortyIdx = reader.readUint();
            int returnTypeIdx = reader.readUint();
            int parametersOff = reader.readUint();

            ProtoId protoId;
            if (parametersOff != 0) {
                Reader reader1 = new Reader(dexBuff, parametersOff);
                int paramSize = reader1.readUint();
                char paramTypes[] = new char[paramSize];
                for (int j = 0; j < paramSize; j++) {
                    paramTypes[j] = reader1.readUshort();
                }
                protoId = new ProtoId(shortyIdx, returnTypeIdx, parametersOff, paramSize, paramTypes);
            } else {
                protoId = new ProtoId(shortyIdx, returnTypeIdx, parametersOff);
            }
            protoIds[i] = protoId;
        }
    }

    public JSONArray toJson() {
        JSONArray jsonIds = new JSONArray();
        for (int i = 0; i < protoIds.length; i++) {
            ProtoId protoId = protoIds[i];
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("id", i);
            jsonItem.put("shorty_idx", protoId.shortyIdx);
            jsonItem.put("return_type_idx", protoId.returnTypeIdx);
            jsonItem.put("parameters_off", protoId.parametersOff);
            if(protoId.parametersOff != 0) {
                jsonItem.put("parameters", protoId.parameters.toJson());
            }
            jsonIds.add(i, jsonItem);
        }
        return jsonIds;
    }
}
