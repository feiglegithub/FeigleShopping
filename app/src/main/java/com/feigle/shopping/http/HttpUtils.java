package com.feigle.shopping.http;

import android.app.Activity;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.utils.Utils;
import com.vector.update_app.HttpManager;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.UpdateCallback;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {
//    public static final String SERVER = "https://n2571455a8.wicp.vip/FeigleShopping/";
    public static final String SERVER = "http://10.8.100.72:8088/FeigleShopping/";

    public static final String SERVLET_LOGIN_REQUEST = "loginForAPPServerlet";
    public static final String SERVLET_SHOPPING_ADD_REQUEST = "shoppingAddForAPPServerlet";
    public static final String SERVLET_GET_COMMODITY_REQUEST = "getCommodityForWXServerlet";
    public static final String SERVLET_BANNER_IMAGE_REQUEST = "bannerImageListForWXServerlet";
    public static final String SERVLET_GET_SHOPPING_CART_REQUEST = "shoppingListForAPPServerlet";
    public static final String SERVLET_GET_COMMODITY_BY_ID_REQUEST = "getCommodityByIdForAPPServerlet";
    public static final String SERVLET_ADD_ADDRESS_REQUEST = "addAddressServerlet";
    public static final String SERVLET_REGISTER_REQUEST = "registerServerlet";
    public static final String SERVLET_GET_ADDRESS_LIST_REQUEST = "getAddressListServerlet";
    public static final String SERVLET_UPDATE_ADDRESS_REQUEST = "updateAddressServerlet";
    public static final String SERVLET_GET_DEF_ADDRESS_REQUEST = "getDefAddressServerlet";
    public static final String SERVLET_ADD_ORDER_REQUEST = "addOrderForAPPServerlet";
    public static final String SERVLET_GET_TRANSPORT_REQUEST = "getTransportByEnableServerlet";
    public static final String SERVLET_GET_ORDER_LIST_REQUEST = "getOrderByUserForAPPServerlet";
    public static final String SERVLET_SHOPPING_DELETE_REQUEST = "deleteShoppingServerlet";
    public static final String SERVLET_UPDATE_PSW_REQUEST = "updatePswServerlet";
    public static final String SERVLET_UPDATE_PHONE_REQUEST = "updatePhoneServerlet";
    public static final String SERVLET_NEW_VERSION_REQUEST = "newVersionServerlet";
    public static final String SERVLET_PREFERENTIAL_REQUEST = "isNewUserServerlet";

    public static final String RESPONSE_PARAM_IS_SUCCESS_FLAG = "flag";
    public static final String RESPONSE_PARAM_DATA = "data";

    public static Map get(String url) {
        Map map = new HashMap();

        url = SERVER + url;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            boolean flag = response.isSuccessful();
            String data = response.body().string();

            map.put("flag", flag);
            map.put("data", data);
            return map;
        } catch (Exception e) {
            e.printStackTrace();

            map.put("flag", false);
            return map;
        }
    }

    public static Map post(String url, String json) {
        Map map = new HashMap();

        url = SERVER + url;
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            boolean flag = response.isSuccessful();
            String data = response.body().string();
            map.put("flag", flag);
            map.put("data", data);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("flag", false);
            return map;
        }
    }

    public static Map updateApp(final Activity activity) {
        final Map map = new HashMap();

        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        Map<String, String> params = new HashMap<String, String>();
        params.put("version", String.valueOf(Utils.getAppVersionCode(activity)));

        new UpdateAppManager
                .Builder()
                //必须设置，当前Activity
                .setActivity(activity)
                //必须设置，实现httpManager接口的对象
                .setHttpManager(new HttpManager() {
                    @Override
                    public void asyncGet(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {
                    }

                    @Override
                    public void asyncPost(@NonNull String url, @NonNull Map<String, String> params, @NonNull Callback callBack) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("version", params.get("version"));

                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonObject.toString());
                            Request request = new Request.Builder().url(url).post(body).build();

                            Response response = client.newCall(request).execute();
                            boolean flag = response.isSuccessful();
                            String data = response.body().string();
                            callBack.onResponse(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.onError(e.getMessage());
                        }
                    }

                    @Override
                    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, @NonNull final FileCallback callback) {
                        OkHttpUtils.get()
                                .url(url)
                                .build()
                                .execute(new FileCallBack(path, fileName) {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        callback.onError(e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(File response, int id) {
                                        callback.onResponse(response);
                                    }

                                    @Override
                                    public void inProgress(float progress, long total, int id) {
                                        super.inProgress(progress, total, id);
                                        callback.onProgress(progress,total);
                                    }

                                    @Override
                                    public void onBefore(Request request, int id) {
                                        super.onBefore(request, id);
                                        callback.onBefore();
                                    }
                                });
                    }
                })
                //必须设置，更新地址
                .setUpdateUrl(SERVER + SERVLET_NEW_VERSION_REQUEST)

                //以下设置，都是可选
                //设置请求方式，默认get
                .setPost(true)
                //添加自定义参数，默认version=1.0.0（app的versionName）；apkKey=唯一表示（在AndroidManifest.xml配置）
                .setParams(params)
                //为按钮，进度条设置颜色，默认从顶部图片自动识别。
                //.setThemeColor(ColorUtil.getRandomColor())
                //设置apk下砸路径，默认是在下载到sd卡下/Download/1.0.0/test.apk
                .setTargetPath(path)
                .build()
                //检测是否有新版本
                .checkNewApp(new UpdateCallback() {
                    /**
                     * 解析json,自定义协议
                     *
                     * @param json 服务器返回的json
                     * @return UpdateAppBean
                     */
                    @Override
                    protected UpdateAppBean parseJson(String json) {
                        UpdateAppBean updateAppBean = new UpdateAppBean();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            updateAppBean
                                    //（必须）是否更新Yes,No
                                    .setUpdate(jsonObject.optString("update"))
                                    //（必须）新版本号，
                                    .setNewVersion(jsonObject.optString("version_name"))
                                    //（必须）下载地址
                                    .setApkFileUrl(jsonObject.optString("apk_file_url"))
                                    //（必须）更新内容
                                    .setUpdateLog(jsonObject.optString("update_log"))
                                    //大小，不设置不显示大小，可以不设置
                                    .setTargetSize(jsonObject.optString("target_size"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        map.put("flag",true);
                        return updateAppBean;
                    }

                    /**
                     * 网络请求之前
                     */
                    @Override
                    public void onBefore() {
                    }

                    /**
                     * 网路请求之后
                     */
                    @Override
                    public void onAfter() {
                    }

                    @Override
                    protected void noNewApp(String error) {
                        map.put("flag",false);
                        map.put("data",error);
                    }
                });
        return map;
    }
}
