package io.agora.whiteboard.netless.manager;

import android.os.Handler;
import android.os.Looper;

import com.herewhite.sdk.Room;
import com.herewhite.sdk.RoomCallbacks;
import com.herewhite.sdk.RoomParams;
import com.herewhite.sdk.WhiteSdk;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.RoomPhase;
import com.herewhite.sdk.domain.RoomState;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.SceneState;

import io.agora.whiteboard.netless.annotation.Appliance;
import io.agora.whiteboard.netless.listener.BoardEventListener;

public class BoardManager extends NetlessManager<Room> implements RoomCallbacks {

    private String appliance;
    private int[] strokeColor;

    private Handler handler = new Handler(Looper.getMainLooper());
    private BoardEventListener listener;

    public void setListener(BoardEventListener listener) {
        this.listener = listener;
    }

    public void init(WhiteSdk sdk, RoomParams params) {
        sdk.joinRoom(params, this, promise);
    }

    public void setAppliance(@Appliance String appliance) {
        if (t != null) {
            MemberState state = new MemberState();
            state.setCurrentApplianceName(appliance);
            t.setMemberState(state);
        } else {
            this.appliance = appliance;
        }
    }

    public String getAppliance() {
        if (t != null) {
            return t.getMemberState().getCurrentApplianceName();
        }
        return null;
    }

    public void setStrokeColor(int[] color) {
        if (t != null) {
            MemberState state = new MemberState();
            state.setStrokeColor(color);
            t.setMemberState(state);
        } else {
            this.strokeColor = color;
        }
    }

    public int[] getStrokeColor() {
        if (t != null) {
            t.getMemberState().getStrokeColor();
        }
        return null;
    }

    public void setSceneIndex(int index) {
        if (t != null) {
            t.setSceneIndex(index, new Promise<Boolean>() {
                @Override
                public void then(Boolean aBoolean) {

                }

                @Override
                public void catchEx(SDKError t) {

                }
            });
        }
    }

    public int getSceneCount() {
        if (t != null) {
            return t.getScenes().length;
        }
        return 0;
    }

    public void pptPreviousStep() {
        if (t != null) {
            t.pptPreviousStep();
        }
    }

    public void pptNextStep() {
        if (t != null) {
            t.pptNextStep();
        }
    }

    public void getRoomPhase(Promise<RoomPhase> promise) {
        if (t != null) {
            t.getRoomPhase(promise);
        } else {
            if (promise != null) {
                promise.then(RoomPhase.disconnected);
            }
        }
    }

    public void refreshViewSize() {
        if (t != null) {
            t.refreshViewSize();
        }
    }

    public void disconnect() {
        if (t != null) {
            t.disconnect();
        }
    }

    @Override
    public void onPhaseChanged(RoomPhase phase) {
        if (listener != null) {
            handler.post(() -> listener.onRoomPhaseChanged(phase));
        }
    }

    @Override
    public void onBeingAbleToCommitChange(boolean isAbleToCommit) {

    }

    @Override
    public void onDisconnectWithError(Exception e) {

    }

    @Override
    public void onKickedWithReason(String reason) {

    }

    @Override
    public void onRoomStateChanged(RoomState modifyState) {
        if (listener != null) {
            SceneState sceneState = modifyState.getSceneState();
            if (sceneState != null) {
                handler.post(() -> listener.onSceneStateChanged(sceneState));
            }
        }
    }

    @Override
    public void onCatchErrorWhenAppendFrame(long userId, Exception error) {

    }

    @Override
    void onSuccess(Room room) {
        if (appliance != null) {
            setAppliance(appliance);
        }
        if (strokeColor != null) {
            setStrokeColor(strokeColor);
        }
        if (listener != null) {
            listener.onSceneStateChanged(room.getSceneState());
        }
    }

    @Override
    void onFail(SDKError error) {

    }

}
