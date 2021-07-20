package com.example.android.miwok;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class WordAdapter extends ArrayAdapter<Word> {
    private int backgroundResID;

    public WordAdapter(Activity context, ArrayList<Word> words, int backgroundResID){
        super(context, 0, words);
        this.backgroundResID = backgroundResID;
    }

    @NonNull
    @Override
    // 'convertView' is a view that the ArrayAdapter will send from the views that has been cached. It can be null
    // when the activity has just started.
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        //Both ways work

        //listItemView.setBackgroundColor(getContext().getResources().getColor(backgroundResID));

        int color = ContextCompat.getColor(getContext(), backgroundResID);
        listItemView.setBackgroundColor(color);

        Word word = getItem(position);

        TextView miwok = listItemView.findViewById(R.id.miwok_word);
        assert word != null;
        miwok.setText(word.getMiwok_word());

        TextView english = listItemView.findViewById(R.id.english_word);
        english.setText(word.getEnglish_word());

        ImageView imageView = listItemView.findViewById(R.id.picture);

        if(word.getImageID() != 0) {
            imageView.setImageResource(word.getImageID());
        }else{
            imageView.setVisibility(ImageView.GONE);
        }

        return listItemView;
    }
}
