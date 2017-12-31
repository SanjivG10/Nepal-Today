package com.techniary.nepaltoday;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sanjiv on 12/28/17.
 */

class TabsViewPagerAdapter extends FragmentPagerAdapter {
    public TabsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                EditorChoiceFragment editorChoiceFragment = new EditorChoiceFragment();
                return editorChoiceFragment;
            case 1:
                PopularFragment popularFragment = new PopularFragment();
                return popularFragment;
            case 2:
                LatestFragment latestFragment = new LatestFragment();
                return latestFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Editor's Choice";
            case 1:
                return "Popular";
            case 2:
                return "Latest";
            default:
                return null;
        }
    }
}
