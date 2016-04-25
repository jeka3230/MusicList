package ru.controlprograms.musiclist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
// Главная активность.
public class MainActivity extends ActionBarActivity {

    private static final String NETWORK_STATE = "NETWORK_STATE";
    private String LIST_INSTANCE_STATE = "LIST_INSTANCE_STATE";

    public static String INTENT_VALUE = "artists";
    public String PENDING_INTENT_VALUE = "android.net.conn.CONNECTIVITY_CHANGE";
    //    Массив для информации обо всех исполнителях.
    private JSONArray mArtists = new JSONArray();
//    Массив для информации об исполнителях, удоавлетворяющих критерию поиска.
    private JSONArray mFilteredArtists = new JSONArray();
    private ListView mListView;

    private Parcelable mListInstanceState;
    private Boolean mNetworkState = false;
//    База данных для поиска.
    private DatabaseTable mDatabase;
//    Адаптер для отображения элементов списка.
    private CustomListAdapter mAdapter;
//    Элемент для отображения загрузки информации.
    private View mLoadingView;
//    Набор методов для ввода-вывода JSONArray.
    private IOJSON mIOJSON;
//    Текстовое представление для демонстрации отсутствия результатов поиска.
    private TextView mTextNothing;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Установаим заголов приложения.
        ActionBar actionBar = getSupportActionBar();
        String activityName = getResources().getString(R.string.main_acivity_title);
        if (actionBar != null) {
            actionBar.setTitle(activityName);
        }
//        Поставим слушателя на состояние подключения к интернету.
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Boolean newNetworkState = isNetworkConnected();
//                Если соединения не было и оно вдруг появилось, заполним список.
                if (!mNetworkState && newNetworkState && mListView == null) {
                    fillMainActivity();
                }
                mNetworkState = newNetworkState;
            }
        };
        registerReceiver(mReceiver,new IntentFilter(PENDING_INTENT_VALUE));

        mIOJSON = new IOJSON(this);
//        Попытаемся считать jsonarry с диска.
        String stringJSON = mIOJSON.loadJSON();
//        Если есть соединение с сетью или на диске jsonarry, заполним список.
        if (mNetworkState) {
            fillMainActivity();
        } else {

            if (stringJSON == null) {
//                Установим шаблон отображающий отсутствие подключения к сети.
                setContentView(R.layout.activity_no_internet);
                Button buttonNoInternet = (Button) findViewById(R.id.button_no_internet);
//                По щелку по кнопке если интернет появился, загрузим jsonarray и заполним список.
                if (buttonNoInternet != null) {
                    buttonNoInternet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNetworkState = isNetworkConnected();
                            if (mNetworkState) {
                                fillMainActivity();
                            }
                        }
                    });
                }
            } else {
                try {
//                    Если на диске был файл, то заполним список.
                    mFilteredArtists = new JSONArray(stringJSON);
                    mArtists=mFilteredArtists;
                    mDatabase = new DatabaseTable(MainActivity.this);
                    fillMainActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
//        Сохранить состояние списка и интернета.
        if (mNetworkState) {
            savedInstanceState.putParcelable(LIST_INSTANCE_STATE, mListView.onSaveInstanceState());
        }
        savedInstanceState.putBoolean(NETWORK_STATE, mNetworkState);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        Востановить состояние списка.
        if (savedInstanceState.getBoolean(NETWORK_STATE)) {
            mListInstanceState = savedInstanceState.getParcelable(LIST_INSTANCE_STATE);
            mListView.onRestoreInstanceState(mListInstanceState);
        }
    }

    // Заполнение меню actionbar.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
//        Изменение списка в зависимости от поискового запроса.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                changeListData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                changeListData(newText);
                return true;
            }

            private void changeListData(String query) {
                if (!query.isEmpty() && mListView != null) {
                    mTextNothing.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
//                    Сделать поисковый запрос.
                    Cursor cursor = mDatabase.getWordMatches(query,null);
                    mFilteredArtists = new JSONArray();
//                    Если список выдачи не пуст, выбрать из массива нужные элементы и обновить список.
                    int positon;
                    if (cursor != null && mFilteredArtists !=null) {
                        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                            positon = Integer.parseInt(cursor.getString(1));
                            try {
                                mFilteredArtists.put(mArtists.getJSONObject(positon));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        mLoadingView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.VISIBLE);
                        mAdapter.setData(mFilteredArtists);
                    } else {
//                        Если список пуст, показать сообщение пользователю.
                        mTextNothing.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    }
                } else {
//                    Если запрос пуст, вывести информацию обо всех.
                    mFilteredArtists = mArtists;
                    mAdapter.setData(mFilteredArtists);
                }
            }

        });
//        Кнопка ощищающая текст поискового запроса.
        ImageView clearButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
//        Если кнопка нажата, очистить список и показать всех исполнителей.
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("",false);
                mFilteredArtists = mArtists;
                mAdapter.setData(mFilteredArtists);
                mTextNothing.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }
        });
//          Обработка нажатия на кнопку назад на actionbar во время поиска.
        MenuItemCompat.setOnActionExpandListener(menu.findItem(R.id.search), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
//                Если кнопка нажата, свернуть поисковый textedit и отобразить всех исполнителей.
                mFilteredArtists = mArtists;
                mAdapter.setData(mFilteredArtists);
                mTextNothing.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                return true;
            }
        });
        return true;
    }

    private void fillMainActivity() {

        setContentView(R.layout.activity_main);
        mLoadingView = findViewById(R.id.loading_spinner);
        mTextNothing = (TextView) findViewById(R.id.textNothing);
        mListView = (ListView) findViewById(R.id.listView);
//        Если json файл ещё не подгрузился, покажем loading.
        if (mFilteredArtists.length() == 0) {
            mListView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.VISIBLE);
        }else {
            mListView.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.GONE);
        }
//        Установим адаптер для списка.
        String url = getResources().getString(R.string.url);
        mAdapter = new CustomListAdapter(this, mFilteredArtists);
        mListView.setAdapter(mAdapter);
//        Загрузим jsonarray из сети.
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int responseLen = response.length();
//                Запишем(перезапишем) jsonarray на диск для использования при следующих запусках и для БД.
                mIOJSON.writeJSONArray(response.toString());
//                Инициализируем базу данных(при первом запуске ещё и заполним).
                mDatabase = new DatabaseTable(MainActivity.this);
                try {
                    mArtists = new JSONArray(response.toString());
                    mFilteredArtists = new JSONArray(response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Уберем loading, покажем список.
                mListView.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
                mAdapter.setData(mFilteredArtists);
            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(this).addToRequetQueue(jsonArrayRequest);
//        По щелку на элемент списка открывается активность с полной информацией об артисте.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, ArtistView.class);
                try {
                    intent.putExtra(INTENT_VALUE, mFilteredArtists.getJSONObject(position).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
//                Анимация для открытия новой активности.
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
            }
        });

    }



    private boolean isNetworkConnected() {
//        Проверить наличие подключения к интернету.
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null;
    }


}
