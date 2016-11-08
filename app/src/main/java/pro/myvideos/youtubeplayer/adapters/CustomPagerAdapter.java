package pro.myvideos.youtubeplayer.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pro.myvideos.youtubeplayer.fragments.TabHomeFragment;
import pro.myvideos.youtubeplayer.fragments.TabPlaylistFragment;

/**
 * Created by B.E.L on 06/11/2016.
 */

public class CustomPagerAdapter extends FragmentPagerAdapter {

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return  position == 0 ? TabHomeFragment.newInstance() : TabPlaylistFragment.newInstance();
    }

    @Override
    public int getItemPosition(Object object) {
        return (object instanceof TabHomeFragment) ? POSITION_UNCHANGED : POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 2;
    }


}
