package com.example.lgh.lgh_blue.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.lgh.lgh_blue.R;
import com.example.lgh.lgh_blue.model.ExpressionAdapter;
import com.example.lgh.lgh_blue.model.MessageAdapter;
import com.example.lgh.lgh_blue.presenter.ChatMessage;
import com.example.lgh.lgh_blue.presenter.Socket;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.example.lgh.lgh_blue.view.Connect.SER_KEY;

public class Chat extends Activity {
    private ImageButton sd,emt;//表情按钮和发送按钮
    private EditText msg;//输入框
    private List<ChatMessage> mData;//消息数据
    private ListView mListView;//显示消息的ListView
    private MessageAdapter mMessageAdapter;//消息适配器
    private GridView mGridView;//显示表情的GridView
    private Spanned mSpanded;//富文本
    private Html.ImageGetter mImageGetter;//获得富文本图片
    private ExpressionAdapter mExpressionAdapter;//表情适配器
    private InputMethodManager mInputMethodManager;//用于控制手机键盘的显示有否的对象
    private Handler handler=null;
    Socket socket;
    Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            mMessageAdapter.notifyDataSetChanged();
            mListView.setSelection(mData.size()-1);
        }
    };



    //表情数据名称
    private  String [] mExpression={"exp1","exp2","exp3","exp4","exp5","exp6",
            "exp7","exp8","exp9","exp10","exp11",
    "exp12","exp13","exp14","exp15"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setLayout();
    }
    private void setLayout(){
        sd=(ImageButton)findViewById(R.id.send);
        emt=(ImageButton)findViewById(R.id.emotion);
        msg=(EditText)findViewById(R.id.msg_edt);
        mListView=(ListView)findViewById(R.id.message_list);
        mGridView=(GridView)findViewById(R.id.chat_emotion);
        socket = (Socket)getIntent().getSerializableExtra(SER_KEY);
        handler= new Handler();
        mInputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //开一个接收信息的线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        String Receive = socket.Chat_receive();
                        socket_receive(Receive);
                        handler.post(runnableUi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        //通过反射获得图片的id
        mImageGetter=new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String s) {
                Drawable drawable=null;
                int id=R.mipmap.exp1;
                if(s!=null){
                    Class clazz=R.mipmap.class;
                    try {
                        Field field=clazz.getDeclaredField(s);
                        id=field.getInt(s);
                    }catch (NoSuchFieldException e){
                        e.printStackTrace();
                    }catch (IllegalAccessException e){
                        e.printStackTrace();
                    }
                }
                drawable=getResources().getDrawable(id);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                return drawable;
            }
        };
        /*
        * 绑定表情适配器
        * */
        mExpressionAdapter=new ExpressionAdapter(getLayoutInflater());
        mGridView.setAdapter(mExpressionAdapter);

        //消息数据初始化
        mData=new ArrayList<ChatMessage>();
        /*
        * 初始化消息适配器
        * */
        mMessageAdapter=new MessageAdapter(getLayoutInflater(),mData,mImageGetter);
        mListView.setAdapter(mMessageAdapter);
        /*
        * 发送按钮随消息框变化
        * */
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                 int len=msg.length();
                if(len==0){
                    sd.setBackgroundDrawable(getResources().getDrawable(R.mipmap.add_2x));
                }
                else{
                    sd.setBackgroundDrawable(getResources().getDrawable(R.mipmap.send_2x));
                }
            }
        });
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //通过mImageGetter获得id获得表情图片，然后将其添加到输入框中。
                mSpanded=Html.fromHtml("<img src='"+mExpression[position]+"'/>",mImageGetter,null);
                msg.getText().insert(msg.getSelectionStart(),mSpanded);
                //msg.getText().insert(msg.getSelectionStart(),String.valueOf(mExpression[position]));
            }
        });
        msg.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(mGridView.getVisibility()==View.VISIBLE){
                    mGridView.setVisibility(View.GONE);
                    emt.setBackground(getDrawable(R.mipmap.emoticon_2x));
                    }

            }
        });
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mListView.setFocusable(true);
                mListView.setFocusableInTouchMode(true);
                mListView.requestFocus();
                InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(msg.getWindowToken(),0);

                if(mGridView.getVisibility()==View.VISIBLE){
                    mGridView.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            emt.setBackground(getDrawable(R.mipmap.emoticon_2x));
                        }
                    }
                }
                return false;
            }
        });
    }
    private void socket_receive(String news){
        ChatMessage dataReceive=new ChatMessage();
        dataReceive.setTime(System.currentTimeMillis());
        /*
        * 判断发送的信息是否为空，如果为空则弹出提示不允许发送
        * */
        if(news.equals("")){
            //Toast.makeText(Chat.this,"消息不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        dataReceive.setInput(filterHtml(news));
        dataReceive.setType(MessageAdapter.RECEIVE);
        dataReceive.setPerson(R.mipmap.portrait1_2x);
        mData.add(dataReceive);
    }
//    public void receive(View view) {
//        ChatMessage dataReceive=new ChatMessage();
//        dataReceive.setTime(System.currentTimeMillis());
//        /*
//        * 判断发送的信息是否为空，如果为空则弹出提示不允许发送
//        * */
//        if(filterHtml(Html.toHtml(msg.getText())).equals("")){
//            //Toast.makeText(Chat.this,"消息不能为空",Toast.LENGTH_SHORT).show();
//            return;
//        }
//            dataReceive.setInput(filterHtml(Html.toHtml(msg.getText())));
//            dataReceive.setType(MessageAdapter.RECEIVE);
//            dataReceive.setPerson(R.mipmap.portrait1_2x);
//            mMessageAdapter.notifyDataSetChanged();
//            mData.add(dataReceive);
//            //mListView.setAdapter(mMessageAdapter);
//            mListView.setSelection(mData.size() - 1);
//            msg.setText("");
//    }

    private String filterHtml(String str) {
        str=str.replaceAll("<(?!br|img)[^>]+>","").trim();
        return str;
    }

    public void send(View view) {
        ChatMessage dataSend=new ChatMessage();
        dataSend.setTime(System.currentTimeMillis());
        /*
        * 判断发送的信息是否为空，如果为空则弹出提示不允许发送
        * */
        if(filterHtml(Html.toHtml(msg.getText())).equals("")){
            //Toast.makeText(Chat.this,"消息不能为空",Toast.LENGTH_SHORT).show();
            /*
            * 添加add功能
            * */

            return;
        }
            dataSend.setInput(filterHtml(Html.toHtml(msg.getText())));
            dataSend.setType(MessageAdapter.SEND);
            dataSend.setPerson(R.mipmap.portrait2_2x);
            mData.add(dataSend);
            mListView.setSelection(mData.size()-1);
            mMessageAdapter.notifyDataSetChanged();
        try {
            socket.Chat_Sent(Html.toHtml(msg.getText()).toString());
        } catch (IOException e) {
            e.printStackTrace();
           // Log.e("有问题","有问题");
        }
        msg.setText("");

    }

    public void emotion(View view) {
        /*
        * 收起GridView同时改变图标
        * */
        if(mGridView.getVisibility()==View.VISIBLE){
            mGridView.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    emt.setBackground(getDrawable(R.mipmap.emoticon_2x));
                }
            }
        }
        /*
        * 弹出GridView同时改变图标
        * */
        else {
            mGridView.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    emt.setBackground(getDrawable(R.mipmap.emoticon2_2x));
                }
            }
        }
        mInputMethodManager.hideSoftInputFromWindow(msg.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void Chat_back(View view) {
        socket=null;
        finish();
    }
}