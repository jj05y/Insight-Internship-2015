package com.shimmerresearch.MultiShimmerRecordReview.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shimmerresearch.multishimmerrecordreview.R;

import java.util.ArrayList;

/**
 * Created by joe on 22/06/15.
 */
public class DrawerListAdapter extends BaseAdapter {

    Context context;
    ArrayList<NavItem> navItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        this.context = context;
        this.navItems = navItems;
    }

    @Override
    public int getCount() {
        return navItems.size();
    }

    @Override
    public Object getItem(int i) {
        return navItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        } else {
            view = convertView;
        }

        TextView textView = (TextView) view.findViewById(R.id.text);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);

        textView.setText(navItems.get(i).getText());
        iconView.setImageResource(navItems.get(i).getIcon());

        return view;
    }
}
