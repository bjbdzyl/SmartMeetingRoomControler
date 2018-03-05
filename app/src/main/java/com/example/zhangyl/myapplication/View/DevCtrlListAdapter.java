package com.example.zhangyl.myapplication.View;

import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.zhangyl.myapplication.Presenter.DevStateChangeEvent;
import com.example.zhangyl.myapplication.R;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class DevCtrlListAdapter extends CommonAdapter<DevCtrlListViewItem>{

    private int nMode;

    public DevCtrlListAdapter(Context context, int layoutId, List<DevCtrlListViewItem> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(final ViewHolder viewHolder, final DevCtrlListViewItem item, int position) {
        viewHolder.setText(R.id.dev_name, item.devInfo.getStrDevName());
        viewHolder.setText(R.id.dev_mode, item.devInfo.getStrDevMode());
        viewHolder.setText(R.id.dev_position, item.devInfo.getStrPosition());
        viewHolder.setText(R.id.dev_io_port, Long.toString(item.devInfo.getLIoPort()));
        Switch on_off_btn = viewHolder.getView(R.id.dev_switch);

        final SeekBar seek_bar = viewHolder.getView(R.id.dev_mode_item_seek_bar);
        final TextView seek_bar_title = viewHolder.getView(R.id.seek_bar_title);

        boolean isOn = item.modeCfg.getOnOffCfg(nMode);
        on_off_btn.setChecked(isOn);
        int seek_bar_value = item.modeCfg.getSeekBarValueCfg(nMode);

        seek_bar.setVisibility(!isOn ? View.INVISIBLE: View.VISIBLE);
        seek_bar_title.setVisibility(!isOn ? View.INVISIBLE: View.VISIBLE);
        seek_bar.setProgress(seek_bar_value);

        on_off_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                seek_bar.setVisibility(!isChecked ? View.INVISIBLE: View.VISIBLE);
                seek_bar_title.setVisibility(!isChecked ? View.INVISIBLE: View.VISIBLE);
                EventBus.getDefault().post(new DevStateChangeEvent(item.devInfo.getLIoPort(), isChecked, seek_bar.getProgress()));
            }
        });
    }

    public void setnMode(int nMode) {
        this.nMode = nMode;
    }
}
