package com.dom.communityapp;

import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;


import com.dom.communityapp.location.BroadCastReceiveUitility;
import com.dom.communityapp.location.LocationListener;
import com.dom.communityapp.location.LocationUpdateCallback;
import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.storage.FirebaseDatabaseStorage;
import com.dom.communityapp.storage.FirebaseObserver;
import com.dom.communityapp.location.LocationSettingAsker;
import com.dom.communityapp.location.PermissionRequestCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.maps.android.ui.IconGenerator;

import java.io.ByteArrayOutputStream;

import static java.lang.Thread.sleep;

public class MapsActivity extends AbstractNavigation implements OnMapReadyCallback, GoogleMap.OnPoiClickListener, GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener, FirebaseObserver, LocationListener {

    private static final LatLng BRISBANE = new LatLng(10, 10);
    private static final float DEFAULT_ZOOM = 15;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String KEY_LOCATION = "location";
    private final FirebaseDatabaseStorage mFirebaseStorage;
    private final BroadCastReceiveUitility mBroadCastRecieveUtility;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private MapFragment mMapFragment;
    private IconGenerator mIconFactory;
    private LocationCommunityService mService;
    private boolean mBound;
    private ServiceConnection mConnection;
    private Bitmap mBitmap;
    private LocationSettingAsker mLocationAsker;
    private LatLng mDefaultLocation = new LatLng(55.676098, 12.568337);
    private boolean mFirstLocation = true;


    public MapsActivity() {
        this.mFirebaseStorage = new FirebaseDatabaseStorage(this);
        this.mFirebaseStorage.addObserver(this);
        this.mBroadCastRecieveUtility = new BroadCastReceiveUitility(this);
    }

    @Override
    protected DrawerLayout getdrawerLayout() {
        return (DrawerLayout) findViewById(R.id.map_layout);
    }

    @Override
    protected int getLayoutid() {
        return R.layout.activity_maps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(getLayoutid());

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);

        }

        this.mLocationAsker = new LocationSettingAsker(this); // Start locationpemission process
        mLocationAsker.askToChangeSettings(); // Ask user to turn on location

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map_linear_layout, mMapFragment);

        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);

        this.mIconFactory = new IconGenerator(this); // Start a factory for custom icons

    }


    @Override
    protected void onStart() {
        super.onStart();
        bindToService();
        mBroadCastRecieveUtility.registerForBroadcasts();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindFromService();
    }


    //https://developers.google.com/maps/documentation/android-api/current-place-tutorial#location-permission
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationAsker.havePermission()) {

                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                mLocationAsker.askForPermission(new PermissionRequestCallback() {
                    @Override
                    public void onPermissionGranted() { //TODO Should we not always let the service askToChangeSettings for permission?
                        updateLocationUI();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        updateMap();

        mMap.setOnPoiClickListener(this);
        mMap.setOnMarkerClickListener(this);

        Marker mBrisbane = mMap.addMarker(new MarkerOptions()
                .position(BRISBANE)
                .title("Brisbane")
                .snippet("The Bane of Bris"));
        mBrisbane.setTag(0);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                updateMap();

            }
        });

    }


    private void updateMap() {

                updateLocationUI();

                if(mBound) {
                    mService.getDeviceLocation();
                }
    }

    private void addIcon(IconGenerator iconFactory, CharSequence text, LatLng position) {
        MarkerOptions markerOptions = new MarkerOptions().
                icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("LORT"))).
                position(position).
                title("HEJ").
                snippet("HEJ HEJ").
                anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        mMap.addMarker(markerOptions);
    }


    @Override
    public void onPoiClick(PointOfInterest poi) {
        Toast.makeText(getApplicationContext(), "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();

    }


    private void centerView() {
        if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        }
    }

    //https://developers.google.com/maps/documentation/android-api/marker#handle_marker_events
    @Override
    public boolean onMarkerClick(Marker marker) {


        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).

        if(mBound) {
            mService.getDeviceLocation();
        }

        return false;
    }


    /**
     * Storage stuff
     **/

    @Override
    public void onDataChanged(String value) {

    }

    @Override
    public void getImage(Uri downloadUrl) {

    }

    @Override
    public void onNewIssue(CommunityIssue issue) {

    }

    @Override
    public void imageDownloaded(CommunityIssue issue) {

        Bitmap issuebitmap = issue.issueImage.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        issuebitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bitmap factory = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        mBitmap = Bitmap.createScaledBitmap(factory, 120, 120, false);

        Toast.makeText(getApplicationContext(), "IMAGE LOADED", Toast.LENGTH_LONG).show();
    }

    /**
     * Service stuff
     **/

    private void bindToService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        if (mBound == false) {
            this.mConnection = createNewServiceConnection();
            final boolean b = bindService(new Intent(this,
                    LocationCommunityService.class), mConnection, Context.BIND_AUTO_CREATE);

            Toast.makeText(getApplicationContext(), "HEJ HEJ", Toast.LENGTH_LONG).show();
        }
    }

    //https://developers.google.com/maps/documentation/android-api/current-place-tutorial#location-permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mLocationAsker.onResult(requestCode, permissions, grantResults)) {
            updateLocationUI();
        }
    }

    void unbindFromService() {
        if (mBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection createNewServiceConnection() {

        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                mService = ((LocationCommunityService.LocalBinder) service).getService();

                mBound = true;


                mService.getDeviceLocation();

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mBound = false;
                mService = null;
            }

            @Override
            public void onBindingDied(ComponentName name) {
                mBound = false;
            }
        };
    }

    
    @Override
    public void locationIncoming(Location location) {
        mLastKnownLocation = location;

        if(mFirstLocation) {
            centerView();
            mFirstLocation = false;
        }
    }
}
