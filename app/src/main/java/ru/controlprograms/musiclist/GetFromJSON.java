package ru.controlprograms.musiclist;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by evgeny on 24.04.16.
 */
//Обработка JSONObject.
public class GetFromJSON {

    private final String NAME = "name";
    private final String GENRES = "genres";
    private final String COVER = "cover";
    private final String SMALL = "small";
    private final String BIG = "big";
    private final String DESCRIPTION = "description";
    private final String ALBUMS = "albums";
    private final String TRACKS = "tracks";

    private final String ALBUM1 = "альбом";
    private final String ALBUM2 = "альбома";
    private final String ALBUM3 = "альбомов";

    private final String TRACKS1 = "песня";
    private final String TRACKS2 = "песни";
    private final String TRACKS3 = "песен";


    JSONObject mJsonObject;

    public GetFromJSON(JSONObject jsonObject) {
        mJsonObject = jsonObject;
    }
    public String getName() throws JSONException {
        return mJsonObject.getString(NAME);
    }

    public String getGenres() throws JSONException {
//        Преобразовать массив жанров в строку.
        return mJsonObject.getString(GENRES).replaceAll("\\[|\\]|\"", "").replace(",",", ");
    }
// Взять адрес большой картинки.
    public String getCoverSmallURL() throws JSONException {
        return mJsonObject.getJSONObject(COVER).getString(SMALL);
    }
// Взять адрес маленькой картинки.
    public String getCoverBigURL() throws JSONException {
        return mJsonObject.getJSONObject(COVER).getString(BIG);
    }
//Преобразовать описание исполнителя.
    public String getDescription() throws JSONException {
        String textDescription = mJsonObject.getString(DESCRIPTION);
//        Заменить первую букву на большую.
        textDescription = textDescription.substring(0,1).toUpperCase() + textDescription.substring(1);
        return textDescription;
    }
// Получить строку для число альбомов, число треков.
    public String getInfoText() throws JSONException {

        int albums = mJsonObject.getInt(ALBUMS);
        int tracks = mJsonObject.getInt(TRACKS);
        String textAlbums = wordForms(albums, ALBUM1, ALBUM2, ALBUM3);
        String textTracks = wordForms(tracks, TRACKS1, TRACKS2, TRACKS3);

        return albums +" " + textAlbums + ", "+tracks +" " + textTracks;
    }
// Обработка окончаний слов.
    private String wordForms(int number, String firstForm, String secondForm, String thirdForm) {
//        Последняя цифра
        int lastDigit = number % 10;
//        Предпоследняя цифра
        int penultDigit = number % 100 / 10;

        if (lastDigit == 1 && penultDigit !=1) {
            return firstForm;
        } else {

            if (lastDigit >1 && lastDigit < 5 && penultDigit !=1 ) {
                return secondForm;
            } else {
                return thirdForm;
            }
        }
    }
}
