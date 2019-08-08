package zipfiles.com.musicplayer.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import zipfiles.com.musicplayer.Adapter.NowPlayingAdapter;
import zipfiles.com.musicplayer.BlurBuilder;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;
import zipfiles.com.musicplayer.Interface.Subscriber;


public class NowPlaying extends AppCompatActivity implements View.OnClickListener ,Subscriber
{

    private ConstraintLayout layout;
    private TextView SongTitle;
    private TextView SongArtist;
    private TextView duration;
    private TextView songposition;
    private CircleImageView SongImage;
    private ImageView SongPlayState;
    private ImageView SongRepeatState;
    private ImageView playNext;
    private ImageView pvPlay;
    private ImageView AddtoFavourite;
    private ImageView ShuffleState;
    private ImageView SongShare;
    private SeekBar seekBar;

    private Thread updateseekbar;

    private MusicPlayerControl control;


    private boolean thread=false;


    private RecyclerView recyclerView;
    private NowPlayingAdapter nowPlayingAdapter;


    private   RotateAnimation rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);

        layout=findViewById(R.id.nowplaying_layout);
        SongImage=findViewById(R.id.nowplaying_songimg);
        SongTitle=findViewById(R.id.nowplaying_songtitle);
        SongArtist=findViewById(R.id.nowplaying_artist);
        SongPlayState=findViewById(R.id.nowplaying_playstate);
        SongRepeatState=findViewById(R.id.nowplaying_repeatstate);
        playNext=findViewById(R.id.nowplaying_playnext);
        pvPlay=findViewById(R.id.nowplaying_playpv);
        seekBar=findViewById(R.id.nowplaying_seekbar);
        duration=findViewById(R.id.duration);
        songposition=findViewById(R.id.currentposition);
        AddtoFavourite=findViewById(R.id.nowplaying_favicon);
        ShuffleState=findViewById(R.id.shuffle_state);
        SongShare=findViewById(R.id.nowplaying_share);
        recyclerView=findViewById(R.id.nowplaying_list_recycleview);

        control=MusicPlayerControl.getinstace(this);

        control.subscribe(this);

        SongTitle.setText(control.getSong().getTitle());
        SongArtist.setText(control.getSong().getArtist());

        String currentduration=control.getCurrentDurationInString();

        duration.setText(currentduration);

        new Threading().execute();

        if(control.getSong().getImage() != null) {
            SongImage.setImageBitmap(control.getSong().getImage());
        }

        if(control.getSong().getImage() != null) {
            Bitmap blurredBitmap = BlurBuilder.blur(this, control.getSong().getImage());
            layout.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
            SongTitle.setTextColor(Color.WHITE);
        }

         rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.setDuration(3200);
        rotate.setRepeatCount(Animation.INFINITE);



        if(control.inFavourites())
            AddtoFavourite.setImageResource(R.mipmap.like);


        if(control.getState().equalsIgnoreCase("pause"))
        {
            SongPlayState.setImageResource(R.drawable.play);
            rotate.cancel();
            rotate.reset();
        }

        else if(control.getState().equalsIgnoreCase("play") || control.getState().equalsIgnoreCase("playFirst"))
        {
            SongPlayState.setImageResource(R.drawable.pause);
            SongImage.startAnimation(rotate);
        }
        else if(control.getState().equalsIgnoreCase("stop"))
        {
            SongPlayState.setImageResource(R.drawable.play);
            rotate.cancel();
            rotate.reset();
        }



        if(control.getRepeatType().equalsIgnoreCase("none"))
        {
            SongRepeatState.setImageResource(R.mipmap.non_repeat);
        }
        else if(control.getRepeatType().equalsIgnoreCase("once"))
        {
            SongRepeatState.setImageResource(R.mipmap.repeat_once);
        }
        else if(control.getRepeatType().equalsIgnoreCase("all"))
        {
            SongRepeatState.setImageResource(R.mipmap.repeat_all);
        }


        if(control.getShuffleState().isEmpty())
        {
           control.setShuffleState("none");
        }
        else
        {
            if(control.getShuffleState().equals("shuffle"))
            {
                ShuffleState.setImageResource(R.mipmap.shuffle);
            }
            else if(control.getShuffleState().equals("none"))
            {
                ShuffleState.setImageResource(R.mipmap.no_shuffle);
            }
        }


        if(control.getCurrent_list() != null) {
            nowPlayingAdapter = new NowPlayingAdapter(control.getCurrent_list(), this);
            recyclerView.setAdapter(nowPlayingAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.scrollToPosition(control.getCurrentPosition());
        }

        SongPlayState.setOnClickListener(this);
        playNext.setOnClickListener(this);
        pvPlay.setOnClickListener(this);
        AddtoFavourite.setOnClickListener(this);
        SongShare.setOnClickListener(this);


        SongRepeatState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(control.getRepeatType().equalsIgnoreCase("none"))
                {
                    control.setRepeatType("once",getApplicationContext());
                    SongRepeatState.setImageResource(R.mipmap.repeat_once);
                }
                else if(control.getRepeatType().equalsIgnoreCase("once"))
                {
                    control.setRepeatType("all",getApplicationContext());
                    SongRepeatState.setImageResource(R.mipmap.repeat_all);
                }
                else if(control.getRepeatType().equalsIgnoreCase("all"))
                {
                    control.setRepeatType("none",getApplicationContext());
                    SongRepeatState.setImageResource(R.mipmap.non_repeat);
                }
            }
        });


        ShuffleState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(control.getShuffleState().equals("none"))
                {
                    control.setShuffleState("shuffle");
                    ShuffleState.setImageResource(R.mipmap.shuffle);
                }
                else if(control.getShuffleState().equals("shuffle"))
                {
                    control.setShuffleState("none");
                    ShuffleState.setImageResource(R.mipmap.no_shuffle);
                }
            }
        });



        control.getMusicplayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(control.getRepeatType().equalsIgnoreCase("none"))
                {
                    control.getMusicplayer().seekTo(0);
                }
                else if(control.getRepeatType().equalsIgnoreCase("once"))
                {
                    control.intentToService("play");
                }
                else if(control.getRepeatType().equalsIgnoreCase("all"))
                {
                   if(!control.getShuffleState().isEmpty() && !control.getShuffleState().equals("none"))
                   {
                       if(control.getShuffleState().equalsIgnoreCase("shuffle"))
                       {
                           thread=true;
                           new Threading().cancel(true);
                           control.stop();
                           control.release();
                           control.playNextWithShuffle();
                           recreate();
                       }
                   }

                   else {
                       thread=true;
                       new Threading().cancel(true);
                       control.stop();
                       control.release();
                       control.playNext();
                       recreate();
                   }
                }
            }
        });

    }

    @Override
    public void onClick(View v)
    {
        if(v == SongPlayState)
        {
            String state=control.getState();

             if(state.equals("playFirst") || state.equals("play"))
            {
                control.setState("pause");
                control.intentToService(control.getState());
                SongPlayState.setImageResource(R.drawable.play);
                rotate.cancel();
                rotate.reset();
            }
            else if(state.equals("pause"))
            {
                control.setState("play");
                control.intentToService(control.getState());
                SongPlayState.setImageResource(R.drawable.pause);
                SongImage.startAnimation(rotate);
            }
            else if(state.equalsIgnoreCase("stop"))
             {
                 control.setState("playFirst");
                 control.intentToService(control.getState());
                 SongPlayState.setImageResource(R.drawable.pause);
                 SongImage.startAnimation(rotate);
             }
        }

        if(v == playNext)
        {
            thread=true;
            new Threading().cancel(true);
            control.stop();
            control.release();
            control.playNext();
            recreate();
        }
         if(v == pvPlay)
        {
            thread=true;
            new Threading().cancel(true);
            control.stop();
            control.release();
            control.pvPlay();
            recreate();
        }
         if(v == AddtoFavourite)
        {
            AddtoFavouriteAction();
        }

        if(v == SongShare)
        {
            Uri uri = Uri.parse(control.getSong().getPath());
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("audio/*");
            share.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(share, "Share"));
        }
    }

    private void AddtoFavouriteAction()
    {
        if(control.inFavourites())
        {
            control.removeFromFavourites();
            AddtoFavourite.setImageResource(R.mipmap.empty_like);
        }
        else
        {
            control.setinFavourites();
            AddtoFavourite.setImageResource(R.mipmap.like);
        }

        control.intentToService(control.getState());
    }


    @Override
    protected void onDestroy() {
        Log.e("destroy","on it");

          thread=true;

        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        control.setIn_Now_Playing(false);
        thread=false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        control.setIn_Now_Playing(true);
        thread=false;
        new Threading().execute();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        control.setIn_Now_Playing(true);
        thread=false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        control.setIn_Now_Playing(false);
    }


    @Override
    protected void onStart() {
        super.onStart();

        control.setIn_Now_Playing(true);

    }

    @Override
    public void update(String state)
    {
        if(state.equalsIgnoreCase("pause"))
        {
            SongPlayState.setImageResource(R.drawable.play);
            rotate.cancel();
            rotate.reset();
        }

        else if(state.equalsIgnoreCase("playFirst"))
        {
            thread=true;
            recreate();
        }
        else if(state.equalsIgnoreCase("play"))
        {
            SongPlayState.setImageResource(R.drawable.pause);
            SongImage.startAnimation(rotate);
        }
        else if(state.equalsIgnoreCase("stop"))
        {
            SongPlayState.setImageResource(R.drawable.play);
            rotate.cancel();
            rotate.reset();
        }


        if(control.inFavourites())
            AddtoFavourite.setImageResource(R.mipmap.like);
        else
            AddtoFavourite.setImageResource(R.mipmap.empty_like);


    }


    public class Threading extends AsyncTask<Void,Integer,Integer>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            seekBar.setMax(control.getCurrentDuration());

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    control.seekTo(seekBar.getProgress());
                }
            });

            thread=false;

        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            seekBar.setProgress(values[0]);
            String currentpositonString = control.getCurrentPositionInString();
            songposition.setText(currentpositonString);
        }

        @Override
        protected Integer doInBackground(Void... voids) {

            int currentposition = 0;

               while (!thread) {

                   try {
                       Thread.sleep(500);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }


                   if(control.getMusicplayer() != null)
                   {
                       if(!control.getState().equalsIgnoreCase("stop"))
                       {
                           try {
                               currentposition = control.getCurrentPosition();
                               publishProgress(currentposition);
                           }
                           catch (Exception e)
                           {
                               try {
                                   Thread.sleep(100);
                               } catch (InterruptedException e1) {
                                   e1.printStackTrace();
                               }
                           }

                       }
                   }
               }

            return null;
        }

        @Override
        protected void onPostExecute(Integer integer)
        {
            super.onPostExecute(integer);
            thread=true;
            cancel(true);
        }
    }
}
