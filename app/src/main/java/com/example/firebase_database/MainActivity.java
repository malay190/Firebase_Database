package com.example.firebase_database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity  {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button mButtonChooseFile;
    private EditText mEnterFileName;
    private ImageView mShowImage;
    private ProgressBar mProgressBar;
    private Button mButtonUpload;
    private TextView mShowUploads;

    private Uri mImageUri;

    //Your files are stored in a Google Cloud Storage bucket. The files in this bucket are presented in a hierarchical structure,
    // just like the file system on your local hard disk, or the data in the Firebase Realtime Database.
    // By creating a reference to a file, your app gains access to it.
    private StorageReference mStorageRef;

    //A Firebase reference represents a particular location in your Database and can be used for reading or
    // writing data to that Database location.
    private DatabaseReference mDatabaseRef;

    //A controllable Task that has a synchronized state machine.
    private StorageTask mUploadTask;

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

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(MainActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }

            }
        });


        mShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageActivity();

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out:
                logOut();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void logOut(){


    }



    public void openFileChooser() {
        Intent intent = new Intent();

        //Retrieve any explicit MIME type included in the intent.A media type (also known as a Multipurpose Internet Mail Extensions or MIME type)
        // is a standard that indicates the nature and format of a document
        intent.setType("image/*");

        //Set the general action to be performed.
        //ACTION_GET_CONTENT: Allow the user to select a particular kind of data and return it.
        intent.setAction(Intent.ACTION_GET_CONTENT);

        //Starting another activity, whether one within your app or from another app, doesn't need to be a one-way operation.
        // You can also start another activity and receive a result back.
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(mShowImage);
        }
    }

    private String getFileExtension(Uri uri) {

        //This class provides applications access to the content model.
        ContentResolver cR = getContentResolver();

        //Two-way map that maps MIME-types to file extensions and vice versa.
        //A singleton is a design pattern that restricts the instantiation of a class to only one instance.
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {

            //Returns a new instance of StorageReference pointing to a child location of the current reference.
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() +
                    "." + getFileExtension(mImageUri));

            // File with extension is uploaded into Firebase Storage with
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload(mEnterFileName.getText().toString().trim(),
                                    taskSnapshot.getStorage().getDownloadUrl().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImageActivity() {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);

    }

}
