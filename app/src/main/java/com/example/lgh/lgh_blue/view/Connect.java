package com.example.lgh.lgh_blue.view;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lgh.lgh_blue.R;
import com.example.lgh.lgh_blue.model.DeviceAdapter;
import com.example.lgh.lgh_blue.presenter.Socket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Connect extends Activity {
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private ListView mMatchedList;//已匹配设备
    private ListView mNearList;//可匹配设备
    private List<String> Device_Near_name = new ArrayList<String>();//放可用设备的名字
    private List<BluetoothDevice> Device_near = new ArrayList<BluetoothDevice>();//放附近可用设备
    private List<BluetoothDevice> Device_match = new ArrayList<BluetoothDevice>();//放匹配可用设备
    private List<String> Device_Match_name = new ArrayList<String>();//放已配对设备的名字

    private DeviceAdapter mDeviceadapter_Near;
    private DeviceAdapter mDeviceadapter_Match;
    private TextView search;//搜索、连接
    private String toWhat;//跳转到的页面
    private static  final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION  = 100;
    public  final static String SER_KEY = "com.example.lgh.lgh_blue.presenter.Socket";
    private BroadcastReceiver receiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//获得已经搜索到的蓝牙设备
            System.out.println(action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART);
                //搜索到的蓝牙设备加入到一个list中
                if(device.getName()!=null && device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART){
                    if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                        if (!Device_Match_name.contains(device.getName())){
                            Device_Match_name.add(device.getName());
                            Device_match.add(device);
                            mDeviceadapter_Match = new DeviceAdapter(Connect.this,R.layout.device_item,Device_Match_name);
                            mMatchedList.setAdapter(mDeviceadapter_Match);
                        }
                    }else{
                        if (!Device_Near_name.contains(device.getName())){
                            Device_Near_name.add(device.getName());
                            Device_near.add(device);
                            mDeviceadapter_Near = new DeviceAdapter(Connect.this, R.layout.device_item, Device_Near_name);
                            mNearList.setAdapter(mDeviceadapter_Near);
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        initView();
    }

    private void initView(){
        mMatchedList=(ListView)findViewById(R.id.matched_list);
        mNearList=(ListView)findViewById(R.id.near_list);
        adapter = BluetoothAdapter.getDefaultAdapter();//获得本设备的蓝牙适配器实例
        Intent intent =getIntent();
        toWhat = intent.getStringExtra("toWhat");
        mMatchedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.cancelDiscovery();//停止搜索
                device = Device_match.get(position);
                int connetTime = 0;
                boolean connected = false;
                initSocket();
                while (!connected && connetTime <= 5) {
                    try {
                        socket.connect();
                        connected = true;
                        if (socket.isConnected()) {
                            Intent intent;
                            if (toWhat.equals("Chat"))
                                intent = new Intent(Connect.this, Chat.class);
                            else
                                intent = new Intent(Connect.this, Game.class);
                            Socket s = new Socket(socket);
                            Bundle mBundle = new Bundle();
                            mBundle.putSerializable(SER_KEY, s);
                            intent.putExtras(mBundle);
                            startActivity(intent);
                            finish();
                        }
                    } catch (IOException e1) {
                        connetTime++;
                        connected = false;
                        // 关闭 socket
                        try {
                            socket.close();
                            socket = null;
                        } catch (IOException e2) {
                            //TODO: handle exception
                        }
                    } finally {
                    }
                }
            }
        });
        mNearList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    // 连接建立之前的先配对
                    adapter.cancelDiscovery();//停止搜索
                    device = Device_near.get(position);
                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        Method creMethod = BluetoothDevice.class
                                .getMethod("createBond");
                        Toast.makeText(view.getContext(),"配对中...",Toast.LENGTH_SHORT).show();
                        creMethod.invoke(device);
                    } else {
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    Toast.makeText(view.getContext(),"无法匹配！",Toast.LENGTH_SHORT).show();
                    //DisplayMessage("无法配对！");
                    e.printStackTrace();
                }
            }
        });
        search=(TextView)findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchBlueDevice(view);
            }
        });
    }
    private void initSocket() {
        BluetoothSocket temp = null;
        try {
            Method m = device.getClass().getMethod(
                    "createRfcommSocket", new Class[] { int.class });
            temp = (BluetoothSocket) m.invoke(device, 29);//这里端口为29
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        socket = temp;
    }


    public void searchBlueDevice(View view) {
        if(adapter.isEnabled()){
            IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND );
            mFilter.addAction(BluetoothDevice.ACTION_FOUND);
            mFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            mFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            // 注册广播接收器，接收并处理搜索结果
            if (Build.VERSION.SDK_INT >= 23) {
                //校验是否已具有模糊定位权限
                if (ContextCompat.checkSelfPermission(Connect.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Connect.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                } else {
                    //具有权限
                    adapter.startDiscovery();
                }
            } else {
                //系统不高于6.0直接执行
                adapter.startDiscovery();
            }
            Device_near.clear();
            Device_Near_name.clear();
            Device_match.clear();
            Device_Match_name.clear();
            registerReceiver(receiver,mFilter);
        }
    }
}
