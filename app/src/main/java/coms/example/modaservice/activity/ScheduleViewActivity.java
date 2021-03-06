package coms.example.modaservice.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import coms.example.modaservice.R;
import coms.example.modaservice.library.ConnectionDetector;
import coms.example.modaservice.library.SessionManager;
import coms.example.modaservice.util.AlertDialogManager;
import coms.example.modaservice.util.Constant;

/**
 * Scheduler Android App
 * Created by Angga on 10/8/2015.
 */
public class ScheduleViewActivity extends AppCompatActivity {

    public static final String TAG = ScheduleViewActivity.class.getSimpleName();
    private final String KEY_ID = "id";
    private final String KEY_TOKEN = "token";
    private final String KEY_EVENT = "event";
    private final String KEY_DATE = "date";
    private final String KEY_TIME = "time";
    private final String KEY_LOCATION = "location";
    private final String KEY_DESCRIPTION = "description";

    private final String DATA_STATUS = "status";
    private final String DATA_SCHEDULE = "schedule";

    private String scheduleId;
    private JSONObject scheduleData;

    private Button buttonBack;
    private Button buttonEdit;
    private Button buttonDelete;
    private TextView labelEvent;
    private TextView labelDate;
    private TextView labelTime;
    private TextView labelLocation;
    private TextView labelDescription;

