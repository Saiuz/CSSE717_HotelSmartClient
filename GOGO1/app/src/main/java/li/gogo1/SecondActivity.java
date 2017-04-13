package li.gogo1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.UnderstanderResult;
import com.iflytek.sunflower.FlowerCollector;

import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {

    private boolean IntroductionGiving_Flag = false;   //如果是false 则不播放介绍短语
    //// TODO: 2017/3/17 此处使用 sharedPreferences 方便进行设置

    private VideoView videoView;
    private ImageButton button_gear;
    private ImageButton button_record;
    private ImageButton ImageButton_Background;
    private Button IntroductionButton;
    private Button SettingButton;
    private ImageView IntroductionImage;
    private LinearLayout SettingLayout;
    private Toast mToast;

    private static String TAG = "debug_voice";
    private int ret = 0;// 函数调用返回值
    private String jsonResult;
    SpeechSynthesizer mTts;

    private int F_turnAround_int = 0;
    private int F_swingLoop_int = 0;
    private boolean F_interrupt = false;
    private boolean F_isSwung = false;
    private boolean SettingFlag = false;     //当此变量为true的时候，表示打开了设置页面
    private boolean onSpeaking = false;
    private boolean onVideoPlaying = false;

    private SpeechUnderstander mSpeechUnderstander;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        //优先加载视频
        videoView = (VideoView)findViewById(R.id.video_secondActivity);
        button_gear = (ImageButton) findViewById(R.id.button_setting);
        button_record = (ImageButton) findViewById(R.id.button_record);
        ImageButton_Background = (ImageButton)findViewById(R.id.ImageButton_BackGround);
        IntroductionButton = (Button)findViewById(R.id.Introduction_Button);
        SettingButton = (Button)findViewById(R.id.Setting_Button);
        IntroductionImage = (ImageView)findViewById(R.id.Introduction_Image);
        SettingLayout = (LinearLayout)findViewById(R.id.Setting_Layout);

        Log.d("debug", "Now onCreate() begin");
        Log.d("debug", "F_interrupt : " + F_interrupt);

        //// FIXME: 2017/3/4 调整videoView的宽和高
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        videoView.setLayoutParams(new RelativeLayout.LayoutParams(dm.widthPixels, dm.heightPixels));
        //↑↑↑调整videoView的宽和高

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.greeting_2);
        videoView.setVideoURI(uri);
        onVideoPlaying = true;
        videoView.start();

        //// TODO: 2017/3/1 增加语音输入按钮、增加设置按钮、预设多套不同宽高比的视频（根据实际屏幕宽高比选择） [done]

        // 初始化语义理解对象
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58a11c93");
        mSpeechUnderstander = SpeechUnderstander.createUnderstander(SecondActivity.this, mSpeechUdrInitListener);
        mToast = Toast.makeText(SecondActivity.this, "", Toast.LENGTH_SHORT);

        //// FIXME: 2017/3/2 添加自IFly-MSC官方文档
        //1.创建 SpeechSynthesizer 对象, 第二个参数：本地合成时传 InitListener
        mTts= SpeechSynthesizer.createSynthesizer(SecondActivity.this, null);
        //2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
        // 设置发音人（更多在线发音人，用户可参见 附录13.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "aismengchun"); //设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
        // 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        // 保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        // 仅支持保存为 pcm 和 wav 格式，如果不需要保存合成音频，注释该行代码
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
        //3.开始合成
//        mTts.startSpeaking("科大讯飞，让世界聆听我们的声音", mSynListener);
        // ↑↑↑  添加自IFly-MSC官方文档

        
        //// FIXME: 2017/3/17  如果第一次进入，则播放介绍语
        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        IntroductionGiving_Flag = sharedPreferences.getBoolean("check", true);
        if (IntroductionGiving_Flag){
            mTts.startSpeaking("您好主人，我是您的智能管家机器人，我是大白。我可以帮您开关电视、空调、灯泡等电器，也可以帮助查询周边好玩的、好吃的地方！设置中有使用说明，等待主人的查看！", mSynListener);
            editor.putBoolean("check", false);
            editor.apply();
        }
        //↑↑↑ 如果第一次进入，则播放介绍语


        //// FIXME: 2017/3/2 显示设置列表
