package pro.myvideos.youtubeplayer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.activities.MainActivity;
import pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome;
import pro.myvideos.youtubeplayer.data.DataManager;
import pro.myvideos.youtubeplayer.data.SearchVideoTask;
import pro.myvideos.youtubeplayer.data.VideoData;

import static pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome.BY;
import static pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome.formatNumberExample;
import static pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome.viewsFormatter;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class TabHomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<VideoData[]> , YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener {

    private static final String KEY_IS_PLAYING = "keyIsPlaying";
    private RecyclerView recyclerView;
    private LinearLayout playerContainer;
    private YouTubePlayer youTubePlayer;
    private TextView videoTitle;
    private TextView videoViewCount;
    private TextView videoPublisher;
    private TextView videoPublishAt;
    private TextView like, dislike;
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
        playerContainer = (LinearLayout)viewGroup.findViewById(R.id.player_container);
        videoTitle = (TextView)viewGroup.findViewById(R.id.video_title);
        videoViewCount = (TextView)viewGroup.findViewById(R.id.video_view_count);
        videoPublisher = (TextView)viewGroup.findViewById(R.id.video_publisher);
        videoPublishAt = (TextView)viewGroup.findViewById(R.id.video_publish_at);
        like = (TextView)viewGroup.findViewById(R.id.video_like);
        dislike = (TextView)viewGroup.findViewById(R.id.video_dislike);
        getLoaderManager().initLoader(0, null, TabHomeFragment.this);

        YouTubePlayerSupportFragment youTubePlayerFragment =
                (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(DataManager.YOUTUBE_DATA_API_KEY, this);
        viewGroup.findViewById(R.id.icon_playlist).setOnClickListener(playlistClickListener);
        return viewGroup;
    }

    public void hidePlayer(){
        if (youTubePlayer != null) {
            playerContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_PLAYING, youTubePlayer != null && youTubePlayer.isPlaying());
    }

    public void playVideoInFragment(VideoData videoData){
        if (youTubePlayer != null) {
            youTubePlayer.loadVideo(videoData.getId());
            playerContainer.setVisibility(View.VISIBLE);
            videoTitle.setText(videoData.getTitle());
            videoViewCount.setText(String.format(viewsFormatter, videoData.getCountViews()));
            videoPublishAt.setText(videoData.getPublished().substring(0, 10));
            videoPublisher.setText(BY + videoData.getChannelTitle());
            like.setText(formatNumberExample(videoData.getLikes()));
            dislike.setText(formatNumberExample(videoData.getDislikes()));
//            ((MainActivity)getActivity()).hideBars();
        }

    }

    public void showPopup(View anchoer) {
//        DroppyMenuPopup.Builder droppyBuilder = new DroppyMenuPopup.Builder(getActivity(), videoViewCount);
//// Add Item with icon
//        droppyBuilder.addMenuItem(new DroppyMenuItem("CREATE NEW PLAYLIST", R.drawable.createplaylist_pop_icon));
//        droppyBuilder.addMenuItem(new DroppyMenuItem("CREATE SECOND PLAYLIST", R.drawable.playlist_pop_exist_icon));
//
//        DroppyMenuPopup droppyMenu = droppyBuilder.build();
//        droppyMenu.show();
        DialogFragment dialog = new DialogAdd();
        dialog.show(getActivity().getSupportFragmentManager(), "");
    }

    private View.OnClickListener playlistClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showPopup(view);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public Loader<VideoData[]> onCreateLoader(int id, Bundle args) {
        return  new SearchVideoTask(getActivity(), "");
    }

    @Override
    public void onLoadFinished(Loader<VideoData[]> loader, VideoData[] data) {
        if (data != null && isAdded()) {
            recyclerView.setAdapter(new RecyclerAdapterHome(data, TabHomeFragment.this, getContext()));
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
        hidePlayer();
        ((MainActivity)getActivity()).hideBars();
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
