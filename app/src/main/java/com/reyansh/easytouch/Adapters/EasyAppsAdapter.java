package com.reyansh.easytouch.Adapters;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.Models.EasyAppsModel;
import com.reyansh.easytouch.R;

import java.util.ArrayList;

/**
 * Created by reyansh on 1/27/18.
 */

public class EasyAppsAdapter extends RecyclerView.Adapter<EasyAppsAdapter.ItemHolder> {

    private ArrayList<EasyAppsModel> mEasyAppsModels;
    private Common mCommon;

    public EasyAppsAdapter() {
        mCommon = ((Common) Common.getInstance());
        mEasyAppsModels = mCommon.getDBAccessHelper().getEasyApps();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_easy_app, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        if (mEasyAppsModels.get(position).mPackageName != null && mEasyAppsModels.get(position).mPackageName.equals("")) {


            if (mEasyAppsModels.get(position).mAppName.equalsIgnoreCase("FAvBack")) {
                holder.mTextView.setVisibility(View.GONE);
            } else {
                holder.mTextView.setText(mEasyAppsModels.get(position).mAppName);
                holder.mTextView.setVisibility(View.VISIBLE);
            }

            if (mEasyAppsModels.get(position).mAppName.equalsIgnoreCase("Wifi")) {
                if (mCommon.getOverLayService().isWifiEnabled()) {
                    holder.mImageIcon.setImageResource(R.drawable.ic_network_wifi);
                } else {
                    holder.mImageIcon.setImageResource(mEasyAppsModels.get(position).mAppIconId);
                }
            } else if (mEasyAppsModels.get(position).mAppName.equalsIgnoreCase("Auto Rotate")) {
                if (mCommon.getOverLayService().getRotationScreenFromSettingsIsEnabled()) {
                    holder.mImageIcon.setImageResource(mEasyAppsModels.get(position).mAppIconId);
                } else {
                    holder.mImageIcon.setImageResource(R.drawable.ic_device);
                }
            } else if (mEasyAppsModels.get(position).mAppName.equalsIgnoreCase("Flashlight")) {
                if (mCommon.getOverLayService().isFlashLightOn) {
                    holder.mImageIcon.setImageResource(R.drawable.ic_bulb_on);
                } else {
                    holder.mImageIcon.setImageResource(mEasyAppsModels.get(position).mAppIconId);
                }
            } else if (mEasyAppsModels.get(position).mAppName.equalsIgnoreCase("Bluetooth")) {
                if (mCommon.getOverLayService().isBluetoothOn()) {
                    holder.mImageIcon.setImageResource(R.drawable.ic_bluetooth_connected);
                } else {
                    holder.mImageIcon.setImageResource(mEasyAppsModels.get(position).mAppIconId);
                }
            } else {
                holder.mImageIcon.setImageResource(mEasyAppsModels.get(position).mAppIconId);

            }

        } else if (!mEasyAppsModels.get(position).mPackageName.equalsIgnoreCase("")) {
            holder.mTextView.setText(mEasyAppsModels.get(position).mAppName);
            try {
                Drawable icon = Common.getInstance().getPackageManager().getApplicationIcon(mEasyAppsModels.get(position).mPackageName);
                holder.mImageIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }


    }

    public void updateData(ArrayList<EasyAppsModel> easyAppsModel) {
        mEasyAppsModels = easyAppsModel;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mEasyAppsModels.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        private ImageView mImageIcon;
        private TextView mTextView;

        public ItemHolder(View itemView) {
            super(itemView);
            mImageIcon = itemView.findViewById(R.id.icon_image);
            mTextView = itemView.findViewById(R.id.app_name);
            GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
            params.width = Common.convertDpToPixels(250) / 3;
            params.height = Common.convertDpToPixels(250) / 3;
            itemView.setLayoutParams(params);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCommon.getOverLayService().performAction(mEasyAppsModels.get(getAdapterPosition()));
                }
            });
        }
    }

}
