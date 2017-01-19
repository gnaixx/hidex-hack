package cc.gnaixx.tools.model.dex.cladef;

import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.model.Uleb128;
import cc.gnaixx.tools.util.Reader;
import cc.gnaixx.tools.util.Writer;

/**
 * 名称: EncodeField
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class EncodedField {
    Uleb128 fieldIdxDiff;  //属性编号 对应field_ids
    Uleb128 accessFlags;   //访问权限


    public EncodedField(Reader reader) {
        this.fieldIdxDiff = reader.readUleb128();
        this.accessFlags = reader.readUleb128();
    }

    public void write(Writer writer) {
        writer.writeUleb128(this.fieldIdxDiff);
        writer.writeUleb128(this.accessFlags);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("field_idx_diff", fieldIdxDiff.getVal());
        json.put("access_flags", accessFlags.getVal());
        return json;
    }
}
