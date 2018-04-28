package com.debd.kgp.dsmusic.utils;


import android.app.Activity;
import android.content.Intent;

import com.debd.kgp.dsmusic.activity.LoginActivity;
import com.debd.kgp.dsmusic.activity.MusicPlayerActivity;
import com.debd.kgp.dsmusic.R;
import com.debd.kgp.dsmusic.activity.ServerInfoActivity;
import com.debd.kgp.dsmusic.activity.ViewProfileActivity;
import com.debd.kgp.dsmusic.model.SideNavItem;
import com.debd.kgp.dsmusic.utils.sharedPreferences.UserCredentialPreference;

import java.util.ArrayList;
import java.util.List;

public class Constants {

    public static final String USER_LOGIN_CREDENTIAL_PREFERENCE = "login_cred_pref";
    public static final String CRED_USERNAME = "username";
    public static final String CRED_PASSWORD = "password";
    public static final String LOGIN_STATUS = "login_status";
    public static final String REMEMBER_STATUS = "remember_status";


    public static final String SERVER_CONFIG_PREFERENCE = "server_info";
    public static final String SERVER_BASE_URL = "base_url";
    public static final String SERVER_PORT = "port";
    public static final String SERVER_REMEMBER_STATUS = "remember_status";

    public static List<SideNavItem> getNavigationListData() {
        List<SideNavItem> items = new ArrayList<>();

        items.add(new SideNavItem("Profile", R.drawable.profile));
        items.add(new SideNavItem("Play", R.drawable.play));
        items.add(new SideNavItem("Server Config", R.drawable.play));
        items.add(new SideNavItem("Logout", R.drawable.exit));
        items.add(new SideNavItem("Exit", R.drawable.exit));

        return items;
    }

    public static void changeActivity(Activity fromContext, String toActivity) {
       if(toActivity.equalsIgnoreCase("Profile")) {
           if(!(fromContext instanceof ViewProfileActivity)) {
               Intent intent = new Intent(fromContext, ViewProfileActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               fromContext.startActivity(intent);
           }
       }else if(toActivity.equalsIgnoreCase("Play")) {
           if(!(fromContext instanceof MusicPlayerActivity)) {
               Intent intent = new Intent(fromContext, MusicPlayerActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               fromContext.startActivity(intent);
           }
       } else if(toActivity.equalsIgnoreCase("Logout")) {
           UserCredentialPreference userCredentialPreference = new UserCredentialPreference(fromContext);
           userCredentialPreference.logout();
           fromContext.finish();
           Intent intent = new Intent(fromContext, LoginActivity.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           fromContext.startActivity(intent);
       } else if(toActivity.equalsIgnoreCase("Exit")) {
           System.exit(0);
       } else if(toActivity.equalsIgnoreCase("Server Config")) {
           fromContext.finish();
           Intent intent = new Intent(fromContext, ServerInfoActivity.class);
           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           fromContext.startActivity(intent);
       }
    }

}
