package com.example.lgh.lgh_blue.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.lgh.lgh_blue.R;

import java.util.List;

/**
 * Created by LGH on 2017/2/15.
 */

public class DeviceAdapter extends ArrayAdapter {
        private final LayoutInflater mInflater;
        private int mResource;
        private List<String> mData;
        public DeviceAdapter(Context context, int resource, List<String> data) {
            super(context, resource);
            mInflater = LayoutInflater.from(context);
            mResource=resource;
            mData=data;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            DeviceAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder=new ViewHolder();
                convertView = mInflater.inflate(mResource, parent,false);
                viewHolder.name=(TextView)convertView.findViewById(R.id.device_name);
                convertView.setTag(viewHolder);
            }else {
                viewHolder=(DeviceAdapter.ViewHolder)convertView.getTag();
            }
            viewHolder.name.setText(mData.get(position));
            return convertView;
        }
    private class ViewHolder {
        TextView name;
    }


}
