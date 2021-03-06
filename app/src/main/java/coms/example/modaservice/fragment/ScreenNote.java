package coms.example.modaservice.fragment;

import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import coms.example.modaservice.R;
import coms.example.modaservice.activity.NoteCreateActivity;
import coms.example.modaservice.activity.NoteViewActivity;
import coms.example.modaservice.adapter.NoteListAdapter;
import coms.example.modaservice.library.ConnectionDetector;
import coms.example.modaservice.library.SessionManager;
import coms.example.modaservice.model.NoteItem;
import coms.example.modaservice.util.AlertDialogManager;
import coms.example.modaservice.util.Constant;

import org.json.JSONArray;
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
import java.util.ArrayList;

/**
 * Scheduler Android App
 * Created by Angga on 10/7/2015.
 */
public class ScreenNote extends Fragment {

    public static final String TAG = ScreenNote.class.getSimpleName();

    private final String KEY_USER_ID = "user_id";
    private final String KEY_TOKEN = "token";
    private final String KEY_ID = "id";
    private final String KEY_TITLE = "title";
    private final String KEY_LABEL = "label";
    private final String KEY_NOTE = "note";
    private final String KEY_TIMESTAMP = "created_at";

    private final String DATA_STATUS = "status";
    private final String DATA_NOTE = "notes";

    protected JSONObject noteData;

    private TextView emptyMessage;
    private ListView noteList;
    private NoteListAdapter noteAdapter;
    private ArrayList<NoteItem> noteItems;

    private AlertDialogManager alert;
    private ConnectionDetector connectionDetector;
    private SessionManager session;

    private Button createNoteButton;
    private LinearLayout loadingScreen;
    private ImageView loadingIcon;
    private AnimationDrawable loadingAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_menu_note, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        alert = new AlertDialogManager();
        connectionDetector = new ConnectionDetector(getActivity());
        session = new SessionManager(getActivity());

        loadingScreen = (LinearLayout) getActivity().findViewById(R.id.loadingScreen);
        loadingIcon = (ImageView) getActivity().findViewById(R.id.loadingIcon);
        loadingIcon.setBackgroundResource(R.drawable.loading_animation);
        loadingAnimation = (AnimationDrawable) loadingIcon.getBackground();

        createNoteButton = (Button) getActivity().findViewById(R.id.buttonSave);
        noteList = (ListView) getActivity().findViewById(R.id.listNote);
        emptyMessage = (TextView) getActivity().findViewById(R.id.emptyMessage);

        noteItems = new ArrayList<>();
        noteAdapter = new NoteListAdapter(getActivity(), noteItems);

        noteList.setOnItemClickListener(new ListNoteListener());
        createNoteButton.setOnClickListener(new CreateNoteListener());

        updateNoteList();
    }

    /**
     * Update note list from database
     */
    public void updateNoteList(){
        if (connectionDetector.isNetworkAvailable()) {
            new RetrieveNoteHandler().execute();
        }
        else {
            Toast.makeText(getActivity(), getString(R.string.disconnect), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Listener each list item to call detail note and show complete list by id
     */
    private class ListNoteListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String noteId = ((TextView) view.findViewById(R.id.listNoteId)).getText().toString();
            Intent intent = new Intent(getActivity(), NoteViewActivity.class);
            intent.putExtra(KEY_ID, noteId);
            startActivityForResult(intent, 200);
        }
    }

    /**
     * Listener to call form new schedule
     */
    private class CreateNoteListener implements View.OnClickListener {
        public void onClick(View v) {
            Intent newNote = new Intent(getActivity(), NoteCreateActivity.class);
            startActivityForResult(newNote, 200);
        }
    }

    /**
     * receive signal for result when call new note activity has closed
     * so note list will be updated on the fly
     *
     * @param requestCode identifier request from this activity
     * @param resultCode  result type from called activity
     * @param data        return data from called activity
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == AppCompatActivity.RESULT_CANCELED){
                updateNoteList();
            }
        }
    }

    /**
     * Async task to retrieve note related by logged in user
     * this method will passing JSON object as [status] and [note]
     */
    private class RetrieveNoteHandler extends AsyncTask<Object, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingScreen.setVisibility(View.VISIBLE);
            loadingAnimation.start();
        }

        @Override
        protected JSONObject doInBackground(Object[] params) {

            JSONObject jsonResponse = null;

            try{
                URL noteUrl = new URL(Constant.URL_NOTE_VIEW);
                HttpURLConnection connection = (HttpURLConnection) noteUrl.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(KEY_TOKEN, session.getUserDetails().get(SessionManager.KEY_TOKEN))
                        .appendQueryParameter(KEY_USER_ID, session.getUserDetails().get(SessionManager.KEY_ID));

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

                    responseData = responseData.replace("\\r\\n", " ");
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
            loadingScreen.setVisibility(View.GONE);
            loadingAnimation.stop();
            noteData = result;
            handleScheduleResponse();
        }

    }

    /**
     * Handle result note from database
     * loop through and populate JSON array to list adapter
     */
    public void handleScheduleResponse(){
        if(noteData == null){
            alert.showAlertDialog(getActivity(), getString(R.string.error_title), getString(R.string.error_message));
        }
        else{
            try {
                if(noteData.getString(DATA_STATUS).equals(Constant.STATUS_SUCCESS)){
                    JSONArray jsonNotes = noteData.getJSONArray(DATA_NOTE);

                    if(jsonNotes.length() == 0){
                        emptyMessage.setVisibility(View.VISIBLE);
                    }
                    else{
                        emptyMessage.setVisibility(View.GONE);
                    }

                    noteItems.clear();
                    for(int i = 0; i < jsonNotes.length(); i++){
                        JSONObject note = jsonNotes.getJSONObject(i);

                        int id = note.getInt(KEY_ID);
                        String title = note.getString(KEY_TITLE);
                        String label = note.getString(KEY_LABEL)+", Created At "+note.getString(KEY_TIMESTAMP).substring(0,16);
                        String content = note.getString(KEY_NOTE);

                        noteItems.add(new NoteItem(id, title, label, content));
                    }
                    noteList.setAdapter(noteAdapter);
                }
                else{
                    alert.showAlertDialog(getActivity(), getString(R.string.error_title), getString(R.string.error_message));
                }
            }
            catch(JSONException e){
                Log.e(TAG, "Exception caught: " + e);
            }
        }
    }
}
