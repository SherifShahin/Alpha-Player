package zipfiles.com.musicplayer.Activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import zipfiles.com.musicplayer.Adapter.AllSongsAdapter;
import zipfiles.com.musicplayer.Adapter.NowPlayingAdapter;
import zipfiles.com.musicplayer.BlurBuilder;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;




public class NowPlaying extends AppCompatActivity implements View.OnClickListener
{

    private LinearLayout layout;
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
    private SeekBar seekBar;

    private Thread updateseekbar;

    private MusicPlayerControl control;


    private static boolean thread=false;


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
        recyclerView=findViewById(R.id.nowplaying_list_recycleview);

        thread=false;

        control=MusicPlayerControl.getinstace(this);

        SongTitle.setText(control.getSong().getTitle());
        SongArtist.setText(control.getSong().getArtist());

        String currentduration=control.getCurrentDurationInString();

        duration.setText(currentduration);

        updateseekbar = new Thread() {
            @Override
            public void run() {
                int currentposition = 0;
                String currentpositonString;

                while(!thread) {
                    try {
                        sleep(500);
                        currentposition = control.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                        currentpositonString = control.getCurrentPositionInString();
                        songposition.setText(currentpositonString);

                        if(control.getState().equalsIgnoreCase("pause"))
                        {
                            SongPlayState.setImageResource(R.drawable.play);

                        }

                        else if(control.getState().equalsIgnoreCase("play") || control.getState().equalsIgnoreCase("playFirst"))
                        {
                            SongPlayState.setImageResource(R.drawable.pause);
                        }

                        else if(control.getState().equalsIgnoreCase("stop")) {
                            SongPlayState.setImageResource(R.drawable.play);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        seekBar.setMax(control.getCurrentDuration());
        updateseekbar.start();
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

        rotate.setDuration(3000);
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


        nowPlayingAdapter=new NowPlayingAdapter(control.getCurrent_list(),this);
        recyclerView.setAdapter(nowPlayingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));


        SongPlayState.setOnClickListener(this);
        playNext.setOnClickListener(this);
        pvPlay.setOnClickListener(this);
        AddtoFavourite.setOnClickListener(this);


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
                           control.playNextWithShuffle();
                           recreate();
                       }
                   }

                   else {
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
            control.playNext();
            recreate();
        }
         if(v == pvPlay)
        {
            thread=true;
            control.pvPlay();
            recreate();
        }
         if(v == AddtoFavourite)
        {
            AddtoFavouriteAction();
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
    }


    @Override
    protected void onDestroy() {
        Log.e("destroy","on it");


        if(updateseekbar != null)
        {
          thread=true;

          if(updateseekbar.isAlive())
          {
              Log.e("destroy","still alive"+updateseekbar.getId());
          }
          else
          {
              Log.e("destroy","destroied");
          }
        }

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


}
