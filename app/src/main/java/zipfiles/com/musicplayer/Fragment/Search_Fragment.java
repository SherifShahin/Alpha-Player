package zipfiles.com.musicplayer.Fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import zipfiles.com.musicplayer.Adapter.AllSongsAdapter;
import zipfiles.com.musicplayer.Model.Song;
import zipfiles.com.musicplayer.R;
import zipfiles.com.musicplayer.ViewModel.SearchViewModel;

public class Search_Fragment extends Fragment {

    private SearchViewModel viewModel;
    private Toolbar SearchToolbar;
    private MaterialSearchView searchView;
    private LiveData<ArrayList<Song>> searchresult;
    private AllSongsAdapter songsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search__fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);

        SearchToolbar = view.findViewById(R.id.Search_toolbar);
        searchView = view.findViewById(R.id.search_view);

                ((AppCompatActivity) getActivity()).setSupportActionBar(SearchToolbar);
        SearchToolbar.setTitle("Search");
        SearchToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        setHasOptionsMenu(true);


        searchresult = viewModel.getSearch(getContext(),"");

        searchresult.observe(getActivity(), new Observer<ArrayList<Song>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Song> songs) {

                songsAdapter = new AllSongsAdapter(songs,getContext());
                RecyclerView recyclerView = view.findViewById(R.id.Search_recycleview);
                recyclerView.setAdapter(songsAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                viewModel.search(newText);

                return true;
            }
        });


    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu,menu);

        super.onCreateOptionsMenu(menu, inflater);

        final MenuItem menuItem= menu.findItem(R.id.action_search);

        searchView.setMenuItem(menuItem);
    }

}
