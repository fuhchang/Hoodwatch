package com.example.hoodwatch.hoodwatch;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by fuh_c on 20/11/2016.
 */

public class GeofenceService extends IntentService {
    public GeofenceService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        if(event.hasError()){
            Log.d(TAG,"Error on intent haha");
        }else{

            int transition = event.getGeofenceTransition();
            List<Geofence> geoList = event.getTriggeringGeofences();
            Geofence gf = geoList.get(0);
            String requestID = gf.getRequestId();
            Intent resultIntent = new Intent(this, GeofenceService.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(GeofenceService.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle("Notification about geofence");
            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){
                Log.d(TAG, "Entering geofence - " + requestID);
                mBuilder.setContentText("You are entering geofence");
            }else if(transition == Geofence.GEOFENCE_TRANSITION_EXIT){
                Log.d(TAG, "Exiting geofence - "+ requestID);
                mBuilder.setContentText("You are exiting geofence");
            }
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1,mBuilder.build());
        }
    }
}
