package com.example.team8capstone.glasstestapplication;

import com.example.team8capstone.glasstestapplication.image.ImageActivity;
import com.example.team8capstone.glasstestapplication.video.VideoActivity;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.Window;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;
import java.lang.Runtime;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

// MainActivity creates the main "powerpoint" view and navigation menu
public class MainActivity extends Activity implements MediaPlayer.OnCompletionListener {
    // Instantiates final int's that specify slide positions
    static final int SLIDE_ONE = 0;
    static final int SLIDE_TWO = 1;
    static final int SLIDE_THREE = 2;
    static final int SLIDE_FOUR = 3;

    // Instantiates final int's that specify slide id's in the menu
    static final int SLIDE_ONE_MENU = 20 + SLIDE_ONE;
    static final int SLIDE_TWO_MENU = 20 + SLIDE_TWO;
    static final int SLIDE_THREE_MENU = 20 + SLIDE_THREE;
    static final int SLIDE_FOUR_MENU = 20 + SLIDE_FOUR;

    // Other variables used (name is self explanatory)
    private CardScrollView mCardScroller;
    private boolean mVoiceMenuEnabled = true;
    private CardScrollAdapter mAdapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPaused = false;

    private Intent image;
    private Intent video;

    private static final String TAG = "LogEntryStart";

    private StringBuilder log=new StringBuilder();

    private Time time = new Time();

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // Instantiates a new intent for the ImageActivity that will be activated when
        // the user wishes to view a picture
        image = new Intent(this, ImageActivity.class);

        // Instantiates a new intent for the VideoActivity that will be activated when
        // the user wishes to view a video
        video = new Intent(this, VideoActivity.class);

