package cc.gnaixx.tools.model.dex.cladef;

import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.model.Uleb128;

/**
 * 名称: StaticValue
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class StaticValues {
    class StaticValue{

    }

    Uleb128     size;           //静态变量个数
    StaticValue staticValue[];  //静态变量信息（赋值）

    public StaticValues(Uleb128 size){
        this.size = size;
        this.staticValue = new StaticValue[this.size.getVal()];
    }

    public JSONObject toJson(){
        JSONObject jsonStaticValues = new JSONObject();
        jsonStaticValues.put("size", size.getVal());
        return jsonStaticValues;
    }
}
