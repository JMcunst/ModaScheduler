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
public class FeaturedCalendarFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_featured_calendar, container, false);

        return rootView;
    }
}
