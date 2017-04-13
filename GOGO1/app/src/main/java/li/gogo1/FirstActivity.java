package li.gogo1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Timer;
import java.util.TimerTask;

public class FirstActivity extends Activity {

    private IntentFilter intentFilter;
    private BleBroadcastReceiver blueBroadcastReceiver;
    private static final int TIMEOUT = 401;
    private boolean ConnectedFlag = false;
    private TimerTask timerTask;
    private Timer timer;
    private Button button_enter;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView text_introduction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        button_enter = (Button)findViewById(R.id.button_enter);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        textView = (TextView)findViewById(R.id.text_loading);
        text_introduction = (TextView)findViewById(R.id.text_introduction);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case TIMEOUT:
                        if (!ConnectedFlag){
                            Toast.makeText(FirstActivity.this, "BLE is still connecting", Toast.LENGTH_LONG).show();
                            button_enter.setVisibility(View.VISIBLE);
                            ConnectedFlag = true;
                        }
                        break;
                }
            }
        };

//        Toolbar mToolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(mToolbar);

        Intent startBleServiceIntent = new Intent(FirstActivity.this, BluetoothService.class);
        startService(startBleServiceIntent);

        /**
         * 　注册广播接收器   问题：能否在handleMessage方法中一并处理
         */
        intentFilter = new IntentFilter();
        intentFilter.addAction("Bluetooth_connected");
        blueBroadcastReceiver = new BleBroadcastReceiver();
        registerReceiver(blueBroadcastReceiver, intentFilter);
        // ↑↑ 注册广播接收器

        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(handler.sendEmptyMessage(TIMEOUT)){
                    timerTask.cancel();
                }
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 3000);

        button_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });

        //// TODO: 2017/2/27  load videos

        //// TODO: 2017/2/28  check Internet

    }

    private void viewChange(){
        progressBar.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        text_introduction.setVisibility(View.VISIBLE);
        button_enter.setVisibility(View.VISIBLE);
    }

    private class BleBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            if (intent.getAction().equals("Bluetooth_connected")){
                if (BluetoothService.BLE_STATE_CONNECTED == intent.getIntExtra("Bluetooth_state", -1)){
                    Toast.makeText(FirstActivity.this, "Bluetooth is connected", Toast.LENGTH_LONG).show();
                    if (!ConnectedFlag){
                        viewChange();
                        ConnectedFlag = true;
                    }else {
                        Toast.makeText(FirstActivity.this, "Bluetooth is connected", Toast.LENGTH_LONG).show();
                    }
                }
            }
            //// TODO: 2017/3/1  与Timer配合
        }
    }
}
