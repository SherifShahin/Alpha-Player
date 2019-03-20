package zipfiles.com.musicplayer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import zipfiles.com.musicplayer.Activity.FolderSongs;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.Model.Folder;
import zipfiles.com.musicplayer.R;


public class FoldersAdapter extends RecyclerView.Adapter<FoldersAdapter.viewHolder>
{

    private ArrayList<Folder> Folders;
    private Context context;
    private MusicPlayerControl control;


    public FoldersAdapter(ArrayList<Folder> folders, Context context) {
        Folders = folders;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        control=MusicPlayerControl.getinstace(context);
        FoldersAdapter.viewHolder viewholder=new FoldersAdapter.viewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_layout, parent, false));
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position)
    {
        int FolderImagePosition=Folders.get(position).getSongs().size()-1;
        if(Folders.get(position).getSongs().get(FolderImagePosition).getImage() != null) {
            holder.imageView.setImageBitmap(Folders.get(position).getSongs().get(FolderImagePosition).getImage());
        }
        else
        holder.imageView.setImageResource(R.drawable.placeholder);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control.setCurrentFolder(Folders.get(position));
                context.startActivity(new Intent(context, FolderSongs.class));
            }
        });

        holder.foldername.setText(Folders.get(position).getName());

        holder.noSongs.setText(Folders.get(position).getSongs().size()+" Songs");
    }



    @Override
    public int getItemCount() {
        return Folders.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder
    {
        private ImageView imageView;
        private TextView foldername;
        private TextView noSongs;
        private LinearLayout layout;

        public viewHolder(View itemView)
        {
            super(itemView);

            imageView=itemView.findViewById(R.id.Folder_img);
            foldername=itemView.findViewById(R.id.Folder_name);
            noSongs=itemView.findViewById(R.id.Folder_noSongs);
            layout=itemView.findViewById(R.id.Folder_layout);
        }
    }






}
