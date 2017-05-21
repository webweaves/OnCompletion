package com.handroid.csw.oncompletion;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;

public class MainActivity extends BaseDemoActivity {

    private DriveId mFolderDriveId;

    private static GoogleApiClient mGoogleApiClient;

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_OPENER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Select a folder", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                IntentSender intentSender = Drive.DriveApi
                        .newOpenFileActivityBuilder()
                        .setMimeType(new String[]{DriveFolder.MIME_TYPE})
                        .build(getGoogleApiClient());

                mGoogleApiClient = getGoogleApiClient();

                try {
                    startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.w(TAG, "Unable to send intent", e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    showMessage("Selected folder's ID: " + driveId);

                    mFolderDriveId = driveId;

                    Drive.DriveApi.newDriveContents(getGoogleApiClient())
                            .setResultCallback(driveContentsCallback);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    DriveFolder folder = mFolderDriveId.asDriveFolder();
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("New file")
                            .setMimeType("text/plain")
                            .setStarred(true).build();

                    ExecutionOptions executionOptions = new ExecutionOptions.Builder()
                            .setNotifyOnCompletion(true)
                            .setTrackingTag("123")
                            .build();

                    folder.createFile(getGoogleApiClient(), changeSet, result.getDriveContents(), executionOptions)
                            .setResultCallback(fileCallback);
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback =
            new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file");
                        return;
                    }
                    showMessage("Created a file: " + result.getDriveFile().getDriveId());
                }
            };

    /**
     * grabs the parent folder resourceid
     * courtesy of http://stackoverflow.com/questions/38012719/get-google-drive-folders-resourceid
     * @param driveId
     * @return
     */
    public static DriveId getParentID(DriveId driveId) {
        MetadataBuffer mdb = null;
        DriveApi.MetadataBufferResult mbRslt = driveId.asDriveResource().listParents(mGoogleApiClient).await();
        if (mbRslt.getStatus().isSuccess()) try {
            mdb = mbRslt.getMetadataBuffer();
            if (mdb.getCount() > 0)
                return mdb.get(0).getDriveId();
        } catch (Exception e) { e.printStackTrace();}
        finally {
            if (mdb != null) mdb.close();
        }
        return null;
    }
}
