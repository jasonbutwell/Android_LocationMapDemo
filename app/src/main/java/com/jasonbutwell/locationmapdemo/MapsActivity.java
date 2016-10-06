package com.jasonbutwell.locationmapdemo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    private Location location;
    private LocationManager locationManager;
    private String provider;
    private Double lat, lon;

    public void updatePositionOnMap() {

        // Specifics for lat long, location string and zoom level we want
        LatLng yourLocation = new LatLng( lat, lon );
        String locationString = "You are here!";
        int zoomLevel = 15;

        mMap.clear();   // clears all other marker occurances

        Marker marker = mMap.addMarker(new MarkerOptions().position(yourLocation).title( locationString ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        marker.showInfoWindow();    // displays the location string straight away

        // move the camera to our current marker and change the zoom level also
        mMap.moveCamera(CameraUpdateFactory.newLatLng( yourLocation ));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( zoomLevel ));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Check to see if the GPS is enabled and switched on - Permission model might also stop this working so check that too!
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        else {
            location = locationManager.getLastKnownLocation( LocationManager.NETWORK_PROVIDER );

            if ( location != null ) {
                Log.i( "Location", "Location Achieved" );
                onLocationChanged(location);
            } else {
                Log.i( "Location", "No Location" );
            }
        }
    }

    // Google Map initialise

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    // When app goes to background

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates if app is paused and goes to the background to save cpu and battery
        locationManager.removeUpdates(this);
    }

    // When app comes to the foreground again

    @Override
    protected void onResume() {
        super.onResume();

        // update window will be 400 milli seconds as specified here
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    // Invoked by the location change listener

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();

        //Log.i("LAT/LON : (",lat.toString()+","+lon.toString()+")");

        // call method to update our map marker
        updatePositionOnMap();
    }

    // Not required here

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }
}
