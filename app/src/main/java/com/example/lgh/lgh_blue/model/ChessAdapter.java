package com.example.lgh.lgh_blue.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.lgh.lgh_blue.R;

/**
 * Created by LGH on 2017/2/12.
 */

public class ChessAdapter extends BaseAdapter {
    private int[]  mChess;
    private LayoutInflater mInflater;
    public ChessAdapter(LayoutInflater mInflater,int[]Chess){
        this.mInflater=mInflater;
        this.mChess=Chess;
    }
    @Override
    public int getCount() {
        return mChess.length;
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
        ChessAdapter.ViewHolder viewHolder=null;
        if(view==null){
            view=mInflater.inflate(R.layout.game_chess,null);
            viewHolder=new ChessAdapter.ViewHolder();
            viewHolder.imageview=(ImageView)view.findViewById(R.id.chess);
            view.setTag(viewHolder);
        }else {
            viewHolder=(ChessAdapter.ViewHolder)view.getTag();
        }
        viewHolder.imageview.setImageResource(mChess[position]);
        return view;
    }
    private class ViewHolder{
        ImageView imageview;
    }
}
