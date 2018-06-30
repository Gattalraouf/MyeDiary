package com.gattal.asta.myediary;

import android.app.DatePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gattal.asta.myediary.dataBase.AppDataBase;
import com.gattal.asta.myediary.dataBase.DiaryEntry;
import com.google.firebase.auth.FirebaseAuth;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DiaryContent extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private AppDataBase mDateBase;
    private int mIndex,mId;
    private FirebaseAuth mAuth;
    private KeyListener mListner;

    @BindView(R.id.date_piker) EditText _datePicker;
    @BindView(R.id.content_edit_text) EditText _content;
    @BindView(R.id.title_edit_text) EditText _title;
    @BindView(R.id.modif) TextView _modify;
    @BindView(R.id.save) TextView _save;
    @BindView(R.id.delete) TextView _delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_content);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        mDateBase = AppDataBase.getsInstance(getApplicationContext());

        Intent intent = getIntent();
        mIndex = intent.getIntExtra("Index",-1);
        mId = intent.getIntExtra("ID",-1);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mIndex==-1) {

            _datePicker.setKeyListener(null);
            SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("dd - MMM - yyyy");
            dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+1"));
            _datePicker.setText(dateTimeInGMT.format(new Date()));

            _modify.setVisibility(View.GONE);
            _delete.setVisibility(View.GONE);

        }
        else {

            _save.setVisibility(View.GONE);

                    final LiveData<DiaryEntry> diaryEntry = mDateBase.diaryDao().loadDiaryById(mId);
                    diaryEntry.observe(this, new Observer<DiaryEntry>() {
                        @Override
                        public void onChanged(@Nullable DiaryEntry diaryEntry1) {
                            diaryEntry.removeObserver(this);
                            _content.setText(diaryEntry1.getContent());
                            _title.setText(diaryEntry1.getTitle());
                            SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("dd - MMM - yyyy");
                            dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                            _datePicker.setText(dateTimeInGMT.format(diaryEntry1.getDate()));
                        }
                    });

            mListner = _content.getKeyListener();
            _datePicker.setKeyListener(null);
            _content.setKeyListener(null);
            _title.setKeyListener(null);

        }

        _modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                _content.setKeyListener(mListner);
                _title.setKeyListener(mListner);

                _modify.setVisibility(View.GONE);
                _save.setVisibility(View.VISIBLE);

            }
        });

        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(String.valueOf(_title.getText()).replaceAll("\\s+","").equals(""))
                    Toast.makeText(getApplicationContext(),"There is no Title",Toast.LENGTH_LONG).show();
                else if(String.valueOf(_content.getText()).replaceAll("\\s+","").equals(""))
                    Toast.makeText(getApplicationContext(),"There is no content",Toast.LENGTH_LONG).show();
                else {
                    Log.d("DiaryContent", "Saving......");

                    Date date = new Date();
                    try {
                        date=new SimpleDateFormat("dd - MMM - yyyy").parse(String.valueOf(_datePicker.getText()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.d("DiaryContent",mAuth.getUid());
                    String uid = mAuth.getUid();
                    String title = String.valueOf(_title.getText());
                    String content = String.valueOf(_content.getText());

                    final DiaryEntry diaryEntry = new DiaryEntry(uid,date,title,content);

                    final DiaryEntry diaryEntryID = new DiaryEntry(mId,uid,date,title,content);

                    AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            if(mIndex==-1) mDateBase.diaryDao().insertDiary(diaryEntry);
                            else mDateBase.diaryDao().updateDiary(diaryEntryID);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("DiaryCOntent","Saved");
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });

        _delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date = new Date();
                try {
                    date=new SimpleDateFormat("dd - MMM - yyyy").parse(String.valueOf(_datePicker.getText()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String uid = mAuth.getCurrentUser().getUid();
                String title = String.valueOf(_title.getText());
                String content = String.valueOf(_content.getText());

                final DiaryEntry diaryEntryID = new DiaryEntry(mId,uid,date,title,content);

                AppExecutors.getsInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        mDateBase.diaryDao().deleteDiary(diaryEntryID);
                        if(mDateBase==null) mDateBase.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                });

            }
        });

        _datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        DiaryContent.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.d("DiaryContent", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                SimpleDateFormat dateTimeInGMT = new SimpleDateFormat("dd - MMM - yyyy");
                dateTimeInGMT.setTimeZone(TimeZone.getTimeZone("GMT+1"));
                _datePicker.setText(dateTimeInGMT.format(calendar.getTime()));
            }
        };
    }

}