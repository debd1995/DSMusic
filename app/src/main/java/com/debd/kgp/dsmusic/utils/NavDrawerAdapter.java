package com.debd.kgp.dsmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.debd.kgp.dsmusic.R;
import com.debd.kgp.dsmusic.model.SideNavItem;

import java.util.List;

public class NavDrawerAdapter extends BaseAdapter {

    private Context context;
    private List<SideNavItem> list;
    public NavDrawerAdapter(Context context, List<SideNavItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        ImageView image;
        TextView text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        LayoutInflater inflater = null;
        if(convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nav_drawer_list_item, null);
            holder = new ViewHolder();
            holder.image = convertView.findViewById(R.id.icon_image);
            holder.text  = convertView.findViewById(R.id.txt_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SideNavItem item = (SideNavItem) getItem(position);
        holder.image.setImageResource(item.getImageDrawable());
        holder.text.setText(item.getData());

        return convertView;
    }
}
