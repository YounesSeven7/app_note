package com.example.noteapp.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.noteapp.data.database.NoteEntity;
import com.example.noteapp.R;
import com.example.noteapp.databinding.ItemCheckNoteBinding;
import com.example.noteapp.databinding.ItemJustTextNoteBinding;
import com.example.noteapp.databinding.ItemPhotoNoteBinding;
import com.example.noteapp.ui.activity.AddAndChangeNote;
import com.example.noteapp.ui.activity.MainActivity;
import com.example.noteapp.ui.listeners.OnItemListenerChangeMenuItem;
import com.example.noteapp.ui.viewModel.ViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder> {
    public List<NoteEntity> listNotes;
    Context context;
    ViewModel viewModel;
    OnItemListenerChangeMenuItem onItemListenerChangeMenuItem;
    int lastPosition;
    boolean animation = true;
    // the default of this value is false, when the user does not click item form recycler view  a long click
    // but if user click a long click the value becomes true
    // and method onItemClickListener from Interface OnItemListener call
    public boolean canChangeStrokeColor;
    List<NoteEntity> selectedListNotes = new ArrayList<>();
    List<Integer> positions = new ArrayList<>();
    boolean changeColor = true;

    public NotesAdapter(List<NoteEntity> listNotes, Context context, ViewModel viewModel) {
        this.listNotes = listNotes;
        this.context = context;
        this.viewModel = viewModel;
    }

    public NotesAdapter(List<NoteEntity> listNotes, Context context, ViewModel viewModel, OnItemListenerChangeMenuItem onItemListenerChangeMenuItem) {
        this.listNotes = listNotes;
        this.context = context;
        this.viewModel = viewModel;
        this.onItemListenerChangeMenuItem = onItemListenerChangeMenuItem;

    }



    @SuppressLint("NotifyDataSetChanged")
    public void addNotes(List<NoteEntity> listNotes) {
        if (this.listNotes.size() == 0) {
            // enter here in first time like , when start app.
            this.listNotes.addAll(listNotes);
            notifyDataSetChanged();
        } else if (this.listNotes.size() == listNotes.size()) {
            // enter here when click and change check state of some one from check boxes
            if (viewModel.position != -1) {
                this.listNotes.set(reverseOrder(viewModel.position), listNotes.get(reverseOrder(viewModel.position)));
                notifyItemChanged(viewModel.position, listNotes.get(viewModel.position));
            }

        } else if (this.listNotes.size() < listNotes.size()) {
            this.listNotes.clear();
            this.listNotes.addAll(listNotes);
            selectedListNotes.clear();
            canChangeStrokeColor = false;
            notifyDataSetChanged();
        } else if (this.listNotes.size() > listNotes.size()){
            this.listNotes.clear();
            this.listNotes.addAll(listNotes);
            selectedListNotes.clear();
            canChangeStrokeColor = false;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        NoteEntity noteEntity = this.listNotes.get(reverseOrder(position));
        if (noteEntity.isChecked() == null && noteEntity.getImageUrl() == null) {
            return AddAndChangeNote.JUST_NOTE;
        } else if (noteEntity.isChecked() != null && noteEntity.getImageUrl() == null) {
            return AddAndChangeNote.CHECK_NOTE;
        } else if (noteEntity.isChecked() == null && noteEntity.getImageUrl() != null) {
            return AddAndChangeNote.PHOTO_NOTE;
        }
        return -1;
    }
    int number;
    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        NoteHolder noteHolder = null;
        if (viewType == AddAndChangeNote.JUST_NOTE) {
            ItemJustTextNoteBinding itemJustTextNoteBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_just_text_note, parent, false);
            noteHolder = new NoteHolder(itemJustTextNoteBinding);
        } else if (viewType == AddAndChangeNote.CHECK_NOTE) {
            ItemCheckNoteBinding itemCheckNoteBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_check_note, parent, false);
            noteHolder = new NoteHolder(itemCheckNoteBinding);
        } else if (viewType == AddAndChangeNote.PHOTO_NOTE) {
            ItemPhotoNoteBinding itemPhotoNoteBinding = DataBindingUtil.inflate(layoutInflater, R.layout.item_photo_note, parent, false);
            noteHolder = new NoteHolder(itemPhotoNoteBinding);
        }
        return noteHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, @SuppressLint("RecyclerView") int position) {
        NoteEntity note = listNotes.get(reverseOrder(position));
        if (holder.itemJustTextNoteBinding != null && holder.itemCheckNoteBinding == null && holder.itemPhotoNoteBinding == null) {
            holder.itemJustTextNoteBinding.setNote(note);
            onClickItem(holder.itemJustTextNoteBinding.getRoot(), holder);
        } else if (holder.itemJustTextNoteBinding == null && holder.itemCheckNoteBinding != null && holder.itemPhotoNoteBinding == null) {
            holder.itemCheckNoteBinding.setNote(note);
            holder.itemCheckNoteBinding.checkboxNoteItemCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // when change check state in some one form check boxes update the state of this check box in room and notify adapter with the change
                    int position = holder.getAdapterPosition();
                    NoteEntity noteEntity = listNotes.get(reverseOrder(position));
                    noteEntity.setChecked(isChecked);
                    viewModel.update(noteEntity, -1);
                }
            });
            onClickItem(holder.itemCheckNoteBinding.getRoot(), holder);
        } else if (holder.itemJustTextNoteBinding == null && holder.itemCheckNoteBinding == null && holder.itemPhotoNoteBinding != null) {
            holder.itemPhotoNoteBinding.setNote(note);
            onClickItem(holder.itemPhotoNoteBinding.getRoot(), holder);
        }
    }


    void onClickItem(View view, NoteHolder holder) {
        MaterialCardView cardView = view.findViewById(R.id.noteItem_cardView);
        if (onItemListenerChangeMenuItem != null) {
            if (listNotes.get(reverseOrder(holder.getAdapterPosition())).getSelected()) {
                cardView.setStrokeWidth(10);
                cardView.setStrokeColor(context.getResources().getColor(R.color.cardViewSelectStrokeColor));
            } else {
                cardView.setStrokeWidth(1);
                cardView.setStrokeColor(context.getResources().getColor(R.color.cardViewStrokeColor));
                if (listNotes.get(reverseOrder(holder.getAdapterPosition())).isAnimation()) {
                    Animation animation = AnimationUtils.loadAnimation(cardView.getContext(), android.R.anim.slide_in_left);
                    cardView.setAnimation(animation);
                }
                listNotes.get(reverseOrder(holder.getAdapterPosition())).setAnimation(true);
                Log.d("younes", holder.getAdapterPosition()+"");
            }
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (canChangeStrokeColor) {
                        if (cardView.getStrokeWidth() == 10){
                            if (onItemListenerChangeMenuItem.onItemAfterFirstLongClickListener(false)){
                                canChangeStrokeColor = false;
                            }
                            unSelectStroke(holder.getAdapterPosition());
                        } else {
                            onItemListenerChangeMenuItem.onItemAfterFirstLongClickListener(true);
                            selectStroke(holder.getAdapterPosition());
                        }
                    } else {
                        onItemListenerChangeMenuItem.onItemFirstLongClickListener();
                        selectStroke(holder.getAdapterPosition());
                        canChangeStrokeColor = true;
                    }
                    return true;
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (canChangeStrokeColor) {
                        if (cardView.getStrokeWidth() == 10){
                            if (onItemListenerChangeMenuItem.onItemAfterFirstLongClickListener(false)){
                                canChangeStrokeColor = false;
                            }
                            unSelectStroke(holder.getAdapterPosition());
                        } else {
                            onItemListenerChangeMenuItem.onItemAfterFirstLongClickListener(true);
                            selectStroke(holder.getAdapterPosition());
                        }
                    } else {
                        onItemListenerChangeMenuItem.onItemUpdateClickListener(listNotes.get(reverseOrder(holder.getAdapterPosition())), holder.getAdapterPosition());
                    }

                }
            });
        } else {
            Animation animation = AnimationUtils.loadAnimation(cardView.getContext(), android.R.anim.slide_in_left);
            cardView.setAnimation(animation);
        }
    }

    public int reverseOrder(int position) {
        int nextPosition = position + 1;
        return this.listNotes.size() - nextPosition;
    }

    void selectStroke(int position) {
        listNotes.get(reverseOrder(position)).setSelected(true);
        notifyItemChanged(position);
    }

    void unSelectStroke(int position) {
        listNotes.get(reverseOrder(position)).setSelected(false);
        listNotes.get(reverseOrder(position)).setAnimation(false);
        notifyItemChanged(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void unSelectAllStroke() {
        for (int i = 0; i < listNotes.size(); i++) {
           listNotes.get(i).setSelected(false);
           listNotes.get(i).setAnimation(false);
        }
        canChangeStrokeColor = false;
        notifyDataSetChanged();
    }

    public void delete() {
        for (int i = 0; i < listNotes.size(); i++) {
            if (listNotes.get(i).getSelected()) {
                selectedListNotes.add(listNotes.get(i));
            }
        }
        viewModel.deleteAll(selectedListNotes);
    }



    public void changeNotesColor(int checkId) {
        int c = checkId;
        for (int i = 0; i < listNotes.size(); i++) {
            if (listNotes.get(reverseOrder(i)).getSelected()) {
                listNotes.get(reverseOrder(i)).setSelected(false);
                listNotes.get(reverseOrder(i)).setAnimation(false);
                listNotes.get(reverseOrder(i)).setColor(setColor(c));
                viewModel.update(listNotes);
                notifyItemChanged(i);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    int setColor(int checkedId) {
        int color = 0;
        switch (checkedId) {
            case R.id.changeToRedColor_rb:
                color = AddAndChangeNote.COLOR_RED;
                break;
            case R.id.changeToYellowColor_rb:
                color = AddAndChangeNote.COLOR_YELLOW;
                break;
            case R.id.changeToBlueColor_rb:
                color = AddAndChangeNote.COLOR_BLUE;
                break;
        }
        return color;
    }

    public void makeACopy () {
        for (int i = 0; i < listNotes.size(); i++) {
            if (listNotes.get(i).getSelected()) {
                selectedListNotes.add(listNotes.get(i));
            }
        }
        viewModel.insert(selectedListNotes);
    }

    @Override
    public int getItemCount() {
        return listNotes.size();
    }


    public class NoteHolder extends RecyclerView.ViewHolder {
        public ItemJustTextNoteBinding itemJustTextNoteBinding;
        public ItemCheckNoteBinding itemCheckNoteBinding;
        public ItemPhotoNoteBinding itemPhotoNoteBinding;

        public NoteHolder(@NonNull ItemJustTextNoteBinding itemJustTextNoteBinding) {
            super(itemJustTextNoteBinding.getRoot());
            this.itemJustTextNoteBinding = itemJustTextNoteBinding;
        }

        public NoteHolder(@NonNull ItemCheckNoteBinding itemCheckNoteBinding) {
            super(itemCheckNoteBinding.getRoot());
            this.itemCheckNoteBinding = itemCheckNoteBinding;
        }

        public NoteHolder(@NonNull ItemPhotoNoteBinding itemPhotoNoteBinding) {
            super(itemPhotoNoteBinding.getRoot());
            this.itemPhotoNoteBinding = itemPhotoNoteBinding;
        }
    }

}
