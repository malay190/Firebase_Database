package com.example.firebase_database;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST =1;

    private Button mButtonChooseFile;
    private EditText mEnterFileName;
    private ImageView mShowImage;
    private ProgressBar mProgressBar;
    private Button mButtonUpload;
    private TextView mShowUploads;

    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonChooseFile = findViewById(R.id.choose_file);
        mEnterFileName = findViewById(R.id.enter_name);
        mShowImage = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mButtonUpload = findViewById(R.id.button_upload);
        mShowUploads = findViewById(R.id.text_view_show_uploads);

        mButtonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    public void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mShowImage);
        }
    }
}
