package zipfiles.com.musicplayer.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import zipfiles.com.musicplayer.Fragment.AllSongsFragment;
import zipfiles.com.musicplayer.Fragment.FavouritesFragment;
import zipfiles.com.musicplayer.Fragment.FolderFragment;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;
import zipfiles.com.musicplayer.Interface.Subscriber;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,View.OnClickListener,Subscriber
{
    private BottomNavigationView bottomNavigationView;

    private CircleImageView imageView;
    private TextView title;
    private TextView artist;
    private ImageView playstate;
    private LinearLayout layout;


    private MusicPlayerControl control;

    private  RotateAnimation rotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageView=findViewById(R.id.main_music_control_layout_image);
        title=findViewById(R.id.main_music_control_layout_title);
        artist=findViewById(R.id.main_music_control_layout_artist);
        playstate=findViewById(R.id.main_music_control_layout_artist_playstate);
        layout=findViewById(R.id.main_music_control_layout);

        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        displayFragment(new AllSongsFragment());


        control=MusicPlayerControl.getinstace(this);
        control.subscribe(this);

        rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        rotate.setDuration(2200);
        rotate.setRepeatCount(Animation.INFINITE);



        if(control.getSong() != null)
        {
            imageView.setImageBitmap(control.getSong().getImage());
            title.setText(control.getSong().getTitle());
            artist.setText(control.getSong().getArtist());

            if(control.getState().equalsIgnoreCase("play"))
            {
                playstate.setImageResource(R.drawable.pause);
            }
            else if(control.getState().equalsIgnoreCase("pause"))
            {
                playstate.setImageResource(R.drawable.play);
            }
            else if(control.getState().equalsIgnoreCase("stop"))
            {
                playstate.setImageResource(R.drawable.play);
            }
        }


        playstate.setOnClickListener(this);
        layout.setOnClickListener(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(control.getSong() != null)
        {
            if(control.getSong().getImage() != null) {
                imageView.setImageBitmap(control.getSong().getImage());
            }
            else
                imageView.setImageResource(R.drawable.placeholder);

            title.setText(control.getSong().getTitle());
            artist.setText(control.getSong().getArtist());

            if(control.getState().equalsIgnoreCase("play"))
            {
                playstate.setImageResource(R.drawable.pause);
                imageView.startAnimation(rotate);
            }
            else if(control.getState().equalsIgnoreCase("pause"))
            {
                playstate.setImageResource(R.drawable.play);
                rotate.cancel();
                rotate.reset();

            }
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        android.support.v4.app.Fragment fragment=null;
        switch (item.getItemId())
        {
            case R.id.all_songs:
                fragment= new AllSongsFragment();
                break;

            case R.id.Favourites:
                fragment=new FavouritesFragment();
                break;

            case R.id.Folders:
                fragment= new FolderFragment();
                break;

        }


        if(fragment != null)
        {
            displayFragment(fragment);
        }

        return true;
    }


    private void displayFragment(android.support.v4.app.Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.Fragment_container,fragment)
                .commit();

    }

    @Override
    public void onClick(View v)
    {
        if(v == layout)
        {
            ActivityOptionsCompat activityOptions= null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        Pair.create(findViewById(R.id.main_music_control_layout_title), "songtitle"),
                        Pair.create(findViewById(R.id.main_music_control_layout_artist), "songartist")
                );
                startActivity(new Intent(this,NowPlaying.class),activityOptions.toBundle());
            }

            else
               startActivity(new Intent(this,NowPlaying.class));
        }

        if(v == playstate)
        {
            if(control.getSong() != null)
            {
                if (control.getState().equalsIgnoreCase("play"))
                {
                    rotate.cancel();
                    rotate.reset();
                    control.setState("pause");
                    playstate.setImageResource(R.drawable.play);
                    control.intentToService("pause");
                }
                else if (control.getState().equalsIgnoreCase("pause"))
                {
                    imageView.startAnimation(rotate);
                    control.setState("play");
                    playstate.setImageResource(R.drawable.pause);
                    control.intentToService("play");

                }
                else if (control.getState().equalsIgnoreCase("stop"))
                {
                    imageView.startAnimation(rotate);
                    control.setState("play");
                    playstate.setImageResource(R.drawable.pause);
                    control.intentToService("playFirst");
                }
            }

        }

    }

    @Override
    public void update(String state) {

        if(state.equalsIgnoreCase("pause"))
        {
            playstate.setImageResource(R.drawable.play);
            rotate.cancel();
            rotate.reset();
        }

        else if(state.equalsIgnoreCase("playFirst"))
        {
            Bitmap bitmap=control.getSong().getImage();

            if(bitmap!=null)
                imageView.setImageBitmap(control.getSong().getImage());
            else
                imageView.setImageResource(R.drawable.placeholder);

            title.setText(control.getSong().getTitle());
            artist.setText(control.getSong().getArtist());
        }
        else if(state.equalsIgnoreCase("play"))
        {
            playstate.setImageResource(R.drawable.pause);
            imageView.startAnimation(rotate);
        }
        else if(state.equalsIgnoreCase("stop"))
        {
            playstate.setImageResource(R.drawable.play);
            rotate.cancel();
            rotate.reset();
        }
    }
}
