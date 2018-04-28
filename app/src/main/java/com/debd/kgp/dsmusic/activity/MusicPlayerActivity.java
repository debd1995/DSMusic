package com.debd.kgp.dsmusic.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.debd.kgp.dsmusic.R;
import com.debd.kgp.dsmusic.dialog.ExitDialog;
import com.debd.kgp.dsmusic.dialog.ExitDialogListener;
import com.debd.kgp.dsmusic.model.SideNavItem;
import com.debd.kgp.dsmusic.utils.Constants;
import com.debd.kgp.dsmusic.utils.NavDrawerAdapter;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener,
        ExitDialogListener, AdapterView.OnItemClickListener {

    private LinearLayout btn_menu;
    private ImageView playPause, next, previous,muteImg, shuffleImg;
    private RelativeLayout mute, playlist, stop, shuffle;
    private MediaPlayer mediaPlayer;

    private DrawerLayout drawerLayout;
    private ImageView sideNave;
    private ListView listView;
    private List<SideNavItem> list;
    private NavDrawerAdapter adapter;

    private URI uri;
    private Map<String, String> headers;

    private boolean backToExit = false;
    private boolean isPlaying = false;
    private boolean isShuffleOn = false;
    private boolean isMuted =false;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initUIComponents();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPreferences = getSharedPreferences(Constants.USER_LOGIN_CREDENTIAL_PREFERENCE, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        list = Constants.getNavigationListData();
        adapter = new NavDrawerAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        /*headers = new HashMap<>();
        uri =  = new URI("192.168.0.1:8084/DSMusicServer/api/user/debd/audio/audioID");

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(this, uri, headers);*/

    }

    private void initUIComponents() {

        playPause = (ImageView)findViewById(R.id.play_pause_button);
        playPause.setOnClickListener(this);

        next = (ImageView)findViewById(R.id.next_button);
        next.setOnClickListener(this);

        previous = (ImageView)findViewById(R.id.previous_button);
        previous.setOnClickListener(this);

        sideNave = (ImageView)findViewById(R.id.nav_button);
        sideNave.setOnClickListener(this);

        drawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer);
        listView = (ListView)findViewById(R.id.nav_drawer_list);

        mute = (RelativeLayout)findViewById(R.id.audio_mute_panel);
        mute.setOnClickListener(this);

        muteImg = (ImageView)findViewById(R.id.audio_mute_image);

        playlist = (RelativeLayout)findViewById(R.id.audio_playlist_panel);
        playlist.setOnClickListener(this);

        stop = (RelativeLayout)findViewById(R.id.audio_stop_panel);
        stop.setOnClickListener(this);

        shuffle = (RelativeLayout)findViewById(R.id.audio_shuffle_panel);
        shuffle.setOnClickListener(this);

        shuffleImg = (ImageView)findViewById(R.id.audio_shuffle_image);
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
            case R.id.play_pause_button :
                playPause();
                break;
            case R.id.previous_button :
                playPrevious();
                break;
            case R.id.next_button :
                playNext();
                break;
            case R.id.audio_mute_panel :
                audioMutecontrol();
                break;
            case R.id.audio_shuffle_panel :
                audioShuffleControl();
                break;
            case R.id.audio_playlist_panel :
                openPlayList();
                break;
            case R.id.audio_stop_panel :
                stopAudio();
                break;
            default :
                Toast.makeText(this, "Nothing will happen", Toast.LENGTH_SHORT).show();
        }
    }

    private void playNext() {
        if(!isPlaying) {
            isPlaying = true;
            playPause.setImageResource(R.drawable.pause);
        }
        Toast.makeText(this, "next", Toast.LENGTH_SHORT).show();
    }

    private void playPrevious() {
        if(!isPlaying) {
            isPlaying = true;
            playPause.setImageResource(R.drawable.pause);
        }
        Toast.makeText(this, "previous", Toast.LENGTH_SHORT).show();
    }

    private void stopAudio() {
        if(isPlaying) {
            isPlaying = false;
            playPause.setImageResource(R.drawable.play);
            Toast.makeText(this, "audio Stopped", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "nothing to stop", Toast.LENGTH_SHORT).show();
        }
    }

    private void openPlayList() {
        Toast.makeText(this, "Playlist Opening", Toast.LENGTH_SHORT).show();
    }

    private void audioShuffleControl() {
        if(isShuffleOn) {
            isShuffleOn = false;
            shuffleImg.setImageResource(R.drawable.shuffle_off);
            Toast.makeText(this, "shuffle off", Toast.LENGTH_SHORT).show();
        } else {
            isShuffleOn = true;
            shuffleImg.setImageResource(R.drawable.shuffle_on);
            Toast.makeText(this, "shuffle on", Toast.LENGTH_SHORT).show();
        }
    }

    private void audioMutecontrol() {
        if(isMuted) {
            isMuted = false;
            muteImg.setImageResource(R.drawable.not_muted);
            Toast.makeText(this, "mute off", Toast.LENGTH_SHORT).show();
        } else {
            isMuted = true;
            muteImg.setImageResource(R.drawable.muted);
            Toast.makeText(this, "mute on", Toast.LENGTH_SHORT).show();
        }
    }

    private void playPause() {
        if(isPlaying) {
            isPlaying = false;
            playPause.setImageResource(R.drawable.play1);
            Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
        }else {
            isPlaying = true;
            playPause.setImageResource(R.drawable.pause);
            Toast.makeText(this, "playing", Toast.LENGTH_SHORT).show();

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("key","value");
        super.onSaveInstanceState(outState);
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

}