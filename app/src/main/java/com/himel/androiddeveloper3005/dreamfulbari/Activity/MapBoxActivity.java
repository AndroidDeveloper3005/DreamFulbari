package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.himel.androiddeveloper3005.dreamfulbari.AppConstant.Constans;
import com.himel.androiddeveloper3005.dreamfulbari.R;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapBoxActivity extends AppCompatActivity implements OnMapReadyCallback, LocationEngineListener
        , PermissionsListener , MapboxMap.OnMapClickListener {
    private MapView mapView;
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originlocation;
    private Point originPosition;
    private Point destinationPosition;
    private Marker destinationMarker;
    private Button startButton;
    private NavigationMapRoute navigationMapRoute;
    private static final String TAG ="MapActivity";

    private static  final double destinationLat = 25.9475451;
    private static  final double destinationLang = 89.5452469;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, Constans.ACCESS_TOKEN);

        setContentView(R.layout.activity_map_box);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.setStyleUrl(Style.MAPBOX_STREETS);
        mapView.getMapAsync(this);
        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launch navigation
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .origin(originPosition)
                        .destination(destinationPosition)
                        .shouldSimulateRoute(true)
                        .build();
                NavigationLauncher.startNavigation(MapBoxActivity.this,options);

            }
        });
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        map = mapboxMap;
        map.addOnMapClickListener(this);
        enablelocation();
    }

    private  void enablelocation(){
        if (PermissionsManager.areLocationPermissionsGranted(this)){
            // my need
            initializedLocationEngine();
            initializedLocationLayer();

        }else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }
    @SuppressLint("MissingPermission")
    private  void initializedLocationEngine(){
        locationEngine = new LocationEngineProvider(this)
                .obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation !=null){
            originlocation = lastLocation;
            setcameraPosition(lastLocation);
        }else {
            locationEngine.addLocationEngineListener(this);
        }

    }
    @SuppressLint("MissingPermission")
    private  void initializedLocationLayer(){
        locationLayerPlugin =
                new LocationLayerPlugin(mapView,map,locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.COMPASS);
    }

    //camera position
    private  void  setcameraPosition(Location location){
        map.animateCamera(CameraUpdateFactory
                .newLatLngZoom(new LatLng(
                        location.getLatitude()
                        ,location.getLongitude()
                ),15.0));

    }


    @Override
    public void onMapClick(@NonNull LatLng point) {
        if (destinationMarker != null){
            map.removeMarker(destinationMarker);
        }
         destinationMarker = map.addMarker(new MarkerOptions().position(point));
         destinationPosition = Point.fromLngLat(point.getLongitude(),point.getLatitude());
         originPosition = Point.fromLngLat(originlocation.getLongitude(),originlocation.getLatitude());
         getRoute(originPosition,destinationPosition);
         startButton.setEnabled(true);
         startButton.setBackgroundResource(R.color.mapbox_blue);


    }
    private  void getRoute(Point origin,Point destination){
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call
                    , Response<DirectionsResponse> response) {
                if (response.body() == null){

                    Log.e(TAG,"No routes found.check access token");
                    return;
                }else if (response.body().routes().size() == 0){
                    Log.e(TAG,"No routes found.");
                    Toast.makeText(getApplicationContext(),"No routes found.",Toast.LENGTH_LONG).show();
                    return;
                }

                DirectionsRoute currentRoute = response.body().routes().get(0);
                if (navigationMapRoute != null){
                    navigationMapRoute.removeRoute();

                }else {
                    navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                }
                navigationMapRoute.addRoute(currentRoute);



            }

            @Override
            public void onFailure(Call<DirectionsResponse> call
                    , Throwable t) {
                Log.e(TAG," Error"+ t.getMessage());

            }
        });

        ;

    }


    @Override
    @SuppressLint("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location !=null){
            this.originlocation = location;
            setcameraPosition(location);
        }

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        // toast or dialog

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted){
            enablelocation();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
    @SuppressLint("MissingPermission")
    @Override

    protected void onStart() {
        super.onStart();
        if (locationEngine != null){
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null){
            locationLayerPlugin.onStart();

        }
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null){
            locationEngine.removeLocationUpdates();

        }
        if (locationLayerPlugin != null){
            locationLayerPlugin.onStop();

        }
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine !=null){
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }


}
