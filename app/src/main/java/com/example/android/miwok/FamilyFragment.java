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

public class FamilyFragment extends Fragment {
    private MediaPlayer familySoundPlayer;
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
                familySoundPlayer.pause();
                // We want to start the sound from beginning if they're paused
                familySoundPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                familySoundPlayer.start();
            }
        }
    };

    public FamilyFragment() {
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

        final ArrayList<Word> familyMembers = new ArrayList<>();

        familyMembers.add(new Word("әpә", "father", R.drawable.family_father, R.raw.family_father));
        familyMembers.add(new Word("әṭa", "mother", R.drawable.family_mother, R.raw.family_mother));
        familyMembers.add(new Word("angsi", "son", R.drawable.family_son, R.raw.family_son));
        familyMembers.add(new Word("tune", "daughter", R.drawable.family_daughter, R.raw.family_daughter));
        familyMembers.add(new Word("taachi", "older brother", R.drawable.family_older_brother, R.raw.family_older_brother));
        familyMembers.add(new Word("chalitti", "younger brother", R.drawable.family_younger_brother, R.raw.family_younger_brother));
        familyMembers.add(new Word("teṭe", "older sister", R.drawable.family_older_sister, R.raw.family_older_sister));
        familyMembers.add(new Word("kolliti", "younger sister", R.drawable.family_younger_sister, R.raw.family_younger_sister));
        familyMembers.add(new Word("ama", "grandmother", R.drawable.family_grandmother, R.raw.family_grandmother));
        familyMembers.add(new Word("paapa", "grandfather", R.drawable.family_grandfather, R.raw.family_grandfather));

        ListView familyList = rootView.findViewById(R.id.list);

        WordAdapter familyListAdapter = new WordAdapter(getActivity(), familyMembers, R.color.category_family);

        familyList.setAdapter(familyListAdapter);

        familyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word familyMember = familyMembers.get(position);

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

                    familySoundPlayer = MediaPlayer.create(getActivity(), familyMember.getSoundID());
                    familySoundPlayer.start();

                    familySoundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
        if (familySoundPlayer != null) {
            familySoundPlayer.release();
            familySoundPlayer = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        } else {
            audioManager.abandonAudioFocus(afChangeListener);
        }
    }
}