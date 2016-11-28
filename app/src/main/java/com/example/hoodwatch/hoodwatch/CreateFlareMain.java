package com.example.hoodwatch.hoodwatch;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.bitmap;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class CreateFlareMain extends AppCompatActivity {
    ImageView im;
    String path;
    Uri imageUri;
    double lat;
    double lng;
    String imgName;
    EditText txt;
    String type;
    String name;
    boolean checkImgExit = false;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flare_main);
        TypefaceProvider.registerDefaultIconSets();
        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");
        LinearLayout inputLayout = (LinearLayout) findViewById(R.id.input);
        inputLayout.bringToFront();
        EditText txt =(EditText) findViewById(R.id.msg);
        im = (ImageView) findViewById(R.id.upload);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                if (ActivityCompat.checkSelfPermission(CreateFlareMain.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateFlareMain.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                }
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } else {
                    Log.d("camera", "no camera");
                }
            }
        });
        BootstrapCircleThumbnail thumb = (BootstrapCircleThumbnail) findViewById(R.id.thumb);
        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("submit", "submitting1");
                createFlare();
            }
        });
        EditText editText = (EditText) findViewById(R.id.msg);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    createFlare();
                }
                return false;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Log.d("geo", "connected to Google api client ");
                boolean check = false;
                if (ActivityCompat.checkSelfPermission(CreateFlareMain.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateFlareMain.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.checkSelfPermission(CreateFlareMain.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(CreateFlareMain.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
                        check = true;
                    }else{
                        check =false;
                    }
                    if(ActivityCompat.checkSelfPermission(CreateFlareMain.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(CreateFlareMain.this,new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},0);
                        check = true;
                    }else{
                        check =false;
                    }

                    if(check==false){
                        return;
                    }

                }
                startLocationMonitoring();
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("geo", "suspended connected to Google api client ");
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.d("geo", "fail tp connect to Google api client - " + connectionResult.getErrorMessage());
            }
        }).build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //im.setImageBitmap(imageBitmap);
            Bitmap imageBitmap = null;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(
                        getContentResolver(), imageUri);
                imgName = "Image";
                saveToInternalStorage(imageBitmap, imgName);
                loadImageFromStorage(path);
                checkImgExit = true;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String imgName) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        path = directory.getPath();
        File mypath = new File(directory, imgName + ".jpg");
        Log.d("path", path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            Log.d("image", "The path is " + path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path) {

        try {
            File f = new File(path, imgName+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img = (ImageView) findViewById(R.id.upload);
            img.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        client.reconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        client.disconnect();
    }

    private void startLocationMonitoring() {
        Log.d("geo", "startLocation monitoring");
        LocationRequest lr = LocationRequest.create().setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, lr, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(CreateFlareMain.this,location.getLatitude()+ " " + location.getLongitude(),Toast.LENGTH_SHORT).show();
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        });


    }

    private void createFlare(){
        try {
            String dir = "/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir";
            Log.d("file", dir);
            String fileName = "flares.csv";
            String filePath = dir + File.separator + fileName;
            FileWriter write = new FileWriter(filePath);
            write.append("\n");
            write.append(imgName);
            write.append(",");
            if(checkImgExit) {
                write.append(imgName);
            }else{
                write.append("");
                write.append(",");
            }
           EditText text = (EditText) findViewById(R.id.msg);
            String check = text.getText().toString();
            if(check.isEmpty()){
                write.append("");
                write.append(",");
            }else {
                write.append(",");
                write.append(text.getText());
                write.append(",");
            }
            write.append(type);
            write.append(",");
            write.append(name);
            write.append(",");
            write.append(Double.toString(lat));
            write.append(",");
            write.append(Double.toString(lng));
            write.append(",");
            SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
            Date date = f.parse(f.format(new Date()));
            long millis = date.getTime();
            write.append(Long.toString(millis));
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(CreateFlareMain.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
