package li.gogo1;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class EntranceActivity extends AppCompatActivity {

    private Bluetooth_Management bluetooth_management;                       //★★★
    private Toast mtoast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        bluetooth_management = new Bluetooth_Management();                   //★★★
        bluetooth_management.mBT_Adapter.startDiscovery();                   //★★★
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//★★★
        registerReceiver(mReceiver, filter);                                 //★★★
    }

    //// TODO: 2017/2/27 如何使得 BroadcastReceiver 异步执行 : 答案->通过在onReceive方法内部启动服务，在服务中执行耗时操作
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {          //★★★
        @Override
        public void onReceive(Context context, Intent intent) {

//            Intent startServiceIntend = new Intent(EntranceActivity.this, BluetoothService.class);
//            startServiceIntend.putExtra("bluManagement", bluetooth_management);

//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_FOUND.equals(action)){
//                BluetoothDevice device_found = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                try {
//
//                    //// FIXME: add at 2017/2/26  如果发现蓝牙设备是 OBAMA 或者 TRUMP，马上连接，然后中断检索
//                    Log.d("debug", "add at 2017/2/26 : find the device :" + device_found.getName());
//                    if (device_found.getName().equals("OBAMA") || device_found.getName().equals("TRUMP")) {
//                        try {
//                            /**
//                             *  if括号中执行connect() , 并返回是否连接上的信息，如果连接上了，就中断检索蓝牙设备
//                             */
//                            if (bluetooth_management.connect(device_found)){
//                                //// TODO: 2017/2/27 connect是阻塞调用，需要显示进度条
//                                //// FIXME: 2017/2/27 这里的cancelDiscovery很可能冗余，因为上一行的connect方法已经调用了cancelDiscovery
//                                bluetooth_management.mBT_Adapter.cancelDiscovery();
//
//                                mtoast.setText("connected to " + device_found.getName());
//                                mtoast.show() ;
//                            }
//
//                        }catch (Exception e){
//                            Log.d("debug", "add at 2017/02/26 : " + "Error when connect to the device : " + e.toString());
//                        }
//                    }
//                    // ↑↑以上 add at 2017/02/26
//
//                }catch (Exception e){
//                    Log.d("debug", "ArrayAdapter error : " + e.toString());
//                }
//                Log.d("debug", "after onReceive" + "deviceUUID: " + device_found.getUuids().toString() + " deviceName: " +device_found.getName() + " deviceAddress: " + device_found.getAddress());
//            }
        }
    };
}
