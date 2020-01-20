package io.agora.education.widget;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import io.agora.education.R;

public class ConfirmDialog extends DialogFragment {

    private String content;
    private DialogClickListener listener;

    public static ConfirmDialog newInstance(DialogClickListener listener, String content) {
        ConfirmDialog fragment = new ConfirmDialog();
        fragment.content = content;
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        listener.clickCancel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme());
        setCancelable(true);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.layout_dialog_confirm, container, false);
        root.findViewById(R.id.tv_dialog_cancel).setOnClickListener(v -> {
            listener.clickCancel();
            dismiss();
        });
        root.findViewById(R.id.tv_dialog_confirm).setOnClickListener(v -> {
            listener.clickConfirm();
            dismiss();
        });
        TextView tvContent = root.findViewById(R.id.tv_content);
        tvContent.setText(content);
        return root;
    }

    public interface DialogClickListener {
        void clickConfirm();

        void clickCancel();
    }

}
