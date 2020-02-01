package io.agora.education.support;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.agora.base.NetworkManager;
import io.agora.education.BuildConfig;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class EduAPI {

    private static final String HOST = BuildConfig.API_BASE_URL;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static void checkVersion() {
        Map<String, Object> params = new HashMap<>();
        params.put("appCode", "edu-demo");
        params.put("osType", 2); // 1 iOS 2 Android
        params.put("terminalType", 1); // 1 phone 2 pad
        params.put("appVersion", BuildConfig.VERSION_NAME);

        String url = HOST + "/edu/v1/app/version";
        RequestBody body = RequestBody.create(JSON, new Gson().toJson(params));

        NetworkManager.getInstance().getRequest(url, new NetworkManager.CallBack() {
            @Override
            public void onSuccess(String json) {
                Log.d("test", json);
                JsonObject roomJSON = new Gson().fromJson(json, JsonObject.class);
            }

            @Override
            public void onFailure(IOException e) {
            }
        });
    }

}
