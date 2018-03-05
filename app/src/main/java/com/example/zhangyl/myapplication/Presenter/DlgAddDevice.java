package com.example.zhangyl.myapplication.Presenter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.zhangyl.myapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ZhangYL on 2018/1/19 0019.
 */

public class DlgAddDevice extends DialogFragment {
    public static final String TAG = "DlgAddDevice";
    @BindView(R.id.text_dev_name)
    EditText textDevName;
    @BindView(R.id.io_port)
    EditText ioPort;
    @BindView(R.id.dev_mode)
    EditText devMode;
    @BindView(R.id.dev_position)
    EditText devPosition;
    @BindView(R.id.spinner_ctrl_method)
    Spinner spinnerCtrlMethod;
    @BindView(R.id.okBtn)
    Button okBtn;
    @BindView(R.id.cancelBtn)
    Button cancelBtn;

    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public interface Callback {
        void onClick(String userName, String password);
    }

    private RegisterMgr reg_info = RegisterMgr.get_instance();

    private Callback callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dlg_add_device, null);
        unbinder = ButterKnife.bind(this, view);
        builder.setView(view);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textDevName.getText().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "请输入设备名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (devMode.getText().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "请输入设备型号", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (devPosition.getText().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "请输入安装位置", Toast.LENGTH_SHORT).show();

                    return;
                }
                if (ioPort.getText().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "请输入通信端口号", Toast.LENGTH_SHORT).show();
                    return;
                }

                DeviceMgr.getInstance().add_device(
                        new MeetingDevice(textDevName.getText().toString(),
                                devPosition.getText().toString(),
                                devMode.getText().toString(),
                                spinnerCtrlMethod.getSelectedItemPosition()+1,
                                Integer.parseInt(ioPort.getText().toString())
                        )
                );
                dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof Callback) {
//            callback = (Callback) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement Callback");
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callback = null;
    }
}
