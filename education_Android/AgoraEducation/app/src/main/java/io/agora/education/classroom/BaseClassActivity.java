package io.agora.education.classroom;

import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import butterknife.BindView;
import io.agora.base.ToastManager;
import io.agora.education.R;
import io.agora.education.base.BaseActivity;
import io.agora.education.classroom.annotation.ClassType;
import io.agora.education.classroom.bean.msg.ChannelMsg;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.fragment.ChatRoomFragment;
import io.agora.education.classroom.fragment.WhiteBoardFragment;
import io.agora.education.classroom.strategy.context.ClassContext;
import io.agora.education.classroom.strategy.context.ClassContextFactory;
import io.agora.education.classroom.strategy.context.ClassEventListener;
import io.agora.education.classroom.widget.TitleView;
import io.agora.education.widget.ConfirmDialog;
import io.agora.rtc.video.VideoCanvas;
import io.agora.sdk.annotation.NetworkQuality;
import io.agora.sdk.manager.RtcManager;

public abstract class BaseClassActivity extends BaseActivity implements ClassEventListener {

    public static final String ROOM_NAME = "room_name";
    public static final String ROOM_NAME_REAL = "room_name_real";
    public static final String YOUR_NAME = "your_name";
    public static final String USER_ID = "user_id";

    @BindView(R.id.title_view)
    protected TitleView title_view;
    @BindView(R.id.layout_whiteboard)
    protected FrameLayout layout_whiteboard;
    @BindView(R.id.layout_share_video)
    protected FrameLayout layout_share_video;

    protected SurfaceView surface_share_video;

    protected WhiteBoardFragment whiteboardFragment = new WhiteBoardFragment();
    protected ChatRoomFragment chatRoomFragment = new ChatRoomFragment();

    public ClassContext classContext;

    @Override
    protected void initData() {
        initStrategy();
    }

    @Override
    protected void initView() {
        title_view.setTitle(getRoomName());

        getSupportFragmentManager().beginTransaction()
                .remove(whiteboardFragment)
                .remove(chatRoomFragment)
                .commitNow();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_whiteboard, whiteboardFragment)
                .add(R.id.layout_chat_room, chatRoomFragment)
                .show(chatRoomFragment)
                .commit();
    }

    protected final void initStrategy() {
        classContext = new ClassContextFactory(this).getClassContext(getClassType(), getRoomNameReal(), getLocal());
        classContext.setClassEventListener(this);
        classContext.joinChannel();
    }

    protected abstract Student getLocal();

    @ClassType
    protected abstract int getClassType();

    @Override
    protected void onDestroy() {
        classContext.release();
        whiteboardFragment.releaseBoard();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showLeaveDialog();
    }

    public void showLeaveDialog() {
        ConfirmDialog.newInstance(new ConfirmDialog.DialogClickListener() {
            @Override
            public void clickConfirm() {
                finish();
            }

            @Override
            public void clickCancel() {

            }
        }, getString(R.string.confirm_leave_room_content)).show(getSupportFragmentManager(), "leave");
    }

    public String getRoomName() {
        return getIntent().getStringExtra(ROOM_NAME);
    }

    public String getRoomNameReal() {
        return getIntent().getStringExtra(ROOM_NAME_REAL);
    }

    public int getMyUserId() {
        return getIntent().getIntExtra(USER_ID, 0);
    }

    public String getMyName() {
        return getIntent().getStringExtra(YOUR_NAME);
    }

    @Override
    public void onTeacherInit(Teacher teacher) {
        if (teacher == null) {
            ToastManager.showShort(R.string.There_is_no_teacher_in_this_classroom);
        }
    }

    @Override
    public void onNetworkQualityChanged(@NetworkQuality int quality) {
        title_view.setNetworkQuality(quality);
    }

    @Override
    public void onClassStateChanged(boolean isStart) {
        title_view.setTimeState(isStart);
    }

    @Override
    public void onWhiteboardIdChanged(String id) {
        whiteboardFragment.initBoard(id);
    }

    @Override
    public void onMuteLocalChat(boolean muted) {
        chatRoomFragment.setMuteLocal(muted);
    }

    @Override
    public void onMuteAllChat(boolean muted) {
        chatRoomFragment.setMuteAll(muted);
    }

    @Override
    public void onChannelMsgReceived(ChannelMsg msg) {
        chatRoomFragment.addMessage(msg);
    }

    @Override
    public void onScreenShareJoined(int uid) {
        if (surface_share_video == null) {
            surface_share_video = RtcManager.instance().createRendererView(this);
        }
        layout_whiteboard.setVisibility(View.GONE);
        layout_share_video.setVisibility(View.VISIBLE);

        removeFromParent(surface_share_video);
        surface_share_video.setTag(uid);
        layout_share_video.addView(surface_share_video, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        RtcManager.instance().setupRemoteVideo(surface_share_video, VideoCanvas.RENDER_MODE_FIT, uid);
    }

    @Override
    public void onScreenShareOffline(int uid) {
        Object tag = surface_share_video.getTag();
        if (tag instanceof Integer) {
            if ((int) tag == uid) {
                layout_whiteboard.setVisibility(View.VISIBLE);
                layout_share_video.setVisibility(View.GONE);

                removeFromParent(surface_share_video);
                surface_share_video = null;
            }
        }
    }

}
