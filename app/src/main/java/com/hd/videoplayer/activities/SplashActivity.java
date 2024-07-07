package com.hd.videoplayer.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.hd.videoplayer.R;
import com.hd.videoplayer.utils.Constants;
import com.tencent.mmkv.MMKV;

import java.nio.channels.FileChannel;
import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 123;
    private static final int REQUEST_PERMISSION_SETTINGS = 103;
    String[] storagePermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= 33) {
            storagePermissions = new String[]{android.Manifest.permission.READ_MEDIA_VIDEO, android.Manifest.permission.POST_NOTIFICATIONS};
        } else {
            if (Build.VERSION.SDK_INT >= 30) {
                storagePermissions = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};
            } else {
                storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            }
        }

        MMKV mmkv = MMKV.defaultMMKV();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!mmkv.decodeBool(Constants.KEY_IS_PERMISSION_GRANTED,false)) {
                    requestStoragePermission();
                    return;
                }
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Arrays.toString(storagePermissions)) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SplashActivity.this, storagePermissions, REQUEST_PERMISSION);
            return;
        }
        MMKV mmkv = MMKV.defaultMMKV();
        mmkv.encode(Constants.KEY_IS_PERMISSION_GRANTED,true);
        Log.d("Permission===", "Permission Granted");
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission===", "Permission Granted");

                MMKV mmkv = MMKV.defaultMMKV();
                mmkv.encode(Constants.KEY_IS_PERMISSION_GRANTED,true);

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                return;
            }
            Log.d("Permission===", "Permission Denied");
            AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
            dialog.setTitle(getResources().getString(R.string.strPermissionDialogTitle))
                    .setMessage(getResources().getString(R.string.strPermissionDialogMessage))
                    .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTINGS);
                        }
                    })
                    .setCancelable(false)
                    .create().show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTINGS) {
            startActivity(new Intent(SplashActivity.this, SplashActivity.class));
            finish();
        }
    }
}