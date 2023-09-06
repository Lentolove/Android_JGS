package com.tsp.learn.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tsp.learn.databinding.CameraActivityBinding;

import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    private CameraActivityBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = CameraActivityBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mBinding.cameraBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //action is capture
            File dir = new File(getCacheDir().toString(), "pictures");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File currentImageFile = new File(dir, System.currentTimeMillis() + ".jpg");
            if (!currentImageFile.exists()) {
                try {
                    currentImageFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
            startActivityForResult(intent, DEFAULT_KEYS_DIALER);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == DEFAULT_KEYS_DIALER){
            if (data != null){
                Toast.makeText(this, "数据", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
