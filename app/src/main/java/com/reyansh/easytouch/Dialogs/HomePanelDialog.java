package com.reyansh.easytouch.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
 * Created by reyansh on 12/18/17.
 */

public class HomePanelDialog extends DialogFragment {


    private Common mApp;
    private ArrayList<EasyAppsModel> mEasyAppsModels;
    private int mOrderWeight;
    private String mAppName;
    private String mForWhich;
    private OnDismiss mOnDismiss;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mOrderWeight = getArguments().getInt("ORDER_WEIGHT");
        mAppName = getArguments().getString("APP_NAME");
        mForWhich = getArguments().getString("FOR_WHICH");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_icon, null);
        mApp = (Common) getActivity().getApplicationContext();

        if (mForWhich.equalsIgnoreCase("FAVORITES")) {
            mEasyAppsModels = mApp.getDBAccessHelper().getAllInstalledApps();
        } else {
            mEasyAppsModels = mApp.getDBAccessHelper().getCustomApps();
        }

        builder.setView(R.layout.dialog_icon);
        builder.setTitle(R.string.replace);
        builder.setView(view);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new IconsDrawable());
        return builder.create();
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mOnDismiss.dismissed();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        mOnDismiss.dismissed();
    }

    public void setOnDismissListener(OnDismiss onDismissListener) {
        mOnDismiss = onDismissListener;
    }

    public interface OnDismiss {
        void dismissed();
    }

    class IconsDrawable extends RecyclerView.Adapter<IconsDrawable.ItemHolder> {

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
                holder.mImageIcon.setImageResource(mEasyAppsModels.get(position).mAppIconId);

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

        @Override
        public int getItemCount() {
            return mEasyAppsModels.size();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            private ImageView mImageIcon;
            private TextView mTextView;

            public ItemHolder(final View itemView) {
                super(itemView);
                mImageIcon = itemView.findViewById(R.id.icon_image);
                mTextView = itemView.findViewById(R.id.app_name);
                if (!mForWhich.equalsIgnoreCase("FAVORITES"))
                    mImageIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.cardview_dark_background));

                GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) itemView.getLayoutParams();
                int margin = Common.convertDpToPixels(10);
                layoutParams.setMargins(margin, margin, margin, margin);
                itemView.setLayoutParams(layoutParams);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mApp.getDBAccessHelper().swapOrders(mAppName, mEasyAppsModels.get(getAdapterPosition()).mAppName,
                                mOrderWeight, mEasyAppsModels.get(getAdapterPosition()).mOrderWeight);
                        if (mApp.isServiceRunning) mApp.getOverLayService().updateData();
                        HomePanelDialog.this.dismiss();
                    }
                });
            }
        }
    }
}
