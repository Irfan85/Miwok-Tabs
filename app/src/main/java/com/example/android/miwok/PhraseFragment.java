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

public class PhraseFragment extends Fragment {
    private MediaPlayer phraseSoundPlayer;
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
                phraseSoundPlayer.pause();
                // We want to start the sound from beginning if they're paused
                phraseSoundPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                phraseSoundPlayer.start();
            }
        }
    };

    public PhraseFragment() {
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

        final ArrayList<Word> phrases = new ArrayList<>();

        phrases.add(new Word("minto wuksus", "Where are you going?", R.raw.phrase_where_are_you_going));
        phrases.add(new Word("tinnә oyaase'nә", "What is your name?", R.raw.phrase_what_is_your_name));
        phrases.add(new Word("oyaaset...", "My name is...", R.raw.phrase_my_name_is));
        phrases.add(new Word("michәksәs?", "How are you feeling?", R.raw.phrase_how_are_you_feeling));
        phrases.add(new Word("kuchi achit", "I’m feeling good.", R.raw.phrase_im_feeling_good));
        phrases.add(new Word("әәnәs'aa?", "Are you coming?", R.raw.phrase_are_you_coming));
        phrases.add(new Word("hәә’ әәnәm", "Yes, I’m coming.", R.raw.phrase_yes_im_coming));
        phrases.add(new Word("әәnәm", "I’m coming.", R.raw.phrase_im_coming));
        phrases.add(new Word("yoowutis", "Let’s go.", R.raw.phrase_lets_go));
        phrases.add(new Word("әnni'nem", "Come here.", R.raw.phrase_come_here));

        ListView phraseList = rootView.findViewById(R.id.list);

        WordAdapter phraseListAdapter = new WordAdapter(getActivity(), phrases, R.color.category_phrases);

        phraseList.setAdapter(phraseListAdapter);

        phraseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word phrase = phrases.get(position);

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

                    phraseSoundPlayer = MediaPlayer.create(getActivity(), phrase.getSoundID());
                    phraseSoundPlayer.start();

                    phraseSoundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
        if (phraseSoundPlayer != null) {
            phraseSoundPlayer.release();
            phraseSoundPlayer = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        } else {
            audioManager.abandonAudioFocus(afChangeListener);
        }
    }
}