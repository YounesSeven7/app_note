package com.example.noteapp.Utils;

import com.example.noteapp.R;
import com.example.noteapp.ui.activity.AddAndChangeNote;

public class SetColorHelper {

    public static int  setColorBackground(int color) {
        int resources = 0;
        if (color == AddAndChangeNote.COLOR_RED) {
            resources = R.color.red;
        } else if (color == AddAndChangeNote.COLOR_YELLOW) {
            resources = R.color.yellow;
        } else if (color == AddAndChangeNote.COLOR_BLUE) {
            resources = R.color.blue;
        }
        return resources;
    }
}