package com.example.keepnotes.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.keepnotes.R;
import com.example.keepnotes.adapter.NoteAdapter;
import com.example.keepnotes.database.NoteDBHelper;
import com.example.keepnotes.fragments.NotesFragment;
import com.example.keepnotes.model.NoteModel;
import com.example.keepnotes.activity.MainActivity.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.view.View.VISIBLE;

public class NewNote extends AppCompatActivity {

    private EditText input_title, input_note_text;
    private ImageView back_button, add_more_button, more_option_button;
    private TextView edited;
    private LinearLayout more_option_layout;
    private NoteDBHelper dbHelper;
    private int mode;
    private Bundle bundle = null;
    private NoteModel note;
    private ConstraintLayout new_note_layout;
    private TextView delete_button_text;
    private ImageView delete_button_icon;

    private ImageView color0, color1, color2, color3, color4, color5, color6, color7, color8, color9, color10, color11, color12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        initViews();
        getIntentData();

        backButtonFunction();
        editTextSaveData();
        moreOptionButtonFunction();
        initColorButtons();
        setBackgroundColor();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        bundle = intent.getExtras();
        if (bundle != null) {
            note = new NoteModel(bundle.getLong("Id"),
                    bundle.getString("Note_Title"),
                    bundle.getString("Note_Text"),
                    bundle.getString("Date_Time"),
                    bundle.getString("Web_Link"),
                    bundle.getString("Image_Path"),
                    bundle.getString("Color"));

            input_title.setText(note.getTitle());
            input_note_text.setText(note.getNote_text());
            edited.setText(note.getDate_time());
            new_note_layout.setBackgroundColor(Color.parseColor(note.getColor()));
            input_title.setBackgroundColor(Color.parseColor(note.getColor()));
            input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
        }
    }

    private void setColorPalate() {
        switch (note.getColor()) {
            case "#ffffff" :
//                Log.d("De : ", "color0");
                color0.setImageResource(R.drawable.ic_selected2);
                break;
            case "#262626" :
                color1.setImageResource(R.drawable.ic_selected);
                break;
            case "#993333" :
                color2.setImageResource(R.drawable.ic_selected);
                break;
            case "#cc9900" :
                color3.setImageResource(R.drawable.ic_selected);
                break;
            case "#cccc00" :
                color4.setImageResource(R.drawable.ic_selected);
                break;
            case "#339933" :
                color5.setImageResource(R.drawable.ic_selected);
                break;
            case "#009999" :
                color6.setImageResource(R.drawable.ic_selected);
                break;
            case "#006666" :
                color7.setImageResource(R.drawable.ic_selected);
                break;
            case "#003366" :
                color8.setImageResource(R.drawable.ic_selected);
                break;
            case "#6600cc" :
                color9.setImageResource(R.drawable.ic_selected);
                break;
            case "#993366" :
                color10.setImageResource(R.drawable.ic_selected);
                break;
            case "#663300":
                color11.setImageResource(R.drawable.ic_selected);
                break;
            case "#666699":
                color12.setImageResource(R.drawable.ic_selected);
                break;
            default:
                Log.d("De :", note.getColor());
        }
    }

    private void editTextSaveData() {
        input_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String title = input_title.getText().toString().trim();
                String currentDate = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(new Date());
                note.setTitle(title);
                note.setDate_time(currentDate);
                dbHelper.updateNoteData(note);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        input_note_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String note_text = input_note_text.getText().toString().trim();
                String currentDate = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault()).format(new Date());
                note.setNote_text(note_text);
                note.setDate_time(currentDate);
                dbHelper.updateNoteData(note);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void moreOptionButtonFunction() {
        more_option_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(more_option_layout.getVisibility() == VISIBLE) {
                    more_option_layout.setVisibility(View.GONE);
                } else {
                    more_option_layout.setVisibility(VISIBLE);
                    setColorPalate();
                }
            }
        });

        delete_button_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.removeNote(note.getId());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        delete_button_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.removeNote(note.getId());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void backButtonFunction() {
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NoteAdapter adapter = new NoteAdapter(getApplicationContext(), NotesFragment.allNotes());
                onBackPressed();
            }
        });
    }

    private void initViews() {
        new_note_layout = findViewById(R.id.new_note_layout);
        input_title = findViewById(R.id.new_note_title);
        input_note_text = findViewById(R.id.new_note_text);
        edited = findViewById(R.id.edit_info);
        back_button = findViewById(R.id.back_button);
        delete_button_text = findViewById(R.id.delete_button_text);
        delete_button_icon = findViewById(R.id.delete_button_icon);
        add_more_button = findViewById(R.id.add_more_button);
        more_option_button = findViewById(R.id.more_option_button);
        more_option_layout = findViewById(R.id.more_option_layout);
        new_note_layout = findViewById(R.id.new_note_layout);
        dbHelper = new NoteDBHelper(this);
    }

    private void initColorButtons() {
        color0 = findViewById(R.id.color0);
        color1 = findViewById(R.id.color1);      color2 = findViewById(R.id.color2);      color3 = findViewById(R.id.color3);
        color4 = findViewById(R.id.color4);      color5 = findViewById(R.id.color5);      color6 = findViewById(R.id.color6);
        color7 = findViewById(R.id.color7);      color8 = findViewById(R.id.color8);      color9 = findViewById(R.id.color9);
        color10 = findViewById(R.id.color10);    color11 = findViewById(R.id.color11);    color12 = findViewById(R.id.color12);
    }

    private void setNoSelectedColor() {
        color0.setImageResource(0);
        color1.setImageResource(0);      color2.setImageResource(0);      color3.setImageResource(0);
        color4.setImageResource(0);      color5.setImageResource(0);      color6.setImageResource(0);
        color7.setImageResource(0);      color8.setImageResource(0);      color9.setImageResource(0);
        color10.setImageResource(0);     color11.setImageResource(0);     color12.setImageResource(0);
    }

    private void setBackgroundColor() {
        color0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color0.setImageResource(R.drawable.ic_selected2);
                new_note_layout.setBackgroundColor(Color.parseColor("#ffffff"));
                note.setColor("#ffffff");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color1.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#262626"));
                note.setColor("#262626");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color2.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#993333"));
                note.setColor("#993333");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color3.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#cc9900"));
                note.setColor("#cc9900");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color4.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#cccc00"));
                note.setColor("#cccc00");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color5.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#339933"));
                note.setColor("#339933");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color6.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#009999"));
                note.setColor("#009999");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
         });

        color7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color7.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#006666"));
                note.setColor("#006666");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });
        color8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color8.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#003366"));
                note.setColor("#003366");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color9.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#6600cc"));
                note.setColor("#6600cc");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color10.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#993366"));
                note.setColor("#993366");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color11.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#663300"));
                note.setColor("#663300");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });

        color12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNoSelectedColor();
                color12.setImageResource(R.drawable.ic_selected);
                new_note_layout.setBackgroundColor(Color.parseColor("#666699"));
                note.setColor("#666699");
                input_title.setBackgroundColor(Color.parseColor(note.getColor()));
                input_note_text.setBackgroundColor(Color.parseColor(note.getColor()));
                dbHelper.updateNoteData(note);
            }
        });
    }
}