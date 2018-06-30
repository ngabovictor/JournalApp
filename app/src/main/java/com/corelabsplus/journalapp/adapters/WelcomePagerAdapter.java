package com.corelabsplus.journalapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.corelabsplus.journalapp.R;

public class WelcomePagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public int[] slide_icons = {
            R.mipmap.hi_white,
            R.mipmap.ic_db,
            R.mipmap.ic_tick
    };

    public String[] slide_titles = {
            "WELCOME",
            "SAVE ON LOCAL AND CLOUD",
            "ACCESS ANYWHERE"
    };

    public String[] slide_descs = {
            "Woow! Glad to have you! Journal App is just the best",
            "Do you have any idea or feeling? Just pen them down here",
            "Click the check icon to get started"
    };

    public WelcomePagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return slide_titles.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.welcome_container, container, false);

        ImageView icon = (ImageView) v.findViewById(R.id.welcome_icon);
        TextView header = (TextView) v.findViewById(R.id.welcome_title);
        TextView desc = (TextView) v.findViewById(R.id.welcome_desc);

        icon.setImageResource(slide_icons[position]);
        header.setText(slide_titles[position]);
        desc.setText(slide_descs[position]);

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}
