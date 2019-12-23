package zipfiles.com.musicplayer.Fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.supercharge.shimmerlayout.ShimmerLayout;
import zipfiles.com.musicplayer.Adapter.FavouritesAdapter;
import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.R;
import zipfiles.com.musicplayer.Model.Song;
import zipfiles.com.musicplayer.ViewModel.FavouritesViewModel;

public class FavouritesFragment extends Fragment
{

    private RecyclerView recyclerView;
    private TextView Favourite_text_view;
    private ProgressBar progressBar;
    private ShimmerLayout shimmerLayout;

    private FavouritesAdapter favouritesAdapter;
    private MusicPlayerControl control;

    private FavouritesViewModel favouritesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favourites_fragment, container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favouritesViewModel = ViewModelProviders.of(this).get(FavouritesViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

            recyclerView=view.findViewById(R.id.Favourites_recycleview);
            Favourite_text_view=view.findViewById(R.id.Favourites_textview);
            progressBar=view.findViewById(R.id.Favourites_Progressbar);
            shimmerLayout = view.findViewById(R.id.Favourites_shimmer_layout);

            control=MusicPlayerControl.getinstace(getContext());

           // progressBar.setVisibility(View.VISIBLE);

            shimmerLayout.startShimmerAnimation();

            final Observer<List<Song>> SongsObserver = new Observer<List<Song>>() {
                @Override
                public void onChanged(@Nullable List<Song> songs)
                {
                    //progressBar.setVisibility(View.GONE);
                    shimmerLayout.stopShimmerAnimation();
                    shimmerLayout.setVisibility(View.GONE);
                    Favourite_text_view.setVisibility(View.GONE);
                    if(songs != null)
                        if (!songs.isEmpty())
                        {
                            favouritesAdapter = new FavouritesAdapter((ArrayList<Song>) songs, getContext());
                            recyclerView.setAdapter(favouritesAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
                        }
                        else
                            Favourite_text_view.setVisibility(View.VISIBLE);
                }
            };

            favouritesViewModel.getSongs(getContext()).removeObservers(this);
            favouritesViewModel.getSongs(getContext()).observe(this,SongsObserver);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
    }

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,0)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                int fromposition = viewHolder.getAdapterPosition();
                int toposition = target.getAdapterPosition();

                ArrayList<Song> fav = control.getFavList();
                Collections.swap(fav,fromposition,toposition);
                control.UpdateFav(fav);

                recyclerView.getAdapter().notifyItemMoved(fromposition,toposition);

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shimmerLayout.stopShimmerAnimation();
    }
}