    private SessionManager session;
    private AlertDialogManager alert;
    private ConnectionDetector connectionDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_schedule_view);

        alert = new AlertDialogManager();
        connectionDetector = new ConnectionDetector(getApplicationContext());
        session = new SessionManager(getApplicationContext());

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        labelEvent = (TextView) findViewById(R.id.event);
        labelDate = (TextView) findViewById(R.id.date);
        labelTime = (TextView) findViewById(R.id.time);
        labelLocation = (TextView) findViewById(R.id.location);
        labelDescription = (TextView) findViewById(R.id.description);

        buttonBack.setOnClickListener(new BackHandler());
        buttonEdit.setOnClickListener(new EditHandler());
        buttonDelete.setOnClickListener(new DeleteHandler());

        Intent i = getIntent();
        scheduleId = i.getStringExtra(KEY_ID);

        updateScheduleDetail();
    }

    /**
     * Update schedule detail from database
     */
    public void updateScheduleDetail(){
        if (connectionDetector.isNetworkAvailable()) {
            new RetrieveScheduleHandler().execute();
        }
        else {
            Toast.makeText(ScheduleViewActivity.this, getString(R.string.disconnect), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Async task to retrieve schedule detail related tapped list
     * this method will passing JSON object as [status] and [schedule]
     */
    private class RetrieveScheduleHandler extends AsyncTask<Object, Void, JSONObject> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(ScheduleViewActivity.this);
            progress.setMessage(getString(R.string.loading_schedule_retrieve));
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected JSONObject doInBackground(Object[] params) {
            JSONObject jsonResponse = null;

            try{
                URL scheduleUrl = new URL(Constant.URL_SCHEDULE_EDIT);
                HttpURLConnection connection = (HttpURLConnection) scheduleUrl.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(KEY_TOKEN, session.getUserDetails().get(SessionManager.KEY_TOKEN))
                        .appendQueryParameter(KEY_ID, scheduleId);

                String query = builder.build().getEncodedQuery();

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                connection.connect();

                int responseCode = connection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = connection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    String responseData = sb.toString();
                    jsonResponse = new JSONObject(responseData);
                }
                else{
                    Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
                }
            }
            catch(MalformedURLException e){
                Log.e(TAG, "Exception caught: " + e);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            scheduleData = result;
            progress.dismiss();
            populateScheduleResponse();
        }
    }

    /**
     * Handle result schedule from database
     * get string from json object and pass to text view
     */
    public void populateScheduleResponse(){
        if(scheduleData == null){
            alert.showAlertDialog(ScheduleViewActivity.this, getString(R.string.error_title), getString(R.string.error_message));
        }
        else{
            try {
                if(scheduleData.getString(DATA_STATUS).equals(Constant.STATUS_SUCCESS)){
                    JSONObject setting = new JSONObject(scheduleData.getString(DATA_SCHEDULE));
                    labelEvent.setText(setting.getString(KEY_EVENT));
                    labelDate.setText(setting.getString(KEY_DATE));
                    labelTime.setText(setting.getString(KEY_TIME).substring(0,5));
                    labelLocation.setText(setting.getString(KEY_LOCATION));
                    labelDescription.setText(setting.getString(KEY_DESCRIPTION));
                }
                else{
                    alert.showAlertDialog(ScheduleViewActivity.this, getString(R.string.restrict_title), getString(R.string.restrict_message));
                    finish();
                }
            }
            catch(JSONException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }

    /**
     * Listener for back to list button
     */
    private class BackHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /**
     * Listener for call edit activity and passing data
     */
    private class EditHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ScheduleViewActivity.this, ScheduleEditActivity.class);
            intent.putExtra(KEY_ID, scheduleId);
            intent.putExtra(KEY_EVENT, labelEvent.getText());
            intent.putExtra(KEY_DATE, labelDate.getText());
            intent.putExtra(KEY_TIME, labelTime.getText());
            intent.putExtra(KEY_LOCATION, labelLocation.getText());
            intent.putExtra(KEY_DESCRIPTION, labelDescription.getText());
            startActivityForResult(intent, 100);
        }
    }

    /**
     * receive signal for result when call edit schedule activity has closed
     * so schedule detail will be updated on the fly
     *
     * @param requestCode identifier request from this activity
     * @param resultCode  result type from called activity
     * @param data        return data from called activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_CANCELED){
                updateScheduleDetail();
            }
        }
    }

    /**
     * Listener for delete schedule button
     * confirm to delete the schedule record
     */
    private class DeleteHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            (new AlertDialog.Builder(ScheduleViewActivity.this))
                    .setTitle(getString(R.string.action_delete_confirm))
                    .setMessage(R.string.message_schedule_delete)
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            new DeleteScheduleHandler().execute();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    /**
     * Async task to make delete request to server
     * this method will return transaction delete status
     */
    private class DeleteScheduleHandler extends AsyncTask<Object, Void, JSONObject> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(ScheduleViewActivity.this);
            progress.setMessage(getString(R.string.loading_schedule_delete));
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected JSONObject doInBackground(Object[] params) {
            JSONObject jsonResponse = null;

            try{
                URL scheduleUrl = new URL(Constant.URL_SCHEDULE_DELETE);
                HttpURLConnection connection = (HttpURLConnection) scheduleUrl.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(KEY_TOKEN, session.getUserDetails().get(SessionManager.KEY_TOKEN))
                        .appendQueryParameter(KEY_ID, scheduleId);

                String query = builder.build().getEncodedQuery();

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                connection.connect();

                int responseCode = connection.getResponseCode();

                if(responseCode == HttpURLConnection.HTTP_OK){
                    InputStream inputStream = connection.getInputStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        sb.append(line);
                    }
                    String responseData = sb.toString();
                    jsonResponse = new JSONObject(responseData);
                }
                else{
                    Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
                }
            }
            catch(MalformedURLException e){
                Log.e(TAG, "Exception caught: " + e);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progress.dismiss();
            handleDeleteScheduleResponse();
        }
    }

    /**
     * Handle result delete schedule from database
     * check delete status
     */
    public void handleDeleteScheduleResponse(){
        if(scheduleData == null){
            alert.showAlertDialog(ScheduleViewActivity.this, getString(R.string.error_title), getString(R.string.error_message));
        }
        else{
            try {
                String status = scheduleData.getString(DATA_STATUS);
                switch (status) {
                    case Constant.STATUS_RESTRICT:
                        alert.showAlertDialog(ScheduleViewActivity.this, getString(R.string.restrict_title), getString(R.string.restrict_message));
                        finish();
                        break;
                    case Constant.STATUS_SUCCESS:
                        Intent returnIntent = new Intent();
                        setResult(RESULT_CANCELED, returnIntent);
                        finish();
                        break;
                    case Constant.STATUS_FAILED:
                        alert.showAlertDialog(ScheduleViewActivity.this, getString(R.string.action_failed), getString(R.string.message_schedule_delete_failed));
                        break;
                }
            }
            catch(JSONException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
