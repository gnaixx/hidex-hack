package cc.gnaixx.tools.model.dex.cladef;

import cc.gnaixx.tools.model.Uleb128;

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

}
