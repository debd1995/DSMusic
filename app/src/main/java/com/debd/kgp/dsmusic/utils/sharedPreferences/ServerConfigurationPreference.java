package com.debd.kgp.dsmusic.utils.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.debd.kgp.dsmusic.model.ServerConfiguration;
import com.debd.kgp.dsmusic.utils.Constants;

public class ServerConfigurationPreference {

    private SharedPreferences serverConfigPreference;
    private SharedPreferences.Editor serverConfigEditor;
    private Context context;

    private ServerConfiguration serverConfiguration;

    public ServerConfigurationPreference(Context context) {
        this.context = context;
        serverConfigPreference = context.getSharedPreferences(Constants.SERVER_CONFIG_PREFERENCE, Context.MODE_PRIVATE);
        serverConfigEditor = serverConfigPreference.edit();
    }

    public ServerConfiguration getServerConfiguration() {

        serverConfiguration = null;

        String url = serverConfigPreference.getString(Constants.SERVER_BASE_URL, null);
        int port = serverConfigPreference.getInt(Constants.SERVER_PORT, 0);

        if(url != null) {
            serverConfiguration = new ServerConfiguration(url, port);
        }
        return serverConfiguration;

    }

    public void saveServerConfiguration(ServerConfiguration configuration) {
        serverConfigEditor.putString(Constants.SERVER_BASE_URL, configuration.getURL());
        serverConfigEditor.putInt(Constants.SERVER_PORT, configuration.getPort());
        serverConfigEditor.commit();
    }



}
