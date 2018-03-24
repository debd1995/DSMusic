package com.debd.kgp.dsmusic.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.debd.kgp.dsmusic.R;

public class MessageDialog extends DialogFragment {

    private String MESSAGE_TITLE = "MESSAGE";
    private String MESSAGE_DESC = "Something Wrong happend.";

    public void setTitle(String title) {
        this.MESSAGE_TITLE = title;
    }
    public void setDescription(String description) {
        this.MESSAGE_DESC = description;
    }

    private OnMessageDialogDismissedListener listener = null;
    private String key;

    public void setOnMessageDialogDismissedListener(String key, OnMessageDialogDismissedListener listener) {
        this.key = key;
        this.listener = listener;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if(dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(retain);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.message_dialog_view);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        TextView txtTitle = dialog.findViewById(R.id.error_title);
        TextView txtDesc = dialog.findViewById(R.id.error_description);
        LinearLayout btnOK = dialog.findViewById(R.id.btn_ok);

        txtTitle.setText(MESSAGE_TITLE);
        txtDesc.setText(MESSAGE_DESC);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) {
                    listener.dialogDismissed(key);
                }
                dialog.dismiss();
            }
        });

        return dialog;
    }

    public interface OnMessageDialogDismissedListener {
        public void dialogDismissed(String key);
    }
}
