package cc.gnaixx.samp.core;

//import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import cc.gnaixx.hidex_inter.Entrance;

/**
 * 名称: Main
 * 描述:
 *
 * @author xiangqing.xue
 * @date 2016/11/16
 */

public class EntranceImpl implements Entrance {
    private final static String TAG = "GNAIXX";
    private final static boolean DEBUG = true;
    private final static boolean RELEASE = false;
    private final static int FLAG = 1;
    public final static int COUNT = 2;
    private final static byte[] NAME = new byte[]{0x49, 0x48};


    @Override
    public String getStaticFields() {
        JSONObject json = new JSONObject();
        try {
            json.put("TAG", TAG);
            json.put("DEBUG", DEBUG);
            json.put("RELEASE", RELEASE);
            json.put("FLAG", FLAG);
            json.put("COUNT", COUNT);
            json.put("NAME", NAME);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
