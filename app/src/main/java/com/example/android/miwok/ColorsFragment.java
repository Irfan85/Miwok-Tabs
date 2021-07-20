package com.example.android.miwok;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ColorsFragment extends Fragment {
    private MediaPlayer colorSoundPlayer;
    private AudioManager audioManager;
    private AudioFocusRequest focusRequest;

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Permanent loss of audio focus
                // Pause playback immediately
                releaseMediaPlayer();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Pause playback
                colorSoundPlayer.pause();
                // We want to start the sound from beginning if they're paused
                colorSoundPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                colorSoundPlayer.start();
            }
        }
    };

    public ColorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.word_list, container, false);

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        // Setting the type of audio focus request we want to make. (Requires Android Oreo or newer)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    .setAudioAttributes(audioAttributes)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(afChangeListener)
                    .build();
        }

        final ArrayList<Word> colors = new ArrayList<>();

        colors.add(new Word("weṭeṭṭi", "red", R.drawable.color_red, R.raw.color_red));
        colors.add(new Word("chokokki", "green", R.drawable.color_green, R.raw.color_green));
        colors.add(new Word("ṭakaakki", "brown", R.drawable.color_brown, R.raw.color_brown));
        colors.add(new Word("ṭopoppi", "gray", R.drawable.color_gray, R.raw.color_gray));
        colors.add(new Word("kululli", "black", R.drawable.color_black, R.raw.color_black));
        colors.add(new Word("kelelli", "white", R.drawable.color_white, R.raw.color_white));
        colors.add(new Word("ṭopiisә", "dusty yellow", R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));
        colors.add(new Word("chiwiiṭә", "mustard yellow", R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));

        ListView colorList = rootView.findViewById(R.id.list);

        WordAdapter colorListAdapter = new WordAdapter(getActivity(), colors, R.color.category_colors);

        colorList.setAdapter(colorListAdapter);

        colorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word color = colors.get(position);

                // Request audio focus for playback
                assert audioManager != null;

                int result;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    result = audioManager.requestAudioFocus(focusRequest);
                } else {
                    result = audioManager.requestAudioFocus(afChangeListener,
                            // Use the music stream.
                            AudioManager.STREAM_MUSIC,
                            // Request permanent focus.
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                }

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // First, release the media player if it's already playing a sound file, so that the sound starts playing
                    // as soon as the user clicks the item.
                    releaseMediaPlayer();

                    colorSoundPlayer = MediaPlayer.create(getActivity(), color.getSoundID());
                    colorSoundPlayer.start();

                    colorSoundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            // We're not using parameter 'mp' because we're using a single MediaPlayer object
                            // for this entire activity. We can't access the global MediaPlayer object using that
                            // parameter. So, we're using a separate function.
                            releaseMediaPlayer();
                        }
                    });
                }
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if (colorSoundPlayer != null) {
            colorSoundPlayer.release();
            colorSoundPlayer = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        } else {
            audioManager.abandonAudioFocus(afChangeListener);
        }
    }
}