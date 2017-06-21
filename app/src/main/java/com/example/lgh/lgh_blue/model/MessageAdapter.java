package com.example.lgh.lgh_blue.model;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lgh.lgh_blue.R;
import com.example.lgh.lgh_blue.presenter.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by LGH on 2017/2/10.
 */

public class MessageAdapter extends BaseAdapter {
    public final static int RECEIVE=0;
    public final static int SEND=1;
    private LayoutInflater mInflater;
    private List<ChatMessage> mData;
    private Html.ImageGetter mImageGetter;
    private SimpleDateFormat format;
    /*
    * 返回不同布局类型的数量
    * */
    @Override
    public int getViewTypeCount(){
        return 2;
    }
    /*
    * 返回当前布局类型
    * */
    @Override
    public int getItemViewType(int position){
        if(0==mData.get(position).getType()){
            return RECEIVE;
        }else if(1==mData.get(position).getType()){
            return SEND;
        }else {
            return 0;
        }
    }
    public MessageAdapter(LayoutInflater mInflater,List<ChatMessage> mData,Html.ImageGetter mImageGetter){
        this.mInflater=mInflater;
        this.mData=mData;
        this.mImageGetter=mImageGetter;
    }
    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if(view==null){
            viewHolder=new ViewHolder();
            /*
            * 通过判断消息类型的不同，加载不同的布局。这里使用了getItemViewType()方法
            * */
            switch (getItemViewType(position)){
                case RECEIVE:view=mInflater.inflate(R.layout.chat_left,null);break;
                case SEND:view=mInflater.inflate(R.layout.chat_right,null);break;
                default:break;
            }
            viewHolder.person=(ImageView)view.findViewById(R.id.chat_person);
            viewHolder.time=(TextView)view.findViewById(R.id.chat_time);
            viewHolder.input=(TextView)view.findViewById(R.id.chat_input);
            view.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder)view.getTag();
        }
        ChatMessage data=mData.get(position);
        //如果不是第一个Item且发送消息的两次时间在1min之间。则不再显示时间；否则显示时间
        if (position!=0){
            ChatMessage dataBefore=mData.get(position-1);
            long dateDifference=data.getTime()-dataBefore.getTime();
            if(dateDifference<60000){
                viewHolder.time.setVisibility(View.GONE);
            }else{
                format=new SimpleDateFormat("EEE HH:mm:ss");
                String time=format.format(new Date(data.getTime()));
                viewHolder.time.setText(time);
            }
        }else{
            format=new SimpleDateFormat("EEE HH:mm:ss");
            String time=format.format(new Date(data.getTime()));
            viewHolder.time.setText(time);
        }
        viewHolder.person.setImageResource(data.getPerson());
        //将收到的数据以文本的形式显示
        Spanned spanned=Html.fromHtml(data.getInput(),mImageGetter,null);
        viewHolder.input.setText(spanned);
        return view;
    }

    private class ViewHolder {
        ImageView person;
        TextView  time;
        TextView input;
    }
}