//        final String items[] = {"检测蓝牙连接","使用说明","??????"};
//        setting_builder = new AlertDialog.Builder(this);
//        setting_builder.setTitle("设置"); //设置标题
////        setting_builder.setIcon(R.mipmap.icon_launcher);//设置图标，图片id即可
//        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
//        setting_builder.setItems(items,new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                mTts.startSpeaking("六六六六六", mSynListener);
//                //// TODO: 2017/3/2 检测蓝牙连接、显示使用说明(dialog)
//            }
//        });
//        button_gear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                setting_builder.create().show();
//            }
//        });
        //↑↑↑显示设置列表


        //// FIXME: 2017/3/18 设置设置按键的响应
        ImageButton_Background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingFlag = false;
                ImageButton_Background.setVisibility(View.GONE);
                SettingLayout.setVisibility(View.GONE);
                IntroductionImage.setVisibility(View.GONE);
            }
        });
        IntroductionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingLayout.setVisibility(View.GONE);
                IntroductionImage.setVisibility(View.VISIBLE);
            }
        });
        SettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingIntent = new Intent(SecondActivity.this, SettingActivity.class);
                startActivity(settingIntent);
            }
        });
        button_gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingFlag = true;
                ImageButton_Background.setVisibility(View.VISIBLE);
                SettingLayout.setVisibility(View.VISIBLE);
            }
        });
        // ↑↑↑ 设置设置按键的响应


        //// FIXME: 2017/3/3 显示返回键弹出菜单列表, [已经在下面的onBackPressed方法中实现直接回到桌面]
