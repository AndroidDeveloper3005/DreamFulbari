package com.himel.androiddeveloper3005.dreamfulbari.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.CustomInfoWindowAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.Adapter.PlaceAutocompleteAdapter;
import com.himel.androiddeveloper3005.dreamfulbari.Model.PlaceInfo;
import com.himel.androiddeveloper3005.dreamfulbari.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback
        , GoogleApiClient.OnConnectionFailedListener{

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Map is ready");
        mMap = googleMap;
        if (mLocationPermissionGranted) {
            getDeveiceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //init();
            buttonClick();

        }
    }



    private static final float DEFAULT_ZOOM = 15f;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = "MapActivity";
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private GoogleMap mMap;
    private AutoCompleteTextView mSearchText;
    private Button mSearch;
    private ImageView mGps,mPlaceInfo,mPlacePicker;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168),new LatLng(71,136)
    );
    private PlaceInfo mPlace;
    private Marker mMarker;

    private static final int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        initializeVariable();
    }

    private void initializeVariable(){

        mSearchText = findViewById(R.id.input_search);
        mSearch = findViewById(R.id.ic_magnify);
        mGps = findViewById(R.id.ic_gps);
        mPlaceInfo = findViewById(R.id.placeinfo_image);
        mPlacePicker = findViewById(R.id.place_picker);

    }

    private void buttonClick(){

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this,mGoogleApiClient,LAT_LNG_BOUNDS,null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText.setOnItemClickListener(mAutoCompleteClickListener);

        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // init();
                geoLocate();


            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Gps Button Clicked.");
                getDeveiceLocation();
            }
        });

        mPlaceInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Place Info :");
                try {
                    if (mMarker.isInfoWindowShown()){
                        mMarker.hideInfoWindow();
                    }
                    else {
                        Log.d(TAG, "Place Info :"+mPlace.toString());
                        mMarker.showInfoWindow();
                    }

                }catch (NullPointerException e){
                    Log.d(TAG, "NullPointerException "+e.getMessage());
                }

            }
        });

        mPlacePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(MapActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d(TAG,"GooglePlayServicesRepairableException : "+e.getMessage());
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d(TAG,"GooglePlayServicesRepairableException : "+e.getMessage());
                }
            }
        });

        hideKeyboard();



    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                PendingResult<PlaceBuffer> placeResult =Places.GeoDataApi.getPlaceById(mGoogleApiClient,place.getId());
                placeResult.setResultCallback(mUpdatePlaceDetailCallBack);


            }
        }
    }


    private void init(){

        Log.d(TAG, "View Initializing");
        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        ||actionId == EditorInfo.IME_ACTION_DONE
                        ||keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        ||keyEvent.getAction() == KeyEvent.KEYCODE_ENTER
                        ){
                    //method for searching
                    //geoLocate();
                }
                return false;
            }
        });
        hideKeyboard();

    }

    private void geoLocate() {

        Log.d(TAG, "Geo Location Searching");

        String serachString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MapActivity.this);

        List<Address> list = new ArrayList<>();

        try{
            list = geocoder.getFromLocationName(serachString,1);

        }
        catch (IOException ex){
            Log.d(TAG, "IOException :"+ ex.getMessage());

        }

        if (list.size() > 0){
            Address address = list.get(0);
            Log.d(TAG, "Geo Location found : " + address.toString());

            //Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM,
                    address.getAddressLine(0));
            hideKeyboard();

        }



    }




    private void initMap(){
        Log.d(TAG,"Map is initaizing");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(MapActivity.this);


    }
    private void getDeveiceLocation(){
        Log.d(TAG,"getting the Device Current  Location.");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionGranted){
                Task<Location> location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {

                        if (task.isSuccessful()){
                            Log.d(TAG,"Location found");
                            Location cuttentLocation = task.getResult();

                            moveCamera(new LatLng(cuttentLocation.getLatitude()
                                            ,cuttentLocation.getLongitude())
                                    ,DEFAULT_ZOOM,
                                    "My Location.");


                        }
                        else {
                            Log.d(TAG,"Current Location is null");
                            Toast.makeText(MapActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();

                        }

                    }
                });



            }

        }catch (SecurityException e){
            Log.d(TAG,"Exception :"+e.getMessage());
            e.printStackTrace();
        }


    }

    private void moveCamera(LatLng latLng,float zoom,PlaceInfo placeInfo) {
        Log.d(TAG,"Moviming the camera to :"+latLng.latitude +", lng : "+ latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        mMap.clear();

        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(MapActivity.this));

        if (placeInfo !=null){
            try {
                String snippet = "Address : " +placeInfo.getAddress() +"\n" +
                        "Phone Number : " +placeInfo.getPhoneNumber() +"\n" +
                        "Web Site  : " +placeInfo.getWebsiteuri() +"\n" +
                        "Rating : " +placeInfo.getRating() +"\n" ;
                MarkerOptions options = new MarkerOptions()
                        .position(latLng)
                        .title(placeInfo.getName())
                        .snippet(snippet);
                mMarker = mMap.addMarker(options);




            }catch (NullPointerException e) {
                Log.d(TAG, "NullPointerException Move Camera: " +e.getMessage());
            }

        }

        else {
            mMap.addMarker(new MarkerOptions().position(latLng));
        }

        hideKeyboard();

    }




    private void moveCamera(LatLng latLng,float zoom,String title) {
        Log.d(TAG,"Moviming the camera to :"+latLng.latitude +", lng : "+ latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if (!title.equals("My Location.")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }
        hideKeyboard();

    }


    //hide keboard

    private void hideKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void getLocationPermission(){
        Log.d(TAG,"Geting Location Permission");
        String []permission = {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                //set boolean
                mLocationPermissionGranted = true;
                initMap();

            }
            else {
                ActivityCompat.requestPermissions(this,
                        permission,
                        LOCATION_PERMISSION_REQUEST_CODE);

            }

        }
        else {
            ActivityCompat.requestPermissions(this,permission,LOCATION_PERMISSION_REQUEST_CODE);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if (grantResults.length > 0){
                    for (int i =0 ;i<grantResults.length;i++){
                        if (grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted =false;
                            Log.d(TAG,"permission failed");
                            return;

                        }

                    }
                    Log.d(TAG,"permission granted");
                    mLocationPermissionGranted =true;

                    //initialize our map
                    initMap();

                }
            }
        }
    }


    /*
    -----------------------Google Place Api autoComplete suggestion -------
  */

    private AdapterView.OnItemClickListener mAutoCompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            hideKeyboard();
            final AutocompletePrediction item =mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            PendingResult<PlaceBuffer>placeResult =Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailCallBack);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailCallBack = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()){
                Log.d(TAG,"Place Query did not complete successfully:" +places.getStatus().toString());
                places.release();
                return;

            }
            hideKeyboard();
            final Place place = places.get(0);

            try {
                mPlace = new PlaceInfo();

                mPlace.setAddress(place.getAddress().toString());

                Log.d(TAG, "Place Address :" + place.getAddress().toString());

                /*mPlace.setAttributions(place.getAttributions().toString());

                Log.d(TAG, "Place Attributions :" + place.getAttributions().toString());
*/
                mPlace.setId(place.getId());

                Log.d(TAG, "Place ID :" + place.getId());

                mPlace.setLatLng(place.getLatLng());

                Log.d(TAG, "Place LatLag :" + place.getLatLng());

                mPlace.setRating(place.getRating());

                Log.d(TAG, "Place Rating :" + place.getRating());

                mPlace.setName(place.getName().toString());

                Log.d(TAG, "Place Name :" + place.getName().toString());

                mPlace.setPhoneNumber(place.getPhoneNumber().toString());

                Log.d(TAG, "Place Phone Number :" + place.getPhoneNumber().toString());

                mPlace.setWebsiteuri(place.getWebsiteUri());

                Log.d(TAG, "Place WebSite :" + place.getWebsiteUri());

                Log.d(TAG, "Place Details :" + mPlace.toString());
            }
            catch (NullPointerException e){
                Log.d(TAG,"NullPointerException :" +e.getMessage());
            }


            moveCamera(new LatLng(
                    place.getViewport().getCenter().latitude,place.getViewport().getCenter().longitude
            ),DEFAULT_ZOOM,mPlace);

            places.release();


        }
    };


}

