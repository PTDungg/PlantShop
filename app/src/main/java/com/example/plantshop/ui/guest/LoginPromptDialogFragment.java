package com.example.plantshop.ui.guest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.plantshop.R;
import com.example.plantshop.ui.auth.LoginActivity;
import com.example.plantshop.ui.auth.SigupActivity;

public class LoginPromptDialogFragment extends DialogFragment {

    public static String TAG = "LoginPromptDialog";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_prompt, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnLogin).setOnClickListener(v -> {
            // Chuyển đến màn hình Đăng nhập
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            dismiss();
        });

        view.findViewById(R.id.btnRegister).setOnClickListener(v -> {
            // Chuyển đến màn hình Đăng ký
            Intent intent = new Intent(getActivity(), SigupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            dismiss();
        });
    }
}