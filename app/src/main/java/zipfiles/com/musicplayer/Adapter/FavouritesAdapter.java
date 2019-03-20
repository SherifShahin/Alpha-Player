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
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import zipfiles.com.musicplayer.Fragment.FavouritesFragment;
import zipfiles.com.musicplayer.R;
import zipfiles.com.musicplayer.Model.Song;
import zipfiles.com.musicplayer.Storage.SharedPrefManger;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.viewHolder>
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


    public FavouritesAdapter(ArrayList<Song> songs, Context context)
    {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        musicPlayerControl=MusicPlayerControl.getinstace(context);
        FavouritesAdapter.viewHolder viewholder=new FavouritesAdapter.viewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_song_layout, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {

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
                    musicPlayerControl.setSong(songs.get(position), position);
                    musicPlayerControl.intentToService("playFirst");

                context.startActivity(new Intent(context,NowPlaying.class));
            }
        });


        holder.aciton.setOnClickListener(new View.OnClickListener() {
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

                    songs.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, songs.size());
                    notifyDataSetChanged();
                    musicPlayerControl.setFav_list(songs);
                    SharedPrefManger.getInstance(context).set_Favourites(songs);
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
            public void onClick(View v) {
                Toast.makeText(context,"its already in fav",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    @Override
    public int getItemCount()
    {
        return songs.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {
        private ImageView aciton;
        private TextView title;
        private TextView artist;
        private CircleImageView imageView;
        private LinearLayout linearLayout;


        public viewHolder(View itemView)
        {
            super(itemView);

            aciton=itemView.findViewById(R.id.main_song_action);
            title=itemView.findViewById(R.id.main_song_title);
            artist=itemView.findViewById(R.id.main_song_artist);
            imageView=itemView.findViewById(R.id.main_song_image);
            linearLayout=itemView.findViewById(R.id.song_layout);
        }
    }

}
