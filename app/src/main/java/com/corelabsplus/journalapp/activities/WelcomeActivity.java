package com.corelabsplus.journalapp.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.corelabsplus.journalapp.R;
import com.corelabsplus.journalapp.adapters.WelcomePagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.navigation_dots_container) LinearLayout navDots;
    @BindView(R.id.prev_btn) ImageButton prevBtn;
    @BindView(R.id.next_btn) ImageButton nextBtn;
    @BindView(R.id.body) RelativeLayout body;

    private WelcomePagerAdapter welcomePagerAdapter;
    private Context context;
    private TextView[] dots;
    private int currentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        context = this;

        welcomePagerAdapter = new WelcomePagerAdapter(context);
        viewPager.setAdapter(welcomePagerAdapter);

        prevBtn.setEnabled(false);
        prevBtn.setVisibility(View.INVISIBLE);

        addDotsNavigation(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentPage = position;

                if (position == 0){
                    prevBtn.setEnabled(false);
                    prevBtn.setVisibility(View.INVISIBLE);
                    //changeColors(getResources().getColor(R.color.colorPrimaryDark), R.color.colorPrimary);
                }

//                else if (position == 1){
//                    changeColors(getResources().getColor(R.color.colorGreenStatus), R.color.colorGreenDark);
//                }

                else if (position == welcomePagerAdapter.getCount() - 1){
                    nextBtn.setImageResource(R.drawable.ic_check);
                    //changeColors(getResources().getColor(R.color.colorOrangeDark), R.color.colorOrange);
                }

                else {
                    prevBtn.setEnabled(true);
                    prevBtn.setVisibility(View.VISIBLE);
                    nextBtn.setImageResource(R.drawable.ic_arrow_forward);
                }
                addDotsNavigation(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage != welcomePagerAdapter.getCount() - 1){
                    viewPager.setCurrentItem(currentPage + 1);
                }

                else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void addDotsNavigation(int current){
        dots = new TextView[welcomePagerAdapter.getCount()];
        navDots.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context);

            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.whiteOpacity));
            navDots.addView(dots[i]);
        }

        if (dots.length > 0){
            dots[current].setTextColor(getResources().getColor(R.color.white));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void changeColors(int status, int background){
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(status);
        }

        body.setBackgroundColor(background);
    }

}
