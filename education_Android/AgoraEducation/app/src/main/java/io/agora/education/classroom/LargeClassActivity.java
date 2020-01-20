package io.agora.education.classroom;

import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.agora.base.ToastManager;
import io.agora.education.R;
import io.agora.education.classroom.annotation.ClassType;
import io.agora.education.classroom.bean.msg.ChannelMsg;
import io.agora.education.classroom.bean.msg.Cmd;
import io.agora.education.classroom.bean.msg.PeerMsg;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.strategy.context.LargeClassContext;
import io.agora.education.classroom.widget.RtcVideoView;
import io.agora.education.classroom.widget.TitleView;
import io.agora.rtc.Constants;

public class LargeClassActivity extends BaseClassActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.title_view)
    protected TitleView title_view;
    @BindView(R.id.layout_video_teacher)
    protected FrameLayout layout_video_teacher;
    @BindView(R.id.layout_video_student)
    protected FrameLayout layout_video_student;
    @Nullable
    @BindView(R.id.layout_tab)
    protected TabLayout layout_tab;
    @BindView(R.id.layout_chat_room)
    protected FrameLayout layout_chat_room;
    @Nullable
    @BindView(R.id.layout_materials)
    protected FrameLayout layout_materials;
    @BindView(R.id.layout_hand_up)
    protected CardView layout_hand_up;

    private RtcVideoView video_teacher;
    private RtcVideoView video_student;

    @Override
    protected int getLayoutResId() {
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            return R.layout.activity_large_class_portrait;
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            return R.layout.activity_large_class_landscape;
        }
    }

    @Override
    protected void initView() {
        super.initView();
        title_view.setTitle(getRoomName());

        if (video_teacher == null) {
            video_teacher = new RtcVideoView(this);
            video_teacher.init(R.layout.item_user_video_large_class_teacher, false);
        }
        removeFromParent(video_teacher);
        layout_video_teacher.addView(video_teacher, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (video_student == null) {
            video_student = new RtcVideoView(this);
            video_student.init(R.layout.item_user_video_mini_class, true);
        }
        removeFromParent(video_student);
        layout_video_student.addView(video_student, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (layout_tab != null)
            layout_tab.addOnTabSelectedListener(this);

        if (surface_share_video != null) {
            removeFromParent(surface_share_video);
            layout_share_video.addView(surface_share_video, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        resetHandState();
    }

    @Override
    protected Student getLocal() {
        return new Student(getMyUserId(), getMyName(), Constants.CLIENT_ROLE_AUDIENCE);
    }

    @Override
    protected int getClassType() {
        return ClassType.LARGE;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(getLayoutResId());
        ButterKnife.bind(this);
        initView();
    }

    @OnClick(R.id.layout_hand_up)
    public void onClick(View view) {
        boolean isSelected = view.isSelected();
        if (isSelected) {
            ((LargeClassContext) classContext).cancel();
        } else {
            ((LargeClassContext) classContext).apply(true);
        }
    }

    @Override
    public void onChannelInfoInit() {
        if (classContext.channelStrategy.getTeacher() == null) {
            ToastManager.showShort(R.string.There_is_no_teacher_in_this_classroom);
        }
    }

    @Override
    public void onLocalChanged(Student local) {
        if (!local.isGenerate)
            ((LargeClassContext) classContext).apply(false);
        showStudentVideo();
    }

    @Override
    public void onTeacherChanged(Teacher teacher) {
        title_view.setTimeState(teacher.class_state == 1);

        video_teacher.setName(teacher.account);
        video_teacher.showRemote(teacher.uid);
        video_teacher.muteVideo(teacher.video == 0);
        video_teacher.muteAudio(teacher.audio == 0);

        showStudentVideo();

        resetHandState();

        joinWhiteboard(teacher.whiteboard_uid);
        chatRoomFragment.setEditTextEnable(teacher.mute_chat == 0);
    }

    @Override
    public void onStudentsChanged(List<Student> students) {
        showStudentVideo();
    }

    private void showStudentVideo() {
        int linkUid = classContext.channelStrategy.getTeacher().link_uid;
        if (linkUid == 0) {
            video_student.setVisibility(View.GONE);
            video_student.setSurfaceView(null);
        } else {
            List<Student> students = classContext.channelStrategy.getAllStudents();
            for (Student student : students) {
                if (linkUid == student.uid) {
                    video_student.setName(student.account);
                    if (getMyUserId() == student.uid) {
                        video_student.showLocal();
                    } else {
                        video_student.showRemote(student.uid);
                    }
                    video_student.muteVideo(student.video == 0);
                    video_student.muteAudio(student.audio == 0);
                    video_student.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    }

    private void resetHandState() {
        Teacher teacher = classContext.channelStrategy.getTeacher();
        if (teacher != null) {
            if (teacher.link_uid == getMyUserId()) {
                layout_hand_up.setEnabled(true);
                layout_hand_up.setSelected(true);
            } else {
                layout_hand_up.setEnabled(teacher.link_uid == 0);
                layout_hand_up.setSelected(false);
            }
        }
    }

    @Override
    public void onChannelMsgReceived(ChannelMsg msg) {
        chatRoomFragment.addMessage(msg);
    }

    @Override
    public void onPeerMsgReceived(PeerMsg msg) {
        if (msg.getCmd() == Cmd.CANCEL) {
            layout_hand_up.setSelected(false);
        }
    }

    @Override
    public void onScreenShareJoined(int uid) {
        showScreenShare(uid);
    }

    @Override
    public void onScreenShareOffline(int uid) {
        dismissScreenShare(uid);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (layout_materials == null)
            return;
        boolean showMaterials = tab.getPosition() == 0;
        layout_materials.setVisibility(showMaterials ? View.VISIBLE : View.GONE);
        layout_chat_room.setVisibility(showMaterials ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
