package pro.myvideos.youtubeplayer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome;
import pro.myvideos.youtubeplayer.data.SearchVideoTask;
import pro.myvideos.youtubeplayer.data.VideoData;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class TabHomeFragment extends Fragment implements LoaderManager.LoaderCallbacks<VideoData[]> {

    private static final String KEY_IS_PLAYING = "keyIsPlaying";
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    public static String query = "";



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
        progressBar = (ProgressBar)viewGroup.findViewById(R.id.progress_bar);
        getLoaderManager().initLoader(0, null, TabHomeFragment.this);
        return viewGroup;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public Loader<VideoData[]> onCreateLoader(int id, Bundle args) {
        progressBar.setVisibility(View.VISIBLE);
        return new SearchVideoTask(getActivity(), query);
    }

    @Override
    public void onLoadFinished(Loader<VideoData[]> loader, VideoData[] data) {
        if (data != null && isAdded()) {
            recyclerView.setAdapter(new RecyclerAdapterHome(data, getContext()));
        }
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onLoaderReset(Loader<VideoData[]> loader) {

    }



}
