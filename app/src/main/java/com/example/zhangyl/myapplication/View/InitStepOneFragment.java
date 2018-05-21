package com.example.zhangyl.myapplication.View;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zhangyl.myapplication.R;
import com.example.zhangyl.myapplication.Presenter.RegisterMgr;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnStepOneFragmentCallBack} interface
 * to handle interaction events.
 * Use the {@link InitStepOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InitStepOneFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "InitStepOneFragment";
    @BindView(R.id.IP_VALUE)
    EditText IPVALUE;
    @BindView(R.id.ROOM_NAME_VALUE)
    EditText ROOMNAMEVALUE;
    @BindView(R.id.ROOM_NUMBER_VALUE)
    EditText ROOMNUMBERVALUE;
    @BindView(R.id.ROOM_SIZE_VALUE)
    EditText ROOMSIZEVALUE;
    @BindView(R.id.SEVER_ADDRESS_VALUE)
    EditText SEVERADDRESSVALUE;
    @BindView(R.id.BTN_NEXT)
    Button BTNNEXT;
    Unbinder unbinder;

    private String mParam1;
    private String mParam2;

    private OnStepOneFragmentCallBack mListener;

    public InitStepOneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InitStepOneFragment.
     */
    public static InitStepOneFragment newInstance(String param1, String param2) {
        InitStepOneFragment fragment = new InitStepOneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void initViewValues(){
        RegisterMgr reg_mgr = RegisterMgr.get_instance();
        IPVALUE.setText(reg_mgr.roomInfo.getControler_ip());
        ROOMSIZEVALUE.setText(new Integer(reg_mgr.roomInfo.getRoom_size()).toString());
        ROOMNAMEVALUE.setText(reg_mgr.roomInfo.getRoom_name());
        SEVERADDRESSVALUE.setText(reg_mgr.getCloud_host());
        ROOMNUMBERVALUE.setText(reg_mgr.roomInfo.getRoom_number());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_init_step_one, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViewValues();
        return view;
    }

//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStepOneFragmentCallBack) {
            mListener = (OnStepOneFragmentCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.BTN_NEXT)
    public void onNextClicked() {

        if (0 == IPVALUE.getText().toString().length()) {
            Toast.makeText(getActivity().getApplicationContext(), "请输入中控机IP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (0 == ROOMSIZEVALUE.getText().toString().length()) {
            Toast.makeText(getActivity().getApplicationContext(), "请输入容纳人数", Toast.LENGTH_SHORT).show();
            return;
        }

        if (0 == ROOMNUMBERVALUE.getText().toString().length()){
            Toast.makeText(getActivity().getApplicationContext(), "请输入房号", Toast.LENGTH_SHORT).show();
            return;
        }

        if (0 == SEVERADDRESSVALUE.getText().toString().length()){
            Toast.makeText(getActivity().getApplicationContext(), "请输入服务器地址", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterMgr reg_info = RegisterMgr.get_instance();
        reg_info.roomInfo.setControler_ip(IPVALUE.getText().toString());
        reg_info.roomInfo.setRoom_name(ROOMNAMEVALUE.getText().toString());
        reg_info.roomInfo.setRoom_number(ROOMNUMBERVALUE.getText().toString());
        reg_info.roomInfo.setRoom_size(Integer.parseInt(ROOMSIZEVALUE.getText().toString()));
        reg_info.setCloud_host(SEVERADDRESSVALUE.getText().toString());

        reg_info.save_room_info();

        mListener.onStepOneOk();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnStepOneFragmentCallBack {
        //void onFragmentInteraction(Uri uri);
        void onStepOneOk();
    }
}
