package io.agora.base;

import android.net.Uri;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetworkManager {

    private static NetworkManager networkManager = new NetworkManager();

    private OkHttpClient client;

    private NetworkManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS).build();
    }

    public static NetworkManager getInstance() {
        return networkManager;
    }

    public static OkHttpClient getOkHttpClient() {
        return networkManager.client;
    }

    public static String appendGetParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }

    public interface CallBack {
        void onSuccess(String json);

        void onFailure(IOException e);
    }

    public Call getRequest(String url, CallBack callBack) {
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (callBack != null)
                    callBack.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    ResponseBody body = response.body();
                    if (body == null) {
                        onFailure(call, new IOException("onResponse error:" + "response body is null."));
                    }

                    if (response.code() == 200) {
                        if (callBack != null)
                            callBack.onSuccess(body.string());
                    } else {
                        onFailure(call, new IOException("onResponse error:" + body.string()));
                    }
                } catch (Throwable e) {
                    onFailure(call, new IOException("onResponse error:" + e.toString()));
                }
            }
        });
        return call;
    }

    public Call postRequest(String url, RequestBody body, CallBack callBack) {
        Request request = new Request.Builder().url(url).post(body).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (callBack != null)
                    callBack.onFailure(e);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    ResponseBody body = response.body();
                    if (body == null) {
                        onFailure(call, new IOException("onResponse error:" + "response body is null."));
                    }

                    if (response.code() == 200) {
                        if (callBack != null)
                            callBack.onSuccess(body.string());
                    } else {
                        onFailure(call, new IOException("onResponse error:" + body.string()));
                    }
                } catch (Throwable e) {
                    onFailure(call, new IOException("onResponse error:" + e.toString()));
                }
            }
        });
        return call;
    }

}
