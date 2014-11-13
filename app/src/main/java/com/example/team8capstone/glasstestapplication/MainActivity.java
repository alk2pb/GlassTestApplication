package com.example.team8capstone.glasstestapplication;


import com.google.android.glass.media.Sounds;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;
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

import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    // Index of api demo cards.
    // Visible for testing.
    static final int CARD_BUILDER = 0;
    static final int CARD_BUILDER_EMBEDDED_LAYOUT = 1;
    static final int CARD_SCROLL_VIEW = 2;
    static final int GESTURE_DETECTOR = 3;
    static final int TEXT_APPEARANCE = 4;
    static final int OPENGL = 5;
    static final int VOICE_MENU = 6;
    static final int SLIDER = 7;


    /** {@link CardScrollView} to use as the main content view. */
    private CardScrollView mCardScroller;

    private String mMovieDirectory;

    private boolean mVoiceMenuEnabled = true;

    private CardScrollAdapter mAdapter;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mMovieDirectory = Environment.getExternalStorageDirectory()+"/"+Environment.DIRECTORY_MOVIES;

        // Requests a voice menu on this activity. As for any other window feature,
        // be sure to request this before setContentView() is called
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        getWindow().requestFeature(Window.FEATURE_OPTIONS_PANEL);

        // Ensure screen stays on during demo.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mCardScroller = new CardScrollView(this);

        mAdapter = new CardAdapter(createCards(this));

        mCardScroller.setAdapter(mAdapter);
        setContentView(mCardScroller);
        setCardScrollerListener();

        //position = mCardScroller.getSelectedItemPosition();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mCardScroller.activate();
    }

    @Override
    protected void onPause() {
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
                    menu.add(Menu.NONE,0,Menu.NONE,Integer.toString(mCardScroller.getSelectedItemPosition()));
                    break;
                case 1:
                    menu.add(Menu.NONE,0,Menu.NONE,Integer.toString(mCardScroller.getSelectedItemPosition()));
                    break;
                default:
                    menu.add(Menu.NONE,0,Menu.NONE,Integer.toString(mCardScroller.getSelectedItemPosition()));
                    break;
            }

            // Dynamically decides between enabling/disabling voice menu.
            return mVoiceMenuEnabled;
        }

        // Good practice to pass through, for options menu.
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        String path;
        File file;
        Intent i = new Intent();
        i.setAction("com.google.glass.action.VIDEOPLAYER");

        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS ||
                featureId == Window.FEATURE_OPTIONS_PANEL) {
            switch (item.getItemId()) {
                /*case R.id.:
                    path = mMovieDirectory+"/"+"Wildlife_512kb.mp4";
                    file = new File(path);
                    if (!file.exists()) {
                        break;
                    }

                    i.putExtra("video_url", path);
                    startActivity(i);
                    break;
                case R.id.menu_coder1:
                    path = mMovieDirectory+"/"+"20140906_114529_673.mp4";
                    file = new File(path);
                    if (!file.exists()) {
                        break;
                    }

                    i.putExtra("video_url", path);
                    startActivity(i);


                    break;*/
                case 0:
                    path = mMovieDirectory+"/"+"20140906_114529_673.mp4";
                    file = new File(path);
                    if (!file.exists()) {
                        break;
                    }

                    i.putExtra("video_url", path);
                    startActivity(i);


                    break;
                case R.id.back:
                    if (mCardScroller.getSelectedItemPosition() > 0)
                    {
                        mCardScroller.setSelection(mCardScroller.getSelectedItemPosition() - 1);
                    }

                    break;
                case R.id.next:

                    if (mCardScroller.getSelectedItemPosition() < mCardScroller.getChildCount())
                    {
                        mCardScroller.setSelection(mCardScroller.getSelectedItemPosition() + 1);
                    }

                    break;
                default: return true;  // No change.
            }

            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**switch(mCardScroller.getSelectedItemPosition())
            {
                case -1:
                    menu.add(Menu.NONE,0,Menu.NONE,"Test");
                    break;
                default:
                    menu.add(Menu.NONE,0,Menu.NONE,"Default");
                    break;
            }
     * Different type of activities can be shown, when tapped on a card.
     */
    private void setCardScrollerListener() {
        /*mCardScroller.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onPreparePanel(WindowUtils.FEATURE_VOICE_COMMANDS, view, );
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });*/
        mCardScroller.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Clicked view at position " + position + ", row-id " + id);
                int soundEffect = Sounds.TAP;
                switch (position) {
                    case CARD_BUILDER:

                        break;

                    case CARD_BUILDER_EMBEDDED_LAYOUT:

                        break;

                    case CARD_SCROLL_VIEW:

                        break;

                    case GESTURE_DETECTOR:

                        break;

                    case TEXT_APPEARANCE:

                        break;

                    case OPENGL:

                        break;

                    case VOICE_MENU:

                        break;

                    case SLIDER:

                        break;

                    default:
                        soundEffect = Sounds.ERROR;
                        Log.d(TAG, "Don't show anything");
                }
                // Play sound.
                AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                am.playSoundEffect(soundEffect);
            }
        });
    }

    /**
     * Create list of API demo cards.
     */
    private List<CardBuilder> createCards(Context context) {
        ArrayList<CardBuilder> cards = new ArrayList<CardBuilder>();
        cards.add(CARD_BUILDER, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(CARD_BUILDER_EMBEDDED_LAYOUT, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(CARD_SCROLL_VIEW, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(GESTURE_DETECTOR, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(TEXT_APPEARANCE, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(OPENGL, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(VOICE_MENU, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        cards.add(SLIDER, new CardBuilder(context, CardBuilder.Layout.TEXT)
                .setText("Test"));
        return cards;
    }

}
