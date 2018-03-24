package com.debd.kgp.dsmusic.utils.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.debd.kgp.dsmusic.model.UserCredential;
import com.debd.kgp.dsmusic.utils.Constants;

public class UserCredentialPreference {

    private SharedPreferences userCredPreference;
    private SharedPreferences.Editor userCredEditor;
    private Context context;

    private UserCredential userCredential;

    public void logout() {
        userCredEditor.putBoolean(Constants.LOGIN_STATUS, false);
        userCredEditor.commit();
    }

    public UserCredentialPreference(Context context) {
        this.context = context;
        userCredPreference = context.getSharedPreferences(Constants.USER_LOGIN_CREDENTIAL_PREFERENCE,Context.MODE_PRIVATE);
        userCredEditor  = userCredPreference.edit();
    }

    public void saveUserCredential(UserCredential credential) {
        userCredEditor.putString(Constants.CRED_USERNAME, credential.getUsername());
        userCredEditor.putString(Constants.CRED_PASSWORD, credential.getPassword());
        userCredEditor.putBoolean(Constants.REMEMBER_STATUS, credential.getRememberStatus());
        userCredEditor.putBoolean(Constants.LOGIN_STATUS, credential.getLoginStatus());
        userCredEditor.commit();
    }
    public UserCredential getUserCredential() {

        userCredential = null;

        String username = userCredPreference.getString(Constants.CRED_USERNAME, null);
        String password = userCredPreference.getString(Constants.CRED_PASSWORD, null);
        boolean loginStatus = userCredPreference.getBoolean(Constants.LOGIN_STATUS, false);
        boolean rememberStatus = userCredPreference.getBoolean(Constants.REMEMBER_STATUS, false);

        if(username != null) {
            userCredential = new UserCredential(username, password, rememberStatus, loginStatus);
        }

        return userCredential;
    }
}
