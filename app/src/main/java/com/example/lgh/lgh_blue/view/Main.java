package com.example.lgh.lgh_blue.view;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;

import com.example.lgh.lgh_blue.R;
import com.example.lgh.lgh_blue.presenter.MainDialog;
import com.example.lgh.lgh_blue.presenter.Socket;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.example.lgh.lgh_blue.view.Connect.SER_KEY;


public class Main extends Activity {
    private BluetoothAdapter adapter;
    private Boolean JiantTing;
    private BluetoothSocket socket;
    private Boolean socketConnectChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter =BluetoothAdapter.getDefaultAdapter();
        JiantTing = false;
        socketConnectChat = false;
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) return  true;
        return super.onKeyDown(keyCode,event);
    }

    public void Chat(View view){
        openBluetooth();
        if(adapter.isEnabled())
        dialog("创建聊天室","加入聊天室",true);
    }

    public void Game(View view) {
        openBluetooth();
        if(adapter.isEnabled())
        dialog("创建游戏房间","加入游戏房间",true);
    }
    private void dialog(final String content1, String content2, final boolean send){
        new MainDialog(Main.this.getWindow().getContext(), R.style.dialog_game, content1,content2, new MainDialog.OnCloseListener() {
            @Override
            public void onClick(final Dialog dialog, String string, boolean confirm) {
                switch (string){
                    case "创建聊天室":{
                        dialog.dismiss();
                        createRoom("Chat");
                        break;
                    }
                    case "加入聊天室":{
                        accessRoom("Chat");
                        dialog.dismiss();
                        break;
                    }
                    case "创建游戏房间":{
                        dialog.dismiss();
                        createRoom("Game");
                        break;
                    }
                    case "加入游戏房间":{
                        accessRoom("Game");
                        dialog.dismiss();
                    }
                    case "取消":{
                        dialog.dismiss();
                        break;
                    }
                        default:break;
                }
            }
        }).show();
    }
    private void accessRoom(String toWhat){
        Intent intent = new Intent();
        Socket s = new Socket(socket);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(SER_KEY, s);
        intent.putExtras(mBundle);
        intent.putExtra("toWhat",toWhat);
        intent.setClass(Main.this,Connect.class);
        startActivity(intent);
    }
    public boolean openBluetooth(){
        if (adapter == null )
            return false;
        // 打开蓝牙
        if (!adapter.isEnabled())
        {
            //启动修改蓝牙可见性的Intent
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            //设置蓝牙可见性的时间，方法本身规定最多可见300秒
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3000);
            startActivity(intent);
        }
        return false;
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
    private void createRoom(final String toWhat){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (JiantTing){
                    jianTingConnect(toWhat);
                    Looper.prepare();
                    dialog("匹配ing...","取消",true);
                    Looper.loop();
                }
                if (adapter.isEnabled() && !JiantTing){
                    createRoomRun();
                    jianTingConnect(toWhat);
                    Looper.prepare();
                    dialog("匹配中...","取消",true);
                    Looper.loop();
                }
            }
        }).start();
    }
    public void createRoomRun() {
        if (!JiantTing) {
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
        }
    }
    private void jianTingConnect(final String toWhat){
        new Thread(new Runnable() {//匹配成功跳转...
            @Override
            public void run() {
                while (!socketConnectChat) {
                    if (socket != null) {
//                        Looper.prepare();
//                        Toast.makeText(Main.this.getWindow().getContext(), "连接成功"+socket.isConnected(), Toast.LENGTH_SHORT).show();
//                        Looper.loop();
                        socketConnectChat = true;
                        Intent intent;
                        if (toWhat.equals("Chat"))
                            intent = new Intent(Main.this, Chat.class);
                        else
                            intent = new Intent(Main.this, Game.class);
                        Socket s = new Socket(socket);
                        Bundle mBundle = new Bundle();
                        mBundle.putSerializable(SER_KEY, s);
                        intent.putExtras(mBundle);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        }).start();
    }
}
