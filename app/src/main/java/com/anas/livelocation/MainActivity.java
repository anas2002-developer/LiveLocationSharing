package com.anas.livelocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anas.livelocation.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // Define a button object for the SOS button
    Button sosButton;

    // Define a String object to hold the Google Maps link
    String mapsLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the SOS button
        sosButton=findViewById(R.id.sos_button);

        // Set a click listener for the SOS button
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Generate a Google Maps link for the user's current location
                generateMapsLink();

                // Start location updates
                startLocationUpdates();
            }
        });
    }

    // Define a method for generating the Google Maps link
    private void generateMapsLink() {

        // Check if the user has granted permission to access their location
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to access the user's location
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Get the user's current location
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Generate a Google Maps link for the user's location
                            mapsLink = "https://www.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
                        }
                    });
        }
    }

    // Define a method for starting location updates
    private void startLocationUpdates() {
        // Create a location request object
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(15000) // Update interval in milliseconds
                .setFastestInterval(1000); // Fastest update interval in milliseconds

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},1);
        }
        // Check if the user has granted permission to access their location
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permission to access the user's location
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            // Create a location callback object
            LocationCallback locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // Get the user's current location
                    Location userLocation = locationResult.getLastLocation();

                    // Send the location and maps link to the police
                    sendSOSCall(userLocation, mapsLink);
                }
            };

            // Request location updates
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }
    private void sendSOSCall(Location userLocation, String mapsLink) {
        // Define a Intent for dialing emergency services number






        String Phone = "7060997580";
        Intent i = new Intent(Intent.ACTION_SEND);
        String locationString = "Latitude: " + userLocation.getLatitude() + "\nLongitude: " + userLocation.getLongitude();
        i.putExtra("location", locationString);
        i.putExtra("maps_link", mapsLink);


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Phone,null,mapsLink,null,null);
            Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Message not sent", Toast.LENGTH_SHORT).show();
        }
    }
}