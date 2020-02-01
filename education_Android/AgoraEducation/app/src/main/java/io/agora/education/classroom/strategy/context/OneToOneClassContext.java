package io.agora.education.classroom.strategy.context;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import io.agora.education.classroom.bean.user.Student;
import io.agora.education.classroom.bean.user.Teacher;
import io.agora.education.classroom.bean.user.User;
import io.agora.education.classroom.strategy.ChannelStrategy;
import io.agora.rtc.Constants;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.sdk.manager.RtcManager;

public class OneToOneClassContext extends ClassContext {

    private final int MAX_STUDENT_NUM = 1;

    OneToOneClassContext(Context context, ChannelStrategy strategy) {
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
        RtcManager.instance().enableDualStreamMode(false);
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
        if (classEventListener instanceof OneToOneClassEventListener) {
            runListener(() -> ((OneToOneClassEventListener) classEventListener).onTeacherMediaChanged(teacher));
        }
    }

    @Override
    public void onLocalChanged(Student local) {
        super.onLocalChanged(local);
        if (local.isGenerate) return;
        if (classEventListener instanceof OneToOneClassEventListener) {
            runListener(() -> ((OneToOneClassEventListener) classEventListener).onLocalMediaChanged(local));
        }
    }

    public interface OneToOneClassEventListener extends ClassEventListener {
        void onTeacherMediaChanged(User user);

        void onLocalMediaChanged(User user);
    }

}
