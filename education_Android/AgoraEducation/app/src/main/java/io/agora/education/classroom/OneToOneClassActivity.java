package io.agora.education.classroom;

import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.education.R;
import io.agora.education.classroom.annotation.ClassType;
import io.agora.education.classroom.bean.msg.ChannelMsg;
import io.agora.education.classroom.bean.msg.PeerMsg;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.strategy.channel.ChannelEventListener;
import io.agora.education.classroom.widget.RtcVideoView;
import io.agora.education.classroom.widget.TitleView;
import io.agora.base.ToastManager;
import io.agora.rtc.Constants;

public class OneToOneClassActivity extends BaseClassActivity implements ChannelEventListener {

    @BindView(R.id.title_view)
    protected TitleView title_view;
    @BindView(R.id.layout_video_teacher)
    protected RtcVideoView video_teacher;
    @BindView(R.id.layout_video_student)
    protected RtcVideoView video_student;
    @BindView(R.id.layout_im)
    protected View layout_im;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_one2one_class;
    }

    @Override
    protected void initView() {
        super.initView();
        title_view.setTitle(getRoomName());

        video_teacher.init(R.layout.item_user_video_one_to_one, false);
        video_student.init(R.layout.item_user_video_one_to_one, true);
        video_student.setOnClickAudioListener(v -> classContext.muteLocalAudio(!video_student.isAudioMuted()));
        video_student.setOnClickVideoListener(v -> classContext.muteLocalVideo(!video_student.isVideoMuted()));
    }

    @Override
    protected Student getLocal() {
        return new Student(getMyUserId(), getMyName(), Constants.CLIENT_ROLE_BROADCASTER);
    }

    @Override
    protected int getClassType() {
        return ClassType.ONE2ONE;
    }

    @OnClick(R.id.iv_float)
    public void onClick(View view) {
        boolean isSelected = view.isSelected();
        view.setSelected(!isSelected);
        layout_im.setVisibility(isSelected ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onChannelInfoInit() {
        if (classContext.channelStrategy.getTeacher() == null) {
            ToastManager.showShort(R.string.There_is_no_teacher_in_this_classroom);
        }
    }

    @Override
    public void onLocalChanged(Student local) {
        video_student.setName(local.account);
        video_student.showLocal();
        video_student.muteVideo(local.video == 0);
        video_student.muteAudio(local.audio == 0);

        chatRoomFragment.setEditTextEnable(local.chat == 1);
    }

    @Override
    public void onTeacherChanged(Teacher teacher) {
        title_view.setTimeState(teacher.class_state == 1);

        video_teacher.setName(teacher.account);
        video_teacher.showRemote(teacher.uid);
        video_teacher.muteVideo(teacher.video == 0);
        video_teacher.muteAudio(teacher.audio == 0);

        joinWhiteboard(teacher.whiteboard_uid);
        chatRoomFragment.setEditTextEnable(teacher.mute_chat == 0);
    }

    @Override
    public void onStudentsChanged(List<Student> students) {

    }

    @Override
    public void onChannelMsgReceived(ChannelMsg msg) {
        chatRoomFragment.addMessage(msg);
    }

    @Override
    public void onPeerMsgReceived(PeerMsg msg) {

    }

    @Override
    public void onScreenShareJoined(int uid) {
        showScreenShare(uid);
    }

    @Override
    public void onScreenShareOffline(int uid) {
        dismissScreenShare(uid);
    }

}
