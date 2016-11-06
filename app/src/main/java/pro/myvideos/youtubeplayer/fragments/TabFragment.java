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

import pro.myvideos.youtubeplayer.adapters.RecyclerAdapterHome;
import pro.myvideos.youtubeplayer.data.SearchVideoHelper;
import pro.myvideos.youtubeplayer.data.SearchVideoTask;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class TabFragment extends Fragment implements LoaderManager.LoaderCallbacks<SearchVideoHelper[]> {

    private RecyclerView recyclerView;

    public static TabFragment newInstance(int position){
        return new TabFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recyclerView = new RecyclerView(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        getLoaderManager().initLoader(0, null, TabFragment.this);
        return recyclerView;
    }

    @Override
    public Loader<SearchVideoHelper[]> onCreateLoader(int id, Bundle args) {
        int[] spinners = {0, 0, 0, 0};
        String[] strings = {"", null, null, null};

        return  new SearchVideoTask(getActivity(), strings, spinners, 0);
    }

    @Override
    public void onLoadFinished(Loader<SearchVideoHelper[]> loader, SearchVideoHelper[] data) {
        recyclerView.setAdapter(new RecyclerAdapterHome(data, getContext()));
    }


    @Override
    public void onLoaderReset(Loader<SearchVideoHelper[]> loader) {

    }
}
