package ru.controlprograms.musiclist;

import android.support.annotation.VisibleForTesting;
import android.test.AndroidTestCase;
import android.view.View;
import android.widget.Adapter;


import org.json.JSONArray;

/**
 * Created by evgeny on 25.04.16.
 */
public class CustomListAdapterTest extends AndroidTestCase {



    CustomListAdapter mAdapter;
    final String jsonString = "[{\"id\":1080505,\"name\":\"Tove Lo\",\"genres\":[\"pop\",\"dance\"," +
            "\"electronics\"],\"tracks\":81,\"albums\":22,\"link\":\"http://www.tove-lo.com/\"," +
            "\"description\":\"шведская певица и автор песен. Она привлекла к себе внимание в" +
            " 2013 году с выпуском сингла «Habits», но настоящего успеха добилась с ремиксом " +
            "хип-хоп продюсера Hippie Sabotage на эту песню, который получил название " +
            "«Stay High». 4 марта 2014 года вышел её дебютный мини-альбом Truth Serum, а 24" +
            " сентября этого же года дебютный студийный альбом Queen of the Clouds. Туве Лу" +
            " является автором песен таких артистов, как Icona Pop, Girls Aloud и Шер Ллойд." +
            "\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/dfc531f5." +
            "p.1080505/300x300\",\"big\":\"http://avatars.yandex.net/get-music-content/dfc53" +
            "1f5.p.1080505/1000x1000\"}}]";

    public CustomListAdapterTest() {
        super();
    }

    protected void setUp() throws Exception {
        super.setUp();
        JSONArray jsonArray = new JSONArray(jsonString);
        mAdapter = new CustomListAdapter(getContext(), jsonArray);
        assertNotNull("Adapter is null",mAdapter);
        assertEquals("Wrong Id",0, mAdapter.getItemId(0));
        assertEquals("GetCount incorrect.", 1, mAdapter.getCount());
        assertEquals("Wrong item", jsonArray.getJSONObject(0), mAdapter.getItem(0));
        assertNotNull("View is null",mAdapter.getView(0, null, null));
    }

}
