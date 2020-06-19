package com.qinsley.mbuildify.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.location.places.Place;
import com.google.gson.Gson;
import com.qinsley.mbuildify.DTO.ArtistDetailsDTO;
import com.qinsley.mbuildify.DTO.CategoryDTO;
import com.qinsley.mbuildify.DTO.UserDTO;
import com.qinsley.mbuildify.R;
import com.qinsley.mbuildify.https.HttpsRequest;
import com.qinsley.mbuildify.interfacess.Consts;
import com.qinsley.mbuildify.interfacess.Helper;
import com.qinsley.mbuildify.interfacess.OnSpinerItemClick;
import com.qinsley.mbuildify.network.NetworkManager;
import com.qinsley.mbuildify.preferences.SharedPrefrence;
import com.qinsley.mbuildify.utils.CustomButton;
import com.qinsley.mbuildify.utils.CustomEditText;
import com.qinsley.mbuildify.utils.CustomTextViewBold;
import com.qinsley.mbuildify.utils.ImageCompression;
import com.qinsley.mbuildify.utils.MainFragment;
import com.qinsley.mbuildify.utils.ProjectUtils;
import com.qinsley.mbuildify.utils.SpinnerDialog;
import com.schibstedspain.leku.LocationPickerActivity;

import org.json.JSONObject;
import org.michaelbel.bottomsheet.BottomSheet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.qinsley.mbuildify.interfacess.Consts.LATITUDE;
import static com.qinsley.mbuildify.interfacess.Consts.LONGITUDE;

public class EditPersnoalInfo extends AppCompatActivity implements View.OnClickListener {
    private String TAG = EditPersnoalInfo.class.getSimpleName();
    private Context mContext;
    private CustomEditText etCategoryD, etNameD, etCityD, etCountryD,etLocationD;
    private ImageView ivBanner;
    private CustomTextViewBold tvText;
    private CustomButton btnSubmit;
    private LinearLayout llBack;

    private ArrayList<CategoryDTO> categoryDTOS = new ArrayList<>();
    private SpinnerDialog spinnerDialogCate;
    private Spinner etGradeD;
    private ArtistDetailsDTO artistDetailsDTO;
    private Place place;
    private double lats = 0;
    private double longs = 0;
    private HashMap<String, String> paramsUpdate = new HashMap<>();
    private UserDTO userDTO;
    private SharedPrefrence preference;


    BottomSheet.Builder builder;
    Uri picUri;
    int PICK_FROM_CAMERA = 1, PICK_FROM_GALLERY = 2;
    int CROP_CAMERA_IMAGE = 3, CROP_GALLERY_IMAGE = 4;
    String imageName;
    String pathOfImage;
    Bitmap bm;
    ImageCompression imageCompression;
    byte[] resultByteArray;
    File file;
    Bitmap bitmap = null;
    private HashMap<String, File> paramsFile = new HashMap<>();
    private ArrayList<String> gradesList = new ArrayList<>();
    private String gradeInt = "0";
    private Spinner spinnerGrade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_persnoal_info);
        mContext = EditPersnoalInfo.this;
        preference = SharedPrefrence.getInstance(mContext);
        userDTO = preference.getParentUser(Consts.USER_DTO);
