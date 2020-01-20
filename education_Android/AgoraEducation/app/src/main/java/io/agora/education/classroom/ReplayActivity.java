package io.agora.education.classroom;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.google.android.exoplayer2.ui.PlayerView;

import butterknife.BindView;
import butterknife.OnClick;
import io.agora.education.base.BaseActivity;
import io.agora.education.classroom.fragment.ReplayBoardFragment;
import io.agora.education.R;

public class ReplayActivity extends BaseActivity {

    public static final String WHITE_BOARD_UID = "white_board_uid";
    public static final String WHITE_BOARD_START_TIME = "white_board_start_time";
    public static final String WHITE_BOARD_END_TIME = "white_board_end_time";
    public static final String WHITE_BOARD_URL = "white_board_url";

    @BindView(R.id.video_view)
    protected PlayerView video_view;

    private ReplayBoardFragment replayBoardFragment;
    private String url, uuid;
    private long startTime, endTime;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_replay;
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        url = intent.getStringExtra(WHITE_BOARD_URL);
        uuid = intent.getStringExtra(WHITE_BOARD_UID);
        startTime = intent.getLongExtra(WHITE_BOARD_START_TIME, 0);
        endTime = intent.getLongExtra(WHITE_BOARD_END_TIME, 0);
    }

    @Override
    protected void initView() {
        video_view.setVisibility(!TextUtils.isEmpty(url) ? View.VISIBLE : View.GONE);
        findViewById(R.id.iv_temp).setVisibility(TextUtils.isEmpty(url) ? View.VISIBLE : View.GONE);

        replayBoardFragment = new ReplayBoardFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_whiteboard, replayBoardFragment)
                .commitNow();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        replayBoardFragment.initReplay(uuid, startTime, endTime);
        replayBoardFragment.setPlayer(video_view, url);
    }

    @Override
    protected void onDestroy() {
        replayBoardFragment.releaseReplay();
        super.onDestroy();
    }

    @OnClick(R.id.iv_back)
    public void onClick(View view) {
        finish();
    }

}
