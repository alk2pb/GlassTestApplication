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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.Window;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements MediaPlayer.OnCompletionListener {

    static final int SLIDE_ONE = 0;
    static final int SLIDE_TWO = 1;
    static final int SLIDE_THREE = 2;
    static final int SLIDE_FOUR = 3;

    private CardScrollView mCardScroller;
    private boolean mVoiceMenuEnabled = true;
    private CardScrollAdapter mAdapter;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPaused = false;

    private Intent image;
    private Intent video;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        image = new Intent(this, ImageActivity.class);
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

    }

    public void onCompletion(MediaPlayer mediaplayer) {
        getWindow().closePanel(WindowUtils.FEATURE_VOICE_COMMANDS);
        closeOptionsMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.reset();
        }

        mCardScroller.deactivate();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mediaPlayer.release();
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
                case SLIDE_ONE:
                    menu.removeItem(R.id._back);
                    menu.findItem(R.id._goto).getSubMenu().removeItem(R.id._1);
                    menu.add(Menu.NONE,0,Menu.NONE,"view picture");
                    break;
                case SLIDE_TWO:
                    menu.add(Menu.NONE,2,Menu.NONE,"play video");
                    menu.findItem(R.id._goto).getSubMenu().removeItem(R.id._2);
                    break;
                case SLIDE_THREE:
                    menu.findItem(R.id._goto).getSubMenu().removeItem(R.id._3);
                        if (!mediaPlayer.isPlaying() && !isPaused) {
                            menu.add(Menu.NONE,1,Menu.NONE,"play audio");
                        }
                    break;
                case SLIDE_FOUR:
                    menu.removeItem(R.id._next);
                    menu.findItem(R.id._goto).getSubMenu().removeItem(R.id._4);
                    menu.add(Menu.NONE,0,Menu.NONE,"view picture");
                    break;
                default:
                    break;
            }

            if (mediaPlayer.isPlaying() || isPaused){
                menu.add(Menu.NONE,3,Menu.NONE,"stop audio");
                if (isPaused){
                    menu.add(Menu.NONE,4,Menu.NONE,"resume");
                }
                else {
                    menu.add(Menu.NONE,5,Menu.NONE,"pause");
                }
                menu.add(Menu.NONE,6,Menu.NONE,"rewind");
                menu.add(Menu.NONE,7,Menu.NONE,"fast forward");
                menu.add(Menu.NONE,8,Menu.NONE,"play from beginning");
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
                    startActivity(image);
                    break;
                case 1:
                    mediaPlayer.start();
                    break;
                case 2:
                    startActivity(video);
                    break;
                case 3:
                    mediaPlayer.reset();
                    isPaused = false;
                    break;
                case 4:
                    mediaPlayer.start();
                    isPaused = false;
                    break;
                case 5:
                    mediaPlayer.pause();
                    isPaused = true;
                    break;
                case 6:
                    if (mediaPlayer.getCurrentPosition() < 3000){
                        mediaPlayer.seekTo(0);
                    }
                    else {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
                    }
                    break;
                case 7:
                    if (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition() < 3000){
                        mediaPlayer.seekTo(mediaPlayer.getDuration());
                    }
                    else {
                        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
                    }
                    break;
                case 8:
                    mediaPlayer.seekTo(0);
                    isPaused = false;
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
                case R.id._cancel:
                    break;
                case R.id._goto_cancel:
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

        mCardScroller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                getWindow().invalidatePanelMenu(WindowUtils.FEATURE_VOICE_COMMANDS);
                invalidateOptionsMenu();
                setMediaResources(mCardScroller.getSelectedItemPosition());
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
