package com.qinsley.mbuildify.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.qinsley.mbuildify.DTO.SkillsDTO;
import com.qinsley.mbuildify.utils.CustomTextView;
import com.qinsley.mbuildify.R;

import java.util.ArrayList;


public class SkillsAdapter extends RecyclerView.Adapter<SkillsAdapter.MyViewHolder> {

    Context mContext;
    ArrayList<SkillsDTO> skillsDTOList;


    public SkillsAdapter(Context mContext, ArrayList<SkillsDTO> skillsDTOList) {
        this.mContext = mContext;
        this.skillsDTOList = skillsDTOList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapterskills, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvSkills.setText(skillsDTOList.get(position).getSkill());
    }

    @Override
    public int getItemCount() {

        return skillsDTOList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        public CustomTextView tvSkills;

        public MyViewHolder(View view) {
            super(view);
            tvSkills = view.findViewById(R.id.tvSkills);


        }
    }

}