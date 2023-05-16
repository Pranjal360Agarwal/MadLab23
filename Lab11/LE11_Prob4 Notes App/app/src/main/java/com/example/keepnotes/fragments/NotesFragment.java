package com.example.keepnotes.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.keepnotes.activity.MainActivity;
import com.example.keepnotes.R;
import com.example.keepnotes.activity.NewNote;
import com.example.keepnotes.adapter.NoteAdapter;
import com.example.keepnotes.database.NoteDBHelper;
import com.example.keepnotes.database.NoteDBContract;
import com.example.keepnotes.model.NoteModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NotesFragment extends Fragment {

    private EditText search_text;
    private ImageView menu_button, view_button;

    private FloatingActionButton fab;
    private NoteDBHelper dbHelper;
    private static SQLiteDatabase database;

    private RecyclerView recyclerView;
    private NoteAdapter adapter;

    private int view_mode = 2;
    private ArrayList<NoteModel> List = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);

        initViews(v);
        setUpMenu();
        setUpFAB();
        setUpRecyclerview(v);
        setUpItemTouchHelper(); // to delete the note on swipe left or right
        viewModeFunction();
        searchFunction();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.updateNoteList(allNotes());
    }

    private void searchFunction() {
        search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        ArrayList<NoteModel> filteredList = new ArrayList<>();
        List = allNotes();
        for(NoteModel item : List) {
            if(item.getTitle().toLowerCase().contains(text.toLowerCase()) || item.getNote_text().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.updateFilteredList(filteredList);
    }

    private void setUpItemTouchHelper() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                dbHelper.removeNote((long) viewHolder.itemView.getTag());
//                final int pos = viewHolder.getAdapterPosition();
//                final NoteModel temp = List.get(pos);
//                List = allNotes();
//                List.remove(viewHolder.getAdapterPosition());
//                adapter.updateNoteList(allNotes());
//                Snackbar.make(viewHolder.itemView, "Undo :", Snackbar.LENGTH_LONG)
//                        .setAction("UNDO", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                List.add(pos, temp);
//
//                            }
//                        }).show();
                adapter.updateNoteList(allNotes());
            }
        }).attachToRecyclerView(recyclerView);
    }

    public static ArrayList<NoteModel> allNotes() {
        ArrayList<NoteModel> List = new ArrayList<>();
        Cursor cursor = database.query(NoteDBContract.NoteEntry.TABLE_NAME, null, null, null, null, null,
                NoteDBContract.NoteEntry.COLUMN_TIMESTAMP + " DESC");
        while(cursor.moveToNext()) {
            NoteModel note = new NoteModel(cursor.getLong(cursor.getColumnIndex(NoteDBContract.NoteEntry.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(NoteDBContract.NoteEntry.COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(NoteDBContract.NoteEntry.COLUMN_NOTE_TEXT)),
                    cursor.getString(cursor.getColumnIndex(NoteDBContract.NoteEntry.COLUMN_DATE_TIME)),
                    cursor.getString(cursor.getColumnIndex(NoteDBContract.NoteEntry.COLUMN_WEB_LINK)),
                    cursor.getString(cursor.getColumnIndex(NoteDBContract.NoteEntry.COLUMN_IMAGE_PATH)),
                    cursor.getString(cursor.getColumnIndex(NoteDBContract.NoteEntry.COLUMN_COLOR)));
            List.add(note);
        }
        cursor.close();
        return List;
    }

    private void setUpRecyclerview(View v) {
        recyclerView = v.findViewById(R.id.note_recyclerview);
        recyclerView.hasFixedSize();
        adapter = new NoteAdapter(getContext(), allNotes());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void setUpFAB() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDate = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(new Date());
                long id = dbHelper.addNewNoteData("", "", currentDate, "", "", "#ffffff");

                Bundle bundle = new Bundle();
                bundle.putLong("Id", id);
                bundle.putString("Note_Title", "");
                bundle.putString("Note_Text", "");
                bundle.putString("Date_Time", currentDate);
                bundle.putString("Web_Link", "");
                bundle.putString("Image_Path", "");
                bundle.putString("Color", "#ffffff");

                Intent intent = new Intent(getContext(), NewNote.class);
                intent.putExtras(bundle);
                startActivity(intent);

                adapter.updateNoteList(allNotes());
            }
        });
    }

    private void viewModeFunction() {
        view_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(view_mode == 2) {
                    view_mode = 1;
                    view_button.setImageResource(R.drawable.ic_grid_view);
                } else {
                    view_mode = 2;
                    view_button.setImageResource(R.drawable.ic_linear_view);
                }
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(view_mode, StaggeredGridLayoutManager.VERTICAL));
            }
        });
    }

    private void setUpMenu() {
        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.drawerHandler();
            }
        });
    }

    private void initViews(View v) {
        search_text = v.findViewById(R.id.search_bar);
        menu_button = v.findViewById(R.id.menu_button);
        view_button = v.findViewById(R.id.view_button);
        fab = v.findViewById(R.id.floatingActionButton);

        dbHelper = new NoteDBHelper(getContext());
        database = dbHelper.getWritableDatabase();
    }
}