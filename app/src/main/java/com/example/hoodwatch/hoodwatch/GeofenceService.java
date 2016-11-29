package com.example.hoodwatch.hoodwatch;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static android.R.id.list;
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
        int mNotificationId = 001;
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        Log.d("geo fence","loading intent handler");
        if(event.hasError()){
            Log.d(TAG,"Error on intent haha");
        }else{
            int transition = event.getGeofenceTransition();
            List<Geofence> geoList = event.getTriggeringGeofences();
            Geofence geofence = geoList.get(0);
            String requestID = geofence.getRequestId();
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Hazard notification");
            if(transition == Geofence.GEOFENCE_TRANSITION_ENTER){
                mBuilder.setContentText("Becareful!!! There is a hazard Near you");
                Log.d("geo fence", "Entering geofence - " + requestID);
            }
            Intent resultIntent = new Intent(this, GeofenceService.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setContentIntent(contentIntent);
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

        }
    }
}
