package coms.example.modaservice.adapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import coms.example.modaservice.fragment.FeaturedCalendarFragment;
import coms.example.modaservice.fragment.FeaturedNoteFragment;
import coms.example.modaservice.fragment.FeaturedScheduleFragment;

/**
 * Scheduler Android App
 * Created by Angga on 10/7/2015.
 */
public class FeaturedPagerAdapter extends FragmentPagerAdapter {

    public FeaturedPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                return new FeaturedCalendarFragment();
            case 1:
                return new FeaturedNoteFragment();
            case 2:
                return new FeaturedScheduleFragment();
            default:
                return new FeaturedCalendarFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
