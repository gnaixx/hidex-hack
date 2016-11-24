package cc.gnaixx.tools.model.dex.cladef;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cc.gnaixx.tools.model.Uleb128;
import cc.gnaixx.tools.model.dex.DexFile;
import cc.gnaixx.tools.tools.Reader;

/**
 * 名称: ClassDefs
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/15
 */

public class ClassDefs {

    class ClassDef {
        int classIdx;       //class类型，对应type_ids
        int accessFlags;    //访问类型，enum
        int superclassIdx;  //supperclass类型，对应type_ids
        int interfacesOff;  //接口偏移，对应type_list
        int sourceFileidx;  //源文件名，对应string_ids
        int annotationsOff; //class注解，位置位于data区，对应annotation_direcotry_item
        int classDataOff;   //class具体用到的数据，位于data区，格式为class_data_item,描述class的field,method,method执行代码
        int staticValueOff; //位于data区，格式为encoded_array_item

        StaticValues staticValues;  // classDataOff不为0时存在
        ClassData    classData;     // staticValueOff不为0存在

        public ClassDef(int classIdx, int accessFlags,
                        int superclassIdx, int interfacesOff,
                        int sourceFileidx, int annotationsOff,
                        int classDataOff, int staticValueOff) {
            this.classIdx = classIdx;
            this.accessFlags = accessFlags;
            this.superclassIdx = superclassIdx;
            this.interfacesOff = interfacesOff;
            this.sourceFileidx = sourceFileidx;
            this.annotationsOff = annotationsOff;
            this.classDataOff = classDataOff;
            this.staticValueOff = staticValueOff;
        }

        public void setClassData(){

        }

        public void setStaticValue(StaticValues staticValues){
            this.staticValues = staticValues;
        }
    }

    ClassDef classDefs[];

    public ClassDefs(byte[] dexbs, int off, int size) {
        Reader reader = new Reader(dexbs, off);
        classDefs = new ClassDef[size];

        for (int i = 0; i < size; i++) {
            int classDataOff;
            int staticValueOff;

            ClassDef classDef = new ClassDef(
                    reader.getUint(), reader.getUint(),
                    reader.getUint(), reader.getUint(),
                    reader.getUint(), reader.getUint(),
                    classDataOff = reader.getUint(),
                    staticValueOff = reader.getUint());

            if(staticValueOff != 0){
                Reader reader1 = new Reader(dexbs, staticValueOff);
                Uleb128 staticSize = reader1.getUleb128();
                StaticValues staticValues = new StaticValues(staticSize);
                classDef.setStaticValue(staticValues);
            }

            classDefs[i] = classDef;
        }
    }

    
    public JSONArray toJson(DexFile dexFile){
        JSONArray jsonDefs = new JSONArray();
        for(int i = 0; i< classDefs.length; i++){
            ClassDef classDef = classDefs[i];
            JSONObject jsonItem = new JSONObject();
            jsonItem.put("class_idx", dexFile.typeIds.getString(dexFile, classDef.classIdx));
            jsonItem.put("access_flags", classDef.accessFlags);
            jsonItem.put("superclass_idx", dexFile.typeIds.getString(dexFile, classDef.superclassIdx));
            jsonItem.put("interfaces_off", classDef.interfacesOff);
            jsonItem.put("source_file_idx", dexFile.stringIds.getData(classDef.sourceFileidx));
            jsonItem.put("annotations_off", classDef.annotationsOff);
            jsonItem.put("class_data_off", classDef.classDataOff);
            jsonItem.put("static_values_off", classDef.staticValueOff);

            jsonItem.put("static_values", classDef.staticValues.toJson());
            jsonDefs.add(i, jsonItem);
        }
        return jsonDefs;
    }
}
