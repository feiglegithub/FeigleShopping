package com.feigle.shopping.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class PayUtils {
    private static String APPID = "2019080566122092";
    private static String RSA2_PRIVATE = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC1hkHyx65T5nCWil2oyYeqHF/Lk+Xs/MZTkH6D5q7GnQ1KJvyN0K8rOIrrGA1qZI59tkiJkiYG+vK8mhUmcNpMSnXI7LocXj51md8Fy1O243soz7v9WbpvrXX/OEWMhSnolp7ZTz1yCWv8ERe+ZHcDjb3NIKFB4FvUH+FLkGUwxY8nIvLHdRR4xhaYpvx36SC/l35qzvQLHldGLP+RTHHyWeco3UHEtEiY0ukmfgHZZCx0+KuFb5qH8J2ontgPbrUEE190jfbR6aJQCl5dE1toF47NQgk/tppDw6txGRWauZacMMFDd7DPXhZw8KbxCb2mbkvoLLiYqTVjtHqhuKR/AgMBAAECggEBAImBu4Fay+7YeF9BT3OlvjQZrGN9lMXyMZZptbzbSXhAzilm4RfYhmk6Otmd7cOzitEl/UrhQBc3kBtYqT7mbCcwnIro8I6+wZe6SNyNysuXBNvEiUaA6x7gCqucB+tLkJSVTJ2XTheNlZRrqk3asuEpRScXWbCacVV+FN918YGATI6/4dlGRPtRbBY6ICuQeiWv+y7s3Y+pq4tjqfXFs+yX9iDsteD/9u4sdpPck4/0YL8Uc0Dyj1WuDHUIeCvj6YFEYiOi4GH88iQKTp0Iti3FfVbY5dAbxelZH7WSrSb3rvCt7AqvntdmvrgGBnpk0WDPqt9/GhQ5/NGOBKRT02ECgYEA97yerF14RsE2EfRs6dEUuCsSHPlMWtcx5ahoi7GWXIdqIHrFwe4eQs5vHXSTKFV7Df55ADpqkcjdlK1PTQsiiYtJfbSOnuTzeOe97fbRfV5k2I3fIIqfAgwJoF2L/9I8oXwHNBCOqPUq+ECHyi6ftRhQJJ7uoE8f8sQgxyaGDY8CgYEAu5RDKiJkeylsc22PfXZRcT9hKUiBKEE8niSwZuR2VYNMC3b1R/Zxwv26Hl/asRr2rA+jhXT5jdj9UJIMlqnpebJbizDlbm9YRVH6fJ+/JrpoFbh1DT3wwpLOyw2uT4DBXZjCvY9BsiY7HCyPF0Oh2DgPERygr7L7v0LSFwXPYhECgYAvaPt8xQ6Fi+hLG1nUjVzHugZnYRZJtDEqYEZRpZS+9HNM7/H0oRtNcbA2FN8sst8v2GlTr9pBOA2frGMmvWDIrc1Mr0c0BWDK3J6ucbO5qFzoTI5M7fpsK92TwRNPy+4qgU/nmNuUXgbxJ3hVKwMJZzO/LmNAbqQQk5QqSKcS5QKBgQCBX6vHVrs42wQqxIjv/ngDhLQttg0mx3/YjvN/s9kN96WuTpizznz+AOOqaXA7MJ6rY4nj1R79v4hPVrwJ+hzGy8YpxUqolXErNnRuGfvL6Us6l+Nbg7oXaQvproDn4qT7Og4ukay9A3NHZlA2unXY3ClAIx+MrwTFEIA4UtCLEQKBgDeHsGJYi2nZylO9DQSjnbnYf/QfAPvMDBOtSiTDJg/kmHAL+a0mlH8akhBFrdb4s0SvrvCEaGlrw88ElBVRo3sLsS3p/He9U0otgVn3TiT1Lvqbwr7Bep0xmqoxPfVyh8rvEsfzOqFdGQoxfvvbiWC59t0zPRyqtYD9/9RCkAMy";

    /**
     * 支付宝支付业务示例
     */
    public static Map<String, String> payV2(Activity activity, String totalAmount, String body, String outTradeNo) {
        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, true, totalAmount, body, outTradeNo);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

//        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, RSA2_PRIVATE, true);
        final String orderInfo = orderParam + "&" + sign;

        PayTask alipay = new PayTask(activity);
        Map<String, String> result = alipay.payV2(orderInfo, true);
        Log.i("msp", result.toString());

        return result;
    }
}
