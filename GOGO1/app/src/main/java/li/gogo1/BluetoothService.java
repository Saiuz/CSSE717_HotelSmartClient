package li.gogo1;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 *   将所有蓝牙操作转移到这个Service中来
 */

public class BluetoothService extends Service {

    public static int BLE_STATE_CONNECTED = 666;

    private Bluetooth_Management bluetooth_management;

    public BluetoothService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        bluetooth_management = new Bluetooth_Management();                   //★★★
        if (bluetooth_management.mBT_Adapter != null){
            bluetooth_management.mBT_Adapter.startDiscovery();               //★★★
        }
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);//★★★
        registerReceiver(broadcastReceiver, filter);                         //★★★
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //// TODO: 2017/2/27 用来在第二个活动中发送数据  在这个方法中写发送数据的程序块
//        byte flag = intent.getByteExtra("flag", (byte)1);
//        if (flag != (byte) 1){
//            byte[] bytes = new byte[1];
//            bytes[0] = flag;
//            bluetooth_management.sendMessage(flag);
//        }
//        return super.onStartCommand(intent, flags, startId);
        int flag = intent.getIntExtra("flag", 0);
        if (flag == Bluetooth_Management.EMPTY_ACTION){
            return super.onStartCommand(intent, flags, startId);
        }else if (flag == Bluetooth_Management.TURN_ON_LIGHT_1){
            byte[] bytes = new byte[1];
            bytes[0] = 'a';
            bluetooth_management.sendMessage(bytes);
            return super.onStartCommand(intent, flags, startId);
        }else {
            return super.onStartCommand(intent, flags, startId);
        }
    }

    @Override
    public void onDestroy(){
        Log.d("debug", "BlueTooth_Service : onDestroy!!!!!!!   onDestroy   onDestroy   onDestroy   onDestroy   onDestroy   onDestroy   ");
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    //// FIXME: add at 2017/2/26
                    Log.d("debug", "add at 2017/2/26 : find the device :" + device.getName());
                    if (device.getName().equals("OBAMA") || device.getName().equals("TRUMP")) {
                        try {
                            /**
                             *  if括号中执行connect() , 并返回是否连接上的信息，如果连接上了，就中断检索蓝牙设备
                             */
                            if (bluetooth_management.connect(device)){
                                //// TODO: 2017/3/1 发送broadcast 告诉FirstActivity，蓝牙已经连接上  if块的最后一行检测cancelDiscovery是否是必要的 [done]
                                Intent broadcastIntent = new Intent("Bluetooth_connected");
                                broadcastIntent.putExtra("Bluetooth_state", BluetoothService.BLE_STATE_CONNECTED);
                                sendBroadcast(broadcastIntent);
                                bluetooth_management.mBT_Adapter.cancelDiscovery();
                            }

                        }catch (Exception e){
                            Log.d("debug", "add at 2017/02/26 : " + "Error when connect to the device : " + e.toString());
                        }
                    }
                    // ↑↑以上 add at 2017/02/26
                }catch (Exception e){
                    Log.d("debug", "ArrayAdapter error : " + e.toString());
                }
                Log.d("debug", "deviceUUID: " + device.getUuids() + " deviceName: " +device.getName() + " deviceAddress: " + device.getAddress());
            }
        }
    };
}
