package ru.controlprograms.musiclist;

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
        String textAlbums = wordForms(albums, "альбом", "альбома", "альбомов");
        String textTracks = wordForms(tracks, "песня", "песни", "песен");

        return albums +" " + textAlbums + ", "+tracks +" " + textTracks;
    }
// Обработка окончаний слов.
    private static String wordForms(int number, String firstForm, String secondForm, String thirdForm) {

        int lastDigit = number % 10;
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
