package ru.controlprograms.musiclist;

import static org.junit.Assert.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by evgeny on 25.04.16.
 */
public class GetFromJSONUnitTest {
    GetFromJSON mGetFromJSON;
    final String jsonString = "{\"id\":1080505,\"name\":\"Tove Lo\",\"genres\":[\"pop\",\"dance\"," +
            "\"electronics\"],\"tracks\":81,\"albums\":22,\"link\":\"http://www.tove-lo.com/\"," +
            "\"description\":\"шведская певица и автор песен. Она привлекла к себе внимание в" +
            " 2013 году с выпуском сингла «Habits», но настоящего успеха добилась с ремиксом " +
            "хип-хоп продюсера Hippie Sabotage на эту песню, который получил название " +
            "«Stay High». 4 марта 2014 года вышел её дебютный мини-альбом Truth Serum, а 24" +
            " сентября этого же года дебютный студийный альбом Queen of the Clouds. Туве Лу" +
            " является автором песен таких артистов, как Icona Pop, Girls Aloud и Шер Ллойд." +
            "\",\"cover\":{\"small\":\"http://avatars.yandex.net/get-music-content/dfc531f5." +
            "p.1080505/300x300\",\"big\":\"http://avatars.yandex.net/get-music-content/dfc53" +
            "1f5.p.1080505/1000x1000\"}}";
    final String NAME = "Tove Lo";
    final String GENRES = "pop, dance, electronics";
    final String COVER_SMALL_URL = "http://avatars.yandex.net/get-music-content/dfc531f5." +
            "p.1080505/300x300";
    final String COVER_BIG_URL = "http://avatars.yandex.net/get-music-content/dfc531f5.p." +
            "1080505/1000x1000";
    final String DESCRIPTION = "Шведская певица и автор песен. Она привлекла к себе внимание в" +
            " 2013 году с выпуском сингла «Habits», но настоящего успеха добилась с ремиксом " +
            "хип-хоп продюсера Hippie Sabotage на эту песню, который получил название " +
            "«Stay High». 4 марта 2014 года вышел её дебютный мини-альбом Truth Serum, а 24" +
            " сентября этого же года дебютный студийный альбом Queen of the Clouds. Туве Лу" +
            " является автором песен таких артистов, как Icona Pop, Girls Aloud и Шер Ллойд.";
    final String INFO_TEXT = "22 альбома, 81 песня";

    @Before
    public void setUp() throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        mGetFromJSON = new GetFromJSON(jsonObject);
    }

    @Test
    public void testName() throws JSONException {
        final String name = mGetFromJSON.getName();
        assertEquals(name, NAME);
    }

    @Test
    public void testCoverSmallURL() throws JSONException {
        final String coverSmallURL = mGetFromJSON.getCoverSmallURL();
        assertEquals(coverSmallURL,COVER_SMALL_URL);
    }

    @Test
    public void testCoverBigURL() throws JSONException {
        final String coverBigURL = mGetFromJSON.getCoverBigURL();
        assertEquals(coverBigURL,COVER_BIG_URL);
    }

    @Test
    public void testDescription() throws JSONException {
        final String description = mGetFromJSON.getDescription();
        assertEquals(description,DESCRIPTION);
    }
    @Test
    public void testInfoText() throws JSONException {
        final String infoText = mGetFromJSON.getInfoText();
        assertEquals(infoText,INFO_TEXT);

    }
}
