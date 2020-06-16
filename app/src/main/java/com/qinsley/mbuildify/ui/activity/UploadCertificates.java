

/**
 * Created by ERIC on 14/2/2020
 **/

package com.qinsley.mbuildify.ui.activity;


import android.view.View;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.qinsley.mbuildify.R;


import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


public class UploadCertificates extends AppCompatActivity implements View.OnClickListener {
    private Button chooseCert, uploadCert, skipCert;
    private ImageView certificateView;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private String gradedFundisName, ungradedFundiName;
    Map<String, Object> certifiedFundisQualification = new HashMap<>();
    Map<String, Object> uncertifiedFundisQualification = new HashMap<>();

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore firebaseFirestore;

    // unique user
    UUID uuid = UUID.randomUUID();
    String uniqueUserDocument =  uuid.toString();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_certificates);

        Intent intent = getIntent();
        gradedFundisName = intent.getStringExtra("fundiNames");
        ungradedFundiName = intent.getStringExtra("fundiNames");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();


        //innitiaizing views
        chooseCert = (Button) findViewById(R.id.chooseCert);
        uploadCert = (Button) findViewById(R.id.uploadCert);
        skipCert = (Button) findViewById(R.id.skipCert);
        certificateView = (ImageView) findViewById(R.id.certificateView);

        uploadCert.setOnClickListener(this);
        chooseCert.setOnClickListener(this);
        skipCert.setOnClickListener(this);


    }

    //Initialize Views
//    public void setUiAction() {
//
//
//
//
//
//    }

    private void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadCertificates.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UploadCertificates.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
            Intent i = new Intent(getApplicationContext(), BaseActivity2.class);
            startActivity(i);
        }
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                certificateView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chooseCert:
                chooseImage();
                break;

            case R.id.uploadCert:

                certifiedFundisQualification.put("name",gradedFundisName);
                CollectionReference graded = firebaseFirestore.collection("Fundis_registered");
                graded.document(uniqueUserDocument).set(certifiedFundisQualification);
                uploadImage();
                break;
            case R.id.skipCert:
                uncertifiedFundisQualification.put("name", ungradedFundiName);
                CollectionReference Ugraded = firebaseFirestore.collection("Uncertified_fundis_registered");
                Ugraded.document(uniqueUserDocument).set(uncertifiedFundisQualification);
                Intent i = new Intent(getApplicationContext(), BaseActivity2.class);
                startActivity(i);
                break;
        }
    }
}
