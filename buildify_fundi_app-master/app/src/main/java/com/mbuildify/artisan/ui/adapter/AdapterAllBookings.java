package com.mbuildify.artisan.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mbuildify.artisan.DTO.ArtistBooking;
import com.mbuildify.artisan.DTO.UserDTO;
import com.mbuildify.artisan.R;
import com.mbuildify.artisan.databinding.AdapterAllBookingsBinding;
import com.mbuildify.artisan.https.HttpsRequest;
import com.mbuildify.artisan.interfacess.Consts;
import com.mbuildify.artisan.interfacess.Helper;
import com.mbuildify.artisan.network.NetworkManager;
import com.mbuildify.artisan.ui.fragment.NewBookings;
import com.mbuildify.artisan.utils.CustomTextView;
import com.mbuildify.artisan.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterAllBookings extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String TAG = AdapterAllBookings.class.getSimpleName();
    private NewBookings newBookings;
    private ArrayList<ArtistBooking> artistBookingsList;
    private UserDTO userDTO;
    private LayoutInflater myInflater;
    private Context context;
    private HashMap<String, String> paramsBookingOp;
    private HashMap<String, String> paramsDecline;
    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;

    public AdapterAllBookings(NewBookings newBookings, ArrayList<ArtistBooking> artistBookingsList, UserDTO userDTO, LayoutInflater myInflater) {
        this.newBookings = newBookings;
        this.artistBookingsList = artistBookingsList;
        this.userDTO = userDTO;
        this.myInflater = myInflater;
        context = newBookings.getActivity();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int viewType) {
        RecyclerView.ViewHolder vh;
        if (myInflater == null) {
            myInflater = LayoutInflater.from(viewGroup.getContext());
        }
        if (viewType == VIEW_ITEM) {
            AdapterAllBookingsBinding binding =
                    DataBindingUtil.inflate(myInflater, R.layout.adapter_all_bookings, viewGroup, false);
            vh = new MyViewHolder(binding);
        } else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_section, viewGroup, false);
            vh = new MyViewHolderSection(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderMain, final int position) {
        if (holderMain instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) holderMain;
            holder.binding.tvName.setText(artistBookingsList.get(position).getUserName());
            holder.binding.tvLocation.setText(artistBookingsList.get(position).getAddress());
            holder.binding.tvDate.setText(ProjectUtils.changeDateFormate1(artistBookingsList.get(position).getBooking_date())+" "+artistBookingsList.get(position).getBooking_time());

            Glide.with(context).
                    load(artistBookingsList.get(position).getUserImage())
                    .placeholder(R.drawable.dummyuser_image)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.binding.ivArtist);
            holder.binding.tvDescription.setText(artistBookingsList.get(position).getDescription());
            if (artistBookingsList.get(position).getBooking_type().equalsIgnoreCase("0") || artistBookingsList.get(position).getBooking_type().equalsIgnoreCase("3")) {

                if (artistBookingsList.get(position).getBooking_flag().equalsIgnoreCase("0")) {
                    holder.binding.llACDE.setVisibility(View.VISIBLE);
                    holder.binding.llSt.setVisibility(View.GONE);
                    holder.binding.tvCompleted.setVisibility(View.GONE);
                    holder.binding.tvRejected.setVisibility(View.GONE);

                } else if (artistBookingsList.get(position).getBooking_flag().equalsIgnoreCase("1")) {
                    holder.binding.llSt.setVisibility(View.VISIBLE);
                    holder.binding.llACDE.setVisibility(View.GONE);
                    holder.binding.tvCompleted.setVisibility(View.GONE);
                    holder.binding.tvRejected.setVisibility(View.GONE);
                } else if (artistBookingsList.get(position).getBooking_flag().equalsIgnoreCase("2")) {
                    holder.binding.llSt.setVisibility(View.GONE);
                    holder.binding.llACDE.setVisibility(View.GONE);
                    holder.binding.tvCompleted.setVisibility(View.GONE);
                    holder.binding.tvRejected.setVisibility(View.VISIBLE);
                } else if (artistBookingsList.get(position).getBooking_flag().equalsIgnoreCase("4")) {
                    holder.binding.llSt.setVisibility(View.GONE);
                    holder.binding.llACDE.setVisibility(View.GONE);
                    holder.binding.tvCompleted.setVisibility(View.VISIBLE);
                    holder.binding.tvRejected.setVisibility(View.GONE);
                }


            } else if (artistBookingsList.get(position).getBooking_type().equalsIgnoreCase("2")) {

                if (artistBookingsList.get(position).getBooking_flag().equalsIgnoreCase("0")) {
                    holder.binding.llACDE.setVisibility(View.VISIBLE);
                    holder.binding.llSt.setVisibility(View.GONE);
                    holder.binding.tvCompleted.setVisibility(View.GONE);
                    holder.binding.tvRejected.setVisibility(View.GONE);

                } else if (artistBookingsList.get(position).getBooking_flag().equalsIgnoreCase("1")) {
                    holder.binding.llSt.setVisibility(View.VISIBLE);
                    holder.binding.llACDE.setVisibility(View.GONE);
                    holder.binding.tvCompleted.setVisibility(View.GONE);
                    holder.binding.tvRejected.setVisibility(View.GONE);
                } else if (artistBookingsList.get(position).getBooking_flag().equalsIgnoreCase("2")) {
                    holder.binding.llSt.setVisibility(View.GONE);
                    holder.binding.llACDE.setVisibility(View.GONE);
                    holder.binding.tvCompleted.setVisibility(View.GONE);
                    holder.binding.tvRejected.setVisibility(View.VISIBLE);
                } else if (artistBookingsList.get(position).getBooking_flag().equalsIgnoreCase("4")) {
                    holder.binding.llSt.setVisibility(View.GONE);
                    holder.binding.llACDE.setVisibility(View.GONE);
                    holder.binding.tvCompleted.setVisibility(View.VISIBLE);
                    holder.binding.tvRejected.setVisibility(View.GONE);
                }

            }

            holder.binding.llAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkManager.isConnectToInternet(context)) {

                        booking("1", position);
                    } else {
                        ProjectUtils.showToast(context, context.getResources().getString(R.string.internet_concation));
                    }
                }
            });
            holder.binding.llDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectUtils.showDialog(context, context.getResources().getString(R.string.dec_cpas), context.getResources().getString(R.string.decline_msg), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            decline(position);

                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, false);

                }
            });
            holder.binding.llStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetworkManager.isConnectToInternet(context)) {

                        booking("2", position);
                    } else {
                        ProjectUtils.showToast(context, context.getResources().getString(R.string.internet_concation));
                    }
                }
            });
            holder.binding.llCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProjectUtils.showDialog(context, context.getResources().getString(R.string.dec_cpas), context.getResources().getString(R.string.decline_msg), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            decline(position);

                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }, false);
                }
            });
        } else {
            MyViewHolderSection view = (MyViewHolderSection) holderMain;
            view.tvSection.setText(artistBookingsList.get(position).getSection_name());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return this.artistBookingsList.get(position).isSection() ? VIEW_SECTION : VIEW_ITEM;
    }

    @Override
    public int getItemCount() {
        return artistBookingsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AdapterAllBookingsBinding binding;

        public MyViewHolder(AdapterAllBookingsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }

    public void booking(String req, int pos) {
        paramsBookingOp = new HashMap<>();
        paramsBookingOp.put(Consts.BOOKING_ID, artistBookingsList.get(pos).getId());
        paramsBookingOp.put(Consts.REQUEST, req);
        paramsBookingOp.put(Consts.USER_ID, artistBookingsList.get(pos).getUser_id());
        ProjectUtils.showProgressDialog(context, true, context.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.BOOKING_OPERATION_API, paramsBookingOp, context).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(context, msg);
                    newBookings.getBookings();


                } else {
                    ProjectUtils.showToast(context, msg);
                }


            }
        });
    }

    public void decline(int pos) {
        paramsDecline = new HashMap<>();
        paramsDecline.put(Consts.USER_ID, userDTO.getUser_id());
        paramsDecline.put(Consts.BOOKING_ID, artistBookingsList.get(pos).getId());
        paramsDecline.put(Consts.DECLINE_BY, "1");
        paramsDecline.put(Consts.DECLINE_REASON, "Busy");
        ProjectUtils.showProgressDialog(context, true, context.getResources().getString(R.string.please_wait));
        new HttpsRequest(Consts.DECLINE_BOOKING_API, paramsDecline, context).stringPost(TAG, new Helper() {
            @Override
            public void backResponse(boolean flag, String msg, JSONObject response) {
                ProjectUtils.pauseProgressDialog();
                if (flag) {
                    ProjectUtils.showToast(context, msg);
                    newBookings.getBookings();

                } else {
                    ProjectUtils.showToast(context, msg);
                }


            }
        });
    }

    public static class MyViewHolderSection extends RecyclerView.ViewHolder {
        public CustomTextView tvSection;

        public MyViewHolderSection(View view) {
            super(view);
            tvSection = view.findViewById(R.id.tvSection);
        }
    }

}
