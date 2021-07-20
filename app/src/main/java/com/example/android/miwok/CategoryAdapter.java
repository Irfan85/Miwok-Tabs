package com.example.android.miwok;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CategoryAdapter extends FragmentStateAdapter {
    private final int NUM_PAGES = 4;

    public CategoryAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new NumbersFragment();
            case 1:
                return new ColorsFragment();
            case 2:
                return new FamilyFragment();
            case 3:
                return new PhraseFragment();
            default:
                // Doing this just because 'default' is required here.
                return new NumbersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
