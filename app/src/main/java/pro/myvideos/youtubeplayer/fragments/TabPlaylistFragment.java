package pro.myvideos.youtubeplayer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pro.myvideos.youtubeplayer.R;
import pro.myvideos.youtubeplayer.adapters.RecyclerAdapterPlaylist;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class TabPlaylistFragment extends Fragment implements View.OnClickListener{

    public static TabPlaylistFragment newInstance(){
        return new TabPlaylistFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_playlist, container, false);
        RecyclerView recyclerView = (RecyclerView) viewGroup.findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(new RecyclerAdapterPlaylist(getContext()));
        viewGroup.findViewById(R.id.btn_create_playlist).setOnClickListener(this);
        return viewGroup;
    }

    @Override
    public void onClick(View view) {
        DialogFragment newDialog = new DialogAddCustomPlaylist();
        DialogAddCustomPlaylist.playedVideo = null;
        newDialog.show(getFragmentManager(), "");
    }
}
