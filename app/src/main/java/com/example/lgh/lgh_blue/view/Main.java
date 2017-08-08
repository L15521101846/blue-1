package com.example.lgh.lgh_blue.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.lgh.lgh_blue.R;
import com.example.lgh.lgh_blue.presenter.Socket;


public class Main extends Activity {

    public  final static String SER_KEY = "com.example.lgh.lgh_blue.presenter.Socket";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void Chat(View view){
        Intent intent=new Intent();
        intent.setClass(Main.this,Chat.class);
        final Socket socket = (Socket)getIntent().getSerializableExtra(SER_KEY);
        if (socket!=null) {
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(SER_KEY, socket);
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }

    public void Game(View view) {
        Intent intent=new Intent();
        intent.setClass(Main.this,Game.class);
        final Socket socket = (Socket)getIntent().getSerializableExtra(SER_KEY);
        if (socket!=null) {
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(SER_KEY, socket);
            intent.putExtras(mBundle);
            startActivity(intent);
        }
    }

    public void Me(View view) {
        Intent intent = new Intent();
        intent.setClass(Main.this,Connect.class);
        startActivity(intent);
    }
}
