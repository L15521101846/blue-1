package com.example.lgh.lgh_blue.presenter;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by sevenjun on 17/2/12.
 */

public class Socket implements Serializable {
    private static BluetoothSocket bluetoothSocket;
    private byte Stream_Data1[] = new byte[500];//发送
    private byte Stream_Data2[] = new byte[500];//接收
    private String String_Data;


    public  Socket(BluetoothSocket bluetoothSocket){
        this.bluetoothSocket = bluetoothSocket;
    }

    public void Chat_Sent(String Msg) throws IOException {
        Stream_Data1 = Msg.getBytes();
        try {
            bluetoothSocket.getOutputStream().write(Stream_Data1);
        }catch (IOException e){

        }
    }

    public String Chat_receive() throws IOException {
        int i = 0;
        try {
            i =   bluetoothSocket.getInputStream().read(Stream_Data2);
        }catch (IOException e){
           // Log.e("有问题","有问题");
        }
        String Data = new String(Stream_Data2);
        String_Data = Data.substring(0,i);
        return String_Data;
    }


    public void Chess_Sent(Coord k) throws IOException {
        String t = k.getFrom()+","+k.getTo();
        Chat_Sent(t);
    }

    public Coord Chess_receive() throws IOException {
        Chat_receive();
        String str[]=String_Data.split(",");
        int from = Integer.parseInt(str[0]);
        int to = Integer.parseInt(str[1]);
        Coord Coord1 = new Coord(from,to);
        return Coord1;
    }
    public void NewGame_Sent(String ng) throws IOException {
        String str=ng;
        Chat_Sent(str);
    }
    public Boolean NewGame_receive() throws IOException {
        Chat_receive();
        if (String_Data.equals("NewGame"))
        return true;
        else return false;
    }
    public void BackGame_Sent(String ng) throws IOException {
        String str=ng;
        Chat_Sent(str);
    }
    public Boolean BackGame_receive() throws IOException {
        Chat_receive();
        if (String_Data.equals("BackGame"))
            return true;
        else return false;
    }
}
