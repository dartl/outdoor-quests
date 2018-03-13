package ru.gawk.historygeocachingdemo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.Manifest;

import android.location.LocationListener;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import ru.gawk.historygeocachingdemo.adapters.InstanceAchievements;
import ru.gawk.historygeocachingdemo.adapters.InstanceDB;
import ru.gawk.historygeocachingdemo.adapters.LiteMessage;
import ru.gawk.historygeocachingdemo.adapters.MarkersInfoAdapter;
import ru.gawk.historygeocachingdemo.adapters.PrefUtil;
import ru.gawk.historygeocachingdemo.adapters.TestStatistics;
import ru.gawk.historygeocachingdemo.models.HistoryPoint;
import ru.gawk.historygeocachingdemo.models.QuestHistory;
import ru.gawk.historygeocachingdemo.windows.FirstHelpWindow;

/**
 * Created by GAWK on 05.03.2017.
 */

public class MapActivity extends ParentActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener {
    private PrefUtil mPrefUtil;
    private InstanceAchievements mInstanceAchievements;

    // Константы
    private static final int STEP_CALL_LOCATION_CHANGE = 2; // Количество метров, которые нужно пройти для обновления позиции

    private GoogleMap mMap; // Объект карты
    private ImageButton imageButtonInfo, imageButtonCheck, imageButtonHonor;
    private ArrayList<HistoryPoint> points; // Список исторических меток
    private int currentHistoryPointIndex;
    private QuestHistory mQuestHistory;
    private Marker currentHistoryMarker; // Текущий маркер следующей цели на карте
    private Circle circlePlayer; // Область видение игровка
    private LocationManager locationManager; // Менеджер местоположения
    private boolean firstPlayerChange = false;
    private LatLng mLatLngCurrentPosition;

