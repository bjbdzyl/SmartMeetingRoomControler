package com.example.zhangyl.myapplication.View;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangyl.myapplication.Presenter.DeviceChangeEvent;
import com.example.zhangyl.myapplication.Presenter.DeviceMgr;
import com.example.zhangyl.myapplication.Presenter.DlgAddDevice;
import com.example.zhangyl.myapplication.Presenter.MeetingDevice;
import com.example.zhangyl.myapplication.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StepTwoFragment.OnStepTwoFragmentCallBack} interface
 * to handle interaction events.
 * Use the {@link StepTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StepTwoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "StepTwoFragment";
    @BindView(R.id.dev_list_view)
    ListView devListView;
    @BindView(R.id.list_view_title)
    TextView listViewTitle;
    @BindView(R.id.btn_add_new_dev)
    Button btnAddNewDev;
    @BindView(R.id.btn_back)
    Button btnBack;
    @BindView(R.id.btn_next)
    Button btnNext;
    Unbinder unbinder;

    CommonAdapter<MeetingDevice> mListAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnStepTwoFragmentCallBack mListener;

    public StepTwoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StepTwoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StepTwoFragment newInstance(String param1, String param2) {
        StepTwoFragment fragment = new StepTwoFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_step_two, container, false);
        unbinder = ButterKnife.bind(this, view);
        mListAdapter = new CommonAdapter<MeetingDevice>(this.getContext(), R.layout.dev_list_item, DeviceMgr.getInstance().device_list) {
            @Override
            protected void convert(ViewHolder viewHolder, final MeetingDevice item, int position) {
                viewHolder.setText(R.id.dev_name, item.getStrDevName());
                viewHolder.setText(R.id.dev_mode, item.getStrDevMode());
                viewHolder.setText(R.id.dev_position, item.getStrPosition());
                viewHolder.setText(R.id.dev_io_port, Long.toString(item.getLIoPort()));
                viewHolder.getView(R.id.dev_item_del_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DeviceMgr.getInstance().delDevice(item);
                    }
                });
            }
        };
        devListView.setAdapter(mListAdapter);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStepTwoFragmentCallBack) {
            mListener = (OnStepTwoFragmentCallBack) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.list_view_title)
    public void onListViewTitleClicked() {
    }

    @OnClick(R.id.btn_add_new_dev)
    public void onBtnAddNewDevClicked() {
        new DlgAddDevice().show(getFragmentManager(), DlgAddDevice.TAG);
    }

    @OnClick(R.id.btn_back)
    public void onBtnBackClicked() {
        mListener.onStepTwoCancel();
    }

    @OnClick(R.id.btn_next)
    public void onBtnNextClicked() {
        mListener.onStepTwoOk();
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
    public interface OnStepTwoFragmentCallBack {
        // TODO: Update argument type and name
        void onStepTwoOk();

        void onStepTwoCancel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeviceChangeEvent(DeviceChangeEvent event) {
        if(event.nType == DeviceChangeEvent.EVT_ADD_DEV) {
            mListAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "fragment, add new device " + event.event_param.getStrDevName() + " " + event.event_param.getLIoPort(), Toast.LENGTH_SHORT).show();
        }

        if(event.nType == DeviceChangeEvent.EVT_DEL_DEV){
            Toast.makeText(getActivity(), "fragment, del new device " + event.event_param.getStrDevName() + " " + event.event_param.getLIoPort(), Toast.LENGTH_SHORT).show();
        }

        if(event.nType == DeviceChangeEvent.EVT_UPT_DEV){
            mListAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "fragment, update device " + event.event_param.getStrDevName() + " " + event.event_param.getLIoPort(), Toast.LENGTH_SHORT).show();
        }
    }
}
