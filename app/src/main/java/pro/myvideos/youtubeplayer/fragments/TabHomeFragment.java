package pro.myvideos.youtubeplayer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.lang.ref.WeakReference;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome;
import pro.myvideos.youtubeplayer.data.DataManager;
import pro.myvideos.youtubeplayer.data.SearchVideoTask;
import pro.myvideos.youtubeplayer.data.VideoData;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class TabHomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<VideoData[]> , YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener {

    private static final String KEY_IS_PLAYING = "keyIsPlaying";
    private RecyclerView recyclerView;
    private static WeakReference<Fragment> weakFragment;
    private static YouTubePlayer youTubePlayer;

    public static TabHomeFragment newInstance(){
        return new TabHomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        getLoaderManager().initLoader(0, null, TabHomeFragment.this);

        YouTubePlayerSupportFragment youTubePlayerFragment =
                (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(DataManager.YOUTUBE_DATA_API_KEY, this);

        weakFragment = new WeakReference<Fragment>(youTubePlayerFragment);
        if (savedInstanceState == null | (savedInstanceState != null && !savedInstanceState.getBoolean(KEY_IS_PLAYING))) {
            hideFragment();
        }

        return viewGroup;
    }

    public static void hideFragment(){
        final Fragment fragment = weakFragment.get();
        if (fragment != null) {
            FragmentTransaction ft = fragment.getChildFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.hide(fragment);
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_PLAYING, youTubePlayer.isPlaying());
    }

    public static void playVideoInFragment(String videoId){
        final Fragment fragment = weakFragment.get();
        if (fragment != null && youTubePlayer != null) {
            youTubePlayer.loadVideo(videoId);
            FragmentTransaction ft = fragment.getChildFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.show(fragment);
            ft.commit();

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        weakFragment = null;
    }

    @Override
    public Loader<VideoData[]> onCreateLoader(int id, Bundle args) {
        return  new SearchVideoTask(getActivity(), "");
    }

    @Override
    public void onLoadFinished(Loader<VideoData[]> loader, VideoData[] data) {
        if (data != null) {
            recyclerView.setAdapter(new RecyclerAdapterHome(data, getContext()));
        }
    }


    @Override
    public void onLoaderReset(Loader<VideoData[]> loader) {

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        youTubePlayer = player;
        player.setOnFullscreenListener(this);
        player.setPlayerStateChangeListener(this);
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

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {
        hideFragment();
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
