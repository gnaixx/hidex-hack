package cc.gnaixx.tools.model.dex.cladef;

import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.model.HackPoint;
import cc.gnaixx.tools.model.Uleb128;
import cc.gnaixx.tools.util.Reader;
import cc.gnaixx.tools.util.Trans;
import cc.gnaixx.tools.util.Writer;

/**
 * 名称: EncodeMethod
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class EncodedMethod {
    public Uleb128 methodIdxDiff; //函数编号 对应method_ids
    public Uleb128 accessFlags;   //访问类型
    public HackPoint codeOff;     //代码偏移 Uleb128

    public EncodedMethod(Reader reader){
        this.methodIdxDiff = reader.readUleb128();
        this.accessFlags = reader.readUleb128();
        this.codeOff = new HackPoint(HackPoint.ULEB128, reader.getOff(), reader.readUleb128().getIntValue());
    }

    public void write(Writer writer){
        writer.writeUleb128(this.methodIdxDiff);
        writer.writeUleb128(this.accessFlags);
        writer.writeUleb128(Trans.intToUleb128(this.codeOff.value));
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("method_idx_diff", methodIdxDiff.getIntValue());
        json.put("access_flags", accessFlags.getIntValue());
        json.put("code_off", codeOff.value);
        return json;
    }
}

