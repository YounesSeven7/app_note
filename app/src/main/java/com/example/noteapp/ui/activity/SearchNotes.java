package com.example.noteapp.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.RadioGroup;

import com.example.noteapp.R;
import com.example.noteapp.data.database.NoteEntity;
import com.example.noteapp.databinding.ActivitySearchNotesBinding;
import com.example.noteapp.ui.adapter.NotesAdapter;
import com.example.noteapp.ui.listeners.OnGetSearchNotes;
import com.example.noteapp.ui.viewModel.ViewModel;

import java.util.List;

public class SearchNotes extends AppCompatActivity {
    ActivitySearchNotesBinding searchBinding;
    ViewModel viewModel;
    OnGetSearchNotes onGetSearchNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchBinding = ActivitySearchNotesBinding.inflate(getLayoutInflater());
        setTheme(R.style.searchActivity);
        setContentView(searchBinding.getRoot());
        viewModel = new ViewModelProvider(this).get(ViewModel.class);
        menu();
        getNotes(searchBinding.searchView.getQuery().toString());
        listenerRadioButton();
    }

    private void rv(List<NoteEntity> noteEntities) {
        NotesAdapter notesAdapter = new NotesAdapter(noteEntities, getBaseContext(), viewModel);
        searchBinding.rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        searchBinding.rv.setHasFixedSize(true);
        searchBinding.rv.setAdapter(notesAdapter);
    }

    private void menu() {
        // i use a custom toolbar like menu because there is have more flexibility then the menu .
        searchBinding.searchView.onActionViewExpanded();
        // here receive list of search notes and set in recyclerview
        setHintColor();
        onGetSearchNotes = new OnGetSearchNotes() {
            @Override
            public void onGetSearchNotes(List<NoteEntity> noteEntities) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (noteEntities.size() == 0) {
                            searchBinding.tvNoNotes.setVisibility(View.VISIBLE);
                            searchBinding.rv.setVisibility(View.GONE);
                        } else {
                            searchBinding.tvNoNotes.setVisibility(View.GONE);
                            searchBinding.rv.setVisibility(View.VISIBLE);
                            rv(noteEntities);
                        }

                    }
                });

            }
        };
        searchBinding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getNotes(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getNotes(newText);
                return false;
            }
        });

        searchBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchNotes.super.onBackPressed();
            }
        });
    }

    void setHintColor() {
        int nightModeFlags = getBaseContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                searchBinding.searchView.setQueryHint(Html.fromHtml("<font color = #ffffff>" + getResources().getString(R.string.search_your_notes) + "</font>"));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                searchBinding.searchView.setQueryHint(Html.fromHtml("<font color = #000000>" + getResources().getString(R.string.search_your_notes) + "</font>"));
                break;

        }
    }

    void getNotes(String text) {
        viewModel.getSearchNotes(onGetSearchNotes, getColorFromRadioButton(), getTypeFromRadioButton(), text);
    }

    int getTypeFromRadioButton()  {
       switch (searchBinding.typeRg.getCheckedRadioButtonId()){
           case R.id.justTaxt_rb:
               return AddAndChangeNote.JUST_NOTE;
           case R.id.checkbox_rb:
               return AddAndChangeNote.CHECK_NOTE;
           case R.id.photo_rb:

               return AddAndChangeNote.PHOTO_NOTE;
       }
       return -1;
    }

    int getColorFromRadioButton() {
        switch (searchBinding.colorRg.getCheckedRadioButtonId()) {
            case R.id.colorOff_rb:
                return 0;
            case R.id.redColor_rb:
                return AddAndChangeNote.COLOR_RED;
            case R.id.yellowColor_rb:
                return AddAndChangeNote.COLOR_YELLOW;
            case R.id.blueColor_rb:
                return AddAndChangeNote.COLOR_BLUE;
        }
        return -1;
    }

    void listenerRadioButton() {
         searchBinding.typeRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(RadioGroup group, int checkedId) {
                 getNotes(searchBinding.searchView.getQuery().toString());
             }
         });

        searchBinding.colorRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                getNotes(searchBinding.searchView.getQuery().toString());
            }
        });
    }

}