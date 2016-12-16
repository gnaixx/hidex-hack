package cc.gnaixx.hidex_hack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;

import cc.gnaixx.hidex_libs.inter.Entrance;
import dalvik.system.DexClassLoader;

import static cc.gnaixx.hidex_hack.tool.FileUtil.copyToCache;
import static cc.gnaixx.hidex_libs.tools.NativeHelper.redex;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG         = "HIDEX";
    private static final String DEX_NAME    = "sampl.dex";
    private static final String HIDEX_NAME  = "hidex.dex";
    private static final String REDEX_NAME  = "redex.dex";

    private Button btnSampl, btnHidex, btnRedex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSampl = (Button) findViewById(R.id.btn_sampl);
        btnHidex = (Button) findViewById(R.id.btn_hidex);
        btnRedex = (Button) findViewById(R.id.btn_redex);
        btnSampl.setOnClickListener(this);
        btnHidex.setOnClickListener(this);
        btnRedex.setOnClickListener(this);
        redex();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sampl:
                loadDex(DEX_NAME);
                break;
            case R.id.btn_hidex:
                loadDex(HIDEX_NAME);
                break;
            case R.id.btn_redex:
                loadDex(REDEX_NAME);
                break;
        }
    }

    private void loadDex(String filename){
        copyToCache(this, filename);
        File dex = new File(this.getFilesDir().getAbsolutePath(), filename);
        if(dex.exists()){
            DexClassLoader loader = new DexClassLoader(
                    dex.getPath(),
                    this.getCacheDir().getAbsolutePath(),
                    null,
                    this.getClassLoader());

            try {
                Class clazz = loader.loadClass("cc.gnaixx.samp.core.EntranceImpl");
                Entrance entrance = (Entrance) clazz.newInstance();
                Log.e(TAG, entrance.getStaticFields());

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
