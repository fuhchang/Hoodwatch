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
import android.graphics.Color;
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
import android.widget.RelativeLayout;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    boolean checkImgExit = false;
    int maxcount;
    String username;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ArrayList<Flare> list;
    private DatabaseReference mDatabase;
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
        maxcount = bundle.getInt("maxcount");
        username = bundle.getString("username");
        imgName = "Image" + (maxcount + 1);
        // get reference to 'users' node


        // store app title to 'app_title' node

        RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_create_flare_main);
        rl.setBackgroundColor(Color.WHITE);
        list = (ArrayList<Flare>) getIntent().getSerializableExtra("map");
        Log.v("array size", Integer.toString(list.size()));
        LinearLayout inputLayout = (LinearLayout) findViewById(R.id.input);
        inputLayout.bringToFront();
        EditText txt = (EditText) findViewById(R.id.msg);
        if (ActivityCompat.checkSelfPermission(CreateFlareMain.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateFlareMain.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
        im = (ImageView) findViewById(R.id.upload);
        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
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
        // BootstrapCircleThumbnail thumb = (BootstrapCircleThumbnail) findViewById(R.id.thumb);
        ImageView thumb = (ImageView) findViewById(R.id.thumb);
        thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("submit", "submitting1");
                createFlare();
            }
        });
        EditText editText = (EditText) findViewById(R.id.msg);
        editText.setTextColor(Color.BLACK);
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
        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                Log.d("geo", "connected to Google api client ");
                boolean check = false;
                if (ActivityCompat.checkSelfPermission(CreateFlareMain.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CreateFlareMain.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(CreateFlareMain.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateFlareMain.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                        check = true;
                    } else {
                        check = false;
                    }
                    if (ActivityCompat.checkSelfPermission(CreateFlareMain.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CreateFlareMain.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                        check = true;
                    } else {
                        check = false;
                    }

                    if (check == false) {
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
        }).addApi(AppIndex.API).build();

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
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        client.reconnect();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    private void startLocationMonitoring() {
        Log.d("geo", "startLocation monitoring");
        LocationRequest lr = LocationRequest.create().setInterval(10000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, lr, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
            }
        });


    }

    private void createFlare() {
//        try {
//            String dir = "/data/user/0/com.example.hoodwatch.hoodwatch/app_imageDir";
//            Log.d("file", dir);
//            String fileName = "flares.csv";
//            String filePath = dir + File.separator + fileName;
//            FileWriter write = new FileWriter(filePath);
//            for(int i=0;i<list.size();i++){
//                write.append(list.get(i).getFlareID());
//                write.append(",");
//                write.append(list.get(i).getImagename());
//                write.append(",");
//                write.append(list.get(i).getFlareText());
//                write.append(",");
//                write.append(list.get(i).getClassification());
//                write.append(",");
//                write.append(list.get(i).getUserName());
//                write.append(",");
//                write.append(Double.toString(list.get(i).getLatitude()));
//                write.append(",");
//                write.append(Double.toString(list.get(i).getLongtitude()));
//                write.append(",");
//                write.append(Long.toString(list.get(i).getTime()));
//                write.append("\n");
//            }
//            write.append(imgName);
//            write.append(",");
//            write.append(imgName);
//            write.append(",");
//           EditText text = (EditText) findViewById(R.id.msg);
//            write.append(text.getText());
//            write.append(",");
//            write.append(type);
//            write.append(",");
//            write.append(username);
//            write.append(",");
//            write.append(Double.toString(lat));
//            write.append(",");
//            write.append(Double.toString(lng));
//            write.append(",");
//            SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
//            Date date = f.parse(f.format(new Date()));
//            long millis = date.getTime();
//            write.append(Long.toString(millis));
//            write.append("\n");
//            write.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Flare flare = new Flare();
        flare.setFlareID(imgName);
        flare.setImagename(imgName);
        EditText text = (EditText) findViewById(R.id.msg);
        flare.setFlareText(text.getText().toString());
        flare.setUserName(username);
        flare.setLatitude(lat);
        flare.setLongtitude(lng);
        SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        Date date = null;
        try {
            date = f.parse(f.format(new Date()));
            long millis = date.getTime();
            flare.setTime(millis);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        flare.setType(type);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("event").child(imgName).setValue(flare);
        Intent intent = new Intent(CreateFlareMain.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CreateFlareMain.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("CreateFlareMain Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
