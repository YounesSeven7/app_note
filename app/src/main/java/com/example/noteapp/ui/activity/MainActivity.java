package com.example.noteapp.ui.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.noteapp.R;
import com.example.noteapp.Utils.LocaleHelper;
import com.example.noteapp.Utils.SharedPreferenceHelper;
import com.example.noteapp.data.database.NoteEntity;
import com.example.noteapp.databinding.AlertDialogChangeColorBinding;
import com.example.noteapp.ui.adapter.NotesAdapter;
import com.example.noteapp.databinding.ActivityMainBinding;
import com.example.noteapp.ui.listeners.OnItemListenerChangeMenuItem;
import com.example.noteapp.ui.viewModel.ViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{
    //
    public static final String EXTRA_NOTE_TO_ADD = "EXTRA_TO_ADD";
    public static final String EXTRA_POSITION_TO_ADD = "EXTRA_POSITION_TO_ADD";
    //
    ActivityMainBinding mainBinding;
    List<NoteEntity> listNotes = new ArrayList<>();
    NotesAdapter notesAdapter;
    ViewModel viewModel;
    MenuItem gridItem, listItem, sendItem, deleteItem, colorItem, makeACopyItem;
    MenuItem trashItem, settingsItem, helpItem;
    OnItemListenerChangeMenuItem onItemListenerChangeMenuItem;
    int numberItemSelected;
    boolean closeNavigationIconVisibility;
    boolean tellAdapter = true;
    // activity for result
    ActivityResultLauncher<Intent> arlToSettings = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    language();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    mainBinding.drawerLayout.close();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        AppCompatDelegate.setDefaultNightMode(SharedPreferenceHelper.getThemeSetting(getBaseContext()));
        language();
        setContentView(mainBinding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        rv();
        menu();
        viewModel.getAllNote().observe(this, new Observer<List<NoteEntity>>() {
            @Override
            public void onChanged(List<NoteEntity> noteEntities) {
                if (noteEntities.size() == 0) {
                    mainBinding.mainFragment.tvNoNotes.setVisibility(View.VISIBLE);
                    mainBinding.mainFragment.rv.setVisibility(View.GONE);
                } else {
                    mainBinding.mainFragment.tvNoNotes.setVisibility(View.GONE);
                    mainBinding.mainFragment.rv.setVisibility(View.VISIBLE);
                    if (tellAdapter) {
                        notesAdapter.addNotes(noteEntities);
                    }
                    tellAdapter = true;
                }

            }
        });
        mainBinding.mainFragment.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AddAndChangeNote.class);
                firstMenuItemVisibility();
                numberItemSelected = 0;
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                        mainBinding.mainFragment.floatingActionButton
                        , ViewCompat.getTransitionName(mainBinding.mainFragment.floatingActionButton));
                startActivity(intent, activityOptionsCompat.toBundle());
            }
        });
        
    }

    void language () {
        LocaleHelper.setLocale(getBaseContext(), SharedPreferenceHelper.getLanguageSetting(getBaseContext()));
    }


    void rv() {
        onItemListenerChangeMenuItem = new OnItemListenerChangeMenuItem() {
                        @Override
            public void onItemFirstLongClickListener() {
                secondMenuItemVisibility();
            }

            @Override
            public boolean onItemAfterFirstLongClickListener(boolean add) {
                if (add) {
                    numberItemSelected++;
                } else {
                    numberItemSelected--;
                }

                if (numberItemSelected != 0) {
                    mainBinding.mainFragment.toolbar.setTitle(String.valueOf(numberItemSelected));
                } else {
                    firstMenuItemVisibility();
                    return true;
                }
                return false;
            }

            @Override
            public void onItemUpdateClickListener(NoteEntity noteEntity, int position) {
                Intent intent = new Intent(MainActivity.this, AddAndChangeNote.class);
                viewModel.position = position;
                intent.putExtra(EXTRA_NOTE_TO_ADD, noteEntity);
                startActivity(intent);
            }
        };
        notesAdapter = new NotesAdapter(listNotes, getBaseContext(), viewModel, onItemListenerChangeMenuItem);
        if (SharedPreferenceHelper.getListItemVisibilitySetting(this)) {
            mainBinding.mainFragment.rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            mainBinding.mainFragment.rv.setLayoutManager(new LinearLayoutManager(this));
        }
        mainBinding.mainFragment.rv.setItemAnimator(null);
        mainBinding.mainFragment.rv.setAdapter(notesAdapter);
    }


    // this menu show when app start and disappears when the user need to delete or update some notes.
    void menu() {
        mainBinding.mainFragment.toolbar.setTitle(R.string.search_your_notes);
        inflateMenuItems();
        gridItem.setVisible(!SharedPreferenceHelper.getListItemVisibilitySetting(this));
        listItem.setVisible(SharedPreferenceHelper.getListItemVisibilitySetting(this));
        menuNavDrawer();
        mainBinding.mainFragment.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.list_rv_item:
                        gridItem.setVisible(true);
                        listItem.setVisible(false);
                        mainBinding.mainFragment.rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                        SharedPreferenceHelper.saveListItemVisibilitySetting(false, getBaseContext());
                        break;
                    case R.id.grid_rv_item:
                        gridItem.setVisible(false);
                        listItem.setVisible(true);
                        SharedPreferenceHelper.saveListItemVisibilitySetting(true, getBaseContext());
                        mainBinding.mainFragment.rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
                        break;
                    case R.id.send_item:
                        send();
                        break;
                    case R.id.delete_item:
                        delete();
                        break;
                    case R.id.color_item:
                        changeColor();
                        break;
                    case R.id.makeACopy_item:
                        makeACopy();
                        break;
                }
                return false;
            }
        });
        mainBinding.mainFragment.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SearchNotes.class);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                        mainBinding.mainFragment.toolbar, Objects.requireNonNull(ViewCompat.getTransitionName(mainBinding.mainFragment.toolbar)));
                startActivity(intent, activityOptionsCompat.toBundle());
            }
        });
        mainBinding.mainFragment.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if (closeNavigationIconVisibility) {
                    // enter her when navigation icon is close icon
                    firstMenuItemVisibility();
                    numberItemSelected = 0;
                    notesAdapter.unSelectAllStroke();
                } else {
                    mainBinding.drawerLayout.openDrawer(Gravity.START);
                }
            }
        });
    }

    void menuNavDrawer() {
        View  view = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        mainBinding.navView.addHeaderView(view);
        mainBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_settings:
                        settings();
                        return true;
                }
                return false;
            }
        });
    }
    // this menu show when the user need to delete or update some notes.
    void inflateMenuItems() {
        mainBinding.mainFragment.toolbar.inflateMenu(R.menu.main_menu);
        mainBinding.navView.inflateMenu(R.menu.menu_drawer_activity_main);
        gridItem = mainBinding.mainFragment.toolbar.getMenu().findItem(R.id.grid_rv_item);
        listItem = mainBinding.mainFragment.toolbar.getMenu().findItem(R.id.list_rv_item);
        sendItem = mainBinding.mainFragment.toolbar.getMenu().findItem(R.id.send_item);
        deleteItem = mainBinding.mainFragment.toolbar.getMenu().findItem(R.id.delete_item);
        colorItem = mainBinding.mainFragment.toolbar.getMenu().findItem(R.id.color_item);
        makeACopyItem = mainBinding.mainFragment.toolbar.getMenu().findItem(R.id.makeACopy_item);
        settingsItem = mainBinding.navView.getMenu().findItem(R.id.nav_settings);
    }

    void firstMenuItemVisibility() {
        gridItem.setVisible(!SharedPreferenceHelper.getListItemVisibilitySetting(this));
        listItem.setVisible(SharedPreferenceHelper.getListItemVisibilitySetting(this));
        sendItem.setVisible(false);
        deleteItem.setVisible(false);
        colorItem.setVisible(false);
        makeACopyItem.setVisible(false);
        mainBinding.mainFragment.toolbar.setNavigationIcon(R.drawable.ic_baseline_dehaze_24);
        closeNavigationIconVisibility = false;
        mainBinding.mainFragment.toolbar.setTitle(R.string.search_your_notes);
    }

    void setTellAdapter (boolean tellAdapter) {
        this.tellAdapter = tellAdapter;
    }

    void secondMenuItemVisibility() {
        gridItem.setVisible(false);
        listItem.setVisible(false);
        sendItem.setVisible(true);
        deleteItem.setVisible(true);
        colorItem.setVisible(true);
        makeACopyItem.setVisible(true);
        mainBinding.mainFragment.toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24);
        closeNavigationIconVisibility = true;
        numberItemSelected = 1;
        mainBinding.mainFragment.toolbar.setTitle(String.valueOf(numberItemSelected));
    }

    void send() {
        List<NoteEntity>  selectedListNotes  = new ArrayList<>();
        for (int i = 0; i < listNotes.size(); i++) {
            if (listNotes.get(i).getSelected()) {
                selectedListNotes.add(listNotes.get(i));
            }
        }

        if (selectedListNotes.size() == 1) {
            String  type = "text/plain";
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            if (selectedListNotes.get(0).getImageUrl() != null) {
                Uri uri = Uri.parse(selectedListNotes.get(0).getImageUrl());
                grantUriPermission(getApplicationContext().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                type = "*/*";
            }
            intent.putExtra(Intent.EXTRA_TEXT, selectedListNotes.get(0).getText());
            intent.setType(type);
            notesAdapter.unSelectAllStroke();
            numberItemSelected = 0;
            firstMenuItemVisibility();
            notesAdapter.canChangeStrokeColor = false;
            startActivity(intent);
        } else if (selectedListNotes.size() > 1) {
            Toast.makeText(getBaseContext(), R.string.you_can_not_send ,Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    void delete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.are_you_sure_about_delete)
                .setMessage(R.string.are_you_sure_about_delete_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notesAdapter.delete();
                        firstMenuItemVisibility();
                        numberItemSelected = 0;
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        notesAdapter.unSelectAllStroke();
                        firstMenuItemVisibility();
                        numberItemSelected = 0;
                    }
                })
                .setBackground(getDrawable(R.drawable.background_border))
                .show();

    }

    void changeColor() {
        View  view = getLayoutInflater().inflate(R.layout.alert_dialog_change_color, null);
        AlertDialogChangeColorBinding binding = AlertDialogChangeColorBinding.bind(view);
        @SuppressLint("UseCompatLoadingForDrawables") MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setView(view)
                .setTitle(R.string.change_color)
                .setBackground(getDrawable(R.drawable.background_border));

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        binding.changeColorRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                firstMenuItemVisibility();
                numberItemSelected = 0;
                notesAdapter.changeNotesColor(checkedId);
                notesAdapter.canChangeStrokeColor = false;
                tellAdapter = false;
                alertDialog.dismiss();
            }
        });
    }



    void makeACopy() {
        //viewModel.insert(mSelectedListNotes);
        notesAdapter.makeACopy();
        firstMenuItemVisibility();
        numberItemSelected = 0;
    }

    void settings() {
        Intent intent = new Intent(MainActivity.this, Settings.class);
        arlToSettings.launch(intent);
    }
}