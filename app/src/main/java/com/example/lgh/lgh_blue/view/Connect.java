package com.example.lgh.lgh_blue.view;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lgh.lgh_blue.R;
import com.example.lgh.lgh_blue.model.DeviceAdapter;
import com.example.lgh.lgh_blue.presenter.Socket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Connect extends Activity {
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private ListView mMatchedList;//已匹配设备
    private ListView mNearList;//可匹配设备
    private TextView mOpen;//打开蓝牙
    private ImageView back;
    private boolean JiantTing;//监听
    private List<String> Device_name = new ArrayList<String>();//放可用设备的名字
    private List<BluetoothDevice> Device = new ArrayList<BluetoothDevice>();//放可用设备
    private List<String> Device_name2 = new ArrayList<String>();//放已配对设备的名字
    private Set<BluetoothDevice> Devices;//放已配对设备
    private DeviceAdapter mDeviceadapter;
    private DeviceAdapter mDeviceadapter2;
    private TextView search,connect;//搜索、连接

    private static  final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION  = 100;
    public  final static String SER_KEY = "com.example.lgh.lgh_blue.presenter.Socket";
    private BroadcastReceiver receiver  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//获得已经搜索到的蓝牙设备
            System.out.println(action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //搜索到的蓝牙设备加入到一个list中
                if(device.getName()!=null){
                    Device_name.add(device.getName());
                    Device.add(device);
                    mDeviceadapter = new DeviceAdapter(Connect.this, R.layout.device_item, Device_name);
                    mNearList.setAdapter(mDeviceadapter);

                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);
        initView();
    }

    private void initView(){
        mMatchedList=(ListView)findViewById(R.id.matched_list);
        mNearList=(ListView)findViewById(R.id.near_list);
        back=(ImageView)findViewById(R.id.search_back);
        adapter = BluetoothAdapter.getDefaultAdapter();
        JiantTing=false;
        mNearList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                adapter.cancelDiscovery();
                device = Device.get(position);

                int connetTime = 0;
                boolean connecting = true;


                boolean connected = false;
                initSocket();
                while (!connected && connetTime <= 5) {
                    try {
                        socket.connect();
                        connected = true;
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
                        connecting = false;
                    }
                }
            }

        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Connect.this,Main.class);
                Socket s = new Socket(socket);
                Bundle mBundle = new Bundle();
                mBundle.putSerializable(SER_KEY,s);
                intent.putExtras(mBundle);
                startActivity(intent);
                finish();
            }
        });
        search=(TextView)findViewById(R.id.search);
        connect=(TextView)findViewById(R.id.connect);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peidui(view);
            }
        });
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lianjie(view);
            }
        });
    }
    private void waitconnect() throws IOException {

        Method listenMethod = null;
        try {
            listenMethod = adapter.getClass().getMethod("listenUsingRfcommOn", new Class[]{int.class});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        BluetoothServerSocket mmServerSocket = null;
        try {
            mmServerSocket = (BluetoothServerSocket) listenMethod.invoke(adapter, Integer.valueOf(29));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        socket = mmServerSocket.accept();
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


    public void peidui(View view) {
        if (adapter == null)
        {
        }
        // 打开蓝牙
        if (!adapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 设置蓝牙可见性，最多300秒
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(intent);

        }
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
            Device_name.clear();
            registerReceiver(receiver,mFilter);
            //查找已经配对的设备，加载到Matched列表中
            Devices=adapter.getBondedDevices();
            if (Devices.size()>0){
                for (BluetoothDevice bluetoothDevice:Devices){
                        if (!Device_name2.contains(bluetoothDevice.getName()))
                            Device_name2.add(bluetoothDevice.getName());
                        //Toast.makeText(Connect.this,device.getName(),Toast.LENGTH_SHORT).show();
                        mDeviceadapter2 = new DeviceAdapter(Connect.this,R.layout.device_item, Device_name2);
                        mMatchedList.setAdapter(mDeviceadapter2);
                }
            }
        }

    }


    public void lianjie(View view) {
        if (!JiantTing) {
            //Toast.makeText(Connect.this,"click",Toast.LENGTH_SHORT).show();
            JiantTing=true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        waitconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
//            final Timer timer=new Timer();
//            TimerTask task=new TimerTask() {
//                int rec=11;
//                @Override
//                public void run() {
//                    rec--;
//                    if(rec<0){
//                        timer.cancel();
//                        JiantTing=false;
//                    }
//                }
//            };
//            timer.schedule(task,1000,1000);
        }
    }
}
