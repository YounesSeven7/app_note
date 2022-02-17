package com.example.noteapp.ui.adapter;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.noteapp.R;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ItemAnimator extends DefaultItemAnimator{

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        Log.d("younes", "younes1");
        return super.animateRemove(holder);
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        Log.d("younes", "younes2");
        return super.animateMove(holder, fromX, fromY, toX, toY);
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        Log.d("younes", "younes3");
        return super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);
    }
}