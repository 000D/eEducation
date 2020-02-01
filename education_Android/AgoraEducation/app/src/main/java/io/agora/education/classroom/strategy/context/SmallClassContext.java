package io.agora.education.classroom.strategy.context;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.bean.user.User;
import io.agora.education.classroom.strategy.ChannelStrategy;
import io.agora.rtc.Constants;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.sdk.manager.RtcManager;

public class SmallClassContext extends ClassContext {

    private final int MAX_STUDENT_NUM = 16;

    SmallClassContext(Context context, ChannelStrategy strategy) {
        super(context, strategy);
    }

    @Override
    public void checkChannelEnterable(@NotNull ResultCallback<Boolean> callback) {
        channelStrategy.queryChannelInfo(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                channelStrategy.queryOnlineStudentNum(new ResultCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer integer) {
                        callback.onSuccess(integer < MAX_STUDENT_NUM);
                    }

                    @Override
                    public void onFailure(ErrorInfo errorInfo) {
                        callback.onFailure(errorInfo);
                    }
                });
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                callback.onFailure(errorInfo);
            }
        });
    }

    @Override
    void preConfig() {
        RtcManager.instance().setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        RtcManager.instance().setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        // enable dual stream mode in small class
        RtcManager.instance().enableDualStreamMode(true);
        RtcManager.instance().setRemoteDefaultVideoStreamType(Constants.VIDEO_STREAM_LOW);
    }

    @Override
    public void onChannelInfoInit() {
        super.onChannelInfoInit();
        if (channelStrategy.getLocal().isGenerate) {
            channelStrategy.updateLocalAttribute(channelStrategy.getLocal(), null);
        }
    }

    @Override
    public void onTeacherChanged(Teacher teacher) {
        super.onTeacherChanged(teacher);
        // teacher need set high stream type
        RtcManager.instance().setRemoteVideoStreamType(teacher.uid, Constants.VIDEO_STREAM_HIGH);
        onUsersMediaChanged();
    }

    @Override
    public void onLocalChanged(Student local) {
        super.onLocalChanged(local);
        onUsersMediaChanged();
    }

    @Override
    public void onStudentsChanged(List<Student> students) {
        super.onStudentsChanged(students);
        onUsersMediaChanged();
    }

    private void onUsersMediaChanged() {
        if (classEventListener instanceof SmallClassEventListener) {
            List<User> users = new ArrayList<>();
            for (Object object : channelStrategy.getAllUsers()) {
                if (object instanceof User) {
                    users.add((User) object);
                }
            }
            runListener(() -> ((SmallClassEventListener) classEventListener).onUsersMediaChanged(users));
        }
    }

    public interface SmallClassEventListener extends ClassEventListener {
        void onUsersMediaChanged(List<User> users);
    }

}
