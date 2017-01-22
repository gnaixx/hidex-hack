package cc.gnaixx.hidex_hack.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.gnaixx.hidex_hack.R;
import cc.gnaixx.hidex_libs.common.ClassLoader;
import cc.gnaixx.hidex_libs.inter.Entrance;

import static cc.gnaixx.hidex_hack.common.ToolKit.copyFromAssets;
import static cc.gnaixx.hidex_hack.common.ToolKit.md5;
import static cc.gnaixx.hidex_hack.common.ToolKit.readFiles;
import static cc.gnaixx.hidex_hack.common.Constant.ENTRANCE;
import static cc.gnaixx.hidex_hack.common.Constant.HIDEX_DEX;
import static cc.gnaixx.hidex_hack.common.Constant.REDEX_DEX;
import static cc.gnaixx.hidex_hack.common.Constant.SOURCE_DEX;
import static cc.gnaixx.hidex_hack.common.ToolKit.readAssets;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnRedex, btnSource, btnTarget, btnHash;
    private TextView tvHash, tvSource, tvTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRedex = (Button) findViewById(R.id.btn_redex);
        btnSource = (Button) findViewById(R.id.btn_source);
        btnTarget = (Button) findViewById(R.id.btn_target);
        btnHash  = (Button) findViewById(R.id.btn_hash);
        btnRedex.setOnClickListener(this);
        btnSource.setOnClickListener(this);
        btnTarget.setOnClickListener(this);
        btnHash.setOnClickListener(this);

        tvHash = (TextView) findViewById(R.id.tv_hash);
        tvSource = (TextView) findViewById(R.id.tv_source);
        tvTarget = (TextView) findViewById(R.id.tv_target);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_redex:
                redex();
                break;
            case R.id.btn_source:
                loadSourceDex();
                break;
            case R.id.btn_target:
                loadTargetDex();
                break;
            case R.id.btn_hash:
                showHash();
                break;
        }
    }

    //redex hidex-samp.dex
    private void redex(){
        new ClassLoader(this).redexFromAssets(HIDEX_DEX, REDEX_DEX);
    }

    //load samp.dex
    private void loadSourceDex(){
        String cachePath = this.getCacheDir().getAbsolutePath();
        copyFromAssets(this, cachePath, SOURCE_DEX);
        ClassLoader loader = new ClassLoader(this);
        Entrance entrance = loader.load(cachePath, SOURCE_DEX, ENTRANCE);
        showData(entrance, tvSource);
    }

    //load redex-hidex-samp.dex
    private void loadTargetDex(){
        String filesPath = this.getFilesDir().getAbsolutePath();
        ClassLoader loader = new ClassLoader(this);
        Entrance entrance = loader.load(filesPath, REDEX_DEX, ENTRANCE);
        showData(entrance, tvTarget);
    }


    private void showData(Entrance entrance, TextView tv){
        byte[] data = new byte[]{0x48, 0x45, 0x4C, 0x4C, 0x4F, 0x2C, 0x20, 0x48, 0x49, 0x44, 0x45, 0x58};
        byte[] key = new byte[]{0x47, 0x4E, 0x41, 0x49, 0x58, 0x58, 0x48, 0x59, 0x47, 0x4E, 0x41, 0x49, 0x58, 0x58, 0x48, 0x59};
        byte[] enData = entrance.encrypt(data, key);
        byte[] deData = entrance.decrypt(enData, key);

        String enStr = new String(enData);
        String deStr = new String(deData);
        String fields = entrance.getStaticFields();
        String hash = entrance.md5(deStr);

        StringBuilder msg = new StringBuilder();
        msg.append("fileds: " + fields + "\n");
        msg.append("encode: " + enStr + "\n");
        msg.append("decode: " + deStr + "\n");
        msg.append("hash: " + hash + "\n");
        tv.setText(msg.toString());
    }

    //check hash
    private void showHash(){
        String filePath = this.getFilesDir().getAbsolutePath();
        byte[] source = readAssets(this, SOURCE_DEX);
        byte[] hidex = readAssets(this, HIDEX_DEX);
        byte[] redex = readFiles(filePath, REDEX_DEX);

        String sourceMd5 = md5(source);
        String hidexMd5 = md5(hidex);
        String redexMd5 = md5(redex);

        StringBuilder msg = new StringBuilder();
        msg.append("sampl: " + sourceMd5 + "\n");
        msg.append("hidex: " + hidexMd5  + "\n");
        msg.append("redex: " + redexMd5  + "\n");
        tvHash.setText(msg.toString());
    }
}
