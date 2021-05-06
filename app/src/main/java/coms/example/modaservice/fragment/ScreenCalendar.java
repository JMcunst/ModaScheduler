package coms.example.modaservice.fragment;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import coms.example.modaservice.R;

/**
 * Scheduler Android App
 * Created by Angga on 10/7/2015.
 */
public class ScreenCalendar extends Fragment {

    public static final String TAG = ScreenCalendar.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_calendar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
