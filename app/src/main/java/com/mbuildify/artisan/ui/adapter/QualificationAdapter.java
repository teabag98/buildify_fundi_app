package com.mbuildify.artisan.ui.adapter;

/**
 * Created by VARUN on 01/01/19.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.recyclerview.widget.RecyclerView;

import com.mbuildify.artisan.DTO.QualificationsDTO;
import com.mbuildify.artisan.R;
import com.mbuildify.artisan.https.HttpsRequest;
import com.mbuildify.artisan.interfacess.Consts;
import com.mbuildify.artisan.interfacess.Helper;
import com.mbuildify.artisan.network.NetworkManager;
import com.mbuildify.artisan.ui.fragment.PersnoalInfo;
import com.mbuildify.artisan.utils.CustomEditText;
import com.mbuildify.artisan.utils.CustomTextView;
import com.mbuildify.artisan.utils.CustomTextViewBold;
import com.mbuildify.artisan.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class QualificationAdapter extends RecyclerView.Adapter<QualificationAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<QualificationsDTO> qualificationsDTOList;

    private HashMap<String, String> paramsUpdate;
    private HashMap<String, String> paramsDelete;
    private Dialog dialogEditQualification;
    private DialogInterface dialog_book;

    private CustomEditText etQaulTitleD;
    private CustomEditText etQaulDesD;
    private CustomTextViewBold tvYesQuali;
    private CustomTextViewBold tvNoQuali;
    private CustomTextViewBold ctvbTitle;
    //private ArtistProfileView artistProfileView;
    private PersnoalInfo persnoalInfo;


    public QualificationAdapter(PersnoalInfo persnoalInfo, Context mContext, ArrayList<QualificationsDTO> qualificationsDTOList) {
        this.persnoalInfo = persnoalInfo;
        this.mContext = mContext;
        this.qualificationsDTOList = qualificationsDTOList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapterqualification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.CTVtitel.setText(qualificationsDTOList.get(position).getTitle());
        holder.CTVdescription.setText(qualificationsDTOList.get(position).getDescription());
        holder.tvEditQuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogQualification(position);
            }
        });
        holder.tvDeleteQuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paramsDelete = new HashMap<>();
                paramsDelete.put(Consts.QUALIFICATION_ID, qualificationsDTOList.get(position).getId());
                deleteDialog();
            }
        });
    }

    @Override
    public int getItemCount() {

        return qualificationsDTOList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {


        public CustomTextView CTVtitel, CTVdescription, tvEditQuali, tvDeleteQuali;

        public MyViewHolder(View view) {
            super(view);

            CTVtitel = view.findViewById(R.id.CTVtitel);
            CTVdescription = view.findViewById(R.id.CTVdescription);
            tvEditQuali = view.findViewById(R.id.tvEditQuali);
            tvDeleteQuali = view.findViewById(R.id.tvDeleteQuali);
        }
    }

    public void dialogQualification(final int pos) {
        paramsUpdate = new HashMap<>();

        dialogEditQualification = new Dialog(mContext/*, android.R.style.Theme_Dialog*/);
        dialogEditQualification.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEditQualification.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEditQualification.setContentView(R.layout.dailog_ar_qualification);


        etQaulTitleD = (CustomEditText) dialogEditQualification.findViewById(R.id.etQaulTitleD);
        etQaulDesD = (CustomEditText) dialogEditQualification.findViewById(R.id.etQaulDesD);
        tvYesQuali = (CustomTextViewBold) dialogEditQualification.findViewById(R.id.tvYesQuali);
        tvNoQuali = (CustomTextViewBold) dialogEditQualification.findViewById(R.id.tvNoQuali);
        ctvbTitle = (CustomTextViewBold) dialogEditQualification.findViewById(R.id.ctvbTitle);

        ctvbTitle.setText(mContext.getResources().getString(R.string.update_qualification));

        etQaulTitleD.setText(qualificationsDTOList.get(pos).getTitle());
        etQaulDesD.setText(qualificationsDTOList.get(pos).getDescription());

        dialogEditQualification.show();
        dialogEditQualification.setCancelable(false);

        tvNoQuali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogEditQualification.dismiss();

            }
        });
        tvYesQuali.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        paramsUpdate.put(Consts.QUALIFICATION_ID, qualificationsDTOList.get(pos).getId());
                        paramsUpdate.put(Consts.TITLE, ProjectUtils.getEditTextValue(etQaulTitleD));
                        paramsUpdate.put(Consts.DESCRIPTION, ProjectUtils.getEditTextValue(etQaulDesD));

                        if (NetworkManager.isConnectToInternet(mContext)) {
                            updateQuali();
                        } else {
                            ProjectUtils.showToast(mContext, mContext.getResources().getString(R.string.internet_concation));
                        }
                    }
                });

    }

    public void updateQuali() {
        ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.UPDATE_QUALIFICATION_API, paramsUpdate, mContext).stringPost("TAG", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    persnoalInfo.getParentData();
                    dialogEditQualification.dismiss();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }


    public void deleteDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle(mContext.getResources().getString(R.string.delete_quali));
            builder.setMessage(mContext.getResources().getString(R.string.delete_quali_msg));
            builder.setCancelable(false);
            builder.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog_book = dialog;
                    deleteQuali();

                }
            });
            builder.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            builder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteQuali() {
        ProjectUtils.showProgressDialog(mContext, true, mContext.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.DELETE_QUALIFICATION_API, paramsDelete, mContext).stringPost("TAG", new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(mContext, msg);
                    persnoalInfo.getParentData();
                    dialog_book.dismiss();
                } else {
                    ProjectUtils.showToast(mContext, msg);
                }


            }
        });
    }

}