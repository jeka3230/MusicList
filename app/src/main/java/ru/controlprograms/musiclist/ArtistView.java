package ru.controlprograms.musiclist;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;
//Активность для отображение полной информации об исполнителе.
public class ArtistView extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_view);
        try {
//            Взять информации об артисет для отображения из строки, переданной интентом.
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("artist"));
            GetFromJSON getFromJSON = new GetFromJSON(jsonObject);
            String name = getFromJSON.getName();
//            Заполнить шаблон.
            String textDescription = getFromJSON.getDescription();
            String textGenres = getFromJSON.getGenres();
            String textInfo = getFromJSON.getInfoText();
            String coverBigUrl = getFromJSON.getCoverBigURL();

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(name);
            }

            if (actionBar != null) {
                actionBar.setDisplayShowHomeEnabled(true);
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setShowHideAnimationEnabled(true);
            }

            NetworkImageView coverBig = (NetworkImageView) findViewById(R.id.cover_big);
            ImageLoader imageLoader = VolleySingleton.getInstance(this).getImageLoader();
            if (coverBig != null) {
                coverBig.setImageUrl(coverBigUrl, imageLoader);
            }

            TextView textViewGenres = (TextView) findViewById(R.id.textGenres);
            if (textViewGenres != null) {
                textViewGenres.setText(textGenres);
            }

            TextView textViewInfo = (TextView)findViewById(R.id.textInfo);
            if (textViewInfo != null) {
                textViewInfo.setText(textInfo);
            }

            TextView textViewDescription = (TextView) findViewById(R.id.textDescription);
            if (textViewDescription != null) {
                textViewDescription.setText(textDescription);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
//            По нажатии на кнопку назад actionbar делать тоже, что и при нажатии на кнопку назад на клавиатуре.
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        При нажатии на кнопку назад закрывать активность и показать анимацию.
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
