package cc.gnaixx.tools.model.dex.cladef;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.model.HackPoint;
import cc.gnaixx.tools.util.Reader;
import cc.gnaixx.tools.util.Trans;
import cc.gnaixx.tools.util.Writer;

/**
 * 名称: ClassData
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class ClassData {
    public HackPoint staticFieldsSize;           //静态变量个数
    public HackPoint instanceFieldsSize;         //实例变量个数
    public HackPoint directMethodsSize;          //直接函数个数
    public HackPoint virtualMethodsSize;         //虚函数个数

    public EncodedField staticFields[];          //静态变量
    public EncodedField instanceFields[];        //实例变量
    public EncodedMethod directMethods[];        //直接函数
    public EncodedMethod virtualMethods[];       //虚函数

    public ClassData(byte[] dexBuff, int off){
        Reader reader = new Reader(dexBuff, off);
        this.staticFieldsSize = new HackPoint(HackPoint.ULEB128, reader.getOff(), reader.readUleb128().getVal());
        this.instanceFieldsSize = new HackPoint(HackPoint.ULEB128, reader.getOff(), reader.readUleb128().getVal());
        this.directMethodsSize = new HackPoint(HackPoint.ULEB128, reader.getOff(), reader.readUleb128().getVal());
        this.virtualMethodsSize = new HackPoint(HackPoint.ULEB128, reader.getOff(), reader.readUleb128().getVal());

        staticFields = new EncodedField[this.staticFieldsSize.value];
        for(int i=0; i<this.staticFieldsSize.value; i++){
            EncodedField field = new EncodedField(reader);
            staticFields[i] = field;
        }
        instanceFields = new EncodedField[this.instanceFieldsSize.value];
        for(int i=0; i<this.instanceFieldsSize.value; i++){
            EncodedField field = new EncodedField(reader);
            instanceFields[i] = field;
        }

        directMethods = new EncodedMethod[this.directMethodsSize.value];
        for(int i=0; i<this.directMethodsSize.value; i++){
            EncodedMethod method = new EncodedMethod(reader);
            directMethods[i] = method;
        }
        virtualMethods = new EncodedMethod[this.virtualMethodsSize.value];
        for(int i=0; i<this.virtualMethodsSize.value; i++){
            EncodedMethod field = new EncodedMethod(reader);
            virtualMethods[i] = field;
        }
    }

    public void write(byte[] dexBuff, int off) {
        Writer writer = new Writer(dexBuff, off);
        writer.writeUleb128(Trans.intToUleb128(this.staticFieldsSize.value));
        writer.writeUleb128(Trans.intToUleb128(this.instanceFieldsSize.value));
        writer.writeUleb128(Trans.intToUleb128(this.directMethodsSize.value));
        writer.writeUleb128(Trans.intToUleb128(this.virtualMethodsSize.value));

        for (int i = 0; i < this.staticFieldsSize.value; i++) {
            this.staticFields[i].write(writer);
        }
        for (int i = 0; i < this.instanceFieldsSize.value; i++) {
            this.instanceFields[i].write(writer);
        }

        for (int i = 0; i < this.directMethodsSize.value; i++) {
            this.directMethods[i].write(writer);
        }
        for (int i = 0; i < this.virtualMethodsSize.value; i++) {
            this.virtualMethods[i].write(writer);
        }
    }

    public JSONObject toJson(){
        JSONObject json = new JSONObject();
        json.put("static_fields_size", this.staticFieldsSize.value);
        json.put("instance_fields_size", this.instanceFieldsSize.value);
        json.put("direct_methods_size", this.directMethodsSize.value);
        json.put("virtual_methods_size", this.virtualMethodsSize.value);

        //json.put("static_fields", arrayJson(0));
        //json.put("instance_fields", arrayJson(1));
        json.put("direct_methods", arrayJson(2));
        json.put("virtual_methods", arrayJson(3));
        return json;
    }

    private JSONArray arrayJson(int type){
        JSONArray jsonArray = new JSONArray();
        switch (type){
            case 0:
                for(int i=0; i<this.staticFieldsSize.value; i++){
                    JSONObject json = this.staticFields[i].toJson();
                    jsonArray.add(json);
                }
                break;
            case 1:
                for(int i=0; i<this.instanceFieldsSize.value; i++){
                    JSONObject json = this.instanceFields[i].toJson();
                    jsonArray.add(json);
                }
                break;
            case 2:
                for(int i=0; i<this.directMethodsSize.value; i++){
                    JSONObject json = this.directMethods[i].toJson();
                    jsonArray.add(json);
                }
                break;
            case 3:
                for(int i=0; i<this.virtualMethodsSize.value; i++){
                    JSONObject json = this.virtualMethods[i].toJson();
                    jsonArray.add(json);
                }
                break;
        }
        return jsonArray;
    }
}
