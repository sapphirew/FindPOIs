package com.example.wanghao.lbsfinal;


import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.location.*;

import android.location.Location;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;

public class MapsActivity extends
        ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    private Location mCurrentLocation;
    private LocationRequest mLocationRequest;
    private LocationManager locationManager;
    protected Boolean mRequestingLocationUpdates = true;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    public  float minDistance = 10;
    public long minTime = 1;


    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

//    private AddressResultReceiver mResultReceiver;
    List<Geofence> mGeofenceList;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        buildGoogleApiClient();

        locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                minTime,
                minDistance, new LocationListener());
        mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        mCurrentLocation = new Location("new");
//        mCurrentLocation.setLatitude(40.447406);
//        mCurrentLocation.setLongitude(-79.952657);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        buildGoogleApiClient();
        checkDistance(mCurrentLocation, 50);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16));
         //Create a GoogleApiClient instance

        createLocationRequest();
        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

    }

    public void checkDistance(Location currentLocation, int distance){
        String FINDPOI = "Find POI!";
        Location iSchool = new Location("iSchool");
        iSchool.setLatitude(40.447406);
        iSchool.setLongitude(-79.952656);
        if(currentLocation.distanceTo(iSchool)<=distance){
            sendNotification(FINDPOI,"iSchool is around you","135 North Bellefield Avenue");
        }

        //WPU 40.4434019, -79.954831
        Location WPU = new Location("WPU");
        WPU.setLatitude(40.4434019);
        WPU.setLongitude(-79.954831);
        if(currentLocation.distanceTo(WPU)<=distance){
            sendNotification(FINDPOI,"William Pitt Union is around you","3959 Fifth Ave");
        }

        Location Wesley = new Location("Wesley");
        Wesley.setLatitude(40.441643);
        Wesley.setLongitude(-79.953818);
        if(currentLocation.distanceTo(Wesley)<=distance){
            sendNotification(FINDPOI,"Wesley W. Posvar Hall is around you","230 S Bouquet St");
        }

        Location Chevron = new Location("Chevron");
        Chevron.setLatitude(40.445727);
        Chevron.setLongitude(-79.957651);
        if(currentLocation.distanceTo(Chevron)<=distance){
            sendNotification(FINDPOI,"Chevron Science Center is around you","219 Parkman Avenue");
        }

        Location Bellefield = new Location("Bellefield Hall");
        Bellefield.setLatitude(40.4454493);
        Bellefield.setLongitude(-79.9506596);
        if(currentLocation.distanceTo(Bellefield)<=distance){
            sendNotification(FINDPOI,"Bellefield Hall is around you","315 South Bellefield Ave");
        }

        Location Scaife = new Location("Scaife Hall");
        Scaife.setLatitude(40.443108);
        Scaife.setLongitude(-79.9613592);
        if(currentLocation.distanceTo(Scaife)<=distance){
            sendNotification(FINDPOI,"Scaife Hall is around you","Alan Magee Scaife Hall");
        }

        Location Hillman = new Location("Hillman Library");
        Hillman.setLatitude(40.442603);
        Hillman.setLongitude(-79.954155);
        if(currentLocation.distanceTo(Hillman)<=distance){
            sendNotification(FINDPOI,"Hillman Library is around you","3960 Forbes Ave");
        }

        Location Cathedral = new Location("Cathedral of Learning");
        Cathedral.setLatitude(40.444294);
        Cathedral.setLongitude(-79.953204);
        if(currentLocation.distanceTo(Cathedral)<=distance){
            sendNotification(FINDPOI,"Cathedral of Learning is around you","4200 Fifth Ave");
        }

        Location Sutherland = new Location("Sutherland Hall");
        Sutherland.setLatitude(40.4453141);
        Sutherland.setLongitude(-79.9616722);
        if(currentLocation.distanceTo(Sutherland)<=distance){
            sendNotification(FINDPOI,"Sutherland Hall is around you","Panther Hall");
        }

        Location Mellon = new Location("Mellon Institute.");
        Mellon.setLatitude(40.44615);
        Mellon.setLongitude(-79.951045);
        if(currentLocation.distanceTo(Mellon)<=distance){
            sendNotification(FINDPOI,"Mellon Institute is around you","4400 Fifth Ave");
        }
    }
    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
//                new LatLng(40.4434019,-79.954831), 16));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()), 16));

        mMap.setMyLocationEnabled(true);

        // You can customize the marker image using images bundled with
        // your app, or dynamically generated bitmaps.
        Marker wpu = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.4434019, -79.954831)).title("William Pitt Union"));

        Marker iSchool = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.447406,-79.952656)).title("iSchool"));

        Marker Wesley = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.441643,-79.953818)).title("Wesley W. Posvar Hall"));

        Marker Chevron = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.445727,-79.957651)).title("Chevron Science Center"));

        Marker Bellefield = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.4454493,-79.9506596)).title("Bellefield Hall"));

        Marker Scaife = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.443108,-79.9613592)).title("Scaife Hall"));

        Marker Hillman = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.442603, -79.954155)).title("Hillman Library"));

        Marker Cathedral = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.444294, -79.953204)).title("Cathedral of Learning"));

        Marker Sutherland = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.4453141, -79.9616722)).title("Sutherland Hall"));

        Marker Mellon = mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(100))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(40.44615, -79.951045)).title("Mellon Institute."));

    }

    private void updateUI() {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 16));
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateUI();
        checkDistance(mCurrentLocation, 200);

    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        public void sendNotification (String s1, String s2, String s3){

            /** Create an intent that will be fired when the user clicks the notification.
             * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
             * notification service can fire it on our behalf.
             */
            Intent intent = new Intent(this,
                    MapsActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            /**
             * Use NotificationCompat.Builder to set up our notification.
             */
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            /** Set the icon that will appear in the notification bar. This icon also appears
             * in the lower right hand corner of the notification itself.
             *
             * Important note: although you can use any drawable as the small icon, Android
             * design guidelines state that the icon should be simple and monochrome. Full-color
             * bitmaps or busy images don't render well on smaller screens and can end up
             * confusing the user.
             */
            builder.setSmallIcon(R.drawable.ic_launcher);

            // Set the intent that will fire when the user taps the notification.
            builder.setContentIntent(pendingIntent);

            // Set the notification to auto-cancel. This means that the notification will disappear
            // after the user taps it, rather than remaining until it's explicitly dismissed.
            builder.setAutoCancel(true);

            /**
             *Build the notification's appearance.
             * Set the large icon, which appears on the left of the notification. In this
             * sample we'll set the large icon to be the same as our app icon. The app icon is a
             * reasonable default if you don't have anything more compelling to use as an icon.
             */
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));

            /**
             * Set the text of the notification. This sample sets the three most commononly used
             * text areas:
             * 1. The content title, which appears in large type at the top of the notification
             * 2. The content text, which appears in smaller text below the title
             * 3. The subtext, which appears under the text on newer devices. Devices running
             *    versions of Android prior to 4.2 will ignore this field, so don't use it for
             *    anything vital!
             */
            builder.setContentTitle(s1);
            builder.setContentText(s2);
            builder.setSubText(s3);


            /**
             * Send the notification. This will immediately display the notification icon in the
             * notification bar.
             */
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            notificationManager.notify(1, builder.build());
        }
    }
}
