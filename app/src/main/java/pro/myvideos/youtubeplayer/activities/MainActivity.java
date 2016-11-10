package pro.myvideos.youtubeplayer.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.util.ArrayList;
import java.util.List;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.adapters.CustomPagerAdapter;
import pro.myvideos.youtubeplayer.data.Helper;
import pro.myvideos.youtubeplayer.data.Playlist;
import pro.myvideos.youtubeplayer.data.SearchSuggestTask;
import pro.myvideos.youtubeplayer.data.VideoData;
import pro.myvideos.youtubeplayer.fragments.DialogAbout;
import pro.myvideos.youtubeplayer.fragments.DialogAddToPlaylist;
import pro.myvideos.youtubeplayer.fragments.TabHomeFragment;

import static pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome.BY;
import static pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome.formatNumberExample;
import static pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome.viewsFormatter;
import static pro.myvideos.youtubeplayer.fragments.TabHomeFragment.query;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
        YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener
            ,SearchView.OnSuggestionListener, SearchView.OnQueryTextListener, TabLayout.OnTabSelectedListener {

    private static final int LOADER_TIME_ID = 0;

    public static ArrayList<Playlist> playlists;

    private LinearLayout playerContainer;
    private ViewPager myViewPager;
    private TabLayout tabLayout;
    private TabLayout.Tab tab0, tab1;
    private SearchView searchView;
    private TextView videoTitle;
    private TextView videoViewCount;
    private TextView videoPublisher;
    private TextView videoPublishAt;
    private TextView like, dislike;
    private View showDesc, hideDesc;
    private LinearLayout detailContainer;
    private YouTubePlayer youTubePlayer;
    private VideoData playedVideo;
    private Playlist playedPlaylist;


    private SimpleCursorAdapter mSimpleCursorAdapter;

    public static String suggestions[];
    private static String keyword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createPlaylist();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        searchView = (SearchView)findViewById(R.id.searchview);
        playerContainer = (LinearLayout)findViewById(R.id.player_container);
        detailContainer = (LinearLayout) findViewById(R.id.detail_container);
        videoTitle = (TextView)findViewById(R.id.video_title);
        videoViewCount = (TextView)findViewById(R.id.video_view_count);
        videoPublisher = (TextView)findViewById(R.id.video_publisher);
        videoPublishAt = (TextView)findViewById(R.id.video_publish_at);
        like = (TextView)findViewById(R.id.video_like);
        dislike = (TextView)findViewById(R.id.video_dislike);
        showDesc = findViewById(R.id.video_btn_show_details);
        showDesc.setOnClickListener(showDetailsListener);
        hideDesc = findViewById(R.id.video_btn_hide_details);
        hideDesc.setOnClickListener(hideDetailsListener);
        View iconPlaylist = findViewById(R.id.icon_playlist);
        iconPlaylist.setOnClickListener(playlistClickListener);
        findViewById(R.id.icon_close_player).setOnClickListener(closePlayerListener);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        myViewPager = (ViewPager)findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        myViewPager.setAdapter(new CustomPagerAdapter(getSupportFragmentManager()));
        myViewPager.addOnPageChangeListener(this);
        tab0 = tabLayout.getTabAt(0);
        tab1 = tabLayout.getTabAt(1);
        tabLayout.addOnTabSelectedListener(this);

        setSuggestAdapter();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSuggestionsAdapter(mSimpleCursorAdapter);
        searchView.setOnSuggestionListener(this);
        searchView.setOnQueryTextListener(this);


    }

    private void setSuggestAdapter() {
        final String[] from = new String[]{"suggestions"};
        final int[] to = new int[]{android.R.id.text1};
        mSimpleCursorAdapter = new SimpleCursorAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
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

    public void playVideo(VideoData videoData){
        this.playedVideo = videoData;
        this.playedPlaylist = null;
        showDesc.setVisibility(View.INVISIBLE);
        hideDesc.setVisibility(View.VISIBLE);
        if (youTubePlayer == null) {
            initialize();
        } else {
            boolean landscape = getResources().getBoolean(R.bool.isLandscape);
            youTubePlayer.setFullscreen(landscape);
            youTubePlayer.loadVideo(videoData.getId());
            playerContainer.setVisibility(View.VISIBLE);
            videoTitle.setText(videoData.getTitle());
            videoViewCount.setText(String.format(viewsFormatter, videoData.getCountViews()));
            videoPublishAt.setText(videoData.getPublished().substring(0, 10));
            videoPublisher.setText(BY + videoData.getChannelTitle());
            like.setText(formatNumberExample(videoData.getLikes()));
            dislike.setText(formatNumberExample(videoData.getDislikes()));
            hideBars();
        }
    }

    public void playList(Playlist playlist){
        playedVideo = null;
        playedPlaylist = playlist;
        myViewPager.setCurrentItem(0);
        showDesc.setVisibility(View.INVISIBLE);
        hideDesc.setVisibility(View.VISIBLE);
        if (youTubePlayer == null) {
            initialize();
        } else {
            boolean landscape = getResources().getBoolean(R.bool.isLandscape);
            youTubePlayer.setFullscreen(landscape);
            List<String> list = new ArrayList<>();
            for (VideoData videoData : playlist.getVideos()){
                list.add(videoData.getId());
            }
            youTubePlayer.loadVideos(list);
            playerContainer.setVisibility(View.VISIBLE);
            hideBars();
        }
    }

    public void hidePlayer(){
        if (youTubePlayer != null) {
            playerContainer.setVisibility(View.GONE);
        }
    }


    private View.OnClickListener playlistClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DialogFragment dialog = new DialogAddToPlaylist();
            DialogAddToPlaylist.playedVideo = playedVideo;
            dialog.show(getSupportFragmentManager(), "");
        }
    };
    private View.OnClickListener showDetailsListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDesc.setVisibility(View.INVISIBLE);
            hideDesc.setVisibility(View.VISIBLE);
            detailContainer.setVisibility(View.VISIBLE);
        }
    };
    private View.OnClickListener hideDetailsListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showDesc.setVisibility(View.VISIBLE);
            hideDesc.setVisibility(View.INVISIBLE);
            detailContainer.setVisibility(View.GONE);
        }
    };
    private View.OnClickListener closePlayerListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                youTubePlayer.pause();
            } catch (IllegalStateException e) {
                initialize();
            }
            showBars();
            hidePlayer();
        }
    };

    private void initialize(){
        YouTubePlayerSupportFragment youTubePlayerFragment =
                (YouTubePlayerSupportFragment) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(Helper.YOUTUBE_DATA_API_KEY, this);

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


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        youTubePlayer = player;
        player.setOnFullscreenListener(this);
        player.setPlayerStateChangeListener(this);
        if (playedVideo != null) {
            playVideo(playedVideo);
        } else if(playedPlaylist != null){
            playList(playedPlaylist);
        }
        Log.d("TAG", "onInitializationSuccess: " + wasRestored);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        youTubePlayer = null;
    }

    @Override
    public void onFullscreen(boolean fullScreen) {

        Log.d("TAG", "onFullscreen: " + fullScreen);
        if (youTubePlayer != null) {
            youTubePlayer.play();
        }
    }

    @Override
    public void onLoading() {
        Log.d("TAG", "loading");

    }

    @Override
    public void onLoaded(String videoId) {
        Log.d("TAG", "loaded");
        if (playedPlaylist != null){
            for (VideoData videoData : playedPlaylist.getVideos()){
                if (videoData.getId().equals(videoId)){
                    videoTitle.setText(videoData.getTitle());
                    videoViewCount.setText(String.format(viewsFormatter, videoData.getCountViews()));
                    videoPublishAt.setText(videoData.getPublished().substring(0, 10));
                    videoPublisher.setText(BY + videoData.getChannelTitle());
                    like.setText(formatNumberExample(videoData.getLikes()));
                    dislike.setText(formatNumberExample(videoData.getDislikes()));
                    break;
                }
            }

        }
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {
        if (youTubePlayer != null && youTubePlayer.hasNext()) {
            youTubePlayer.next();
        } else {
            hidePlayer();
        }
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Log.d("TAG", errorReason.toString());
        initialize();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        keyword = newText;
        if (newText == null || newText.length() < 1 || newText.length() > 30) {
            updateSuggestAdapter(null);
            return false;
        }
        getSupportLoaderManager().restartLoader(LOADER_TIME_ID, null, myLoader);
        return false;
    }

    // SEARCH SUGGESTIONS

    /**
     * Updates search suggestions
     */
    private void updateSuggestAdapter(String[] results) {
        final MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "suggestions"});
        if (results != null) {
            for (int i = 0; i < results.length; i++) {
                c.addRow(new Object[]{i, results[i]});
            }
        }
        mSimpleCursorAdapter.changeCursor(c);
        suggestions = results;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        if (searchView != null && suggestions != null
                && suggestions.length > position && suggestions[position] != null) {
            searchView.setQuery(suggestions[position], true);
            searchView.clearFocus();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }



    // SEARCH
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            for (Fragment fragment : getSupportFragmentManager().getFragments()){
                if (fragment instanceof TabHomeFragment){
                    fragment.getLoaderManager().restartLoader(0, null, (TabHomeFragment)fragment);
                    break;
                }
            }
            myViewPager.setCurrentItem(0);
            searchView.setQuery("", false);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean landscape = getResources().getBoolean(R.bool.isLandscape);
        if (youTubePlayer != null && youTubePlayer.isPlaying()) {
            youTubePlayer.setFullscreen(landscape);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about    :
                DialogAbout dialog = new DialogAbout();
                dialog.show(getSupportFragmentManager(), "");
                return true;
            case R.id.action_more_apps:

                return true;
            case R.id.action_Rate:
                actionRate();
                return true;
            case R.id.action_share:
                actionShare();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void actionRate() {
        String appPackageName = getPackageName();
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.rate_url) + appPackageName));
        marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(marketIntent);
    }

    private void actionShare() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String appPackageNam = getPackageName();
        sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_msg) + appPackageNam);
        sendIntent.setType("text/plain");
        MainActivity.this.startActivity(Intent.createChooser(sendIntent, getString(R.string.share)));
    }

    /**
     * Suggestions Loader
     */
    private LoaderManager.LoaderCallbacks<String[]> myLoader = new LoaderManager.LoaderCallbacks<String[]>() {
        @Override
        public Loader<String[]> onCreateLoader(int i, Bundle bundle) {
            Loader<String[]> loader = null;
            if (i == LOADER_TIME_ID) {
                loader = new SearchSuggestTask(MainActivity.this, keyword);
            }
            return loader;
        }

        @Override
        public void onLoadFinished(Loader<String[]> loader, String[] results) {
            if (results != null && results.length > 0) {
                updateSuggestAdapter(results);

            }
        }

        @Override
        public void onLoaderReset(Loader<String[]> loader) {

        }
    };

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        myViewPager.setCurrentItem(tab.equals(tab0) ? 0 : 1);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
