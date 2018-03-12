package com.example.zhangyl.myapplication.View;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zhangyl.myapplication.Presenter.DeviceMgr;
import com.example.zhangyl.myapplication.Presenter.MeetingDevice;
import com.example.zhangyl.myapplication.Presenter.ModeCfgMgr;
import com.example.zhangyl.myapplication.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InnerMeetingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnerMeetingFragment extends Fragment {
    // TODO: 初始化界面、启动倒计时、各按钮响应事件实现
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "InnerMeetingFragment";
    @BindView(R.id.room_name_title)
    TextView roomNameTitle;
    @BindView(R.id.room_state)
    TextView roomState;
    @BindView(R.id.room_user)
    TextView roomUser;
    @BindView(R.id.text_clock)
    TextView textClock;
    @BindView(R.id.btn_sys_cfg)
    Button btnSysCfg;
    @BindView(R.id.btn_stop_meeting)
    Button btnStopMeeting;
    @BindView(R.id.btn_add_meeting_time)
    Button btnAddMeetingTime;
    @BindView(R.id.meeting_title)
    TextView meetingTitle;
    @BindView(R.id.meeting_running)
    TextView meetingRunning;
    @BindView(R.id.meeting_left_time)
    TextView meetingLeftTime;
    @BindView(R.id.meeting_progress_bar)
    ProgressBar meetingProgressBar;
    @BindView(R.id.dev_list_view)
    ListView devListView;
    Unbinder unbinder;

    DevCtrlListAdapter mListAdapter ;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private List<DevCtrlListViewItem> mDevList = new ArrayList<>();

    public InnerMeetingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InnerMeetingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InnerMeetingFragment newInstance(String param1, String param2) {
        InnerMeetingFragment fragment = new InnerMeetingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        initDevCfgList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inner_meeting, container, false);
        unbinder = ButterKnife.bind(this, view);
        mListAdapter = new DevCtrlListAdapter(getActivity(), R.layout.mode_cfg_dev_item, mDevList);
        devListView.setAdapter(mListAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            Log.i(TAG, "onAttach:  must implement OnFragmentInteractionListener");
        }

    }

    private void initDevCfgList(){
        mDevList.clear();
        Log.i(TAG, "initDevCfgList: devlist size " + DeviceMgr.getInstance().device_list.size() );
        for (MeetingDevice device :
                DeviceMgr.getInstance().device_list) {

            DevCtrlListViewItem listItem = new DevCtrlListViewItem();
            listItem.devInfo = device;

            for (ModeCfgDbItem cfg : ModeCfgMgr.getInstance().getCfgList()){
                if (cfg.getMIoPort() == device.getLIoPort()){
                    listItem.modeCfg = cfg;
                    break;
                }
            }

            if (listItem.modeCfg == null) {
                listItem.modeCfg = new ModeCfgDbItem();
            }
            mDevList.add(listItem);
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

    @OnClick(R.id.btn_sys_cfg)
    public void onBtnSysCfgClicked() {
    }

    @OnClick(R.id.btn_stop_meeting)
    public void onBtnStopMeetingClicked() {
    }

    @OnClick(R.id.btn_add_meeting_time)
    public void onBtnAddMeetingTimeClicked() {
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
