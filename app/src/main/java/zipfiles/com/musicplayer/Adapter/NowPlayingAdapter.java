package zipfiles.com.musicplayer.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.Model.Song;
import zipfiles.com.musicplayer.R;

public class NowPlayingAdapter extends RecyclerView.Adapter<NowPlayingAdapter.viewHolder>
{
    private ArrayList<Song> songs;
    private Context context;
    private MusicPlayerControl musicPlayerControl;

    public NowPlayingAdapter(ArrayList<Song> songs, Context context) {
        this.songs = songs;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        musicPlayerControl= MusicPlayerControl.getinstace(context);
        NowPlayingAdapter.viewHolder viewholder=new NowPlayingAdapter.viewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nowplaying_list_item, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {

        if(songs.get(position).getImage() != null) {
            holder.imageView.setImageBitmap(songs.get(position).getImage());
        }
        else
            holder.imageView.setImageResource(R.drawable.placeholder2);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                musicPlayerControl.stop();
                musicPlayerControl.release();
                musicPlayerControl.setSong(songs.get(position),position);
                musicPlayerControl.intentToService("playFirst");
                ((Activity)context).recreate();
            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {

        private RelativeLayout layout;
        private ImageView imageView;


        public viewHolder(View itemView) {
            super(itemView);

            layout=itemView.findViewById(R.id.nowplaying_list_item_layout);
            imageView=itemView.findViewById(R.id.nowplaying_list_item_img);
        }
    }
}
