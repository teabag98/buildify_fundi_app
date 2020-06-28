package com.mbuildify.artisan.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.mbuildify.artisan.R;
import com.mbuildify.artisan.ui.activity.BaseActivity2;
import com.mbuildify.artisan.utils.CustomTextView;

public class JobsFrag extends Fragment implements View.OnClickListener{
    private View view;
    private BaseActivity2 baseActivity;
    private AppliedJobsFrag appliedJobsFrag = new AppliedJobsFrag();
    private AllJobsFrag allJobsFrag = new AllJobsFrag();
    private FragmentManager fragmentManager;
    private CustomTextView tvAllJobs, tvAppliedJobs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_jobs, container, false);
        baseActivity.headerNameTV.setText(getResources().getString(R.string.jobs));
        fragmentManager = getChildFragmentManager();
        tvAppliedJobs = (CustomTextView) view.findViewById(R.id.tvAppliedJobs);
        tvAllJobs = (CustomTextView) view.findViewById(R.id.tvAllJobs);

        tvAllJobs.setOnClickListener(this);
        tvAppliedJobs.setOnClickListener(this);

        fragmentManager.beginTransaction().add(R.id.frame, allJobsFrag).commit();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = (BaseActivity2) activity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvAllJobs:
                setSelected(true, false);
                fragmentManager.beginTransaction().replace(R.id.frame, allJobsFrag).commit();
                break;
            case R.id.tvAppliedJobs:
                setSelected(false, true);
                fragmentManager.beginTransaction().replace(R.id.frame, appliedJobsFrag).commit();
                break;
        }

    }

    public void setSelected(boolean firstBTN, boolean secondBTN) {
        if (firstBTN) {
            tvAllJobs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvAllJobs.setTextColor(getResources().getColor(R.color.white));
            tvAppliedJobs.setBackgroundColor(getResources().getColor(R.color.white));
            tvAppliedJobs.setTextColor(getResources().getColor(R.color.gray));
        }
        if (secondBTN) {
            tvAllJobs.setBackgroundColor(getResources().getColor(R.color.white));
            tvAllJobs.setTextColor(getResources().getColor(R.color.gray));
            tvAppliedJobs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            tvAppliedJobs.setTextColor(getResources().getColor(R.color.white));


        }
        tvAllJobs.setSelected(firstBTN);
        tvAppliedJobs.setSelected(secondBTN);
    }
}
