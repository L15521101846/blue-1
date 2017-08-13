package com.example.lgh.lgh_blue.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.lgh.lgh_blue.R;

import java.util.Timer;
import java.util.TimerTask;

public class Welcome extends Activity {
    private TextView t;
    private   int rec=4;
    Timer timer=new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        t=(TextView)findViewById(R.id.time);
        timer.schedule(task,0,1000);
    }
    TimerTask task=new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    rec--;
                    if(rec<0){
                        Go(Welcome.this.getWindow().getDecorView());
                    }else {
                        t.setText("跳过:"+rec);
                    }
                }
            });

        }
    };

    public void Go(View view){
        timer.cancel();
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter.isEnabled())
        adapter.disable();
        Intent intent = new Intent();
        intent.setClass(Welcome.this, Main.class);
        startActivity(intent);
        finish();
    }
}
