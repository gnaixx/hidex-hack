package cc.gnaixx.hidex_hack.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cc.gnaixx.hidex_hack.R;
import cc.gnaixx.hidex_hack.common.ClassLoader;

import static cc.gnaixx.hidex_hack.common.ToolKit.copyToCache;
import static cc.gnaixx.hidex_hack.common.ToolKit.md5;
import static cc.gnaixx.hidex_hack.common.ToolKit.readFiles;
import static cc.gnaixx.hidex_hack.config.Constant.HIDEX_DEX;
import static cc.gnaixx.hidex_hack.config.Constant.REDEX_DEX;
import static cc.gnaixx.hidex_hack.config.Constant.SOURCE_DEX;
import static cc.gnaixx.hidex_hack.common.ToolKit.readAssets;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnRedex, btnSource, btnTarget, btnHash;
    private TextView tvMsg;
    private ClassLoader classLoader;

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

        tvMsg = (TextView) findViewById(R.id.tv_msg);
        classLoader = new ClassLoader(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_redex:
                classLoader.redex(HIDEX_DEX, REDEX_DEX);
                break;
            case R.id.btn_source:
                loadSourceDex();
                break;
            case R.id.btn_target:

                break;
            case R.id.btn_hash:
                showHash();
                break;
        }
    }

    private void loadSourceDex(){
        copyToCache(this, SOURCE_DEX);
        classLoader.load(SOURCE_DEX);
    }

    private void showHash(){
        byte[] source = readAssets(this, SOURCE_DEX);
        byte[] hidex = readAssets(this, HIDEX_DEX);
        byte[] redex = readFiles(this, REDEX_DEX);

        String sourceMd5 = md5(source);
        String hidexMd5 = md5(hidex);
        String redexMd5 = md5(redex);

        StringBuilder msg = new StringBuilder();
        msg.append("source: " + sourceMd5 + "\n");
        msg.append("hidex : " + hidexMd5  + "\n");
        msg.append("redex : " + redexMd5  + "\n");
        tvMsg.setText(msg.toString());
    }
}
