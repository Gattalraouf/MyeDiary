package com.gattal.asta.myediary;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.gattal.asta.myediary.dataBase.AppDataBase;
import com.gattal.asta.myediary.dataBase.DiaryEntry;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class Home extends AppCompatActivity
        implements myDiaryAdapter.ListItemClickListener {

    private static final String TAG = "Home";

    private static final int NUM_LIST_ITEMS = 1000;
    private FloatingActionButton mFab;
    private myDiaryAdapter mAdapter;
    private RecyclerView mMainDiaryRecyclerView;
    private AppDataBase mDB;
    private FirebaseAuth mAuth;

    @BindView(R.id.imageView) ImageView _img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        mFab = findViewById(R.id.floatingActionButton);

        mMainDiaryRecyclerView = findViewById(R.id.MainDiaryRecyclerView);

        mDB = AppDataBase.getsInstance(getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiaryContent.class);
                intent.putExtra("Index",-1);
                startActivity(intent);
            }
        });

        //Setting The recycler view with it's Adapter
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mMainDiaryRecyclerView.setLayoutManager(layoutManager);
        mMainDiaryRecyclerView.setHasFixedSize(true);
        mAdapter = new myDiaryAdapter(NUM_LIST_ITEMS,this);
        mMainDiaryRecyclerView.setAdapter(mAdapter);

        LiveData<List<DiaryEntry>>diaries =mDB.diaryDao().loadDiariesByUser(mAuth.getUid());
        diaries.observe(this, new Observer<List<DiaryEntry>>() {
            @Override
            public void onChanged(@Nullable List<DiaryEntry> diaryEntries) {
                if(diaryEntries.size()!=0){
                    _img.setVisibility(View.GONE);
                }
                else _img.setVisibility(View.VISIBLE);
                mAdapter.setmDiaryEntries(diaryEntries);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent(getApplicationContext(), DiaryContent.class);
        intent.putExtra("Index",clickedItemIndex);
        intent.putExtra("ID",mAdapter.getmDiaryEntries().get(clickedItemIndex).getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
