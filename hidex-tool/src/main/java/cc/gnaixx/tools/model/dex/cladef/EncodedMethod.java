package cc.gnaixx.tools.model.dex.cladef;

import cc.gnaixx.tools.model.Uleb128;

/**
 * 名称: EncodeMethod
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class EncodedMethod {
    Uleb128 methodIdsDiff; //函数编号 对应method_ids
    Uleb128 accessFlags;   //访问类型
    Uleb128 codeOff;       //代码偏移
}
