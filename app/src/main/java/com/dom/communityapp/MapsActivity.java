package com.dom.communityapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import com.dom.communityapp.location.BroadCastReceiveUitility;
import com.dom.communityapp.location.LocationListener;
import com.dom.communityapp.models.CommunityIssue;
import com.dom.communityapp.permisssion.LocationSettingAsker;
import com.dom.communityapp.permisssion.PermissionRequestCallback;
import com.dom.communityapp.storage.FirebaseDatabaseStorageService;
import com.dom.communityapp.storage.IssueLocationListener;
import com.dom.communityapp.storage.IssueResolver;
import com.dom.communityapp.ui.InfoWindowAdapterManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import butterknife.ButterKnife;

public class MapsActivity extends AbstractNavigation implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, LocationListener, IssueLocationListener, IssueResolver {


    //Map related stuff
    private static final float DEFAULT_ZOOM = 15;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final String KEY_LOCATION = "location";
    private static final String KEY_MAP = "";
    private final BroadCastReceiveUitility mBroadCastRecieveUtility;
    private GoogleMap mMap;
    private Location mLastKnownLocation;
    private MapFragment mMapFragment;
    private HashMap<String, CommunityIssue> mIssues;

    /* Services */
    //Storage service
    private FirebaseDatabaseStorageService mFirebaseStorageService;
    private boolean mStorageServiceBound = false;
    private ServiceConnection mStorageServiceConnection;

    //Location service
    private LocationCommunityService mLocationService;
    private boolean mLocationServiceBound;
    private ServiceConnection mLocationServiceConnection;


    private LocationSettingAsker mLocationAsker;
    private boolean mFirstLocation = true;


    private CommunityIssue lastIssue;
    private InfoWindowAdapterManager mAdapterManager;

    FragmentManager Manager = getFragmentManager();
    //ImageView detailsImage;


