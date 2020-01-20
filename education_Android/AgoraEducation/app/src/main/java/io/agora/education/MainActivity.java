package io.agora.education;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.base.ToastManager;
import io.agora.education.base.BaseActivity;
import io.agora.education.classroom.BaseClassActivity;
import io.agora.education.classroom.LargeClassActivity;
import io.agora.education.classroom.OneToOneClassActivity;
import io.agora.education.classroom.SmallClassActivity;
import io.agora.education.classroom.annotation.ClassType;
import io.agora.education.classroom.strategy.context.ClassContext;
import io.agora.education.classroom.strategy.context.ClassContextFactory;
import io.agora.education.util.AppUtil;
import io.agora.education.util.CryptoUtil;
import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;
import io.agora.sdk.manager.RtmManager;

public class MainActivity extends BaseActivity {

    private final int PERMISSION_CODE = 100;

    @BindView(R.id.et_room_name)
    protected EditText et_room_name;
    @BindView(R.id.et_your_name)
    protected EditText et_your_name;
    @BindView(R.id.et_room_type)
    protected EditText et_room_type;
    @BindView(R.id.card_room_type)
    protected CardView card_room_type;

    private int myUserId;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        et_room_name.setText("1234");
        et_your_name.setText("000");
    }

    @Override
    protected void initData() {
        myUserId = (int) (System.currentTimeMillis() * 1000 % 1000000);
        RtmManager.instance().login(getString(R.string.agora_rtm_token), myUserId);
    }

    private void joinRoom() {
        String roomName = et_room_name.getText().toString();
        if (TextUtils.isEmpty(roomName)) {
            ToastManager.showShort(R.string.Room_name_should_not_be_empty);
            return;
        }

        String yourName = et_your_name.getText().toString();
        if (TextUtils.isEmpty(yourName)) {
            ToastManager.showShort(R.string.Your_name_should_not_be_empty);
            return;
        }

        String roomType = et_room_type.getText().toString();
        if (TextUtils.isEmpty(roomType)) {
            ToastManager.showShort(R.string.Room_type_should_not_be_empty);
            return;
        }

//        if (rtmManager().getLoginStatus() != RtmManager.LOGIN_STATUS_SUCCESS) {
//            ToastManager.showShort("RTM login not success，please check and try later！");
//            rtmManager().login(String.valueOf(myUserId));
//        }

        Intent intent = new Intent();
        int roomTypeInt;
        if (roomType.equals(getString(R.string.one2one_class))) {
            intent.setClass(this, OneToOneClassActivity.class);
            roomTypeInt = ClassType.ONE2ONE;
        } else if (roomType.equals(getString(R.string.small_class))) {
            intent.setClass(this, SmallClassActivity.class);
            roomTypeInt = ClassType.SMALL;
        } else {
            intent.setClass(this, LargeClassActivity.class);
            roomTypeInt = ClassType.LARGE;
        }
        String roomNameReal = roomTypeInt + CryptoUtil.md5(roomName);
        intent.putExtra(BaseClassActivity.ROOM_NAME, roomName)
                .putExtra(BaseClassActivity.ROOM_NAME_REAL, roomNameReal)
                .putExtra(BaseClassActivity.USER_ID, myUserId)
                .putExtra(BaseClassActivity.YOUR_NAME, yourName);

        ClassContext classContext = new ClassContextFactory(this).getClassContext(roomTypeInt, roomNameReal, null);
        classContext.checkChannelEnterable(new ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                classContext.release();
                if (aBoolean) {
                    startActivity(intent);
                } else {
                    ToastManager.showShort(R.string.the_room_is_full);
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                classContext.release();
                ToastManager.showShort(R.string.get_channel_attr_failed);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSION_CODE)
            return;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                ToastManager.showShort(R.string.No_enough_permissions);
                return;
            }
        }
        joinRoom();
    }

    @OnClick({R.id.iv_setting, R.id.et_room_type, R.id.btn_join, R.id.tv_one2one, R.id.tv_small_class, R.id.tv_large_class})
    public void onClickRoomType(View view) {
        switch (view.getId()) {
            case R.id.iv_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.et_room_type:
                if (card_room_type.getVisibility() == View.GONE) {
                    card_room_type.setVisibility(View.VISIBLE);
                } else {
                    card_room_type.setVisibility(View.GONE);
                }
                break;
            case R.id.btn_join:
                String[] requestPermission = {
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                if (AppUtil.checkAndRequestAppPermission(this, requestPermission, PERMISSION_CODE)) {
                    joinRoom();
                }
                break;
            case R.id.tv_one2one:
                et_room_type.setText(getString(R.string.one2one_class));
                card_room_type.setVisibility(View.GONE);
                break;
            case R.id.tv_small_class:
                et_room_type.setText(getString(R.string.small_class));
                card_room_type.setVisibility(View.GONE);
                break;
            case R.id.tv_large_class:
                et_room_type.setText(getString(R.string.large_class));
                card_room_type.setVisibility(View.GONE);
                break;
        }
    }

}
