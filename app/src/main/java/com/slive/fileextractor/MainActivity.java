package com.slive.fileextractor;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.slive.util.CommonTitleBar;
import com.slive.util.FileUtil;
import com.slive.util.ToastUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener{
    private static final String TAG = "read---------file";
    private final int NEED_READ_AND_WRITE = 200;
    private Button start, sure;
    private EditText key_content;
    private TagFlowLayout id_flowlayout;
    private List<String> keys;
    private TagAdapter mAdapter;
    private CommonTitleBar commonTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        start = findViewById(R.id.start);
        id_flowlayout = findViewById(R.id.id_flowlayout);
        sure = findViewById(R.id.sure);
        key_content = findViewById(R.id.key_content);
        commonTitleBar = findViewById(R.id.title);
        commonTitleBar.setTitleTxt("文件提取器");
        setBtnDeleteBackground(false);

        keys = new ArrayList<>();
        keys.add("ACCESSION");
        keys.add("/country");
        keys.add("/collection_date");

        setListener();
    }

    private void setListener(){
        start.setOnClickListener(this);
        sure.setOnClickListener(this);

        key_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    setBtnDeleteBackground(true);
                } else {
                    setBtnDeleteBackground(false);
                }
            }
        });

        final LayoutInflater mInflater = LayoutInflater.from(this);
        mAdapter = new TagAdapter<String>(keys) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) mInflater.inflate(R.layout.tv,
                        id_flowlayout, false);
                tv.setText(s);
                return tv;
            }
        };
        id_flowlayout.setAdapter(mAdapter);
        id_flowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                String clickStr = (String) mAdapter.getItem(position);
                if (keys.contains(clickStr)) {
                    keys.remove(clickStr);
                    mAdapter.notifyDataChanged();
                }
                return false;
            }
        });
    }

    private void createBaseFile(){
        FileUtil.createFile("original");
        FileUtil.createFile("result");
    }

    private void requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, NEED_READ_AND_WRITE);
            } else {
                createBaseFile();
            }
        } else {
            createBaseFile();
        }
    }

    private void extractorFile(){
        ToastUtil.show(this, "开始提取文件...");

        if (keys.size() == 0) {
            ToastUtil.show(this, "请先设置要提取的关键字");
            return;
        }

        String originalPath = FileUtil.getFilePath() + "original";
        String resultPath = FileUtil.getFilePath() + "result";

        File file = new File(originalPath);
        File fileResult = new File(resultPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!fileResult.exists()) {
            fileResult.mkdirs();
        }
        File fileResultTxt = new File(fileResult, "result.txt");
        if (fileResultTxt.exists()) {
            fileResultTxt.delete();
        }
        try {
            fileResultTxt.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File[] files = file.listFiles();
        File sourceFile = null;
        if (files != null) {
            if (files.length > 1) {
                ToastUtil.show(this, "请确保source文件夹中有且只有一个源文件");
            } else {
                for (int i = 0; i < files.length; i++) {
                    sourceFile = files[0];
                }
            }
        } else {
            ToastUtil.show(this, "请确保source文件夹中有且只有一个源文件");
        }

        if (sourceFile != null) {
            BufferedReader br = null;
            BufferedWriter bw = null;
            try {
                br = new BufferedReader(new FileReader(sourceFile));
                bw = new BufferedWriter(new FileWriter(fileResultTxt, true));
                String line = null;
                while ((line = br.readLine()) != null) {
                    for (String key : keys) {
                        if (line.contains(key)) {
                            bw.write(line);
                            bw.newLine();
                            bw.flush();
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                    if (bw != null) {
                        bw.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ToastUtil.show(this, "文件提取结束");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case NEED_READ_AND_WRITE:
                // 如果权限被拒绝，grantResults 为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createBaseFile();
                } else {
                    ToastUtil.show(this, "读写文件被拒绝，即将退出！");
                    Process.killProcess(Process.myPid());
                }
                break;
        }
    }

    private void setBtnDeleteBackground(boolean isEnable) {
        if (isEnable) {
            sure.setBackgroundResource(R.drawable.button_shape);
            sure.setTextColor(Color.WHITE);
        } else {
            sure.setBackgroundResource(R.drawable.button_noclickable_shape);
            sure.setTextColor(ContextCompat.getColor(this, R.color.color_b7b8bd));
        }
        sure.setEnabled(isEnable);
    }

    private void addTags(){
        if (keys == null) {
            keys = new ArrayList<>();
        }
        String tags = key_content.getText().toString();
        if (TextUtils.isEmpty(tags)) {
            ToastUtil.show(this, "输入的关键字为空！");
            setBtnDeleteBackground(false);
            return;
        }
        keys.add(tags);
        mAdapter.notifyDataChanged();

        key_content.setText("");

        setBtnDeleteBackground(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start:
                extractorFile();
                break;
            case R.id.sure:
                addTags();
                break;
            default:
                break;
        }
    }
}