    public MapsActivity() {
        this.mBroadCastRecieveUtility = new BroadCastReceiveUitility(this);
        mIssues = new HashMap<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);

        }


        this.mLocationAsker = new LocationSettingAsker(this); // Start locationpemission process
        mLocationAsker.askToChangeSettings(null); // Ask user to turn on location


        mMapFragment = MapFragment.newInstance();

        FragmentTransaction fragmentTransaction = Manager.beginTransaction();
        fragmentTransaction.add(R.id.map_container, mMapFragment);

        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);

            /*
            unbindFromService();
            outState.putSerializable(CURRENT_CITY_KEY, mCurrentCity);
            outState.putSerializable(EXTRA_CITY_NAME, mCityName);
            outState.putSerializable(EXTRA_CITY_ID, mCityID);
             */

            super.onSaveInstanceState(outState);
        }
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
    protected void onStart() {
        super.onStart();
        bindToLocationService();
        bindToStorageService();
        mBroadCastRecieveUtility.registerForBroadcasts();
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindFromLocationService();
        unbindFromStorageService();
    }

    //https://developers.google.com/maps/documentation/android-api/current-place-tutorial#location-permission
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        mAdapterManager = new InfoWindowAdapterManager(this, this);
        mMap.setInfoWindowAdapter(mAdapterManager);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                mAdapterManager.clickedInfoWindow(marker);
            }
        });

        updateMap();
    }

    @SuppressLint("MissingPermission")
    private void updateMap() {

        if (mLocationAsker.havePermission()) {

            if (mLocationServiceBound) {
                mLocationService.getDeviceLocation();
            }

            updateLocationUI();

        } else {

            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mLastKnownLocation = null;
            mLocationAsker.askForPermission(new PermissionRequestCallback() {
                @Override
                public void onPermissionGranted() {
                    mFirstLocation = true;
                    updateMap();
                }

                @Override
                public void onPermissionRefused() {
                    alertAndAsk();
                }

                @Override
                public boolean expirable() {
                    return false;
                }


            });
            mFirstLocation = true;
        }


    }

    private void alertAndAsk() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_location_permission_message)
                .setTitle(R.string.no_location_permission_title);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish(); // We cannot do anything abort the app
            }
        });


        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mLocationAsker.askForPermission(new PermissionRequestCallback() {
                    @Override
                    public void onPermissionGranted() {
                        updateMap();
                    }

                    @Override
                    public void onPermissionRefused() {
                        alertAndAsk();
                    }

                    @Override
                    public boolean expirable() {
                        return false;
                    }
                });
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void centerView() {
        if (mLastKnownLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(mLastKnownLocation.getLatitude(),
                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
        }
    }


    /**
     * Firebase service callbacks - and storage stuff
     **/

    private void startLocationListening(Location location) {
        mFirebaseStorageService.addLocationListener(this);
        mFirebaseStorageService.addLocationQuery(location, 2);

    }

    @Override
    public void issueRemoved(CommunityIssue issue) {
        mIssues.remove(issue.getFirebaseID());
        mAdapterManager.removeAdapterByIssue(issue);
    }

    @Override
    public void newIssue(CommunityIssue issue) {
        mIssues.put(issue.getFirebaseID(), issue);
        addIcon(issue);
    }

    @Override
    public void movedIssue(CommunityIssue issue) {
        moveIcon(issue);
    }

    @Override
    public void onImageDownloaded(CommunityIssue incomingissue) {


        CommunityIssue newissue = null;

        if (mIssues.containsKey(incomingissue.getFirebaseID())) {
            newissue = mIssues.get(incomingissue.getFirebaseID());

            Bitmap issuebitmap = incomingissue.getIssueImage().getBitmap();
            newissue.getIssueImage().setBitmap(issuebitmap);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            issuebitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            Bitmap factory = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            Bitmap croppedbitmap = Bitmap.createScaledBitmap(factory, 120, 120, false);
        } else {

            //TODO ADD to halløj
        }


    }


    private Marker createMarker(CommunityIssue issue) {
        MarkerOptions markerOptions = new MarkerOptions().
                position(issue.getCoordinate());

        BitmapDescriptor bitmapdescriptor = BitmapDescriptorFactory.fromResource(issue.getIcon());
        markerOptions.icon(bitmapdescriptor);
        Marker currentMarker = mMap.addMarker(markerOptions);

        return currentMarker;
    }

    private void moveIcon(CommunityIssue issue) {
        Marker marker = createMarker(issue);
        mAdapterManager.changeMarker(issue, marker);
    }

    /**
     * Firebase Service stuff
     */

    private void bindToStorageService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        if (!mStorageServiceBound) {
            this.mStorageServiceConnection = createNewStorageServiceConnection();
            bindService(new Intent(this,
                    FirebaseDatabaseStorageService.class), mStorageServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }


    void unbindFromStorageService() {
        if (mStorageServiceBound) {
            // Detach our existing connection.
            unbindService(mStorageServiceConnection);
            mStorageServiceBound = false;
        }
    }

    private ServiceConnection createNewStorageServiceConnection() {

        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                mFirebaseStorageService = ((FirebaseDatabaseStorageService.LocalBinder) service).getService();

                mStorageServiceBound = true;

                addListener();

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mStorageServiceBound = false;
                mFirebaseStorageService = null;
            }

            @Override
            public void onBindingDied(ComponentName name) {
                mStorageServiceBound = false;
            }
        };
    }

    private void addListener() {
        if(mLastKnownLocation != null) {
            startLocationListening(mLastKnownLocation);
        }
    }


    /**
     * Location Service stuff
     **/

    private void bindToLocationService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        if (!mLocationServiceBound) {
            this.mLocationServiceConnection = createNewLocationServiceConnection();
            final boolean b = bindService(new Intent(this,
                    LocationCommunityService.class), mLocationServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    void unbindFromLocationService() {
        if (mLocationServiceBound) {
            // Detach our existing connection.
            unbindService(mLocationServiceConnection);
            mLocationServiceBound = false;
        }
    }

    //https://developers.google.com/maps/documentation/android-api/current-place-tutorial#location-permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mLocationAsker.onResult(requestCode, permissions, grantResults)) {
            updateLocationUI();
        }
    }

    private void addIcon(CommunityIssue issue) {
        if(issue != null) {
            Marker marker = createMarker(issue);
            this.mAdapterManager.addAdapter(marker, issue);
        }
    }

    private ServiceConnection createNewLocationServiceConnection() {

        return new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                mLocationService = ((LocationCommunityService.LocalBinder) service).getService();

                mLocationServiceBound = true;

                mFirstLocation = true;
                if (mLocationAsker.havePermission()) mLocationService.getDeviceLocation();

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                mLocationServiceBound = false;
                mLocationService = null;
            }

            @Override
            public void onBindingDied(ComponentName name) {
                mLocationServiceBound = false;
            }
        };
    }

    @Override
    public void locationIncoming(Location location) {
        mLastKnownLocation = location;

        if (this.mFirstLocation) {
            centerView();
            if(mStorageServiceBound) {
                startLocationListening(location);
            }
            mFirstLocation = false;
        }
    }


    @Override
    public void resolve(CommunityIssue issue) {
        mIssues.remove(issue);
        mAdapterManager.removeAdapterByIssue(issue);
        if(mStorageServiceBound) {
            mFirebaseStorageService.removeIssue(issue);
        }
    }
}
