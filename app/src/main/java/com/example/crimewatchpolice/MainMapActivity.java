package com.example.crimewatchpolice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.crimewatchpolice.databinding.ActivityMainMapBinding;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainMapActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback, OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMainMapBinding binding;
    BottomNavigationView bottomNavigation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    Location myLocation;
    GPSTracker gpsTracker;
    private static String sender;
    String stringLatitude;
    String stringLongitude;
    AlertDialog.Builder builder;
    DatabaseReference reference;
    MediaPlayer mp;
    String username;
    String theLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        mp = MediaPlayer.create(this, R.raw.sample);
        reference = FirebaseDatabase.getInstance().getReference("crimes");
        builder = new AlertDialog.Builder(this);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                title = (TextView)findViewById(R.id.textCrimeTitle);
//                location = (TextView)findViewById(R.id.textCrimeLocation);
//                description = (TextView)findViewById(R.id.textCrimeDescription);

//                title.append(callingIntent.getExtras().getString("crimeTitle"));
//                location.append(callingIntent.getExtras().getString("crimeLocation"));
//                description.append(callingIntent.getExtras().getString("crimeDescription"));
                Intent myIntent = new Intent(MainMapActivity.this, BottomActivity.class);
                myIntent.putExtra("crimeTitle",(String) snapshot.child("crimeTitle").getValue());
                myIntent.putExtra("crimeLocation",(String) snapshot.child("crimeLocation").getValue());
                myIntent.putExtra("crimeDescription",(String) snapshot.child("crimeDescription").getValue());


                mp.start();
//                final Dialog mBottomSheetDialog = new Dialog(MainMapActivity.this, R.style.MaterialDialogSheet);
//                mBottomSheetDialog.setContentView(R.layout.activity_bottom); // your custom view.
//                mBottomSheetDialog.setCancelable(true);
//                mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
//                //startActivity(myIntent);
//                mBottomSheetDialog.show();
               showBottomSheetDialog((String) snapshot.child("crimeTitle").getValue(), (String) snapshot.child("crimeLocation").getValue(), (String) snapshot.child("crimeDescription").getValue(), snapshot);

//
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityMainMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bottomNavigation = findViewById(R.id.bottom_navigation);
        sender = this.getIntent().getExtras().getString("username");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        gpsTracker = new GPSTracker(this);

        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            stringLatitude = String.valueOf(gpsTracker.latitude);
            stringLongitude = String.valueOf(gpsTracker.longitude);
            String country = gpsTracker.getCountryName(this);
            String city = gpsTracker.getLocality(this);
            String postalCode = gpsTracker.getPostalCode(this);
            String addressLine = gpsTracker.getAddressLine(this);

            System.out.println(stringLatitude);
            System.out.println(stringLongitude);
            System.out.println("Country:"+country);
            System.out.println("City:"+city);

        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //gpsTracker.showSettingsAlert();
            System.out.println("No gps setting enabled");
        }
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_add:
//                            openFragment(ReportCrimeFragment.newInstance(sender,stringLatitude, stringLongitude));
                            return true;
                        case R.id.navigation_sms:
                            Intent intent = new Intent(MainMapActivity.this, MainMapActivity.class);
                            startActivity(intent);
                            return true;
                        case R.id.navigation_profile:
//                            openFragment(ProfileFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
//            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
            //Manifest.permission.ACCESS_FINE_LOCATION, true);
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;

    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        myLocation = location;
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

//        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation();
//        }
        else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
//        PermissionUtils.PermissionDeniedDialog
//                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String stringLatitude = null;
        String stringLongitude = null;
        if (gpsTracker.getIsGPSTrackingEnabled())
        {
            stringLatitude = String.valueOf(gpsTracker.latitude);
            stringLongitude = String.valueOf(gpsTracker.longitude);
            String country = gpsTracker.getCountryName(this);
            String city = gpsTracker.getLocality(this);
            String postalCode = gpsTracker.getPostalCode(this);
            String addressLine = gpsTracker.getAddressLine(this);

            System.out.println(stringLatitude);
            System.out.println(stringLongitude);
            System.out.println("Country:"+country);
            System.out.println("City:"+city);

        }
        else
        {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            //gpsTracker.showSettingsAlert();
            System.out.println("No gps setting enabled");
        }
