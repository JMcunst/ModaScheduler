package coms.example.modaservice.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import coms.example.modaservice.R;
import coms.example.modaservice.library.ConnectionDetector;
import coms.example.modaservice.library.SessionManager;
import coms.example.modaservice.util.AlertDialogManager;
import coms.example.modaservice.util.Constant;

/**
 * Scheduler Android App
 * Created by Angga on 10/7/2015.
 */
public class NoteCreateActivity extends AppCompatActivity {
    public static final String TAG = NoteCreateActivity.class.getSimpleName();

    private final String KEY_ID = "user_id";
    private final String KEY_TOKEN = "token";
    private final String KEY_TITLE = "title";
    private final String KEY_LABEL = "label";
    private final String KEY_NOTE = "note";

    private final String DATA_STATUS = "status";

    protected JSONObject noteData;

    private Button buttonSave;
    private EditText textTitle;
    private EditText textLabel;
    private EditText textNote;

    private TextView infoTitle;
    private TextView infoLabel;
    private TextView infoNote;

    private SessionManager session;
    private AlertDialogManager alert;
    private ConnectionDetector connectionDetector;

    private String title;
    private String label;
    private String note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_note_add);

        alert = new AlertDialogManager();

        connectionDetector = new ConnectionDetector(NoteCreateActivity.this);

        session = new SessionManager(NoteCreateActivity.this);

        buttonSave = (Button) findViewById(R.id.buttonSave);
        textTitle = (EditText) findViewById(R.id.title);
        textLabel = (EditText) findViewById(R.id.label);
        textNote = (EditText) findViewById(R.id.note);

        infoTitle = (TextView) findViewById(R.id.infoTitle);
        infoLabel = (TextView) findViewById(R.id.infoLabel);
        infoNote = (TextView) findViewById(R.id.infoNote);

        buttonSave.setOnClickListener(new SaveHandler());
    }

    /**
     * check validation
     *
     * @return boolean
     */
    private boolean isValidated(){
        boolean checkTitle = true;
        boolean checkLabel = true;
        boolean checkNote = true;

        title = textTitle.getText().toString();
        if(title.trim().isEmpty()){
            infoTitle.setText("Field Title can't be empty");
            infoTitle.setVisibility(View.VISIBLE);
            checkTitle = false;
        }
        else if(title.length() > 200){
            infoTitle.setText("Field Title allow max length 200 characters");
            infoTitle.setVisibility(View.VISIBLE);
            checkTitle = false;
        }

        label = textLabel.getText().toString();
        if(label.trim().isEmpty()){
            infoLabel.setText("Field Label can't be empty");
            infoLabel.setVisibility(View.VISIBLE);
            checkLabel = false;
        }
        else if(label.length() > 50){
            infoLabel.setText("Field Label allow max length 50 characters");
            infoLabel.setVisibility(View.VISIBLE);
            checkLabel = false;
        }

        note = textNote.getText().toString();
        if(note.trim().isEmpty()){
            infoNote.setText("Field Note can't be empty");
            infoNote.setVisibility(View.VISIBLE);
            checkNote = false;
        }

        return checkTitle && checkLabel && checkNote;
    }

    /**
     * Listener for save button
     */
    private class SaveHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (connectionDetector.isNetworkAvailable()) {
                infoTitle.setVisibility(View.GONE);
                infoLabel.setVisibility(View.GONE);
                infoNote.setVisibility(View.GONE);
                if(isValidated()){
                    if (connectionDetector.isNetworkAvailable()) {
                        new SaveNoteHandler().execute(title, label, note);
                    }
                    else {
                        Toast.makeText(NoteCreateActivity.this, getString(R.string.disconnect), Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    alert.showAlertDialog(NoteCreateActivity.this, getString(R.string.validation), getString(R.string.validation_message));
                }
            } else {
                Toast.makeText(NoteCreateActivity.this, getString(R.string.disconnect), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class SaveNoteHandler extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress = new ProgressDialog(NoteCreateActivity.this);
            progress.setMessage(getString(R.string.loading_note_save));
            progress.setIndeterminate(false);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject jsonResponse = null;

            try{
                URL accountUrl = new URL(Constant.URL_NOTE_INSERT);
                HttpURLConnection connection = (HttpURLConnection) accountUrl.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(KEY_TITLE, params[0])
                        .appendQueryParameter(KEY_LABEL, params[1])
                        .appendQueryParameter(KEY_NOTE, params[2])
                        .appendQueryParameter(KEY_ID, session.getUserDetails().get(SessionManager.KEY_ID))
                        .appendQueryParameter(KEY_TOKEN, session.getUserDetails().get(SessionManager.KEY_TOKEN));

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
                    Log.e(TAG, responseData);
                    jsonResponse = new JSONObject(responseData);
                }
                else{
                    Log.i(TAG, "Unsuccessful HTTP Response Code: " + responseCode);
                }
            } catch (Exception e){
                Log.e(TAG, "Exception caught: " + e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            noteData = result;
            progress.dismiss();
            handleSaveNoteResponse();
        }
    }

    /**
     * Handle result save note from database
     * check save status
     */
    public void handleSaveNoteResponse(){
        if(noteData == null){
            alert.showAlertDialog(NoteCreateActivity.this, getString(R.string.error_title), getString(R.string.error_message));
        }
        else{
            try {
                String status = noteData.getString(DATA_STATUS);
                switch (status) {
                    case Constant.STATUS_RESTRICT:
                        alert.showAlertDialog(NoteCreateActivity.this, getString(R.string.restrict_title), getString(R.string.restrict_message));
                        finish();
                        break;
                    case Constant.STATUS_SUCCESS:
                        Intent returnIntent = new Intent();
                        setResult(RESULT_CANCELED, returnIntent);
                        finish();
                        break;
                    case Constant.STATUS_FAILED:
                        alert.showAlertDialog(NoteCreateActivity.this, getString(R.string.action_failed), getString(R.string.message_note_save_failed));
                        break;
                }
            }
            catch(JSONException e){
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
