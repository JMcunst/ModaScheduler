package coms.example.modaservice.adapter;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import coms.example.modaservice.fragment.DashboardIncomingFragment;
import coms.example.modaservice.fragment.DashboardTodayFragment;
import coms.example.modaservice.fragment.DashboardTomorrowFragment;

/**
 * Scheduler Android App
 * Created by Angga on 10/7/2015.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {
    private String incoming;
    private String today;
    private String tomorrow;

    public DashboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        Bundle bundle = new Bundle();
        switch (index) {
            default:
            case 0:
                bundle.putString("data", incoming);
                DashboardIncomingFragment incomingFragment = new DashboardIncomingFragment();
                incomingFragment.setArguments(bundle);
                return incomingFragment;
            case 1:
                bundle.putString("data", today);
                DashboardTodayFragment todayFragment = new DashboardTodayFragment();
                todayFragment.setArguments(bundle);
                return todayFragment;
            case 2:
                bundle.putString("data", tomorrow);
                DashboardTomorrowFragment tomorrowFragment = new DashboardTomorrowFragment();
                tomorrowFragment.setArguments(bundle);
                return tomorrowFragment;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void updateSchedule(String incoming, String today, String tomorrow){
        this.incoming = incoming;
        this.today = today;
        this.tomorrow = tomorrow;
    }
}
