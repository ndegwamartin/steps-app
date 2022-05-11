/*
 * Copyright 2016. World Health Organization
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onaio.steps.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.onaio.steps.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import timber.log.Timber;

/**
 * Created by onamacuser on 12/11/2015.
 */
public class BackupLocationActivity extends AppCompatActivity {
    /**
     * The requestCode with which the storage access framework is triggered for input folder.
     */
    private static final int REQUEST_CODE_STORAGE_ACCESS_INPUT = 4;
    private static final String APP_SETTINGS = "STEPS SETTINGS";
    private static final String SD_CARD_PERM = "WRITE PERMISSIONS";
    SharedPreferences sharedPreferences;
    Button chooseBackupLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_backup_location);
        // Request permissions.
        sharedPreferences = getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
        chooseBackupLocationButton = (Button) findViewById(R.id.backup_location_button);
        chooseBackupLocationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                triggerStorageAccessFramework();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void triggerStorageAccessFramework() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_STORAGE_ACCESS_INPUT);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public final void onActivityResult(final int requestCode, final int resultCode, final Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == REQUEST_CODE_STORAGE_ACCESS_INPUT) {
            Uri treeUri = null;
            if (resultCode == Activity.RESULT_OK) {
                // Get Uri from Storage Access Framework.
                treeUri = resultData.getData();

                // Persist URI in shared preference so that you can use it later.
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SD_CARD_PERM, "GRANT_WRITE_ACCESS");
                editor.commit();

                // Persist access permissions.
                final int takeFlags = resultData.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                // noinspection ResourceType
                this.getContentResolver().takePersistableUriPermission(treeUri, takeFlags);

                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                // Create a new file and write into it
                DocumentFile stepDir = pickedDir.findFile("STEPS");
                if (stepDir == null) {
                    stepDir = pickedDir.createDirectory("STEPS");
                }

                if (stepDir != null) {
                    // Copy the CSV household file
                    copyFilesToStepsDir(stepDir, this.getFilesDir());

                    // Copies the ODK dir
                    File odkDir = new File(Environment.getExternalStorageDirectory() + "/odk");
                    DocumentFile stepsODKDir = stepDir.createDirectory("odk");
                    copyFilesToStepsDir(stepsODKDir, odkDir);
                } else {
                    Timber.e("Cannot copy files");
                }
            }
        }
        Intent toMenu = new Intent(this, HouseholdListActivity.class);
        startActivity(toMenu);
    }

    private void copyFilesToStepsDir(DocumentFile stepDir, File file) {
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                DocumentFile dir = stepDir.createDirectory(f.getName());
                copyFilesToStepsDir(dir, f);
            } else if (f.isFile()) {
                DocumentFile newFile = stepDir.createFile("", f.getName());
                try {
                    InputStream in = new FileInputStream(f);
                    OutputStream out = getContentResolver().openOutputStream(newFile.getUri());
                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
