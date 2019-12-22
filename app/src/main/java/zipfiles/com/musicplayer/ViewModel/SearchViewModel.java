package zipfiles.com.musicplayer.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import zipfiles.com.musicplayer.Control.MusicPlayerControl;
import zipfiles.com.musicplayer.Model.Song;


public class SearchViewModel extends ViewModel
{
    private MutableLiveData<ArrayList<Song>> searchresult;
    private MusicPlayerControl control;

    public LiveData<ArrayList<Song>> getSearch(Context context, String query)
    {
        if (searchresult == null)
        {
            control = MusicPlayerControl.getinstace(context);
            searchresult = new MutableLiveData<>();
            search(query);
        }
        return searchresult;
    }

    public void search(String query)
    {
        if(!query.isEmpty())
        searchresult.setValue(control.search(query));

        if(searchresult.getValue() != null) {
            if (!searchresult.getValue().isEmpty())
                Log.e("result", searchresult.getValue().get(0).getTitle());
            else
                Log.e("result","empty");
        }
        else
            Log.e("result", "null");

    }
}
