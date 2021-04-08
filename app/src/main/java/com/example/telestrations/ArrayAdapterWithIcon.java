package com.example.telestrations;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayAdapterWithIcon extends ArrayAdapter<String> {

    private List<Integer> images;
    private List<Integer> colors;

    public ArrayAdapterWithIcon(Context context, List<String> items, List<Integer> images) {
        super(context, R.layout.select_avatar, items);
        this.images = images;
    }

    public ArrayAdapterWithIcon(Context context, List<String> items, List<Integer> images, List<Integer> colors) {
        super(context, R.layout.list_players, items);
        this.images = images;
        this.colors = colors;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(android.R.id.text1);

        if (colors != null){
            textView.setTextColor(colors.get(position));
        }
        textView.setCompoundDrawablesRelativeWithIntrinsicBounds(images.get(position), 0, 0, 0);

        textView.setCompoundDrawablePadding(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics()));
        return view;
    }

}
