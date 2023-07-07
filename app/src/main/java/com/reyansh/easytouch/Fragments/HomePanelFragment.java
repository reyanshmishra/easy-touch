package com.reyansh.easytouch.Fragments;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.Dialogs.HomePanelDialog;
import com.reyansh.easytouch.Models.EasyAppsModel;
import com.reyansh.easytouch.R;

import java.util.ArrayList;

/**
 * Created by reyansh on 1/27/18.
 */

public class HomePanelFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private View mView;
    private Common mCommon;
    private ArrayList<EasyAppsModel> mEasyAppsModels;
    private String mForWhich;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_home_panel, container, false);
        mRecyclerView = mView.findViewById(R.id.recycler_view);


        mCommon = (Common) getActivity().getApplicationContext();
        mForWhich = getArguments().getString("FOR_WHICH");

        if (mForWhich.equalsIgnoreCase("HOME")) {
            mEasyAppsModels = mCommon.getDBAccessHelper().getEasyApps();
        } else if (mForWhich.equalsIgnoreCase("FAVORITES")) {
            mEasyAppsModels = mCommon.getDBAccessHelper().getInstalledApps();
        } else {
            mEasyAppsModels = mCommon.getDBAccessHelper().getEasyDeviceApps();
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setAdapter(new HomePanelAdapter());
        return mView;
    }


    public class HomePanelAdapter extends RecyclerView.Adapter<HomePanelAdapter.ItemHolder> {


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
                        String appName = mEasyAppsModels.get(getAdapterPosition()).mAppName;
                        if (appName.equalsIgnoreCase("Favorites") || appName.equalsIgnoreCase("Device") || appName.equalsIgnoreCase("FAvBack")) {
                            Toast.makeText(Common.getInstance(), R.string.can_not_change_this_position, Toast.LENGTH_SHORT).show();
                        } else {
                            HomePanelDialog homePanelDialog = new HomePanelDialog();
                            homePanelDialog.setOnDismissListener(new HomePanelDialog.OnDismiss() {
                                @Override
                                public void dismissed() {
                                    if (mForWhich.equalsIgnoreCase("HOME")) {
                                        mEasyAppsModels = mCommon.getDBAccessHelper().getEasyApps();
                                    } else if (mForWhich.equalsIgnoreCase("FAVORITES")) {
                                        mEasyAppsModels = mCommon.getDBAccessHelper().getInstalledApps();
                                    } else {
                                        mEasyAppsModels = mCommon.getDBAccessHelper().getEasyDeviceApps();
                                    }
                                    mRecyclerView.getAdapter().notifyDataSetChanged();
                                }
                            });
                            Bundle bundle = new Bundle();
                            bundle.putInt("ORDER_WEIGHT", mEasyAppsModels.get(getAdapterPosition()).mOrderWeight);
                            bundle.putString("APP_NAME", mEasyAppsModels.get(getAdapterPosition()).mAppName);
                            bundle.putString("FOR_WHICH", mForWhich);

                            homePanelDialog.setArguments(bundle);
                            homePanelDialog.show(getFragmentManager(), ":");
                        }
                    }
                });
            }
        }
    }


}
