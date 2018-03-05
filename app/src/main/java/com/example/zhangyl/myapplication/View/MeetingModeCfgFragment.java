package com.example.zhangyl.myapplication.View;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zhangyl.myapplication.Presenter.CloudApi;
import com.example.zhangyl.myapplication.Presenter.DeviceChangeEvent;
import com.example.zhangyl.myapplication.Presenter.DeviceMgr;
import com.example.zhangyl.myapplication.Presenter.MeetingDevice;
import com.example.zhangyl.myapplication.Presenter.ModeCfgChangeEvent;
import com.example.zhangyl.myapplication.Presenter.ModeCfgMgr;
import com.example.zhangyl.myapplication.R;
import com.example.zhangyl.myapplication.Presenter.RegisterMgr;
import com.zhy.adapter.abslistview.CommonAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MeetingModeCfgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeetingModeCfgFragment extends Fragment {
    // TODO: 把逻辑抽成presenter，M  V 分层，用Dagger进行依赖流入以便解耦
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "MeetingModeFragment";
    @BindView(R.id.tabs)
    TabLayout tabs; // todo 替换成 https://github.com/hackware1993/MagicIndicator
    @BindView(R.id.container)
    ViewPager viewPager;
    @BindView(R.id.register_meeting_room)
    Button registerMeetingRoom;
    @BindView(R.id.back_btn)
    Button btn_back;
    @BindView(R.id.reg_prog_bar)
    SmoothProgressBar mProgBar;

    Unbinder unbinder;

    private SubViewAdapter adapter;
    private List<View> list_sub_frags;
    private String[] titles = {"会前", "会中", "会后"};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public MeetingModeCfgFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeetingModeCfgFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeetingModeCfgFragment newInstance(String param1, String param2) {
        MeetingModeCfgFragment fragment = new MeetingModeCfgFragment();
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
        View view = inflater.inflate(R.layout.fragment_meeting_mode_cfg, container, false);
        unbinder = ButterKnife.bind(this, view);
        tabs.setupWithViewPager(viewPager);
        list_sub_frags = new ArrayList<>();
        View viewBeforeMeeting = inflater.inflate(R.layout.fragment_mode_cfg_before_meeting, null);
        ModeCfgListAdapter before_adapter = new ModeCfgListAdapter(getActivity(), R.layout.mode_cfg_dev_item, mDevCfgList);
        before_adapter.setnMode(ModeCfgDbItem.MODE_BEFORE_MEETING);
        ((ListView) viewBeforeMeeting.findViewById(R.id.dev_list_view)).setAdapter(before_adapter);
        list_sub_frags.add(viewBeforeMeeting);


        View viewInMeeting = inflater.inflate(R.layout.fragment_mode_cfg_before_meeting, null);
        ModeCfgListAdapter in_adapter = new ModeCfgListAdapter(getActivity(), R.layout.mode_cfg_dev_item, mDevCfgList);
        in_adapter.setnMode(ModeCfgDbItem.MODE_IN_MEETING);
        ((ListView) viewInMeeting.findViewById(R.id.dev_list_view)).setAdapter(in_adapter);
        list_sub_frags.add(viewInMeeting);


        View viewAfterMeeting = inflater.inflate(R.layout.fragment_mode_cfg_before_meeting, null);
        ModeCfgListAdapter after_adapter = new ModeCfgListAdapter(getActivity(), R.layout.mode_cfg_dev_item, mDevCfgList);
        after_adapter.setnMode(ModeCfgDbItem.MODE_AFTER_MEETING);
        ((ListView) viewAfterMeeting.findViewById(R.id.dev_list_view)).setAdapter(after_adapter);
        list_sub_frags.add(viewAfterMeeting);

        adapter = new SubViewAdapter();
        viewPager.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.register_meeting_room)
    public void onRegBtnClicked() {
        // 配置入库。调用RegisterMgr进行注册。异步执行，动画进度。
        Log.i(TAG, "onRegBtnClicked: try to show progress bar");
        mProgBar.setVisibility(View.VISIBLE);
        //RegisterMgr.get_instance().DoRegister();
        EventBus.getDefault().post(new RegisterMgr.RegisterEvent(RegisterMgr.RegisterEvent.START_REGISTER, null));
    }

    @OnClick(R.id.back_btn)
    public void onBtnBack(){
        //TODO 返回上一步
        mCallBack.onBackToStepTwo();
    }

    public interface OnModeCfgFragmentCallBack {
        void onBackToStepTwo();

        void onRegisterEnd();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnModeCfgFragmentCallBack) {
            mCallBack = (OnModeCfgFragmentCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        EventBus.getDefault().register(this);
    }
    private List<DevCtrlListViewItem> mDevCfgList = new ArrayList<>();

    /**
     * 初始化配置列表，没有配置的，按默认值初始化
     */
    private void initDevCfgList(){
        mDevCfgList.clear();
        Log.i(TAG, "initDevCfgList: devlist size " + DeviceMgr.getInstance().device_list.size() );
        for (MeetingDevice device :
                DeviceMgr.getInstance().device_list) {

            DevCtrlListViewItem listItem = new DevCtrlListViewItem();
            listItem.devInfo = device;

            for (ModeCfgDbItem cfg : ModeCfgMgr.getInstance().mCfgList){
                if (cfg.getMIoPort() == device.getLIoPort()){
                    listItem.modeCfg = cfg;
                    break;
                }
            }

            if (listItem.modeCfg == null) {
                listItem.modeCfg = new ModeCfgDbItem();
            }
            mDevCfgList.add(listItem);
        }
    }

    private OnModeCfgFragmentCallBack mCallBack;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeviceChangeEvent event) {
        initDevCfgList();
        for (View subView :
                list_sub_frags) {
            CommonAdapter<ModeCfgDbItem> adapter = (CommonAdapter<ModeCfgDbItem>)((ListView)subView.findViewById(R.id.dev_list_view)).getAdapter();
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onCfgChangeEvent(ModeCfgChangeEvent event){
        ModeCfgMgr.getInstance().updateCfg(event.getParam());
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onStartRegisterEvent(RegisterMgr.RegisterEvent event){
        if(event.mState == RegisterMgr.RegisterEvent.START_REGISTER){
            Log.i(TAG, "onStartRegisterEvent: get register msg" + event.mState);
            RegisterMgr.get_instance().DoRegister();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEndRegisterEvent(RegisterMgr.RegisterEvent event){
        if (event.mState == RegisterMgr.RegisterEvent.END_REGISTER){
            Log.i(TAG, "onEndRegisterEvent: get regsiter msg ===== " + event.mState + event.mRegResult);
            if (event.mRegResult.equals(CloudApi.RESULT_SUCC)){
                // 首次上传设备列表
                DeviceMgr.getInstance().postDevList();
                mCallBack.onRegisterEnd();
            } else {
                Toast.makeText(getActivity(), "注册失败："+event.mRegResult, Toast.LENGTH_SHORT).show();
            }
            mProgBar.setVisibility(View.INVISIBLE);
        }
    }

    class SubViewAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return list_sub_frags.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view=list_sub_frags.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(list_sub_frags.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
