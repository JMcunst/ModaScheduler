package coms.example.modaservice.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import coms.example.modaservice.LoginActivity;

import java.util.HashMap;

public class SessionManager {
	// Shared Preferences
    SharedPreferences pref;
     
    // Editor for Shared preferences
    Editor editor;
     
    // Context
    Context _context;
     
    // Shared pref mode
    int PRIVATE_MODE = 0;
     
    // Shared pref file name
    private static final String PREF_NAME = "SPF_PREF";
     
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
     
    // User id
    public static final String KEY_ID = "id";
    
    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";
    
    // User detail
    public static final String KEY_WORK = "work";

    // User detail
    public static final String KEY_ABOUT = "about";
     
    // Username address (make variable public to access from outside)
    public static final String KEY_USERNAME = "username";
    
    // User token
    public static final String KEY_TOKEN = "token";
     
    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        editor.apply();
    }


    /**
     * Create login session
     *
     * @param id is identification number of record data in database
     * @param name of user
     * @param work is title or role in company
     * @param about is simple description of user
     * @param username is a account id for credential
     * @param token is a secret key which generated from id and username
     */
    public void createLoginSession(String id, String name, String work, String about, String username, String token){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
         
        // Storing name in pref
        editor.putString(KEY_ID, id);
        
        // Storing name in pref
        editor.putString(KEY_NAME, name);
        
        // Storing work
        editor.putString(KEY_WORK, work);

        // Storing description
        editor.putString(KEY_ABOUT, about);
         
        // Storing email in pref
        editor.putString(KEY_USERNAME, username);
        
        // Storing key
        editor.putString(KEY_TOKEN, token);
         
        // commit changes
        editor.commit();
    }


    /**
     * Check login method will check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     *
     * @return boolean
     */
    public boolean checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
             
            // Staring Login Activity
            _context.startActivity(i);
            return false;
        }
        return true;
         
    }


    /**
     * Get stored session data
     *
     * @return HashMap
     */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        // user name
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        
        // work
        user.put(KEY_WORK, pref.getString(KEY_WORK, null));

        // about
        user.put(KEY_ABOUT, pref.getString(KEY_ABOUT, null));
         
        // user email id
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        
        // user token
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));
         
        // return user
        return user;
    }
     
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
    	editor.remove(KEY_ID);
    	editor.remove(KEY_NAME);
    	editor.remove(KEY_WORK);
        editor.remove(KEY_ABOUT);
    	editor.remove(KEY_USERNAME);
    	editor.remove(KEY_TOKEN);
    	editor.remove(IS_LOGIN);
    	
        //editor.clear();
        editor.commit();
        
        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         
        // Staring Login Activity
        _context.startActivity(i);
    }
     
    /**
     * Quick check for login
     * **/
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
    
}
