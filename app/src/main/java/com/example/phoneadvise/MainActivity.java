package com.example.phoneadvise;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private String latitud;
    private String longitud;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String KEY_NAME = "telNumero";
    private SharedPreferences sharedPreferences;
    EditText etnumero;
    Button btnnumero;
    /**
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar toolbar=findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //PhoneCallListener phoneListener= new PhoneCallListener();
        //TelephonyManager telephonyManager=(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        //FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab) ;
    https://www.google.com/maps/search/20.1354413+-98.3806732

    }
    */
private static final int PERMISSION_REQUEST_LOCATION = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etnumero=findViewById(R.id.etnumber);
        btnnumero=findViewById(R.id.btnnumero);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        btnnumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number_provided=etnumero.getText().toString();
                if(!number_provided.isEmpty()){
                    setNumero(number_provided);
                    etnumero.setText(getNumero());
                }
            }
        });

        //sendSMS("7752655987","Hola");



        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) !=PackageManager.PERMISSION_GRANTED) {
            Log.d("SHIPIT","NOT HAVE PERMISSION FOR PHONE_STATE");
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},0);} else Log.d("SHIPIT","HAVE THE PHONE STATE PERMISSIONS");


        if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SHIPIT","NOT HAVE PERMISSION FOR CALL_LOG");
            requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG},0);} else Log.d("SHIPIT","HAVE THE READ CALL LOG");


        if (checkSelfPermission(Manifest.permission.WRITE_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SHIPIT","NOT HAVE PERMISSION FOR CALL_LOG");
            requestPermissions(new String[]{Manifest.permission.WRITE_CALL_LOG},0);} else Log.d("SHIPIT","HAVE THE Write CALL LOG");
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SHIPIT","NOT HAVE PERMISSION FOR CALL_LOG");
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE},0);} else Log.d("SHIPIT","HAVE THE Write CALL LOG");


        if (checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SHIPIT","NOT HAVE PERMISSION FOR CALL_LOG");
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_NUMBERS},0);} else Log.d("SHIPIT","HAVE THE Write CALL LOG");




        PhoneCallListener listenerPhone=new PhoneCallListener();
        TelephonyManager telephonyManager=(TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    double altitude = location.getAltitude();

                    // Utiliza los valores de latitud, longitud y altitud como necesites
                    latitud=latitude+"";
                    longitud=longitude+"";
                    listenerPhone.latitud2 =latitude+"";
                    listenerPhone.longitud2=longitude+"";
                    //Toast.makeText(MainActivity.this, "Latitud: " + latitud + ", Longitud: " + longitud + ", Altitud: " + altitude, Toast.LENGTH_SHORT).show();
                    System.out.println("https://www.google.com/maps/search/"+latitud+"+"+longitud);
                }
            }
        };
    }

    private void setNumero(String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, name);
        editor.apply();
    }

    private String getNumero() {
        return sharedPreferences.getString(KEY_NAME, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // Intervalo de actualización de la ubicación en milisegundos
        locationRequest.setFastestInterval(2000); // Intervalo más rápido de actualización en milisegundos
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Se requiere permiso de ubicación", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "Permiso denegado para enviar SMS.", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(getApplicationContext(), "Permiso denegado para enviar SMS.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void sendSMS(String phoneNumber, String message) {


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS enviado.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error al enviar el SMS.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }






    public class PhoneCallListener extends PhoneStateListener  {
        private boolean isRinging=false;
        private String number="";
        private long callStartTime;
        public String latitud2;
        public String longitud2;

        private Timer callTimer;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            System.out.println(phoneNumber+"asfadf");
            super.onCallStateChanged(state, phoneNumber);


            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    // La llamada está inactiva (no hay llamadas entrantes o salientes)
                    if (isRinging) {
                        // Verificar si la llamada entrante no ha sido contestada
                        long callDuration = System.currentTimeMillis() - callStartTime;
                        if (callDuration > 18000) {
                            // La llamada no ha sido contestada dentro del límite de tiempo
                            // Realiza la acción necesaria
                            System.out.println("No contestado");
                            System.out.println(this.latitud2);
                            System.out.println(phoneNumber+"");
                            if((phoneNumber+"").equals(getNumero())){
                                sendSMS(phoneNumber+"","https://www.google.com/maps/search/"+latitud2+"+"+longitud2);
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+phoneNumber+""));//change the number
                                startActivity(callIntent);

                            }


                        }
                    }
                    // Reinicia las variables para la siguiente llamada
                    isRinging = false;
                    callStartTime = 0;
                    break;

                case TelephonyManager.CALL_STATE_RINGING:
                    // Hay una llamada entrante
                    isRinging = true;
                    number=phoneNumber;
                    callStartTime = System.currentTimeMillis();
                    startCallTimer();
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // Hay una llamada saliente o en progreso
                    stopCallTimer();
                    break;
            }
        }

        private void startCallTimer() {
            callTimer = new Timer();
            callTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // El temporizador ha expirado, la llamada no ha sido contestada
                    // Realiza la acción necesaria
                    System.out.println("No contestado");
                    System.out.println(latitud2);
                }
            }, 18000);
        }

        private void stopCallTimer() {
            if (callTimer != null) {
                callTimer.cancel();
                callTimer = null;
            }
        }

        private void sendSMS(String phoneNumber, String message) {


            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                //Toast.makeText(getApplicationContext(), "SMS enviado.", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), "Error al enviar el SMS.", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

    }
}