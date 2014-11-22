package com.example.team8capstone.glasstestapplication;


import com.example.team8capstone.glasstestapplication.image.ImageActivity;
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
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.os.Bundle;
import android.view.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    static final int SLIDE_ONE = 0;
    static final int SLIDE_TWO = 1;
    static final int SLIDE_THREE = 2;
    static final int SLIDE_FOUR = 3;

    private CardScrollView mCardScroller;

    private String mMovieDirectory;

    private boolean mVoiceMenuEnabled = true;

    private CardScrollAdapter mAdapter;

    private String path;
    private File file;
    private Intent i = new Intent();
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mMovieDirectory = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_MOVIES;

        // Requests a voice menu on this activity. As for any other window feature,
        // be sure to request this before setContentView() is called
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        //getWindow().requestFeature(Window.FEATURE_OPTIONS_PANEL);

        // Ensure screen stays on during demo.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCardScroller = new CardScrollView(this);

        mAdapter = new CardAdapter(createCards(this), this);

        mCardScroller.setAdapter(mAdapter);

        setCardScrollerListener();

        setContentView(mCardScroller);

        i.setAction("com.google.glass.action.VIDEOPLAYER");

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        try {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.release();
            }
        }
        catch(Exception e) {

        }

        mCardScroller.deactivate();
        super.onPause();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            getMenuInflater().inflate(R.menu.voice_menu, menu);
            return true;
        }
        // Good practice to pass through, for options menu.
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {

            switch(mCardScroller.getSelectedItemPosition())
            {
                case 0:
                    menu.removeItem(R.id._back);
                    menu.findItem(R.id._goto).getSubMenu().removeItem(R.id._1);
                    menu.add(Menu.NONE,0,Menu.NONE,"view picture");
                    break;
                case 1:
                    menu.add(Menu.NONE,2,Menu.NONE,"play video");
                    menu.findItem(R.id._goto).getSubMenu().removeItem(R.id._2);
                    path = mMovieDirectory+"/"+"Wildlife_512kb.mp4";
                    file = new File(path);
                    if (!file.exists()) {
                        break;
                    }

                    i.putExtra("video_url", path);
                    break;
                case 2:
                    menu.findItem(R.id._goto).getSubMenu().removeItem(R.id._3);
                    try {
                        mediaPlayer.isPlaying();
                    }
                    catch(Exception e) {
                        menu.add(Menu.NONE,1,Menu.NONE,"play audio");
                    }
                    break;
                case 3:
                    menu.removeItem(R.id._next);
                    menu.findItem(R.id._goto).getSubMenu().removeItem(R.id._4);
                    menu.add(Menu.NONE,0,Menu.NONE,"view picture");
                    break;
                default:
                    break;
            }

            try {
                if (mediaPlayer.isPlaying()){
                    menu.add(Menu.NONE,3,Menu.NONE,"stop audio");
                }
            }
            catch(Exception e) {

            }

          // Dynamically decides between enabling/disabling voice menu.
          return mVoiceMenuEnabled;
        }

        // Good practice to pass through, for options menu.
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                case 0:
                    Intent image = new Intent(MainActivity.this, ImageActivity.class);
                    image.putExtra("position", (float) mCardScroller.getSelectedItemPosition());
                    startActivity(image);
                    break;
                case 1:
                    mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.sound_file_1);
                    mediaPlayer.start();
                    break;
                case 2:
                    startActivity(i);
                    break;
                case 3:
                    try {
                        if (mediaPlayer.isPlaying()){
                            mediaPlayer.release();
                        }
                    }
                    catch(Exception e) {

                    }
                    break;
                case R.id._next:
                    if (mCardScroller.getSelectedItemPosition() < mCardScroller.getChildCount())
                    {
                        mCardScroller.setSelection(mCardScroller.getSelectedItemPosition() + 1);
                    }
                    break;
                case R.id._back:
                    if (mCardScroller.getSelectedItemPosition() > 0)
                    {
                        mCardScroller.setSelection(mCardScroller.getSelectedItemPosition() - 1);
                    }
                    break;
                case R.id._exit_yes:
                    finish();
                    break;
                case R.id._1:
                    mCardScroller.setSelection(0);
                    break;
                case R.id._2:
                    mCardScroller.setSelection(1);
                    break;
                case R.id._3:
                    mCardScroller.setSelection(2);
                    break;
                case R.id._4:
                    mCardScroller.setSelection(3);
                    break;
                default:
                    return true;
            }

            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onPanelClosed (int featureId, Menu menu) {
        getWindow().invalidatePanelMenu(WindowUtils.FEATURE_VOICE_COMMANDS);
        invalidateOptionsMenu();
    }

    private void setCardScrollerListener() {

        mCardScroller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getWindow().invalidatePanelMenu(WindowUtils.FEATURE_VOICE_COMMANDS);
                invalidateOptionsMenu();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
                openOptionsMenu();
            }

        });
    }

    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
        cards.add(SLIDE_ONE, new CardBuilder(context, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.leftcolumnlayout));
        cards.add(SLIDE_TWO, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(SLIDE_THREE, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(SLIDE_FOUR, new CardBuilder(context, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.leftcolumnlayout));

        return cards;
    }



}
