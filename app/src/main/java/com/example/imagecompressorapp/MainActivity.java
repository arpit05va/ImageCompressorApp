package com.example.imagecompressorapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import id.zelory.compressor.Compressor;

public class MainActivity extends AppCompatActivity {

    public static final int RESULT_IMAGE = 1;

    ImageView imgOriginal,imgCompress;
    TextView txtOriginal,textCompress,txtQuality;
    EditText txtHeight,txtWidth;
    SeekBar seekBar;
    Button btnPick,btnCompress;
    File originalImage,compressImage;
    private static String filepath;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/myAppCompressor");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        askPermission();
        imgOriginal = findViewById(R.id.imgOriginal);
        imgCompress = findViewById(R.id.imgCompress);
        txtOriginal = findViewById(R.id.txtOriginal);
        textCompress = findViewById(R.id.txtCompress);
        txtQuality = findViewById(R.id.txtQuality);
        txtHeight = findViewById(R.id.txtHeight);
        txtWidth = findViewById(R.id.txtWidth);
        seekBar = findViewById(R.id.seekQuality);
        btnPick = findViewById(R.id.btnPick);
        btnCompress = findViewById(R.id.btnCompresd);

        filepath = path.getAbsolutePath();

        if(!path.exists())
        {
            path.mkdirs();
        }

        // seekbar

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                txtQuality.setText("Quality: "+ i);
                seekBar.setMax(100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Pick image from gallery

        btnPick.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                openGallery();
            }
        });

         // compress function start

        btnCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quality = seekBar.getProgress();
                int width = Integer.parseInt(txtWidth.getText().toString());
                int height = Integer.parseInt(txtHeight.getText().toString());

                try {
                    compressImage = new Compressor(MainActivity.this)
                            .setMaxWidth(width)
                            .setMaxHeight(height)
                            .setQuality(quality)
                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                            .setDestinationDirectoryPath(filepath)
                            .compressToFile(originalImage);

                    File finalFile = new File(filepath,originalImage.getName());
                    Bitmap finalBitmap = BitmapFactory.decodeFile(finalFile.getAbsolutePath());
                    imgCompress.setImageBitmap(finalBitmap);
                    textCompress.setText("Size: "+Formatter.formatShortFileSize(MainActivity.this,finalFile.length()));

                   Toast.makeText(MainActivity.this, "Image Compressed && Saved!!", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error while Compress", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,RESULT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK)
        {
            btnCompress.setVisibility(View.VISIBLE);
            final Uri imageUri = data.getData();
            try {
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imgOriginal.setImageBitmap(selectedImage);
                originalImage = new File(imageUri.getPath().replace("raw/",""));
                txtOriginal.setText("Size"+ Formatter.formatShortFileSize(this,originalImage.length()));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void askPermission() {
       Dexter.withContext(this)
               .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                       Manifest.permission.WRITE_EXTERNAL_STORAGE)
               .withListener(new MultiplePermissionsListener() {
                   @Override
                   public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                   }

                   @Override
                   public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                       permissionToken.continuePermissionRequest();

                   }
               }).check();

    }
}














