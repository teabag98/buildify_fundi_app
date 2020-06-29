package com.mbuildify.artisan.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mbuildify.artisan.DTO.CategoryDTO;
import com.mbuildify.artisan.DTO.SkillsDTO;
import com.mbuildify.artisan.R;
import com.mbuildify.artisan.interfacess.OnSpinerItemClick;
import java.util.ArrayList;
import java.util.List;


public class SpinnerDialog {
    ArrayList<CategoryDTO> categoryDTOS;
    ArrayList<SkillsDTO> skillsDTOS;
    Activity context;
    String dTitle;
    String closeTitle = "Close";
    OnSpinerItemClick onSpinerItemClick;
    AlertDialog alertDialog;
    int style;
    ListView listView;
    MyAdapterRadio myAdapterRadio;
    int pos;
    boolean isSelected;
    public ArrayList<CategoryDTO> gradeArray;

    public SpinnerDialog(Activity activity, ArrayList<CategoryDTO> categoryDTOS, String dialogTitle) {
        this.categoryDTOS = categoryDTOS;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.gradeArray = gradeArray;
    }
   public SpinnerDialog(Activity activity, ArrayList<SkillsDTO> skillsDTOS, String dialogTitle,String closeTitle) {
        this.skillsDTOS = skillsDTOS;
        this.context = activity;
        this.dTitle = dialogTitle;
        this.closeTitle = closeTitle;
    }
    public  SpinnerDialog(Activity activity,ArrayList<CategoryDTO> gradeArray){
            this.context = activity;
            this.gradeArray = gradeArray;
    }

    public void bindOnSpinerListener(OnSpinerItemClick onSpinerItemClick1) {
        this.onSpinerItemClick = onSpinerItemClick1;
    }

    public void showGradeSpinerDialog() {
        Builder adb = new Builder(this.context);
        View v = this.context.getLayoutInflater().inflate(R.layout.dialog_layout_radio, (ViewGroup) null);
        TextView close = (TextView) v.findViewById(R.id.close);
        TextView title = (TextView) v.findViewById(R.id.spinerTitle);
        title.setText(this.dTitle);
        listView = (ListView) v.findViewById(R.id.list);
        final List<String> gradeArray = new ArrayList<String>();
        gradeArray.add("Grade 1");
        gradeArray.add("Grade 2");
        gradeArray.add("Grade 3");
        gradeArray.add("Ungraded Artisan");

        myAdapterRadio = new MyAdapterRadio(this.context, this.gradeArray);
        listView.setAdapter(myAdapterRadio);
        adb.setView(v);


        this.alertDialog = adb.create();
        this.alertDialog.getWindow().getAttributes().windowAnimations = this.style;
        this.alertDialog.setCancelable(false);

        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SpinnerDialog.this.alertDialog.dismiss();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void setSelected(
                    boolean selected) {
                isSelected = selected;
            }
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t = (TextView) view.findViewById(R.id.text1);
                String selectedID = "";
                for (int j = 0; j < gradeArray.size(); j++) {
                    if (t.getText().toString().equalsIgnoreCase(gradeArray.get(j))) {
                        pos = j;
                        selectedID = gradeArray.get(j);
                    }
                    if (j == i) {
                        gradeArray.get(j);
                    } else {
                        gradeArray.get(j);
                    }
                }
                onSpinerItemClick.onClick(t.getText().toString(), selectedID, pos);
                alertDialog.dismiss();
            }
        });


        this.alertDialog.show();
    }


    public void showSpinerDialog() {
        Builder adb = new Builder(this.context);
        View v = this.context.getLayoutInflater().inflate(R.layout.dialog_layout_radio, (ViewGroup) null);
        TextView close = (TextView) v.findViewById(R.id.close);
        TextView title = (TextView) v.findViewById(R.id.spinerTitle);
        title.setText(this.dTitle);
        listView = (ListView) v.findViewById(R.id.list);

        myAdapterRadio = new MyAdapterRadio(this.context, this.categoryDTOS);
        listView.setAdapter(myAdapterRadio);
        adb.setView(v);


        this.alertDialog = adb.create();
        this.alertDialog.getWindow().getAttributes().windowAnimations = this.style;
        this.alertDialog.setCancelable(false);

        close.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SpinnerDialog.this.alertDialog.dismiss();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView t = (TextView) view.findViewById(R.id.text1);
                String selectedID = "";
                for (int j = 0; j < categoryDTOS.size(); j++) {
                    if (t.getText().toString().equalsIgnoreCase(categoryDTOS.get(j).toString())) {
                        pos = j;
                        selectedID = categoryDTOS.get(j).getId();
                    }
                    if (j == i) {
                        categoryDTOS.get(j).setSelected(true);
                    } else {
                        categoryDTOS.get(j).setSelected(false);
                    }
                }
                onSpinerItemClick.onClick(t.getText().toString(), selectedID, pos);
                alertDialog.dismiss();
            }
        });


        this.alertDialog.show();
    }




    public class MyAdapterRadio extends BaseAdapter {

        ArrayList<CategoryDTO> arrayList;
        LayoutInflater inflater;


        public MyAdapterRadio(Context context, ArrayList<CategoryDTO> arrayList) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            public CustomTextView text1;
            public RadioButton radioBtn;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.spinner_view_radio, parent, false);
                holder.text1 = (CustomTextView) convertView.findViewById(R.id.text1);
                holder.radioBtn = (RadioButton) convertView.findViewById(R.id.radioBtn);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.text1.setText(arrayList.get(position).getCat_name());
            holder.text1.setTypeface(null, Typeface.NORMAL);

            if (arrayList.get(position).isSelected()) {
                holder.radioBtn.setChecked(true);
            } else {
                holder.radioBtn.setChecked(false);
            }


            return convertView;
        }

    }

}