//        Spinner dropdown = (Spinner) findViewById(R.id.etCategoryG);
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add("Grade 1");
//        arrayList.add("Grade 2");
//        arrayList.add("Grade 3");
//        arrayList.add("Ungraded Artisan");
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dropdown.setAdapter(arrayAdapter);
//        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String gradeSelected = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + gradeSelected, Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });


        if (getIntent().hasExtra(Consts.CATEGORY_list)) {
            categoryDTOS = (ArrayList<CategoryDTO>) getIntent().getSerializableExtra(Consts.CATEGORY_list);
            artistDetailsDTO = (ArtistDetailsDTO) getIntent().getSerializableExtra(Consts.ARTIST_DTO);
        }
        setUiAction();
    }


    public void setUiAction() {
        etCategoryD = (CustomEditText) findViewById(R.id.etCategoryD);
        // grade selection
        spinnerGrade = (Spinner) findViewById(R.id.etGradeD);
//        etGradeD = (Spinner) findViewById(R.id.etGradeD);
        etLocationD = (CustomEditText) findViewById(R.id.etLocationD);
        etNameD = (CustomEditText) findViewById(R.id.etNameD);
        etCityD = (CustomEditText) findViewById(R.id.etCityD);
        etCountryD = (CustomEditText) findViewById(R.id.etCountryD);
        tvText = (CustomTextViewBold) findViewById(R.id.tvText);
        btnSubmit = (CustomButton) findViewById(R.id.btnSubmit);
        llBack = (LinearLayout) findViewById(R.id.llBack);


        etCategoryD.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        llBack.setOnClickListener(this);
        etLocationD.setOnClickListener(this);

        builder = new BottomSheet.Builder(EditPersnoalInfo.this);
        builder.setTitle(getResources().getString(R.string.select_img));
        builder.setMenu(R.menu.menu_cards,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.camera_cards:
                        if (ProjectUtils.hasPermissionInManifest(EditPersnoalInfo.this, PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(EditPersnoalInfo.this, PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                try {
                                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    File file = getOutputMediaFile(1);
                                    if (!file.exists()) {
                                        try {
                                            ProjectUtils.pauseProgressDialog();
                                            file.createNewFile();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        //Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.asd", newFile);
                                        picUri = FileProvider.getUriForFile(mContext.getApplicationContext(), mContext.getApplicationContext().getPackageName() + ".fileprovider", file);
                                    } else {
                                        picUri = Uri.fromFile(file); // create
                                    }

                                    preference.setValue(Consts.IMAGE_URI_CAMERA, picUri.toString());
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the image file
                                    startActivityForResult(intent, PICK_FROM_CAMERA);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        break;
                    case R.id.gallery_cards:
                        if (ProjectUtils.hasPermissionInManifest(EditPersnoalInfo.this, PICK_FROM_CAMERA, Manifest.permission.CAMERA)) {
                            if (ProjectUtils.hasPermissionInManifest(EditPersnoalInfo.this, PICK_FROM_GALLERY, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                File file = getOutputMediaFile(1);
                                if (!file.exists()) {
                                    try {
                                        file.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                picUri = Uri.fromFile(file);

                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_pic)), PICK_FROM_GALLERY);

                            }
                        }
                        break;
                    case R.id.cancel_cards:
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                        break;
                }
            }
        });

//        gradesList.add("select your grade");
        gradesList.add("-- select your grade --");
        gradesList.add("Grade 1");
        gradesList.add("Grade 2");
        gradesList.add("Grade 3");
        gradesList.add("Ungraded Artisan");

        ArrayAdapter<String> gradeArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gradesList);
        gradeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGrade.setPrompt("-- select your grade --");
        spinnerGrade.setAdapter(gradeArrayAdapter);
        spinnerGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String gradeSelected = parent.getItemAtPosition(position).toString();
                if (gradeSelected == "-- select your grade --") {
                    gradeInt = "0";
                } else if (gradeSelected == "Grade 1") {
                    gradeInt = "4";
                } else if (gradeSelected == "Grade 2") {
                    gradeInt = "3";
                } else if (gradeSelected == "Grade 3") {
                    gradeInt = "2";
                } else if (gradeSelected == "Ungraded Artisan") {
                    gradeInt = "1";
                }
                    Toast.makeText(parent.getContext(), "Selected: " + gradeSelected, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onNothingSelected (AdapterView < ? > parent){
                    Toast.makeText(parent.getContext(), getResources().getString(R.string.select_grade), Toast.LENGTH_LONG).show();
                    gradeInt = "0";
                }
            });

            //cate selection
            spinnerDialogCate =new

            SpinnerDialog((Activity) mContext,categoryDTOS,getResources().

            getString(R.string.select_cate));// With 	Animation
        spinnerDialogCate.bindOnSpinerListener(new

            OnSpinerItemClick() {
                @Override
                public void onClick (String item, String id,int position){
                    etCategoryD.setText(item);
                    paramsUpdate.put(Consts.CATEGORY_ID, id);
                    tvText.setText(getResources().getString(R.string.commis_msg) + categoryDTOS.get(position).getCurrency_type() + categoryDTOS.get(position).getPrice());


                }
            });


        if(artistDetailsDTO !=null)

            {
                showData();
            }

        }

        private File getOutputMediaFile ( int type){
            String root = Environment.getExternalStorageDirectory().toString();
            File mediaStorageDir = new File(root, Consts.APP_NAME);
            /**Create the storage directory if it does not exist*/
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }

            /**Create a media file name*/
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            if (type == 1) {
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        Consts.APP_NAME + timeStamp + ".png");

                imageName = Consts.APP_NAME + timeStamp + ".png";
            } else {
                return null;
            }
            return mediaFile;
        }

        public void showData () {
            for (int j = 0; j < categoryDTOS.size(); j++) {
                if (categoryDTOS.get(j).getId().equalsIgnoreCase(artistDetailsDTO.getCategory_id())) {
                    categoryDTOS.get(j).setSelected(true);
                    etCategoryD.setText(categoryDTOS.get(j).getCat_name());
                    tvText.setText(getResources().getString(R.string.commis_msg) + categoryDTOS.get(j).getCurrency_type() + categoryDTOS.get(j).getPrice());


                }
            }

            etCategoryD.setText(artistDetailsDTO.getCategory_name());
            etNameD.setText(artistDetailsDTO.getName());
            etCityD.setText(artistDetailsDTO.getCity());
            etCountryD.setText(artistDetailsDTO.getCountry());
            etLocationD.setText(artistDetailsDTO.getLocation());


        }


        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.etCategoryD:
                    if (NetworkManager.isConnectToInternet(mContext)) {
                        if (categoryDTOS.size() > 0)
                            spinnerDialogCate.showSpinerDialog();
                    } else {
                        ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                    }

