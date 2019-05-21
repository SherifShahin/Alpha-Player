package zipfiles.com.musicplayer.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import zipfiles.com.musicplayer.Activity.NowPlaying;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;
import zipfiles.com.musicplayer.Model.Song;
import zipfiles.com.musicplayer.Storage.SharedPrefManger;

public class AllSongsAdapter extends RecyclerView.Adapter<AllSongsAdapter.viewHolder>
{
    private ArrayList<Song> songs;
    private Context context;
    private MusicPlayerControl musicPlayerControl;

    private Dialog dialog;

    private TextView song_title;
    private TextView delete;
    private TextView share;
    private TextView favourite;
    private TextView ringtone;


    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public AllSongsAdapter(ArrayList<Song> songs, Context context)
    {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        musicPlayerControl=MusicPlayerControl.getinstace(context);
        AllSongsAdapter.viewHolder viewholder=new AllSongsAdapter.viewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_song_layout, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, final int position)
    {
        if(songs.get(position).getImage() != null) {
            holder.imageView.setImageBitmap(songs.get(position).getImage());
        }
        else
            holder.imageView.setImageResource(R.drawable.placeholder);

        holder.title.setText(songs.get(position).getTitle());
        holder.artist.setText(songs.get(position).getArtist());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    musicPlayerControl.setCurrent_list(songs);
                    musicPlayerControl.stop();
                    musicPlayerControl.release();
                    musicPlayerControl.setSong(songs.get(position), position);
                    musicPlayerControl.intentToService("playFirst");

                ActivityOptionsCompat activityOptions= null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                            Pair.create(holder.itemView.findViewById(R.id.main_song_title), "songtitle"),
                            Pair.create(holder.itemView.findViewById(R.id.main_song_artist), "songartist")
                    );
                    context.startActivity(new Intent(context,NowPlaying.class),activityOptions.toBundle());
                }

                else
                context.startActivity(new Intent(context,NowPlaying.class));
            }
        });


        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopup(songs.get(position).getPath(),position);
            }
        });


    }


    private void showpopup(final String path, final int position)
    {
        dialog=new Dialog(context);

        dialog.setContentView(R.layout.song_popup);

        song_title=dialog.findViewById(R.id.song_popup_title);
        share=dialog.findViewById(R.id.song_popup_share);
        delete=dialog.findViewById(R.id.song_popup_delete);
        favourite=dialog.findViewById(R.id.song_popup_favourite);
        ringtone=dialog.findViewById(R.id.song_popup_ringtone);


        dialog.setCancelable(true);

        song_title.setText(songs.get(position).getTitle());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = new File(path);
                file.delete();
                if(file.exists()){
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(file.exists()){
                        context.deleteFile(file.getName());
                    }
                }
                else {
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();

                    if(musicPlayerControl.getPositionInFav(songs.get(position).getTitle()) != -1 )
                    {
                        ArrayList<Song> list=musicPlayerControl.getFav_list();
                        list.remove(musicPlayerControl.getPositionInFav(songs.get(position).getTitle()));
                        musicPlayerControl.setFav_list(list);
                        SharedPrefManger.getInstance(context).set_Favourites(list);
                    }

                    songs.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, songs.size());
                    notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        });


        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(path);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(Intent.createChooser(share, "Share Sound File"));

                dialog.dismiss();
            }
        });


        ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File ringFile = new File(path);

                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DATA, path);
                values.put(MediaStore.MediaColumns.TITLE, songs.get(position).getTitle());
                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
                values.put(MediaStore.MediaColumns.SIZE, ringFile.length());
                values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
                values.put(MediaStore.Audio.Media.DURATION, 230);
                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
                values.put(MediaStore.Audio.Media.IS_ALARM, false);
                values.put(MediaStore.Audio.Media.IS_MUSIC, false);



                Uri uri = MediaStore.Audio.Media.getContentUriForPath(path);

                context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + ringFile.getAbsolutePath() + "\"",
                        null);

                Uri newUri = context.getContentResolver().insert(uri,values);

                try {
                    RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
                } catch (Throwable t) {

                }

                dialog.dismiss();
            }
        });



        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(musicPlayerControl.getFav_list() != null)
                if(musicPlayerControl.getPositionInFav(songs.get(position).getTitle()) == -1) {
                    musicPlayerControl.setSongInFavourites(songs.get(position));
                }
                else
                {
                    Toast.makeText(context,"its already in fav",Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {
        private ImageView action;
        private TextView title;
        private TextView artist;
        private CircleImageView imageView;
        private LinearLayout linearLayout;

        public viewHolder(View itemView)
        {
            super(itemView);

            action=itemView.findViewById(R.id.main_song_action);
            title=itemView.findViewById(R.id.main_song_title);
            artist=itemView.findViewById(R.id.main_song_artist);
            linearLayout=itemView.findViewById(R.id.song_layout);
            imageView=itemView.findViewById(R.id.main_song_image);
        }
    }

}
