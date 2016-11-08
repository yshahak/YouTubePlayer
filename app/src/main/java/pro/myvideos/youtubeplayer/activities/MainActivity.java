package pro.myvideos.youtubeplayer.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.adapters.CustomPagerAdapter;
import pro.myvideos.youtubeplayer.data.Playlist;
import pro.myvideos.youtubeplayer.data.VideoData;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager myViewPager;
    private TabLayout tabLayout;
    private TabLayout.Tab tab0, tab1;
    private SearchView searchView;
    public static ArrayList<Playlist> playlists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createPlaylist();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (SearchView)findViewById(R.id.searchview);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
//        new Thread(){
//            public void run(){
//                DataManager.connector(MainActivity.this);
//                List<VideoItem> searchResults = DataManager.search("nba");
//                if (searchResults != null) {
//                    for (VideoItem videoItem : searchResults){
//                        Log.d("TAG", videoItem.getTitle());
//                    }
//                }
//
//            }
//        }.start();
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

    public void hideBars(){
        tabLayout.setVisibility(View.GONE);
        searchView.setVisibility(View.GONE);
    }

    public void showBars(){
        tabLayout.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
    }

    private void createPlaylist(){
        if (playlists == null) {
            playlists = VideoData.load(getApplicationContext());
            if (playlists == null) {
                playlists = new ArrayList<>();
                Playlist favorites = new Playlist("Favourites");
                playlists.add(favorites);
                VideoData.save(getApplicationContext(), playlists);
            }
        }
    }

    public void saveAndUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                VideoData.save(getApplicationContext(), playlists);
            }
        }).start();
        myViewPager.getAdapter().notifyDataSetChanged();
    }


}
