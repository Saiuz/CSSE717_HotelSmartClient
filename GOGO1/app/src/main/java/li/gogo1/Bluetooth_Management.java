package li.gogo1;

//// TODO: 2017/2/26 整个类可以不做太大修改就移植到 GOGO1 APP 中
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class Bluetooth_Management extends Thread {
    public static int TURN_ON_LIGHT_1 = 88888;
    public static int TURN_OFF_LIGHT_1 = 88;
    public static int EMPTY_ACTION = 8;

    public BluetoothAdapter mBT_Adapter;
    public BluetoothSocket mBT_Socket;
    public BluetoothDevice mBT_Device;
    private Set<BluetoothDevice> deviceSet;

    private OutputStream OStream;
    private InputStream IStream;
    private List<String> messageList = new ArrayList<>();

    private boolean isConnected = false;
    private char DELIMITER = '#';

    Bluetooth_Management(){
        try {
            this.mBT_Adapter = BluetoothAdapter.getDefaultAdapter();
        }catch (Exception e){
            Log.d("debug", "get default adapter error. Exception : " + e.toString());
        }
        if (mBT_Adapter == null)
            Log.d("debug", "this device does not support Bluetooth！");
        else if(!mBT_Adapter.isEnabled())
            Log.d("debug", "Bluetooth is not activated");
        try {
            deviceSet = mBT_Adapter.getBondedDevices();
            Log.d("debug", deviceSet.toString());
        }catch (Exception e){
            Log.d("debug", "get bonded devices error, Exception : " + e.toString());
        }
        try {
            for (BluetoothDevice BT_Device : deviceSet)
                Log.d("debug", "String: " + BT_Device.toString() + "Name" + BT_Device.getName());
        }
        catch (Exception e){
            Log.d("debug", e.toString());
        }
    }

    public boolean connect(BluetoothDevice Device_to_Connect){
        try {
            /**
             *  add at 2017/02/26
             * Cancel discovery because it will slow down the connection !!!
             */
            mBT_Adapter.cancelDiscovery();
            // ↑↑ add at 2017/02/26

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mBT_Socket = Device_to_Connect.createRfcommSocketToServiceRecord(uuid);
            mBT_Socket.connect();
            //// TODO: 2017/2/18  connect为阻塞调用，需要显示进度条
            OStream = mBT_Socket.getOutputStream();
            IStream = mBT_Socket.getInputStream();

            isConnected = true;
            if (isConnected){
                Log.d("debug", "has connected to device " + Device_to_Connect.getName());
            }
            this.start();
            return true;
        }catch (Exception e){
            Log.d("debug", "Error when connecting! ——" + e.toString());
            return false;
        }
    }

    @Override
    public void run(){
        while (true){
            try {
                byte ch, buffer[] = new byte[1024];
                int i = 0;

                while((ch=(byte)IStream.read()) != DELIMITER){
                    buffer[i++] = ch;
                }
                buffer[i] = '\0';
                final String msg = new String(buffer);
                messageHandle(msg.trim());
                Log.d("debug", "message receive :" + msg.trim().toString());
            }catch (Exception e){
                Log.d("debug", "Error while running the receive thread: " + e.toString());
            }
        }
    }
    /*通过蓝牙发出数据，从主线程中直接调用 */
    public void sendMessage(byte[] bytes){
        try {
//            String UTF_8str = new String(bytes, "UTF-8");
//            Log.d("debug", "--------->>>>>>" + bytes.toString() + "UTF-8 : " + UTF_8str + " getbytes : " + UTF_8str.getBytes());
            OStream.write(bytes);
//            OStream.write(UTF_8str.getBytes());
            Log.d("debug", "OutputStream is: " + OStream.toString());
        }catch (IOException e){
            Log.d("debug", "Error when sending message : "+ e.toString());
        }catch (Exception e){
            Log.d("debug", "Error when sending message [Exception,3/20] : "+ e.toString());
        }
    }

    public void sendMessage(int intTYPE){
        try {
            if (intTYPE == TURN_ON_LIGHT_1)
                OStream.write('a');
            Log.d("debug", "enter sendMessage(int)");
        }catch (IOException e){
            Log.d("debug", "IOException when sending int type message :" + e.toString());
        }
    }

    private void messageHandle(String message){
        try {
            messageList.add(message);
            try {
                this.notify();
            }catch (IllegalMonitorStateException e){
                Log.d("debug", "Error when this(Bluetooth_Management).notify: " + e.toString());
            }
        }catch (Exception e){
            Log.d("debug", "Error when execute messageHandle method: " + e.toString());
        }
    }

    public void clearAllMessage(){
        messageList.clear();
    }
    public int countMessages(){
        return messageList.size();
    }
    public String getLastMessage(){
        if (messageList.size() == 0){
            Log.d("debug", "The message list is empty ");
            return "";
        }
        return messageList.get(messageList.size()-1);
    }
    public String getMessage(int i){
        return messageList.get(i);
    }
    public boolean isMessageExist(int i){
        try {
            String s = messageList.get(i);
            if (s.length()>0)
                return true;
            else
                return false;
        }catch (Exception e){
            Log.d("debug", "Error when checking if NO.i message exist in the messageList" + e.toString());
            return false;
        }
    }

}
