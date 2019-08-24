package zipfiles.com.musicplayer.Activity;

import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import java.util.Collections;

import zipfiles.com.musicplayer.Adapter.AllSongsAdapter;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;

public class FolderSongs extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private AllSongsAdapter allSongsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private MusicPlayerControl control;

    private boolean booleanthread=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_songs);

        control=MusicPlayerControl.getinstace(this);

        Toolbar toolbar = findViewById(R.id.folder_toolbar);
        toolbar.setTitle(control.getCurrentFolder().getName());
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        recyclerView=findViewById(R.id.folder_songs_recycleview);
        swipeRefreshLayout=findViewById(R.id.folder_swipeRefresh);

        allSongsAdapter=new AllSongsAdapter(control.getCurrentFolder().getSongs(),this);
        recyclerView.setAdapter(allSongsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
               swiprefreshaction();

            }
        });
    }

    private void swiprefreshaction()
    {
        final Thread thread = new Thread(){
            public void run(){

                while(booleanthread)
                {
                    control.searchInFolder(control.getCurrentFolder().getUrl());
                    updateuiInTHread();
                    booleanthread=false;
                }
            }
        };
        thread.start();
    }

    private void updateuiInTHread()
    {
        this.runOnUiThread(new Runnable() {
            public void run() {
                allSongsAdapter.setSongs(control.getCurrentFolder().getSongs());
                allSongsAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(allSongsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.VERTICAL,false));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        booleanthread= false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        booleanthread= false;
    }
}
