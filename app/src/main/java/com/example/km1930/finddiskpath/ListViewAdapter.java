package com.example.km1930.finddiskpath;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by djf on 2017/4/26.
 */

public class ListViewAdapter extends BaseAdapter {
    List<String> datas;
    Context mContext;

    public ListViewAdapter(@NonNull List<String> datas, Context mContext) {
        this.datas = datas;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //加载布局为一个视图
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout
                    .listview_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        StringBuffer msg = new StringBuffer();
        String data = datas.get(position);
        msg.append(data);
        File file1 = new File(data);
        if (file1.exists()) {
            msg.insert(0,"路径：");
            if (file1.getTotalSpace() > 0) {
                msg.append("  总空间：" + FileUtils.formatFileSize(file1.getTotalSpace(),false) +
                        "  可用空间：" + FileUtils.formatFileSize(file1.getFreeSpace(),false));
            }
        }
        viewHolder.tv.setText(msg.toString());
        return convertView;
    }

    public static class ViewHolder {
        public View rootView;
        public TextView tv;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.tv = (TextView) rootView.findViewById(R.id.tv);
        }

    }
}
