package ru.controlprograms.musiclist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by evgeny on 18.04.16.
 */
// Адаптер для списка.
public class CustomListAdapter extends BaseAdapter {

    private Context mContext;
//    Массив информации об исполнителях.
    private JSONArray mArtists;

    public CustomListAdapter(Context context, JSONArray artists) {
        mArtists = artists;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mArtists.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return mArtists.getJSONArray(position);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Заполнение элемента списка.
        View view = convertView;
        LayoutInflater layoutInflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
//            Взять шаблон.
            view = layoutInflater.inflate(R.layout.list_item, parent,  false);
        }
//        Взять данные из массива и заполнить все элементы шаблона.
        try {
            JSONObject artist = mArtists.getJSONObject(position);
            GetFromJSON getFromJSON = new GetFromJSON(artist);
            String name = getFromJSON.getName();
            String genres = getFromJSON.getGenres();
            String info = getFromJSON.getInfoText();
            String coverSmallUrl = getFromJSON.getCoverSmallURL();

            ((TextView) view.findViewById(R.id.textName)).setText(name);
            ((TextView) view.findViewById(R.id.textGenres)).setText(genres);
            ((TextView) view.findViewById(R.id.textInfo)).setText(info);

            NetworkImageView cover_small = (NetworkImageView) view.findViewById(R.id.cover_small);
            ImageLoader imageLoader = VolleySingleton.getInstance(mContext).getImageLoader();
            cover_small.setImageUrl(coverSmallUrl, imageLoader);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
//Устновить данные для отображения списком.
    public void setData(JSONArray artists) {
        mArtists = artists;
        notifyDataSetChanged();
    }
}
