package com.handroid.csw.oncompletion;

/**
 * Created by csw on 20/05/17.
 */

import android.util.Log;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;

public class CSWDriveEventService extends DriveEventService {

    private String TAG = "CSWDriveEventService";

    @Override
    public void onCompletion(CompletionEvent event) {
        Log.d(TAG, "Action completed with status: " + event.getStatus());
        DriveId driveId = event.getDriveId();
        Log.d(TAG, "File ResourceId: " + driveId.getResourceId());
        DriveId folderDriveId = MainActivity.getParentID(driveId);
        if (folderDriveId.getResourceId() != null && folderDriveId.getResourceId() != null) {
            Log.d(TAG, "Folder ResourceId: " + folderDriveId.getResourceId());
        }
        event.dismiss();
    }
}
