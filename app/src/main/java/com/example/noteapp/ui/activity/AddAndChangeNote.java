package com.example.noteapp.ui.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.noteapp.Utils.LocaleHelper;
import com.example.noteapp.Utils.SetColorHelper;
import com.example.noteapp.Utils.SharedPreferenceHelper;
import com.example.noteapp.data.database.NoteEntity;
import com.example.noteapp.R;
import com.example.noteapp.databinding.ActivityAddOrChangeNoteBinding;
import com.example.noteapp.ui.viewModel.ViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class AddAndChangeNote extends AppCompatActivity {
    // for save color note when activity recreate
    public static final int COLOR_RED = 1;
    public static final int COLOR_YELLOW = 2;
    public static final int COLOR_BLUE = 3;
    //
    public static final int JUST_NOTE = 4;
    public static final int CHECK_NOTE = 5;
    public static final int PHOTO_NOTE = 6;

    // for recovery color and type of note from Bundle
    private static final String SAVE_COLOR = "SAVE_COLOR";
    private static final String SAVE_TYPE = "SAVE_TYPE";
    //for put note in intent
    ActivityAddOrChangeNoteBinding addNoteBinding;
    ViewModel viewModel;
    int color = 1;
    int type = 4;
    Uri imageUri;
    NoteEntity updateNoteEntity;
    int position;
    // for permission
    ActivityResultLauncher<String> arlPermission = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (result) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                        intent.setType("image/*");
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        arlImageUrl.launch(intent);

                    }
                }
            }
    );

    // for uri image
    ActivityResultLauncher<Intent> arlImageUrl = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK ) {
                        imageUri = result.getData().getData();
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        addNoteBinding.ivPhotoNote.setImageURI(imageUri);
                        }
                    }
                }

    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addNoteBinding = ActivityAddOrChangeNoteBinding.inflate(getLayoutInflater());
        language();
        setContentView(addNoteBinding.getRoot());
        // saveNote, it is a method return the last value before make recreate for activity
        // return the value for each of color and type variables
        if (getIntent().getSerializableExtra(MainActivity.EXTRA_NOTE_TO_ADD) != null) {
            addNoteBinding.btnAdd.setText(R.string.change);
            updateNoteEntity = (NoteEntity) getIntent().getSerializableExtra(MainActivity.EXTRA_NOTE_TO_ADD);
            addNoteBinding.etTextNote.setText(updateNoteEntity.getText());
            color = updateNoteEntity.getColor();
            addNoteBinding.clContainer.setBackgroundResource(SetColorHelper.setColorBackground(color));
            switch (color) {
                case COLOR_RED:
                    addNoteBinding.rbRed.setChecked(true);
                    break;
                case COLOR_YELLOW:
                    addNoteBinding.rbYellow.setChecked(true);
                    break;
                case COLOR_BLUE:
                    addNoteBinding.rbBlue.setChecked(true);
                    break;
            }
            if (updateNoteEntity.isChecked() == null && updateNoteEntity.getImageUrl() == null) {
                type = JUST_NOTE;
                addNoteBinding.rbJustNote.setChecked(true);
            } else if (updateNoteEntity.isChecked() != null && updateNoteEntity.getImageUrl() == null) {
                type = CHECK_NOTE;
                addNoteBinding.rbCheckNote.setChecked(true);
                addNoteBinding.checkboxNote.setVisibility(View.VISIBLE);
                addNoteBinding.checkboxNote.setChecked(updateNoteEntity.isChecked());
            } else if (updateNoteEntity.isChecked() == null && updateNoteEntity.getImageUrl() != null) {
                type = PHOTO_NOTE;
                addNoteBinding.rbPhotoNote.setChecked(true);
                addNoteBinding.ivPhotoNote.setVisibility(View.VISIBLE);
                imageUri = Uri.parse(updateNoteEntity.getImageUrl());
                addNoteBinding.ivPhotoNote.setImageURI(imageUri);
            }
        } else {
            addNoteBinding.btnAdd.setText(R.string.add);
            saveNote(savedInstanceState);
        }
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        selectTypeNote();
        selectColorNote();
        onClickAddOrChangeButton();
        getImage();
    }


    void language () {
        LocaleHelper.setLocale(getBaseContext(), SharedPreferenceHelper.getLanguageSetting(getBaseContext()));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SAVE_TYPE, type);
        outState.putInt(SAVE_COLOR, color);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBackPressed() {
        if (addNoteBinding.btnAdd.getText().equals(getText(R.string.add))) {
            if (!TextUtils.isEmpty(addNoteBinding.etTextNote.getText())) {
                new MaterialAlertDialogBuilder(this)
                        .setTitle(R.string.are_you_sure)
                        .setMessage(R.string.explain)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setBackground(getDrawable(R.drawable.background_border))
                        .show();
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }

    }

    // type note
    void selectTypeNote () {
            addNoteBinding.rbJustNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectJustNote();
                }
            });

            addNoteBinding.rbCheckNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectCheckNote();

                }
            });

            addNoteBinding.rbPhotoNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectPhotoNote();
                }
            });

        }

    void selectJustNote() {
        addNoteBinding.ivPhotoNote.setVisibility(View.GONE);
        addNoteBinding.checkboxNote.setVisibility(View.GONE);
        type = JUST_NOTE;
        }

    void selectCheckNote () {
        addNoteBinding.ivPhotoNote.setVisibility(View.GONE);
        addNoteBinding.checkboxNote.setVisibility(View.VISIBLE);
        type = CHECK_NOTE;
    }

    void selectPhotoNote () {
        addNoteBinding.ivPhotoNote.setVisibility(View.VISIBLE);
        addNoteBinding.checkboxNote.setVisibility(View.GONE);
        type = PHOTO_NOTE;
    }

    // color note
    void selectColorNote () {
        addNoteBinding.rbRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRedColor();
            }
        });

        addNoteBinding.rbYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectYellowColor();
            }
        });

        addNoteBinding.rbBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBlueColor();
            }
        });
    }

    void selectRedColor () {
        addNoteBinding.clContainer.setBackgroundResource(R.color.red);
        color = COLOR_RED;
        changeTextColor(color);
    }

    void selectYellowColor () {
        addNoteBinding.clContainer.setBackgroundResource(R.color.yellow);
        color = COLOR_YELLOW;
        changeTextColor(color);
    }

    void selectBlueColor () {
        addNoteBinding.clContainer.setBackgroundResource(R.color.blue);
        color = COLOR_BLUE;
        changeTextColor(color);
    }

    void changeTextColor(int color) {
        int resource = 0;
        if (color == COLOR_RED || color == COLOR_YELLOW) {
            resource = getResources().getColor(R.color.white);
        } else if (color == COLOR_BLUE) {
            resource = getResources().getColor(R.color.black);
        }
        addNoteBinding.etTextNote.setHintTextColor(resource);
        addNoteBinding.etTextNote.setTextColor(resource);
        addNoteBinding.checkboxNote.setTextColor(resource);
    }

    // save note when activity recreate
    void saveNote(Bundle save) {
        if (save != null) {
            switch (save.getInt(SAVE_COLOR)) {
                case COLOR_RED:
                    selectRedColor();
                    break;
                case COLOR_YELLOW:
                    selectYellowColor();
                    break;
                case COLOR_BLUE:
                    selectBlueColor();
                    break;
            }

            switch (save.getInt(SAVE_TYPE)) {
                case JUST_NOTE:
                    selectJustNote();
                    break;
                case CHECK_NOTE:
                    selectCheckNote();
                    break;
                case PHOTO_NOTE:
                    selectPhotoNote();
                    break;
            }
        }
    }

    // get image form phone
    void getImage() {
        addNoteBinding.ivPhotoNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arlPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        });
    }

    // onClick add
    void onClickAddOrChangeButton() {
        addNoteBinding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = addNoteBinding.etTextNote.getText().toString();
                if (addNoteBinding.btnAdd.getText().equals(getText(R.string.add))) {
                    if (!TextUtils.isEmpty(text)) {
                        NoteEntity noteEntity = null;
                        if (addNoteBinding.ivPhotoNote.getVisibility() == View.GONE && addNoteBinding.checkboxNote.getVisibility() == View.GONE) {
                            noteEntity = new NoteEntity(text, color, null, null);
                            viewModel.insert(noteEntity);
                            finish();
                        } else if (addNoteBinding.checkboxNote.getVisibility() == View.VISIBLE) {
                            Toast.makeText(getBaseContext(), "younes", Toast.LENGTH_SHORT).show();
                            Boolean verifyChecked = addNoteBinding.checkboxNote.isChecked();
                            noteEntity = new NoteEntity(text, color, verifyChecked, null);
                            viewModel.insert(noteEntity);
                            finish();
                        } else if (addNoteBinding.ivPhotoNote.getVisibility() == View.VISIBLE) {
                            if (imageUri != null) {
                                noteEntity = new NoteEntity(text, color, null, imageUri.toString());
                                viewModel.insert(noteEntity);
                                finish();
                            } else {
                                Toast.makeText(getBaseContext(), R.string.take_image, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), R.string.no_text, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    if (!TextUtils.isEmpty(text)) {
                        updateNoteEntity.setText(text);
                        updateNoteEntity.setColor(color);
                        updateNoteEntity.setImageUrl(null);
                        updateNoteEntity.setChecked(null);
                        if (addNoteBinding.checkboxNote.getVisibility() == View.VISIBLE) {
                            updateNoteEntity.setChecked(addNoteBinding.checkboxNote.isChecked());
                        } else if (addNoteBinding.ivPhotoNote.getVisibility() == View.VISIBLE) {
                            if (imageUri != null) {
                                updateNoteEntity.setImageUrl(imageUri.toString());
                            } else {
                                Toast.makeText(getBaseContext(), R.string.take_image, Toast.LENGTH_LONG).show();
                            }
                        }
                        viewModel.update(updateNoteEntity);
                        finish();
                    }
                    else {
                        Toast.makeText(getBaseContext(), R.string.no_text, Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
    }

}