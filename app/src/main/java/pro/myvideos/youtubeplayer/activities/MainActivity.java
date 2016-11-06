package pro.myvideos.youtubeplayer.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.List;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.adapters.CustomPagerAdapter;
import pro.myvideos.youtubeplayer.data.DataManager;
import pro.myvideos.youtubeplayer.data.VideoItem;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager myViewPager;
    private TabLayout tabLayout;
    private TabLayout.Tab tab0, tab1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        new Thread(){
            public void run(){
                DataManager.connector(MainActivity.this);
                List<VideoItem> searchResults = DataManager.search("nba");
                if (searchResults != null) {
                    for (VideoItem videoItem : searchResults){
                        Log.d("TAG", videoItem.getTitle());
                    }
                }

            }
        }.start();
        myViewPager = (ViewPager)findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        myViewPager.setAdapter(new CustomPagerAdapter(getSupportFragmentManager()));
        myViewPager.addOnPageChangeListener(this);
        tab0 = tabLayout.getTabAt(0);
        tab1 = tabLayout.getTabAt(1);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0){
            tab0.select();
        } else {
            tab1.select();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