        // Requests a voice menu on this activity. As for any other window feature,
        // be sure to request this before setContentView() is called
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        // Ensure screen stays on during demo.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCardScroller = new CardScrollView(this);
        mAdapter = new CardAdapter(createCards(this), this);
        mCardScroller.setAdapter(mAdapter);
        setCardScrollerListener();
        setContentView(mCardScroller);

        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));


            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }

        } catch (IOException e) {
        }
    }

    // When the MediaPlayer finishes, close and refresh the menu
    public void onCompletion(MediaPlayer mediaplayer) {
        getWindow().closePanel(WindowUtils.FEATURE_VOICE_COMMANDS);
        closeOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
        time.setToNow();
        Log.i(TAG,time.toString() + ", " + "MainActivity activated" + " LogEntryEnd");
        Log.i(TAG,time.toString() + ", " + "Current Slide:" + mCardScroller.getSelectedItemPosition() + " LogEntryEnd");
    }

    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }

        mCardScroller.deactivate();
        time.setToNow();
        Log.i(TAG, time.toString() + ", " + "MainActivity deactivated" + " LogEntryEnd");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // Release the MediaPlayer when the activity finishes
        mediaPlayer.release();
        time.setToNow();
        Log.i(TAG,time.toString() + ", " + "MainActivity destroyed" + " LogEntryEnd");
        try {
            File file = new File(Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_DOWNLOADS + "/log_" + time.toString().replace("/","_") + ".txt");

            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(log.toString());
            output.close();
        }
        catch (Exception e){
            Log.e(TAG,e.getMessage());
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.voice_menu, menu);
            time.setToNow();
            Log.i(TAG,time.toString() + ", " + "Menu created" + " LogEntryEnd");
            return true;
        }
        // Good practice to pass through, for options menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {

            int position = mCardScroller.getSelectedItemPosition();

            addDefaultMenuOptions(menu, position);
            addCustomMenuOptions(menu, position);

            // If the MediaPlayer is playing, add audio options
            if (mediaPlayer.isPlaying() || isPaused){
                addAudioMenuOptions(menu);

            }

            collapseMenu(menu,1);
            time.setToNow();
            Log.i(TAG,time.toString() + ", " + "Menu populated" + " LogEntryEnd");

          // Dynamically decides between enabling/disabling voice menu.
          return mVoiceMenuEnabled;
        }

        // Good practice to pass through, for options menu.
        return super.onPreparePanel(featureId, view, menu);
    }

    // Add default menu options
    private void addDefaultMenuOptions(Menu menu, int position){
        menu.add(Menu.NONE,10,Menu.NONE,"next");
        menu.add(Menu.NONE,11,Menu.NONE,"back");
        menu.addSubMenu(Menu.NONE,12,Menu.NONE,"goto");
        setGotoMenuOptions(menu, position);
        menu.addSubMenu(Menu.NONE,13,Menu.NONE,"exit");
        menu.findItem(13).getSubMenu().add(Menu.NONE,14,Menu.NONE,"yes");
    }

    private void setGotoMenuOptions(Menu menu, int position) {
        // Add menu options based on slide position
        switch(position)
        {
            case SLIDE_ONE:
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_TWO_MENU,Menu.NONE,"2");
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_THREE_MENU,Menu.NONE,"3");
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_FOUR_MENU,Menu.NONE,"4");
                break;
            case SLIDE_TWO:
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_ONE_MENU,Menu.NONE,"1");
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_THREE_MENU,Menu.NONE,"3");
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_FOUR_MENU,Menu.NONE,"4");
                break;
            case SLIDE_THREE:
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_ONE_MENU,Menu.NONE,"1");
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_TWO_MENU,Menu.NONE,"2");
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_FOUR_MENU,Menu.NONE,"4");
                break;
            case SLIDE_FOUR:
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_ONE_MENU,Menu.NONE,"1");
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_TWO_MENU,Menu.NONE,"2");
                menu.findItem(12).getSubMenu().add(Menu.NONE,SLIDE_THREE_MENU,Menu.NONE,"3");
                break;
            default:
                break;
        }
    }

    private void addCustomMenuOptions(Menu menu, int position){
        // Add menu options based on slide position
        switch(position)
        {
            case SLIDE_ONE:
                menu.removeItem(11);
                menu.findItem(12).getSubMenu().removeItem(SLIDE_ONE_MENU);
                menu.add(Menu.NONE,0,Menu.NONE,"view picture");
                break;
            case SLIDE_TWO:
                menu.add(Menu.NONE,2,Menu.NONE,"play video");
                menu.findItem(12).getSubMenu().removeItem(SLIDE_TWO_MENU);
                break;
            case SLIDE_THREE:
                menu.findItem(12).getSubMenu().removeItem(SLIDE_THREE_MENU);
                if (!mediaPlayer.isPlaying() && !isPaused) {
                    menu.add(Menu.NONE,1,Menu.NONE,"play audio");
                }
                break;
            case SLIDE_FOUR:
                menu.removeItem(10);
                menu.findItem(12).getSubMenu().removeItem(SLIDE_FOUR_MENU);
                menu.add(Menu.NONE,0,Menu.NONE,"view picture");
                break;
            default:
                break;
        }
    }

    private void addAudioMenuOptions(Menu menu){
        menu.add(Menu.NONE,3,Menu.NONE,"stop audio");
        menu.addSubMenu(Menu.NONE,9,Menu.NONE,"audio options");
        if (isPaused){
            menu.findItem(9).getSubMenu().add(Menu.NONE,4,Menu.NONE,"resume");
        }
        else {
            menu.findItem(9).getSubMenu().add(Menu.NONE,5,Menu.NONE,"pause");
        }
        menu.findItem(9).getSubMenu().add(Menu.NONE,6,Menu.NONE,"rewind");
        menu.findItem(9).getSubMenu().add(Menu.NONE,7,Menu.NONE,"fast forward");
        menu.findItem(9).getSubMenu().add(Menu.NONE,8,Menu.NONE,"play from beginning");
    }

    // Collapse a menu to prevent menu options from going off the viewable screen
    private void collapseMenu(Menu menu, int level){
        if (level == 1){
            if (menu.size() > 4){
                menu.addSubMenu(Menu.NONE,level*100,Menu.NONE,"more options");
            }
        }
        else {
            if (menu.size() > 3){
                menu.addSubMenu(Menu.NONE,level*100,Menu.NONE,"more options");
            }
        }

        if (level == 1){
            if (menu.size() > 4){
                for (int i = 3; i < menu.size(); i++){
                    if (!(menu.getItem(i).getItemId()/100 >= 1)){
                        if(menu.getItem(i).hasSubMenu()){
                            reMenu(menu.findItem(level*100).getSubMenu(), menu.getItem(i));
                        }
                        else {
                            menu.findItem(level*100).getSubMenu().add(Menu.NONE,menu.getItem(i).getItemId(),Menu.NONE,menu.getItem(i).getTitle());
                        }
                        menu.removeItem(menu.getItem(i).getItemId());
                    }
                }
            }
        }
        else {
            if (menu.size() > 3){
                for (int i = 2; i < menu.size(); i++){
                    if (!(menu.getItem(i).getItemId()/100 >= 1)){
                        if(menu.getItem(i).hasSubMenu()){
                            reMenu(menu.findItem(level*100).getSubMenu(), menu.getItem(i));
                        }
                        else {
                            menu.findItem(level*100).getSubMenu().add(Menu.NONE,menu.getItem(i).getItemId(),Menu.NONE,menu.getItem(i).getTitle());
                        }
                        menu.removeItem(menu.getItem(i).getItemId());
                    }
                }
            }
        }

        for (int i = 0; i < menu.size(); i++){
            if (menu.getItem(i).hasSubMenu()){
                collapseMenu(menu.getItem(i).getSubMenu(),level+1);
            }
        }
        menu.add(Menu.NONE,99,Menu.NONE,"cancel");
    }

    // Add a pre-populated SubMenu to another Menu
    private void reMenu(Menu menu, MenuItem reMenu){
        menu.addSubMenu(Menu.NONE,reMenu.getItemId(),Menu.NONE,reMenu.getTitle());
        for (int i = 0; i < reMenu.getSubMenu().size(); i++){
            if (reMenu.getSubMenu().getItem(i).hasSubMenu()){
                menu.findItem(reMenu.getItemId()).getSubMenu().addSubMenu(Menu.NONE,reMenu.getSubMenu().getItem(i).getItemId(),Menu.NONE,reMenu.getSubMenu().getItem(i).getTitle());
                reMenu(menu.findItem(reMenu.getItemId()).getSubMenu().findItem(reMenu.getSubMenu().getItem(i).getItemId()).getSubMenu(),reMenu.getSubMenu().getItem(i));
            }
            else{
                menu.findItem(reMenu.getItemId()).getSubMenu().add(Menu.NONE,reMenu.getSubMenu().getItem(i).getItemId(),Menu.NONE,reMenu.getSubMenu().getItem(i).getTitle());
            }
        }
    }

    // Set menu item actions
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                case 0:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "ImageActivity started" + " LogEntryEnd");
                    startActivity(image);
                    break;
                case 1:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "MediaPlayer started" + " LogEntryEnd");
                    mediaPlayer.start();
                    break;
                case 2:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "VideoActivity started" + " LogEntryEnd");
                    startActivity(video);
                    break;
                case 3:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "MediaPlayer stopped" + " LogEntryEnd");
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    isPaused = false;
                    break;
                case 4:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "MediaPlayer resumed" + " LogEntryEnd");
                    mediaPlayer.start();
                    isPaused = false;
                    break;
                case 5:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "MediaPlayer paused" + " LogEntryEnd");
                    mediaPlayer.pause();
                    isPaused = true;
                    break;
                case 6:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "MediaPlayer rewinded" + " LogEntryEnd");
                    if (mediaPlayer.getCurrentPosition() < 3000){
                        mediaPlayer.seekTo(0);
                    }
                    else {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
                    }
                    break;
                case 7:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "ImageActivity fast forwarded" + " LogEntryEnd");
                    if (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() < 3000){
                        mediaPlayer.seekTo(mediaPlayer.getDuration());
                    }
                    else {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
                    }
                    break;
                case 8:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "MediaPlayer restarted" + " LogEntryEnd");
                    mediaPlayer.seekTo(0);
                    isPaused = false;
                    break;
                case 10:
                    if (mCardScroller.getSelectedItemPosition() < mCardScroller.getChildCount())
                    {
                        mCardScroller.setSelection(mCardScroller.getSelectedItemPosition() + 1);
                    }
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "Next slide selected" + " LogEntryEnd");
                    break;
                case 11:
                    if (mCardScroller.getSelectedItemPosition() > 0)
                    {
                        mCardScroller.setSelection(mCardScroller.getSelectedItemPosition() - 1);
                    }
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "Previous slide selected" + " LogEntryEnd");
                    break;
                case 14:
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "Exit selected" + " LogEntryEnd");
                    finish();
                    break;
                case SLIDE_ONE_MENU:
                    mCardScroller.setSelection(0);
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "Goto selected" + " LogEntryEnd");
                    break;
                case SLIDE_TWO_MENU:
                    mCardScroller.setSelection(1);
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "Goto selected" + " LogEntryEnd");
                    break;
                case SLIDE_THREE_MENU:
                    mCardScroller.setSelection(2);
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "Goto selected" + " LogEntryEnd");
                    break;
                case SLIDE_FOUR_MENU:
                    mCardScroller.setSelection(3);
                    time.setToNow();
                    Log.i(TAG,time.toString() + ", " + "Goto selected" + " LogEntryEnd");
                    break;
                default:
                    return true;
            }

            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onMenuOpened (int featureId, Menu menu) {
        time.setToNow();
        Log.i(TAG,time.toString() + ", " + "Menu opened" + " LogEntryEnd");
        return true;
    }

    // When the menu is closed, refresh it
    @Override
    public void onPanelClosed (int featureId, Menu menu) {
        getWindow().invalidatePanelMenu(WindowUtils.FEATURE_VOICE_COMMANDS);
        invalidateOptionsMenu();
        time.setToNow();
        Log.i(TAG,time.toString() + ", " + "Menu closed" + " LogEntryEnd");
    }

    // Set media resources based on slide position
    private void setMediaResources(int position){
        switch(position)
        {
            case SLIDE_ONE:
                image.removeExtra("resource");
                image.putExtra("resource", R.drawable.beach);
                break;
            case SLIDE_TWO:
                video.removeExtra("resource");
                video.putExtra("resource", R.raw.video_file_1);
                break;
            case SLIDE_THREE:
                if (!mediaPlayer.isPlaying() && !isPaused) {
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.sound_file_1);
                    mediaPlayer.setOnCompletionListener(MainActivity.this);
                }
                break;
            case SLIDE_FOUR:
                image.removeExtra("resource");
                image.putExtra("resource", R.drawable.supplies);
                break;
            default:
                break;
        }
    }

    private void setCardScrollerListener() {
        // When an item is selected, refresh the menu
        mCardScroller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getWindow().invalidatePanelMenu(WindowUtils.FEATURE_VOICE_COMMANDS);
                invalidateOptionsMenu();
                setMediaResources(mCardScroller.getSelectedItemPosition());
                time.setToNow();
                Log.i(TAG,time.toString() + ", " + "New slide selected" + " LogEntryEnd");
                Log.i(TAG,time.toString() + ", " + "Current slide: " + mCardScroller.getSelectedItemPosition() + " LogEntryEnd");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Add sound effect when an slide is clicked
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int soundEffect = Sounds.TAP;
                switch (position) {
                    case SLIDE_ONE:
                        break;
                    case SLIDE_TWO:
                        break;
                    case SLIDE_THREE:
                        break;
                    case SLIDE_FOUR:
                        break;
                    default:
                        soundEffect = Sounds.ERROR;
                }
                time.setToNow();
                Log.i(TAG,time.toString() + ", " + "Slide " + mCardScroller.getSelectedItemPosition() + " tapped" + " LogEntryEnd");
                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
                openOptionsMenu();
            }
        });
    }

    // Create the slides for the "powerpoint" view
    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
        cards.add(SLIDE_ONE, new CardBuilder(context, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.left_column_layout));
        cards.add(SLIDE_TWO, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(SLIDE_THREE, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(SLIDE_FOUR, new CardBuilder(context, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.left_column_layout));

        return cards;
    }
}
