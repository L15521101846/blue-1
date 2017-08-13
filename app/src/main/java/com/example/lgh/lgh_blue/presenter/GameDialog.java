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

public class GameDialog extends Dialog implements View.OnClickListener{
    private TextView contentTxt;
    private TextView okTxt;
    private Context mContext;
    private String content;
    private OnCloseListener listener;
    public GameDialog(Context context) {
        super(context);
        this.mContext=context;
    }
    public GameDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.content=content;
        this.listener = listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_game);
        setCanceledOnTouchOutside(false);
        initView();
    }
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK) return  true;
        return super.onKeyDown(keyCode,event);
    }

    private void initView() {
        contentTxt=(TextView)findViewById(R.id.content);
        okTxt=(TextView)findViewById(R.id.ok);
        okTxt.setOnClickListener(this);
        contentTxt.setText(content);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ok && listener != null){
            listener.onClick(this,true);
            this.dismiss();
        }
    }
    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }
}