//        back_builder = new AlertDialog.Builder(SecondActivity.this);
//        back_builder.setTitle("退出");
//        back_builder.setMessage("选择退出方式");
//        back_builder.setPositiveButton("后台挂起", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Intent home = new Intent();
//                home.setAction("android.intent.action.MAIN");
//                home.addCategory("android.intent.category.HOME");
//                startActivity(home);
//            }
//        });
//        back_builder.setNegativeButton("退出应用", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //// TODO: 2017/3/3 添加完全销毁应用的代码
//            }
//        });
        // ↑↑↑ 显示返回键弹出菜单列表

        //// FIXME: 2017/3/4 设置录音按键
        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                F_interrupt = true;
                setParam();
                if(mSpeechUnderstander.isUnderstanding()){// 开始前检查状态
                    mSpeechUnderstander.stopUnderstanding();
                    showTip("停止录音");
                }else {
                    ret = mSpeechUnderstander.startUnderstanding(mSpeechUnderstanderListener);
                    if(ret != 0){
                        showTip("语义理解失败,错误码:"	+ ret);
                    }else {
//                        showTip(getString(R.string.text_begin));
                        showTip("getString(R.string.text_begin)");
                    }
                }
            }
        });
        //↑↑↑ 设置录音按键
        
        //// FIXME: 2017/3/4 监听播放完成的事件
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                onVideoPlaying = false;
                //// TODO: 2017/3/20 添加如果interrupt为true的时候的执行操作   原来这里的代码是if(F_interrupt){}
                if (F_interrupt){
                    // 大白正在说话、或者用户正在说话，大白准备说话动作
                }else {
                    if (F_turnAround_int != 0){
                        //继续转圈
                        play_video_byNum(5, 0);
                        F_turnAround_int = F_turnAround_int - 1;
                    }else {
                        if (F_swingLoop_int != 0){
                            // 程序块， 当F_swingLoop_int==1的时候，播放[退出左右晃]视频
                            if (F_swingLoop_int == 1){
                                //播放[退出左右晃]视频
                                play_video_byNum(4,3);
                                F_swingLoop_int = F_swingLoop_int - 1;
                            }else {
                                //继续左右晃
                                play_video_byNum(4, 2);
                                F_swingLoop_int = F_swingLoop_int - 1;
                            }
                        }else {
                            //既没有打断，也没有晃或者转圈。
                            ////TODO 2017/3/4 选择晃还是转圈（可以根据上一次是转圈还是晃来判断），同时可以播放序号为7-9的微小动作 [done]
                            if (getRandom_int(2) == 2){
                                //执行：播放7-9微小动作
                                play_video_byNum((getRandom_int(3)+6), 0);
                            }else {
                                //执行：选择左右晃或者转圈
                                if (!F_isSwung){
                                    //上一次不是左右晃，则执行左右晃
                                    F_swingLoop_int = getRandom_int(2) + 1;
                                    play_video_byNum(4,1);
                                }else {
                                    //上一次是左右晃，执行转圈
                                    F_turnAround_int = getRandom_int(2);
                                    play_video_byNum(5,0);
                                    F_turnAround_int = F_turnAround_int - 1;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume() {
        //移动数据统计分析
        FlowerCollector.onResume(SecondActivity.this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.greeting_2);
        videoView.setVideoURI(uri);
        onVideoPlaying = true;
        videoView.start();
    }

    private int getRandom_int(int max){
        return (int)((Math.random()) * max) + 1;  // 产生 [1 , max+1) 区间的随机整数
    }

    private void play_video_byNum(int num, int num_2){
        Uri uri_temp;
        switch (num){
            case 1:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.balalala_1);
                break;
            case 2:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.greeting_2);
                break;
            case 3:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.idea_comes_up_3);
                break;
            case 4:
                switch (num_2){
                    case 1:
                        uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.swing_enter_4);
                        break;
                    case 2:
                        uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.swing_loop_4);
                        break;
                    case 3:
                        uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.swing_exit_4);
                        break;
                    default:
                        uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.swing_loop_4);
                        break;
                }
                break;
            case 5:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.turn_around_5);
                break;
            case 6:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.shaking_head_6);
                break;
            case 7:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.turn_left_7);
                break;
            case 8:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.turn_left_right_8);
                break;
            case 9:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.head_up_down_9);
                break;
            default:
                uri_temp = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.turn_left_right_8);
                break;
        }
        videoView.setVideoURI(uri_temp);
        onVideoPlaying = true;
        videoView.start();
    }

    /**
     * 语义理解回调。
     */
    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onResult(final UnderstanderResult result) {
            if (null != result) {
                Log.d(TAG, result.getResultString());

                // 显示
                String text = result.getResultString();
                if (!TextUtils.isEmpty(text)) {
                    //// TODO: 2017/2/26  parse json file   String text is the json result [done]
                    parseJson(text);
                    Log.d("debug", "the JSON :");
                    Log.d("debug", text);
                }
            } else {
                showTip("识别结果不正确。");
            }
        }

        private void parseJson(String jsonString){
            //this json file can not be transformed into JSONArray
            try {
                //// TODO: 2017/2/26      check if  "rc" = 0 first  [done]
                //   初始化变量  凡是Object都初始化为null 凡是String变量都设为 "NOT EXIST"
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject semanticObject = null;
                JSONObject slotsObject = null;
                JSONObject locationObject = null;
                JSONObject answerObject = null;
                String attr = "NOT EXIST", attrValue = "NOT EXIST", attrType = "NOT EXIST",
                        modifier = "NOT EXIST", location_room = "NOT EXIST", location_type = "NOT EXIST";

                if (jsonObject != null ){
                    if (jsonObject.optInt("rc", -1) == 0){
                        semanticObject = jsonObject.optJSONObject("semantic");
                        answerObject   = jsonObject.optJSONObject("answer");
                        if (semanticObject != null){
                            slotsObject = semanticObject.optJSONObject("slots");
                            if (slotsObject != null){
                                locationObject  = slotsObject.optJSONObject("location");
                                attr       =      slotsObject.optString("attr", "NOT EXIST");
                                attrValue  =      slotsObject.optString("attrValue", "NOT EXIST");
                                attrValue  =      slotsObject.optString("attrValue", "NOT EXIST");
                                modifier   =      slotsObject.optString("modifier", "NOT EXIST");
                                if (locationObject != null){
                                    location_room = locationObject.optString("room", "NOT EXIST");
                                    location_type = locationObject.optString("type", "NOT EXIST");
                                }
                                if (attr.equals("开关")){
                                    if (attrValue.equals("开")){
                                        //// TODO: 2017/2/26  ★★★ 将 if (attrValue.equals("开") 下的代码块放在函数中，以便在判断 attrValue为关的时候使用
                                        switch (location_room){
                                            case "浴室":
                                                Log.d("debug", "location_room : 浴室 ; open the light in the bathroom");
                                                mTts.startSpeaking("正在打开浴室的灯", mSynListener);
                                                //// TODO: 2017/2/26   send bluetooth message to control the light one   [done]
                                                Intent intent = new Intent(SecondActivity.this, BluetoothService.class);
                                                intent.putExtra("flag", Bluetooth_Management.TURN_ON_LIGHT_1);
                                                startService(intent);
                                                play_video_byNum(3, 0); //播放idea_comes_up_3
                                                break;
                                            case "客厅":
                                                Log.d("debug", "location_room :客厅 ; open the light in the hall");
                                                //// TODO: 2017/2/26   send bluetooth message to control the light two
                                                break;
                                            default:
                                                switch (modifier){
                                                    case "床头灯":
                                                        break;
                                                    case "吊灯":
                                                        break;
                                                    case "台灯":
                                                        break;
                                                    case "射灯":
                                                        break;
                                                    default:
                                                        break;
                                                }
                                        }
                                    }else if (attrValue == "关"){

                                    }
                                }
                            }else{
                                //// TODO: 2017/2/26 handle error from semantic cloud parse
                            }
                        }
                        if (answerObject != null){
                            //// TODO: 2017/3/4  说话(讯飞智能回复)时 播放视频
                            String type_answer;
                            String text_answer;
                            type_answer = answerObject.optString("type", "NOT EXIST");
                            text_answer = answerObject.optString("text", "NOT EXIST");
                            if (type_answer.equals("T") && !(text_answer.equals("NOT EXIST"))){
                                mTts.startSpeaking(text_answer, mSynListener);
                            }
                        }
                    }
                        //// TODO: 2017/2/26    all JSONObjects must use after checking it`s null or not  [done]
                }

            }catch (JSONException Je){
                Log.d("debug", "Error when create JSONArray : " + Je.toString());
            }catch (Exception e){
                Log.d("debug", "Error when parsing JSON : " + e.toString());
            }finally {
                Log.d("debug", "JSON parsing finish");
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, data.length+"");
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    public void setParam(){
//        String lang = mSharedPreferences.getString("understander_language_preference", "mandarin");
        String lang = "mandarin";
        //getString方法是用来从SharedPreferences中提取字段，参数1是KEY,参数2是如果字段不存在则返回的值
        //此处默认是 中文普通话
        if (lang.equals("en_us")) {
            // 设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
        }else {
            // 设置语言
            mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lang);
        }
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        //mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("understander_vadbos_preference", "4000"));
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS,"4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        //mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("understander_vadeos_preference", "1000"));
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS,"1000");

        // 设置标点符号，默认：1（有标点）
        //mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("understander_punc_preference", "1"));
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,"1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mSpeechUnderstander.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/sud.wav");
    }

    private InitListener mSpeechUdrInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            }
        }
    };

    // 设置返回键的响应
    @Override
    public void onBackPressed(){
        if (onSpeaking){
            mTts.stopSpeaking();
        }
        if (SettingFlag){
            SettingFlag = false;
            ImageButton_Background.setVisibility(View.GONE);
            SettingLayout.setVisibility(View.GONE);
            IntroductionImage.setVisibility(View.GONE);
        }else {
            Intent home = new Intent();
            home.setAction("android.intent.action.MAIN");
            home.addCategory("android.intent.category.HOME");
            startActivity(home);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 退出时释放连接
        mSpeechUnderstander.cancel();
        mSpeechUnderstander.destroy();
    }

    @Override
    protected void onPause() {
        //移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(SecondActivity.this);
        super.onPause();
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    //合成监听器
    private SynthesizerListener mSynListener = new SynthesizerListener(){
        //会话结束回调接口，没有错误时，error为null
        public void onCompleted(SpeechError error) {
            //// FIXME: 2017/3/21 语音播放完成后就执行转圈或者balalala
            if (!onVideoPlaying){
                if (getRandom_int(2) == 1){
                    play_video_byNum(5,1);
                }else {
                    play_video_byNum(1,1);
                }
            }
            F_interrupt = false;
            onSpeaking = false;
        }
//缓冲进度回调
//percent为缓冲进度0~100，beginPos为缓冲音频在文本中开始位置，endPos表示缓冲音频在文本中结束位置，info为附加信息。
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {}
        //开始播放
        public void onSpeakBegin() {
            F_interrupt = true;
            onSpeaking = true;
        }
        //暂停播放
        public void onSpeakPaused() {
            onSpeaking = false;
        }
//播放进度回调
//percent为播放进度0~100,beginPos为播放音频在文本中开始位置，endPos表示播放音频在文本中结束位置.
        public void onSpeakProgress(int percent, int beginPos, int endPos) {}
        //恢复播放回调接口
        public void onSpeakResumed() {}
        //会话事件回调接口
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {}
    };

    private class BleBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            if (intent.getAction().equals("Bluetooth_connected")){
                if (BluetoothService.BLE_STATE_CONNECTED == intent.getIntExtra("Bluetooth_state", -1)){
                    Toast.makeText(SecondActivity.this, "Bluetooth is connected", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