    private long mCurrentMillis = new Date().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_map);

        mPrefUtil = new PrefUtil(this);
        mInstanceAchievements = InstanceAchievements.getInstance(this);

        /* Вешаем события на кнопки */
        imageButtonInfo = (ImageButton) findViewById(R.id.imageButtonInfo);

        imageButtonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuestActivity.class);
                intent.putExtra("points", new MarkersInfoAdapter(points, currentHistoryPointIndex));
                intent.putExtra("quest_number", mQuestHistory.getNumber());
                startActivity(intent);
            }
        });

        /* Скрываем вспомогательные кнопки */
        imageButtonCheck = (ImageButton) findViewById(R.id.imageButtonCheck);

        imageButtonHonor = (ImageButton) findViewById(R.id.imageButtonHonor);
        imageButtonHonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
                startActivity(intent);
            }
        });

        imageButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNextMark();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_current_quest).setCheckable(true).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    void init() {
        if (mInstanceDB.getActiveQuest() == -1) finish();


        if (mQuestHistory == null || mQuestHistory.getNumber() != mInstanceDB.getActiveQuest()) {
            mQuestHistory = mInstanceDB.getCurrentQuestHistory();

            points = mQuestHistory.getPoints();

            if (!getIntent().getBooleanExtra("active", true)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(getString(R.string.quest_start_message_first) + " \"" + mQuestHistory.getName() + "\"." +
                        getString(R.string.quest_start_message_end))
                        .setTitle(R.string.quest_start_title);

                AlertDialog dialog = builder.create();
                dialog.show();
            }

            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            if (mMap != null && !getIntent().getBooleanExtra("active", true)) {
                mMap.clear();
                startMarks();
            }
        }

        setTitle(mQuestHistory.getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPrefUtil.saveLong(PrefUtil.ACTIVE_QUEST, -1);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("GAWK_ERR","Call onMapReady");
        mMap = googleMap;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkPermissions(); // Проверяем возможность получения данных о местоположении игрока
        startMarks();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("GAWK_ERR","onLocationChanged - " + location);
        sendCoordinateToServer(location);
        mLatLngCurrentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        changePlayer();
        eventChangePlayerPosition_CheckMark();
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    private void sendCoordinateToServer(Location location) {
        Log.e("GAWK_ERR", "sendCoordinateToServer()");
        if (new Date().getTime() - mCurrentMillis > 10000) {
            String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            new TestStatistics.saveCoordinate().execute(android_id, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }
    }


    // Функция, отображающая уже активные метки
    private void startMarks() {
        for (int  i = 0; i < points.size(); i++) {
            if (points.get(i).isCheck() || i == 0) {
                HistoryPoint historyPoint = points.get(i);
                LatLng point = new LatLng(historyPoint.getLatitude(), historyPoint.getLongitude());
                currentHistoryMarker = mMap.addMarker(new MarkerOptions()
                        .position(point).title(historyPoint.getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,15));
                if (!points.get(i+1).isCheck()) { // если следующая метка не активна - эта активная
                    currentHistoryPointIndex = i;
                }
            }
        }
    }

    public void changePlayer() {
        LatLng player = mLatLngCurrentPosition;
        if (firstPlayerChange) {
            circlePlayer.remove();
        }
        firstPlayerChange = true;
        circlePlayer = mMap.addCircle(new CircleOptions()
                .center(player)
                .radius(points.get(currentHistoryPointIndex).getRadius())
                .strokeWidth(1)
                .strokeColor(Color.argb(150, 0, 0, 255))
                .fillColor(Color.argb(40, 0, 0, 255)));
    }

    public void eventChangePlayerPosition_CheckMark() {
        if (CalculationDistance(mLatLngCurrentPosition,new LatLng(points.get(currentHistoryPointIndex).getLatitude(),points.get(currentHistoryPointIndex).getLongitude())) // Считаем растояние от игрока до метки
                < points.get(currentHistoryPointIndex).getRadius()) { // Сравниваем с радиусом для метки, считаемым, что она в зоне видимости
            if (!points.get(currentHistoryPointIndex).isCheck()) {
                LatLng point = new LatLng(points.get(currentHistoryPointIndex).getLatitude(), points.get(currentHistoryPointIndex).getLongitude());
                currentHistoryMarker = mMap.addMarker(new MarkerOptions().position(point)
                        .title(points.get(currentHistoryPointIndex).getName())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                points.get(currentHistoryPointIndex).setCheck(true);
            }
            currentHistoryMarker.setVisible(true);
        } else if (CalculationDistance(mLatLngCurrentPosition,new LatLng(points.get(currentHistoryPointIndex).getLatitude(),points.get(currentHistoryPointIndex).getLongitude())) // Считаем растояние от игрока до метки
                > points.get(currentHistoryPointIndex).getRadius()){
            if (currentHistoryPointIndex == 0) return;
            currentHistoryMarker.setVisible(false);
        }
    }

    // Проверяем, находится ли пользователь рядом с меткой
    private void checkNextMark() {
        try {
            if (CalculationDistance(mLatLngCurrentPosition,
                    new LatLng(points.get(currentHistoryPointIndex).getLatitude(),
                            points.get(currentHistoryPointIndex).getLongitude())) // Считаем растояние от игрока до метки
                    < points.get(currentHistoryPointIndex).getRadius()/2) { // Сравниваем с радиусом для метки, считаемым, что она в зоне доступа

                String android_id = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                String str = "Найдена новая точка - Quest(number=" + mQuestHistory.getNumber()
                        + "); Point(number=" + points.get(currentHistoryPointIndex).getNumber()
                        + ", name=" + points.get(currentHistoryPointIndex).getName() + ")";
                new TestStatistics.saveActions().execute(android_id, str);

                currentHistoryPointIndex++;
                if (currentHistoryPointIndex == points.size()) {

                    mPrefUtil.saveInt(PrefUtil.COMPLETE_QUEST,mPrefUtil.getInt(PrefUtil.COMPLETE_QUEST,0)+1);

                    mInstanceAchievements.checkAchievements();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setMessage(getString(R.string.quest_end_message) + " " + mQuestHistory.getName())
                            .setTitle(R.string.quest_end_title);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    currentHistoryMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    LatLng point = new LatLng(points.get(currentHistoryPointIndex).getLatitude(), points.get(currentHistoryPointIndex).getLongitude());
                    currentHistoryMarker = mMap.addMarker(new MarkerOptions().position(point).title(points.get(currentHistoryPointIndex).getName()));
                    currentHistoryMarker.setVisible(points.get(currentHistoryPointIndex).isCheck());

                    FirstHelpWindow mFirstHelpWindow = new FirstHelpWindow(
                            points.get(currentHistoryPointIndex).getHelps().get(0).getData(),
                            points.get(currentHistoryPointIndex).getName(),
                            points.get(currentHistoryPointIndex).getHelps().get(0).getImage());

                    mFirstHelpWindow.show(getFragmentManager(),"FirstHelpWindow");
                }
            } else {
                LiteMessage mLiteMessage = new LiteMessage(this, getString(R.string.quest_error_check_false));
                mLiteMessage.show();
            }
        } catch (NullPointerException e) {
            LiteMessage mLiteMessage = new LiteMessage(this, getString(R.string.quest_error_not_location_player));
            mLiteMessage.show();
        }

    }

    // Считает растояние по Lat-Lng двух меток
    public static double CalculationDistance(LatLng StartP, LatLng EndP) {
        return CalculationDistanceByCoord(StartP.latitude, StartP.longitude, EndP.latitude, EndP.longitude);
    }

    // Считает растояние по координатам двух меток
    private static double CalculationDistanceByCoord(double startPointLat,double startPointLon,double endPointLat,double endPointLon){
        float[] results = new float[1];
        Location.distanceBetween(startPointLat, startPointLon, endPointLat, endPointLon, results);
        return results[0];
    }


    // Проверяем разрешение на получение данных о местоположении
    private static final int REQUEST_EXTERNAL_LOCATION = 1;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e("GAWK_ERR","Call checkPermissions() - true");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, STEP_CALL_LOCATION_CHANGE, this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, STEP_CALL_LOCATION_CHANGE, this);
            mMap.setMyLocationEnabled(true);
            return true;
        } else {
            Log.e("GAWK_ERR","Call checkPermissions() - false");
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_LOCATION,
                    REQUEST_EXTERNAL_LOCATION
            );
            // Show rationale and request permission.
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            default:
                break;
        }
    }
}
