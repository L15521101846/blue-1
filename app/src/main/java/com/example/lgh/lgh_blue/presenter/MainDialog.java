package com.example.lgh.lgh_blue.presenter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.example.lgh.lgh_blue.R;

/**
 * Created by LGH on 2017/8/8.
 */

public class MainDialog extends Dialog implements View.OnClickListener{
    private TextView createTxt;
    private TextView accessTxt;
    private Context mContext;
    private String content1;
    private String content2;
    private OnCloseListener listener;
    public MainDialog(Context context) {
        super(context);
        this.mContext=context;
    }
    public MainDialog(Context context, int themeResId, String content1,String content2,OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content1=content1;
        this.content2=content2;
        this.listener = listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_main);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        createTxt=(TextView)findViewById(R.id.createR);
        accessTxt=(TextView)findViewById(R.id.accessR);
        if (!content1.equals("匹配中..."))
        createTxt.setOnClickListener(this);
        createTxt.setText(content1);
        accessTxt.setOnClickListener(this);
        accessTxt.setText(content2);
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) return  true;
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.createR && listener != null){
            listener.onClick(this,content1,true);
        }
        if (view.getId() == R.id.accessR && listener != null){
            listener.onClick(this,content2,true);
        }
    }
    public interface OnCloseListener{
        void onClick(Dialog dialog,String string, boolean confirm);
    }
}
