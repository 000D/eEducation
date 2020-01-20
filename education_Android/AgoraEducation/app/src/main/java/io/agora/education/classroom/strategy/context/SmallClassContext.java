package io.agora.education.classroom.strategy.context;

import android.content.Context;

import org.jetbrains.annotations.NotNull;

import io.agora.education.classroom.strategy.channel.ChannelStrategy;
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
        RtcManager.instance().enableDualStreamMode(true);
    }

    @Override
    public void onChannelInfoInit() {
        super.onChannelInfoInit();
        if (channelStrategy.getLocal().isGenerate) {
            channelStrategy.updateLocalAttribute(channelStrategy.getLocal(), null);
        }
    }

}
