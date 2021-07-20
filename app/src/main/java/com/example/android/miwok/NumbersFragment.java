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

public class NumbersFragment extends Fragment {
    private MediaPlayer numberSoundPlayer;
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
                numberSoundPlayer.pause();
                // We want to start the sound from beginning if they're paused
                numberSoundPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                numberSoundPlayer.start();
            }
        }
    };

    public NumbersFragment() {
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

        final ArrayList<Word> numbers = new ArrayList<>();

        numbers.add(new Word("lutti", "one", R.drawable.number_one, R.raw.number_one));
        numbers.add(new Word("otiiko", "two", R.drawable.number_two, R.raw.number_two));
        numbers.add(new Word("tolookosu", "three", R.drawable.number_three, R.raw.number_three));
        numbers.add(new Word("oyyisa", "four", R.drawable.number_four, R.raw.number_four));
        numbers.add(new Word("massokka", "five", R.drawable.number_five, R.raw.number_five));
        numbers.add(new Word("temmokka", "six", R.drawable.number_six, R.raw.number_six));
        numbers.add(new Word("kenekaku", "seven", R.drawable.number_seven, R.raw.number_seven));
        numbers.add(new Word("kawinta", "eight", R.drawable.number_eight, R.raw.number_eight));
        numbers.add(new Word("wo'e", "nine", R.drawable.number_nine, R.raw.number_nine));
        numbers.add(new Word("na'aacha", "ten", R.drawable.number_ten, R.raw.number_ten));

        ListView numberList = rootView.findViewById(R.id.list);

        WordAdapter numberAdapter = new WordAdapter(getActivity(), numbers, R.color.category_numbers);

        numberList.setAdapter(numberAdapter);

        numberList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word number = numbers.get(position);

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

                    numberSoundPlayer = MediaPlayer.create(getActivity(), number.getSoundID());
                    numberSoundPlayer.start();

                    numberSoundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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
        if (numberSoundPlayer != null) {
            numberSoundPlayer.release();
            numberSoundPlayer = null;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest);
        } else {
            audioManager.abandonAudioFocus(afChangeListener);
        }
    }
}