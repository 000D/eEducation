package io.agora.education.classroom.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.education.R;
import io.agora.education.classroom.BaseClassActivity;
import io.agora.rtc.IRtcEngineEventHandler;

public class TitleView extends ConstraintLayout {

    @Nullable
    @BindView(R.id.ic_wifi)
    protected ImageView ic_wifi;
    @BindView(R.id.tv_room_name)
    protected TextView tv_room_name;
    @Nullable
    @BindView(R.id.time_view)
    protected TimeView time_view;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        ButterKnife.bind(this);
    }

    private void init() {
        int layoutResId;
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutResId = R.layout.layout_title_portrait;
        } else {
            layoutResId = R.layout.layout_title_landscape;
        }
        LayoutInflater.from(getContext()).inflate(layoutResId, this, true);
    }

    public void setTitle(String title) {
        tv_room_name.setText(title);
    }

    public void setWifiState(IRtcEngineEventHandler.RtcStats stats) {
        if (ic_wifi != null) {
            if (stats.rxPacketLossRate > 30 || stats.txPacketLossRate > 30) {
                ic_wifi.setColorFilter(getResources().getColor(R.color.red_FF0D19));
            } else {
                ic_wifi.clearColorFilter();
            }
        }
    }

    public void setTimeState(boolean start) {
        if (time_view != null) {
            if (start) {
                if (!time_view.isStarted())
                    time_view.start();
            } else {
                time_view.stop();
            }
        }
    }

    @OnClick(R.id.iv_close)
    public void onClock(View view) {
        Context context = getContext();
        if (context instanceof BaseClassActivity) {
            ((BaseClassActivity) context).showLeaveDialog();
        }
    }

}