//        // Add a marker in Sydney and move the camera
        assert stringLatitude != null;

        LatLng sydney = new LatLng(Double.parseDouble(stringLatitude), Double.parseDouble(stringLongitude));
        mMap.addMarker(new MarkerOptions().position(sydney).title("Me"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 14.0f));
        mMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        System.out.println(myLocation);
        enableMyLocation();
        onMyLocationButtonClick();
    }

    private void showBottomSheetDialog(String tit, String loc, String desc, DataSnapshot snapshot) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);
        TextView title = bottomSheetDialog.findViewById(R.id.textCrimeTitle);
        TextView  location = bottomSheetDialog.findViewById(R.id.textCrimeLocation);
        TextView description = bottomSheetDialog.findViewById(R.id.textCrimeDescription);
        Button accept = bottomSheetDialog.findViewById(R.id.acceptButton);
        Button reject = bottomSheetDialog.findViewById(R.id.reject);

        title.append(tit);
        location.append(loc);
        description.append(desc);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

                String respondent = username;
                String respondentLocation = theLocation;
                String status = "active";
                String uniqueID =  reference.getDatabase().getReference("crimes").getKey();
                //CrimeRespond crimeRespond = new CrimeRespond();
                System.out.println("Key :"+snapshot.getKey());
                reference.child((snapshot.getKey())).child("respondent").setValue(respondent);
                reference.child((snapshot.getKey())).child("respondentLocation").setValue(respondentLocation);
                reference.child((snapshot.getKey())).child("respondentStatus").setValue(status);

                String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+respondentLocation+"&destination="+snapshot.child("crimeLocation")+"&key=AIzaSyAEWcMQQo6bfe27ih75v2a-8zp1gdUWZfg";
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("routes");
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONArray legsObject = jsonObject.getJSONArray("legs");
                                for(int j = 0; j < legsObject.length(); j++){
                                    JSONObject jsonObject1 = legsObject.getJSONObject(j);
                                    JSONArray steps = jsonObject1.getJSONArray("steps");
                                    for(int k = 0; k < steps.length(); k++){
                                        JSONObject jsonObject2 = steps.getJSONObject(k);
                                        System.out.println(jsonObject2.getJSONObject("start_location").getString("lat"));
                                        mMap.addPolyline(new PolylineOptions()
                                                .color(0xffffffff)
                                                .jointType(JointType.ROUND)
                                                .clickable(true)
                                                .add(
                                                        new LatLng(Double.parseDouble(jsonObject2.getJSONObject("start_location").getString("lat")), Double.parseDouble(jsonObject2.getJSONObject("start_location").getString("lng"))),
                                                        new LatLng(Double.parseDouble(jsonObject2.getJSONObject("end_location").getString("lat")),  Double.parseDouble(jsonObject2.getJSONObject("end_location").getString("lng")))
                                                ));

                                    }
                                    JSONObject distance = jsonObject1.getJSONObject("distance");
                                    JSONObject duration = jsonObject1.getJSONObject("duration");
                                    JSONObject end = jsonObject1.getJSONObject("end_location");
                                    JSONObject start = jsonObject1.getJSONObject("start_location");
                                    System.out.println(distance.toString(4));
                                    System.out.println(distance.getString("value"));
                                    LatLng strt = new LatLng(Double.parseDouble(start.getString("lat")), Double.parseDouble(start.getString("lng")));
                                    LatLng stp = new LatLng(Double.parseDouble(end.getString("lat")), Double.parseDouble(end.getString("lng")));
                                    mMap.addMarker(new MarkerOptions().position(strt).title("Start"));
                                    mMap.addMarker(new MarkerOptions().position(stp).title("Destination"));
                                    showBottomSheetDialog1(duration.getString("text"), distance.getString("text"));
                                }
                                //JSONArray steps = legsObject.getJSONArray(6);
                                //System.out.println(legsObject.toString(4));
                            }
                            //System.out.println(stepsObject.toString(4));
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String formatted_address = jsonObject.getString("formatted_address");
                                //System.out.println(formatted_address);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                requestQueue.add(jsonObjectRequest);
            }
        });

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Rejected", Toast.LENGTH_LONG).show();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }

    private void showBottomSheetDialog1(String dur, String dist) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.arrival_time);

        TextView distance = bottomSheetDialog.findViewById(R.id.distanceToDestination);
        TextView duration = bottomSheetDialog.findViewById(R.id.journeyTime);
        Button start = bottomSheetDialog.findViewById(R.id.start);
        Button cancel = bottomSheetDialog.findViewById(R.id.cancel);

        distance.append(dist);
        duration.append(dur);

        bottomSheetDialog.show();
    }


}