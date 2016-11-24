package cc.gnaixx.tools.model.dex.cladef;

import cc.gnaixx.tools.model.Uleb128;

/**
 * 名称: ClassData
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/24
 */

public class ClassData {
    Uleb128 staticFieldsSize;
    Uleb128 instanceFieldsSize;
    Uleb128 directMethodsSize;
    Uleb128 virtualMethodsSize;

    EncodedField staticFields[];
    EncodedField instanceFields[];
    EncodedMethod directMethods[];
    EncodedMethod virtualMethods[];
}
