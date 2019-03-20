package zipfiles.com.musicplayer.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import zipfiles.com.musicplayer.Adapter.FoldersAdapter;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;

public class FolderFragment extends Fragment
{
    private FoldersAdapter foldersAdapter;
    private RecyclerView recyclerView;

    private MusicPlayerControl control;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.folders_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView=view.findViewById(R.id.folders_recycleview);


        control=MusicPlayerControl.getinstace(getContext());

        foldersAdapter = new FoldersAdapter(control.getFolders(),getContext());
        recyclerView.setAdapter(foldersAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2,LinearLayoutManager.VERTICAL, false));

    }
}
