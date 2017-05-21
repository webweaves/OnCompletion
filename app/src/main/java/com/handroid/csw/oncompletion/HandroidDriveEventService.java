package com.handroid.csw.oncompletion;

/**
 * Created by csw on 20/05/17.
 */


import android.util.Log;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;

/**
 * Created by csw on 31/01/17.
 */

public class HandroidDriveEventService extends DriveEventService {

    private String TAG = "HandroidDriveEventSrv";

    @Override
    public void onCompletion(CompletionEvent event) {
        Log.d(TAG, "Action completed with status: " + event.getStatus());
        //Toast.makeText(context, "Action completed with status: " + event.getStatus(), Toast.LENGTH_LONG).show();
        System.out.println("EVENT_"+event.getDriveId().getResourceId());


        DriveId driveId = event.getDriveId();
        Log.d(TAG, "File ResourceId: " + driveId.getResourceId());
        DriveId folderDriveId = MainActivity.getParentID(driveId);



        if (folderDriveId.getResourceId() != null && folderDriveId.getResourceId() != null)
            Log.d(TAG, "Folder ResourceId: " + folderDriveId.getResourceId());



        event.dismiss();
    }

}
