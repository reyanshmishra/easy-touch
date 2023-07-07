package com.reyansh.easytouch.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.colorpicker.ColorPickerPalette;
import com.android.colorpicker.ColorPickerSwatch;
import com.reyansh.easytouch.Common;
import com.reyansh.easytouch.R;
import com.reyansh.easytouch.Utils.PreferencesHelper;


/**
 * Created by reyansh on 12/18/17.
 */

public class IconsDialog extends DialogFragment {


    private Common mApp;
    private TypedArray icons;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_icon, null);
        icons = getResources().obtainTypedArray(R.array.icons);


        mApp = (Common) getActivity().getApplicationContext();
        builder.setView(R.layout.dialog_icon);
        builder.setTitle(R.string.select_icon);
        builder.setView(view);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new IconsDrawable());


        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }


    class IconsDrawable extends RecyclerView.Adapter<IconsDrawable.ItemHolder> {

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_icon, parent, false));
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.mIconImage.setImageResource(icons.getResourceId(position, -1));
        }

        @Override
        public int getItemCount() {
            return icons.length();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            private ImageView mIconImage;

            public ItemHolder(View itemView) {
                super(itemView);
                mIconImage = itemView.findViewById(R.id.icon_image);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mApp.isServiceRunning) {
                            PreferencesHelper.getInstance().put(PreferencesHelper.Key.ICON_DRAWABLE, icons.getResourceId(getAdapterPosition(), -1));
                            mApp.getOverLayService().setIcon(icons.getResourceId(getAdapterPosition(), -1));
                        }
                    }
                });
            }
        }
    }
}
