package io.agora.education.classroom;

import android.view.View;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.base.ToastManager;
import io.agora.education.R;
import io.agora.education.classroom.adapter.ClassVideoAdapter;
import io.agora.education.classroom.annotation.ClassType;
import io.agora.education.classroom.bean.msg.ChannelMsg;
import io.agora.education.classroom.bean.msg.PeerMsg;
import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.fragment.StudentListFragment;
import io.agora.education.classroom.widget.TitleView;
import io.agora.rtc.Constants;

public class SmallClassActivity extends BaseClassActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.title_view)
    protected TitleView title_view;
    @BindView(R.id.rcv_videos)
    protected RecyclerView rcv_videos;
    @BindView(R.id.layout_im)
    protected View layout_im;
    @BindView(R.id.layout_tab)
    protected TabLayout layout_tab;

    private ClassVideoAdapter adapter;
    private StudentListFragment studentListFragment;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_small_class;
    }

    @Override
    protected void initData() {
        super.initData();
        adapter = new ClassVideoAdapter(getLocal());
    }

    @Override
    protected void initView() {
        super.initView();
        title_view.setTitle(getRoomName());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv_videos.setLayoutManager(layoutManager);
        rcv_videos.setAdapter(adapter);

        layout_tab.addOnTabSelectedListener(this);

        studentListFragment = new StudentListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_chat_room, studentListFragment)
                .hide(studentListFragment)
                .commit();
    }

    @Override
    protected Student getLocal() {
        return new Student(getMyUserId(), getMyName(), Constants.CLIENT_ROLE_BROADCASTER);
    }

    @Override
    protected int getClassType() {
        return ClassType.SMALL;
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
        adapter.setLocal(local);
        chatRoomFragment.setEditTextEnable(local.chat == 1);
        studentListFragment.setStudentList(classContext.channelStrategy.getAllStudents());
    }

    @Override
    public void onTeacherChanged(Teacher teacher) {
        title_view.setTimeState(teacher.class_state == 1);

        adapter.setTeacher(teacher);

        joinWhiteboard(teacher.whiteboard_uid);
        chatRoomFragment.setEditTextEnable(teacher.mute_chat == 0);
    }

    @Override
    public void onStudentsChanged(List<Student> students) {
        adapter.setStudents(students);
        studentListFragment.setStudentList(classContext.channelStrategy.getAllStudents());
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (tab.getPosition() == 0) {
            transaction.show(chatRoomFragment).hide(studentListFragment);
        } else {
            transaction.show(studentListFragment).hide(chatRoomFragment);
        }
        transaction.commit();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
