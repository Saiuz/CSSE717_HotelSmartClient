package li.gogo1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/*public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "IatDemo";
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 听写结果内容
    private EditText mResultText;
    // 用户词表下载结果
    private String mDownloadResult = "";

    private Toast mToast;

    private int ret;

    private Button startRecording;

    private String voiceResult = "";

    private TextView text_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIat = SpeechRecognizer.createRecognizer(this, mInitListener);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        text_result = (TextView) this.findViewById(R.id.TextView_Result);
        startRecording = (Button) this.findViewById(R.id.Record_Button1);
        startRecording.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void getVoice() {
        SpeechUtility.createUtility(MainActivity.this, "appid=540593c3");
        // 设置参数
        setParam();
        // 不显示听写对话框
        ret = mIat.startListening(recognizerListener);

    }

    public void setParam() {

        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        // 设置语音前端点
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        // 设置音频保存路径
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/xunfei");
    }

    *//**
     * 初始化监听器。
     *//*
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code == ErrorCode.SUCCESS) {
                Toast.makeText(MainActivity.this, "init success", 0).show();
            }
        }
    };

    *//**
     * 用户词表下载监听器。
     *//*
    private SpeechListener downloadlistener = new SpeechListener() {

        @Override
        public void onData(byte[] data) {
            try {
                if (data != null && data.length > 1)
                    mDownloadResult = new String(data, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error != null) {

            } else if (TextUtils.isEmpty(mDownloadResult)) {

            } else {
                mResultText.setText("");
                UserWords userwords = new UserWords(mDownloadResult.toString());
                if (userwords == null || userwords.getKeys() == null) {

                    return;
                }
                for (String key : userwords.getKeys()) {
                    mResultText.append(key + ":");
                    for (String word : userwords.getWords(key)) {
                        mResultText.append(word + ",");
                    }
                    mResultText.append("\n");
                }

            }
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
        }

    };

    *//**
     * 听写监听器。
     *//*
    private RecognizerListener recognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            Toast.makeText(MainActivity.this, "begin speech", 0).show();
        }

        @Override
        public void onError(SpeechError error) {
            Toast.makeText(MainActivity.this, error.getErrorDescription(), 0).show();
        }

        @Override
        public void onEndOfSpeech() {
            Toast.makeText(MainActivity.this, "end", 0).show();
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, String msg) {
            Toast.makeText(MainActivity.this, "on event", 0).show();
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());
            voiceResult = voiceResult + text;
            if (isLast) {
                text_result.setText(voiceResult);
            }
        }

        @Override
        public void onVolumeChanged(int volume) {

        }

    };

    @Override
    protected void onResume() {
        // SpeechUtility.getUtility().checkServiceInstalled();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_recording:
                getVoice();
                break;
        }
    }

}*/


/*
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}*/



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_click;
    private Button Jump_btn;
    private EditText mResultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_click = (Button) findViewById(R.id.Record_Button1);
        Jump_btn = (Button) findViewById(R.id.Jump_Button);
        mResultText = ((EditText) findViewById(R.id.Result_EditText));

        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58a11c93");

        btn_click.setOnClickListener(this);
        Jump_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        btnVoice();
        Intent intent = null;
        switch (v.getId()){
            case R.id.Jump_Button:
                intent = new Intent(MainActivity.this, UnderstandingDEMO.class);
                if (intent != null)
                    startActivity(intent);
                break;
            case R.id.Record_Button1:
                btnVoice();
        }
    }

    //TODO 开始说话：
    private void btnVoice() {
        RecognizerDialog dialog = new RecognizerDialog(this,null);
        dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        dialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                printResult(recognizerResult);
            }
            @Override
            public void onError(SpeechError speechError) {
                Log.d("debug", speechError.toString());
            }
        });
        dialog.show();
        Toast.makeText(this, "请开始说话", Toast.LENGTH_SHORT).show();
    }

    //回调结果：
    private void printResult(RecognizerResult results) {
        String text = parseIatResult(results.getResultString());
        // 自动填写地址
        mResultText.append(text);
    }

    //// TODO: 2017/2/21 这个方法是对语音转写文字的JSON处理，语义识别(Semantic)的JSON解析需要自己实现
    @NonNull
    public static String parseIatResult(String json) {
        Log.d("debug", json.toString());
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }
}

