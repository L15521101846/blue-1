package com.example.lgh.lgh_blue.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.lgh.lgh_blue.R;

/**
 * Created by LGH on 2017/2/11.
 */

public class ExpressionAdapter extends BaseAdapter{
    //初始化表情数据
    private int[]  mExpression={R.mipmap.exp1,R.mipmap.exp2,R.mipmap.exp3,R.mipmap.exp4,R.mipmap.exp5,
            R.mipmap.exp6,R.mipmap.exp7,R.mipmap.exp8,R.mipmap.exp9,R.mipmap.exp10,
            R.mipmap.exp11,R.mipmap.exp12,R.mipmap.exp13,R.mipmap.exp14,R.mipmap.exp15};
    private LayoutInflater mInflater;
    public ExpressionAdapter(LayoutInflater  mInflater){
        this.mInflater=mInflater;
    }
    @Override
    public int getCount() {
        return mExpression.length;
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
            view=mInflater.inflate(R.layout.chat_expression,null);
            viewHolder=new ViewHolder();
            viewHolder.imageview=(ImageView)view.findViewById(R.id.expression);
            view.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.imageview.setImageResource(mExpression[position]);
        return view;
    }
    private class ViewHolder {
        ImageView imageview;
    }
}
