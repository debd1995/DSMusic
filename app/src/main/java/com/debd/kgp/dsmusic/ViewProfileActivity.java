package com.debd.kgp.dsmusic;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.debd.kgp.dsmusic.dialog.ExitDialog;
import com.debd.kgp.dsmusic.dialog.ExitDialogListener;
import com.debd.kgp.dsmusic.dialog.MessageDialog;
import com.debd.kgp.dsmusic.model.ServerConfiguration;
import com.debd.kgp.dsmusic.model.SideNavItem;
import com.debd.kgp.dsmusic.model.UserCredential;
import com.debd.kgp.dsmusic.model.restAPIModel.Error;
import com.debd.kgp.dsmusic.model.restAPIModel.User;
import com.debd.kgp.dsmusic.restClient.ClientAPI;
import com.debd.kgp.dsmusic.utils.Constants;
import com.debd.kgp.dsmusic.utils.NavDrawerAdapter;
import com.debd.kgp.dsmusic.utils.sharedPreferences.ServerConfigurationPreference;
import com.debd.kgp.dsmusic.utils.sharedPreferences.UserCredentialPreference;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewProfileActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, ExitDialogListener {

    private TextView txtName, txtEmail, txtPhone;
    private LinearLayout totalSongPanel, totalPlaylistPanel;
    private ImageView btnMenu;

    private DrawerLayout drawerLayout;
    private ImageView sideNave;
    private ListView listView;
    private List<SideNavItem> list;
    private NavDrawerAdapter adapter;

    private ServerConfigurationPreference serverConfigurationPreference;
    private ServerConfiguration serverConfiguration;

    private UserCredentialPreference userCredentialPreference;
    private UserCredential userCredential;

    private Retrofit retrofit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initUIComponents();
        serverConfigurationPreference = new ServerConfigurationPreference(this);
        serverConfiguration = serverConfigurationPreference.getServerConfiguration();

        userCredentialPreference = new UserCredentialPreference(this);
        userCredential = userCredentialPreference.getUserCredential();

        list = Constants.getNavigationListData();
        adapter = new NavDrawerAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        requestUserDetails();
    }

    private void requestUserDetails() {
        String authHeader = null;
        String uname = userCredential.getUsername();
        String pass = userCredential.getPassword();

        if(uname.length() >= 1 && pass.length() >= 1) {
            String base = uname+":"+pass;
            authHeader = "Basic "+ Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);
        }
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://"+serverConfiguration.getURL()+":"+serverConfiguration.getPort()+"/DSMusicServer/api/")
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()));

        retrofit = builder.build();
        ClientAPI clientAPI = retrofit.create(ClientAPI.class);
        Call<User> call = clientAPI.getProfile(userCredential.getUsername(), authHeader);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    // success response
                    if(response.code() == 200) {
                        User user = response.body();
                        txtName.setText(user.getfName()+" "+user.getlName());
                        txtPhone.setText(""+user.getPhoneNumber());
                        txtEmail.setText(user.getEmail());
                    } else {
                        // success but not OK response returned.
                        Toast.makeText(ViewProfileActivity.this, "CODE : "+response.code(), Toast.LENGTH_SHORT).show();
                    }

                }else {
                    // error response
                    Error serverError = parseError(response.errorBody());
                    String errorTitle = "Can't load profile";
                    String errorDesc = "Unknown Error";

                    if(serverError != null) {
                        errorDesc = serverError.getDescription()
                                +"\n"+serverError.getResponseMessage()
                                +"\n"+serverError.getSuggession();
                    }
                    MessageDialog authErrorDialog = new MessageDialog();
                    authErrorDialog.setRetainInstance(true);
                    //authErrorDialog.setOnMessageDialogDismissedListener("errorDialog", LoginActivity.this);
                    authErrorDialog.setTitle(errorTitle);
                    authErrorDialog.setDescription(errorDesc);
                    authErrorDialog.show(getSupportFragmentManager(), "error");
                }

            }

            private Error parseError(ResponseBody responseBody) {
                Converter<ResponseBody, Error> errorConverter =
                        ViewProfileActivity.this
                                .retrofit
                                .responseBodyConverter(Error.class, new Annotation[0]);
                Error error = null;
                try {
                    error = errorConverter.convert(responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return error;
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
                MessageDialog messageDialog = new MessageDialog();
                messageDialog.setRetainInstance(true);
                //messageDialog.setOnMessageDialogDismissedListener("errorDialog", LoginActivity.this);
                messageDialog.setTitle("Can't load profile");
                messageDialog.setDescription(t.getMessage()+
                        "\nFailed to connect to server"+
                        "\nCheck your network connection");
                messageDialog.show(getSupportFragmentManager(), "error");
            }
        });
    }

    private void initUIComponents() {
        txtName = (TextView)findViewById(R.id.txt_name);
        txtEmail = (TextView)findViewById(R.id.txt_email);
        txtPhone = (TextView)findViewById(R.id.txt_phone);

        sideNave = (ImageView)findViewById(R.id.nav_button);
        sideNave.setOnClickListener(this);

        drawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer);
        listView = (ListView)findViewById(R.id.nav_drawer_list);
        listView.setOnItemClickListener(this);

        /*totalSongPanel = (LinearLayout)findViewById(R.id.total_songs_panel);
        totalPlaylistPanel = (LinearLayout)findViewById(R.id.total_playlist_panel);*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_button :
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if(list.get(position).getData().equalsIgnoreCase("Exit")) {
            ExitDialog dialog = new ExitDialog(this);
            dialog.setRetainInstance(true);
            dialog.show(getSupportFragmentManager(), "Exit");
        } else {
            Constants.changeActivity(this, list.get(position).getData());
        }

    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            ExitDialog dialog = new ExitDialog(this);
            dialog.setRetainInstance(true);
            dialog.show(getSupportFragmentManager(), "Exit");
        }
    }

    @Override
    public void onExit(boolean status) {
        if(status) {
            finish();
            System.exit(0);
            return;
        }
    }
}
