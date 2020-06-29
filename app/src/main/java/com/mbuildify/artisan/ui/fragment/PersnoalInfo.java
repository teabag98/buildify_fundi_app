package com.mbuildify.artisan.ui.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.mbuildify.artisan.DTO.ArtistDetailsDTO;
import com.mbuildify.artisan.DTO.QualificationsDTO;
import com.mbuildify.artisan.DTO.UserDTO;
import com.mbuildify.artisan.R;
import com.mbuildify.artisan.databinding.DailogArQualificationBinding;
import com.mbuildify.artisan.databinding.FragmentPersnoalInfoBinding;
import com.mbuildify.artisan.https.HttpsRequest;
import com.mbuildify.artisan.interfacess.Consts;
import com.mbuildify.artisan.interfacess.Helper;
import com.mbuildify.artisan.network.NetworkManager;
import com.mbuildify.artisan.preferences.SharedPrefrence;
import com.mbuildify.artisan.ui.adapter.QualificationAdapter;
import com.mbuildify.artisan.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PersnoalInfo extends Fragment implements View.OnClickListener {
    private static final String TAG = PersnoalInfo.class.getCanonicalName();
    private FragmentPersnoalInfoBinding binding;
    private ArtistDetailsDTO artistDetailsDTO;
    private Bundle bundle;
    private ArrayList<QualificationsDTO> qualificationsDTOList;
    private QualificationAdapter qualificationAdapter;
    private LinearLayoutManager mLayoutManagerQuali;
    private UserDTO userDTO;
    private SharedPrefrence prefrence;
    private ArtistProfile parentFrag;
    private HashMap<String, String> paramsUpdate;
    private Dialog dialogEditQualification;
    private HashMap<String, String> paramsRate = new HashMap<>();
    private HashMap<String, String> params;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_persnoal_info, container, false);
        prefrence = SharedPrefrence.getInstance(getActivity());
        userDTO = prefrence.getParentUser(Consts.USER_DTO);
        parentFrag = ((ArtistProfile) PersnoalInfo.this.getParentFragment());
        bundle = this.getArguments();
        artistDetailsDTO = (ArtistDetailsDTO) bundle.getSerializable(Consts.ARTIST_DTO);

        paramsRate.put(Consts.ARTIST_ID, userDTO.getUser_id());

        showUiAction();
        return binding.getRoot();
    }

    public void showUiAction() {
        binding.btnUpdate.setOnClickListener(this);
        binding.ivEditQualification.setOnClickListener(this);

        mLayoutManagerQuali = new LinearLayoutManager(getActivity().getApplicationContext());
        binding.rvQualification.setLayoutManager(mLayoutManagerQuali);


        binding.switchRate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isShown()) {
                    if (b == true) {
                        paramsRate.put(Consts.ARTIST_COMMISSION_TYPE, "0");
                        chnageRate();
                    } else {
                        paramsRate.put(Consts.ARTIST_COMMISSION_TYPE, "1");
                        chnageRate();
                    }
                }
            }
        });

        showData();
    }

    public void showData() {
        if (artistDetailsDTO.getArtist_commission_type().equalsIgnoreCase("0")) {
            binding.switchRate.setChecked(true);
            binding.tvRate.setText(getResources().getString(R.string.hour_rate));
            binding.tvArtistRate.setText(getResources().getString(R.string.rate) + " " + artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + getResources().getString(R.string.hr));

        } else {
            binding.switchRate.setChecked(false);
            binding.tvRate.setText(getResources().getString(R.string.fix_rate));
            binding.tvArtistRate.setText(getResources().getString(R.string.rate) + " " + artistDetailsDTO.getCurrency_type() + artistDetailsDTO.getPrice() + " " + getResources().getString(R.string.fixed_rate));

        }

        qualificationsDTOList = new ArrayList<>();
        qualificationsDTOList = artistDetailsDTO.getQualifications();
        qualificationAdapter = new QualificationAdapter(PersnoalInfo.this, getActivity(), qualificationsDTOList);
        binding.rvQualification.setAdapter(qualificationAdapter);

        binding.ratingbar.setRating(Float.parseFloat(artistDetailsDTO.getAva_rating()));
        binding.tvRating.setText("(" + artistDetailsDTO.getAva_rating() + "/5)");

        binding.tvJobComplete.setText(artistDetailsDTO.getJobDone() + " " + getResources().getString(R.string.jobs_comleted));
        binding.tvProfileComplete.setText(artistDetailsDTO.getCompletePercentages() + "% " + getResources().getString(R.string.completion));



        showDataSelf();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEditQualification:
                dialogQualification();
                break;
            case R.id.btnUpdate:
                submitProfile();
                break;
        }
    }

    public void dialogQualification() {
        paramsUpdate = new HashMap<>();

        dialogEditQualification = new Dialog(getActivity()/*, android.R.style.Theme_Dialog*/);
        dialogEditQualification.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEditQualification.requestWindowFeature(Window.FEATURE_NO_TITLE);
        final DailogArQualificationBinding binding1 = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dailog_ar_qualification, null, false);
        dialogEditQualification.setContentView(binding1.getRoot());

        dialogEditQualification.show();
        dialogEditQualification.setCancelable(false);

        binding1.tvNoQuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditQualification.dismiss();

            }
        });
        binding1.tvYesQuali.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paramsUpdate.put(Consts.USER_ID, userDTO.getUser_id());
                        paramsUpdate.put(Consts.TITLE, ProjectUtils.getEditTextValue(binding1.etQaulTitleD));
                        paramsUpdate.put(Consts.DESCRIPTION, ProjectUtils.getEditTextValue(binding1.etQaulDesD));

                        if (NetworkManager.isConnectToInternet(getActivity())) {
                            if (!ProjectUtils.isEditTextFilled(binding1.etQaulTitleD)) {
                                ProjectUtils.showLong(getActivity(), getResources().getString(R.string.val_title1));
                                return;
                            } else if (!ProjectUtils.isEditTextFilled(binding1.etQaulDesD)) {
                                ProjectUtils.showLong(getActivity(), getResources().getString(R.string.val_description));
                                return;
                            } else {
                                addQualification();
                            }
                        } else {
                            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
                        }
                    }
                });

    }

    public void addQualification() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.ADD_QUALIFICATION_API, paramsUpdate, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(getActivity(), msg);
                    parentFrag.getArtist();
                    dialogEditQualification.dismiss();
                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }
        });
    }

    public void chnageRate() {
        new HttpsRequest(Consts.CHANGE_COMMISSION_ARTIST_API, paramsRate, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                if (flag) {
                    ProjectUtils.showLong(getActivity(), msg);
                    parentFrag.getArtist();
                } else {
                    ProjectUtils.showLong(getActivity(), msg);
                }
            }
        });
    }

    public void submitProfile() {
        params = new HashMap<>();
        params.put(Consts.USER_ID, userDTO.getUser_id());
        params.put(Consts.NAME, ProjectUtils.getEditTextValue(binding.etName));
        params.put(Consts.MOBILE, ProjectUtils.getEditTextValue(binding.etMobileNo));

        if (binding.rbGenderF.isChecked()) {
            params.put(Consts.GENDER, "0");
        } else if (binding.rbGenderM.isChecked()) {
            params.put(Consts.GENDER, "1");
        } else {
            params.put(Consts.GENDER, "2");
        }
        if (NetworkManager.isConnectToInternet(getActivity())) {
            updateProfileSelf();
        } else {
            ProjectUtils.showToast(getActivity(), getResources().getString(R.string.internet_concation));
        }
    }

    public void updateProfileSelf() {
        ProjectUtils.showProgressDialog(getActivity(), true, getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.UPDATE_PROFILE_API, params, getActivity()).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    try {
                        ProjectUtils.showToast(getActivity(), msg);
                        userDTO = new Gson().fromJson(response.getJSONObject("data").toString(), UserDTO.class);
                        prefrence.setParentUser(userDTO, Consts.USER_DTO);
                        showDataSelf();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    ProjectUtils.showToast(getActivity(), msg);
                }


            }

        });
    }

    private void showDataSelf() {
        binding.etName.setText(userDTO.getName());
        binding.etEmail.setText(userDTO.getEmail_id());
        binding.etMobileNo.setText(userDTO.getMobile());

        if (userDTO.getGender().equalsIgnoreCase("0")) {
            binding.rbGenderM.setChecked(false);
            binding.rbGenderF.setChecked(true);
            binding.rbGenderO.setChecked(false);
        } else if (userDTO.getGender().equalsIgnoreCase("1")) {
            binding.rbGenderM.setChecked(true);
            binding.rbGenderF.setChecked(false);
            binding.rbGenderO.setChecked(false);
        } else {
            binding.rbGenderM.setChecked(false);
            binding.rbGenderF.setChecked(false);
            binding.rbGenderO.setChecked(true);
        }
    }

    public void getParentData() {
        parentFrag.getArtist();
    }
}
