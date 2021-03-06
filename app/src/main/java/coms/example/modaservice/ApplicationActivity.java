package coms.example.modaservice;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import coms.example.modaservice.fragment.ScreenDashboard;
import coms.example.modaservice.fragment.ScreenIncoming;
import coms.example.modaservice.fragment.ScreenNote;
import coms.example.modaservice.fragment.ScreenSchedule;
import coms.example.modaservice.fragment.ScreenSetting;
import coms.example.modaservice.fragment.ScreenToday;
import coms.example.modaservice.fragment.ScreenTomorrow;
import coms.example.modaservice.library.SessionManager;


/**
 * Scheduler Android App
 * Created by Angga on 10/7/2015.
 */
public class ApplicationActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private SessionManager session;

    private long lastPress;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        session = new SessionManager(ApplicationActivity.this);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment objectFragment;
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(position){
            default:
            case 0:
                objectFragment = new ScreenDashboard();
                break;
            case 1:
                objectFragment = new ScreenSchedule();
                break;
            case 2:
                objectFragment = new ScreenIncoming();
                break;
            case 3:
                objectFragment = new ScreenToday();
                break;
            case 4:
                objectFragment = new ScreenTomorrow();
                break;
            case 5:
                objectFragment = new ScreenNote();
                break;
            case 6:
                objectFragment = new ScreenSetting();
                break;
            case 7:
                objectFragment = new ScreenDashboard();
                finish();
                session.logoutUser();
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, objectFragment)
                .commit();

        onSectionAttached(position + 1);
    }

    public void onSectionAttached(int number) {
        switch (number) {
            default:
            case 1:
                mTitle = getString(R.string.title_dashboard);
                break;
            case 2:
                mTitle = getString(R.string.title_schedule);
                break;
            case 3:
                mTitle = getString(R.string.title_incoming);
                break;
            case 4:
                mTitle = getString(R.string.title_today);
                break;
            case 5:
                mTitle = getString(R.string.title_tomorrow);
                break;
            case 6:
                mTitle = getString(R.string.title_note);
                break;
            case 7:
                mTitle = getString(R.string.title_setting);
                break;
            case 8:
                mTitle = getString(R.string.title_logout);
                break;
        }
        getSupportActionBar().setTitle(mTitle);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            onNavigationDrawerItemSelected(6);
            mNavigationDrawerFragment.selectItem(6);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

//        @Override
//        public void onAttach(AppCompatActivity activity) {
//            super.onAttach(activity);
//            ((ApplicationActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastPress > 5000){
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        }else{
            super.onBackPressed();
        }
    }
}