//                break;
//            case R.id.etCategoryG:
//                if (NetworkManager.isConnectToInternet(mContext)) {
//                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    dropdown.setAdapter(arrayAdapter);
//                } else {
//                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
//                }
                    break;
                case R.id.btnSubmit:
                    if (NetworkManager.isConnectToInternet(mContext)) {
                        submitPersonalProfile();
                    } else {
                        ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                    }
                    break;
                case R.id.etLocationD:
                    if (NetworkManager.isConnectToInternet(mContext)) {
                        findPlace();
                    } else {
                        ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                    }
                    break;
                case R.id.ivBanner:
                    builder.show();
                    break;
                case R.id.llBack:
                    finish();
                    overridePendingTransition(R.anim.stay, R.anim.slide_down);
                    break;
            }
        }

        @Override
        public void onBackPressed () {
            //super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.stay, R.anim.slide_down);
        }

        @Override
        public void onActivityResult ( int requestCode, int resultCode, Intent data){
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CROP_CAMERA_IMAGE) {
                if (data != null) {
                    picUri = Uri.parse(data.getExtras().getString("resultUri"));
                    try {
                        //bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                        pathOfImage = picUri.getPath();
                        imageCompression = new ImageCompression(EditPersnoalInfo.this);
                        imageCompression.execute(pathOfImage);
                        imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                            @Override
                            public void processFinish(String imagePath) {
                                try {
                                    // bitmap = MediaStore.Images.Media.getBitmap(SaveDetailsActivityNew.this.getContentResolver(), resultUri);
                                    file = new File(imagePath);
                                    Glide.with(mContext).load("file://" + imagePath)
                                            .thumbnail(0.5f)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(ivBanner);

                                    Log.e("image", imagePath);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (requestCode == CROP_GALLERY_IMAGE) {
                if (data != null) {
                    picUri = Uri.parse(data.getExtras().getString("resultUri"));
                    try {
                        bm = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), picUri);
                        pathOfImage = picUri.getPath();
                        imageCompression = new ImageCompression(EditPersnoalInfo.this);
                        imageCompression.execute(pathOfImage);
                        imageCompression.setOnTaskFinishedEvent(new ImageCompression.AsyncResponse() {
                            @Override
                            public void processFinish(String imagePath) {
                                Log.e("image", imagePath);
                                try {
                                    file = new File(imagePath);

                                    Glide.with(mContext).load("file://" + imagePath)
                                            .thumbnail(0.5f)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(ivBanner);

                                    Log.e("image", imagePath);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (requestCode == PICK_FROM_CAMERA && resultCode == RESULT_OK) {
                if (picUri != null) {
                    picUri = Uri.parse(preference.getValue(Consts.IMAGE_URI_CAMERA));
                    startCropping(picUri, CROP_CAMERA_IMAGE);
                } else {
                    picUri = Uri.parse(preference
                            .getValue(Consts.IMAGE_URI_CAMERA));
                    startCropping(picUri, CROP_CAMERA_IMAGE);
                }
            }
            if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
                try {
                    Uri tempUri = data.getData();
                    Log.e("front tempUri", "" + tempUri);
                    if (tempUri != null) {
                        startCropping(tempUri, CROP_GALLERY_IMAGE);
                    } else {

                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            if (requestCode == 101) {
                if (resultCode == RESULT_OK) {
                    try {
                        getAddress(data.getDoubleExtra(LATITUDE, 0.0), data.getDoubleExtra(LONGITUDE, 0.0));


                    } catch (Exception e) {

                    }
                }
            }


        }

        public void startCropping (Uri uri,int requestCode){
            Intent intent = new Intent(mContext, MainFragment.class);
            intent.putExtra("imageUri", uri.toString());
            intent.putExtra("requestCode", requestCode);
            startActivityForResult(intent, requestCode);
        }

        private void findPlace () {
            Intent locationPickerIntent = new LocationPickerActivity.Builder()
                    .withGooglePlacesEnabled()
                    //.withLocation(41.4036299, 2.1743558)
                    .build(mContext);

            startActivityForResult(locationPickerIntent, 101);
        }

        public void getAddress ( double lat, double lng){
            Geocoder geocoder = new Geocoder(EditPersnoalInfo.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                Address obj = addresses.get(0);
                String add = obj.getAddressLine(0);
                add = add + "\n" + obj.getCountryName();
                add = add + "\n" + obj.getCountryCode();
                add = add + "\n" + obj.getAdminArea();
                add = add + "\n" + obj.getPostalCode();
                add = add + "\n" + obj.getSubAdminArea();
                add = add + "\n" + obj.getLocality();
                add = add + "\n" + obj.getSubThoroughfare();
                Log.e("IGA", "Address" + add);
                // Toast.makeText(this, "Address=>" + add,
                // Toast.LENGTH_SHORT).show();

                // TennisAppActivity.showDialog(add);

                etLocationD.setText(obj.getAddressLine(0));

                lats = lat;
                longs = lng;


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void submitPersonalProfile () {


            if (!validation(etCategoryD, getResources().getString(R.string.val_cat_sele))) {
                return;
            } else if (!validation(etNameD, getResources().getString(R.string.val_name))) {
                return;
            } else if (!validation(etCityD, getResources().getString(R.string.val_city))) {
                return;
            } else if (!validation(etCountryD, getResources().getString(R.string.val_country))) {
                return;
            } else {
                if (NetworkManager.isConnectToInternet(mContext)) {


                    paramsUpdate.put(Consts.USER_ID, userDTO.getUser_id());
                    paramsUpdate.put(Consts.NAME, ProjectUtils.getEditTextValue(etNameD));
                    paramsUpdate.put(Consts.COUNTRY, ProjectUtils.getEditTextValue(etCountryD));
                    paramsUpdate.put(Consts.LOCATION, ProjectUtils.getEditTextValue(etLocationD));
                    paramsUpdate.put(Consts.GRADE, gradeInt);

                    if (lats != 0)
                        paramsUpdate.put(LATITUDE, String.valueOf(lats));

                    if (longs != 0)
                        paramsUpdate.put(LONGITUDE, String.valueOf(longs));


                    updateProfile();

                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_concation));
                }
            }
        }

        public boolean validation (CustomEditText editText, String msg){
            if (!ProjectUtils.isEditTextFilled(editText)) {
                ProjectUtils.showLong(mContext, msg);
                return false;
            } else {
                return true;
            }
        }

        public void updateProfile () {
            ProjectUtils.showProgressDialog(mContext, true, getResources().getString(R.string.please_wait));
            new HttpsRequest(Consts.UPDATE_PROFILE_ARTIST_API, paramsUpdate, paramsFile, mContext).imagePost(TAG, new Helper() {
                @Override
                public void backResponse(boolean flag, String msg, JSONObject response) {
                    ProjectUtils.pauseProgressDialog();
                    if (flag) {

                        try {
                            ProjectUtils.showToast(mContext, msg);
                            artistDetailsDTO = new Gson().fromJson(response.getJSONObject("data").toString(), ArtistDetailsDTO.class);
                            userDTO.setIs_profile(1);
                            preference.setParentUser(userDTO, Consts.USER_DTO);
//                        finish();

                            String inputtedFundiName = etNameD.getText().toString();
                            Intent i = new Intent(getApplicationContext(), UploadCertificates.class);
                            i.putExtra("fundiNames", inputtedFundiName);
                            startActivity(i);

                            overridePendingTransition(R.anim.stay, R.anim.slide_down);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        ProjectUtils.showToast(mContext, msg);
                    }
                }
            });
        }
    }
