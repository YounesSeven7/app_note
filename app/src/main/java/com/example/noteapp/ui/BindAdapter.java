package com.example.noteapp.ui;

import android.net.Uri;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BindingAdapter;

import com.example.noteapp.R;
import com.example.noteapp.Utils.SetColorHelper;
import com.example.noteapp.ui.activity.AddAndChangeNote;

public class BindAdapter {
    // this method set isChecked state and make listener for checked box state
    @BindingAdapter("set_checked_state")
    public static void setNoteVisibility(CheckBox checkBox, Boolean isChecked) {
        if (isChecked == null) {
            checkBox.setVisibility(View.GONE);
        } else {
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(isChecked);
        }
    }

    // image view adapter
    @BindingAdapter("set_uri")
    public static void  setUri(ImageView imageView, String imageUrl) {

        imageView.setImageURI(Uri.parse(imageUrl));


    }

    @BindingAdapter("set_note_background_color")
    public static void setNoteColorBackground(ConstraintLayout constraintLayout, int color) {
        constraintLayout.setBackgroundResource(SetColorHelper.setColorBackground(color));
    }

    @BindingAdapter("set_note_text_color")
    public static void  setNoteTextColor(TextView textView, int color) {
        int resources = 0;
        if (color == AddAndChangeNote.COLOR_RED || color == AddAndChangeNote.COLOR_YELLOW) {
            resources = textView.getContext().getResources().getColor(R.color.white);
        } else if (color == AddAndChangeNote.COLOR_BLUE) {
            resources = textView.getContext().getResources().getColor(R.color.black);
        }
        textView.setTextColor(resources);
    }

    @BindingAdapter("set_note_text_background")
    public static void setNoteTextBackground(TextView textView,  int color) {
        textView.setBackgroundResource(SetColorHelper.setColorBackground(color));
    }
}
