package cc.gnaixx.hidex_load.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cc.gnaixx.hidex_libs.inter.Entrance;
import cc.gnaixx.hidex_load.R;
import cc.gnaixx.hidex_load.loader.CustDexClassLoader;

import static cc.gnaixx.hidex_load.tool.Constant.IMPL_NAME;
import static cc.gnaixx.hidex_load.tool.Constant.TAG;
import static cc.gnaixx.hidex_load.tool.FileTool.readAssets;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        byte[] dexBytes = readAssets(this, "samp.dex");
        injectDexClassLoader(dexBytes);
    }

    private void injectDexClassLoader(byte[] dexBytes){
        Context context = getApplicationContext();
        CustDexClassLoader custLoader = new CustDexClassLoader(context, dexBytes);

        try {
            Class clazz = custLoader.findClass(IMPL_NAME);
            Entrance entrance = (Entrance) clazz.newInstance();
            Log.i(TAG, entrance.getStaticFields());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
