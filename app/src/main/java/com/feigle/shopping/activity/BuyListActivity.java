package com.feigle.shopping.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.adapter.MyBuyListRecyclerViewAdapter;
import com.feigle.shopping.bean.AddressBean;
import com.feigle.shopping.bean.CommodityBean;
import com.feigle.shopping.bean.TransportBean;
import com.feigle.shopping.http.HttpUtils;
import com.feigle.shopping.utils.OrderInfoUtil2_0;
import com.feigle.shopping.utils.PayResult;
import com.feigle.shopping.utils.PayUtils;
import com.feigle.shopping.utils.SharedPreferencesUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BuyListActivity extends AppCompatActivity {

    private TextView contactTextView;
    private TextView chooseAddressTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private RecyclerView recyclerView;
    private TextView ticketTextView;
    private TextView totalTextView;
    private TextView preferentialTextView;
    private TextView transportTextView;
    private TextView orderNumberTextView;
    private TextView paymentTextView;
    private Button commitButton;
    private Button guideButton;
    private ConstraintLayout addressConstraintLayout;

    private XPopup.Builder mXPopup;
    private LoadingPopupView mLoadingPopupView;

    List<CommodityBean> commodityBeans = new ArrayList<>();
    private AddressBean addressBean = new AddressBean();
    private TransportBean transportBean = new TransportBean();

    private String name;
    private double total = 0;
    private int preferential = 0;
    private double transport = 8;
    private double payment;
    private String orderNumber;

    public static final int REQUEST_CODE_ADDRESS_ACTIVITY = 1;

    private MyBuyListRecyclerViewAdapter myBuyListRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_list);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        commodityBeans = (List<CommodityBean>) intent.getSerializableExtra("commodityBeans");

        initView();

        new GetTransportAsyncTask().execute(HttpUtils.SERVLET_GET_TRANSPORT_REQUEST);
        new GetDefAddressAsyncTask().execute(HttpUtils.SERVLET_GET_DEF_ADDRESS_REQUEST, name);
        new IsNewAsyncTask().execute(HttpUtils.SERVLET_PREFERENTIAL_REQUEST);
    }

    private void initView() {
        mXPopup = new XPopup.Builder(this);
        mLoadingPopupView = mXPopup.asLoading(getString(R.string.upload));

        contactTextView = findViewById(R.id.contactTextView);
        chooseAddressTextView = findViewById(R.id.chooseAddressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        addressTextView = findViewById(R.id.addressTextView);
        ticketTextView = findViewById(R.id.ticketTextView);
        totalTextView = findViewById(R.id.totalTextView);
        preferentialTextView = findViewById(R.id.preferentialTextView);
        transportTextView = findViewById(R.id.transportNumberTextView);
        paymentTextView = findViewById(R.id.paymentTextView);
        orderNumberTextView = findViewById(R.id.orderNumberTextView);
        commitButton = findViewById(R.id.commitButton);
        guideButton = findViewById(R.id.guideButton);
        addressConstraintLayout = findViewById(R.id.addressConstraintLayout);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myBuyListRecyclerViewAdapter = new MyBuyListRecyclerViewAdapter(commodityBeans);
        recyclerView.setAdapter(myBuyListRecyclerViewAdapter);
    }

    private void updateAddressView(AddressBean addressBean) {
        contactTextView.setText(addressBean.getContact());
        phoneTextView.setText(addressBean.getPhone());
        addressTextView.setText(addressBean.getAddress());

        if (addressBean.getAddress().equals(""))
            chooseAddressTextView.setVisibility(View.VISIBLE);
        else {
            chooseAddressTextView.setVisibility(View.GONE);
        }
    }

    private void setMoney() {
        try {
            double weight = 0;
            int quantity = 0;
            double total = 0;

            for (CommodityBean commodityBean : commodityBeans) {
                weight += commodityBean.getWeight() * commodityBean.getQuantity();
                total += commodityBean.getPrice() * commodityBean.getQuantity();
            }

            setTransport(weight, Integer.parseInt(transportBean.getDef()), Integer.parseInt(transportBean.getPrice()));
            setTotal(total);
            setPayment(total, preferential, transport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTotal(double total) {
        totalTextView.setText(String.valueOf(total));
    }

    private void setPayment(double total, int preferential, double transport) {
        payment = total - preferential + transport;
        paymentTextView.setText("实付：" + payment);
    }

    private void setTransport(double weight, int defPrice, int price) {
        weight = Math.ceil(weight);
        transport = defPrice + (weight - 1) * price;
        transportTextView.setText(String.valueOf(transport));
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.addressConstraintLayout:
                intent = new Intent(getBaseContext(), ChooseAddressActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADDRESS_ACTIVITY);
                break;
            case R.id.commitButton:
                if (payment < Integer.parseInt(transportBean.getPrice()) || addressBean.getAddress().equals(""))
                    showAlert(this, "订单信息有误,请重新创建订单!");
                else
//                    new PayAsyncTask().execute();
                    new AddOrderAsyncTask().execute(HttpUtils.SERVLET_ADD_ORDER_REQUEST, name);
                break;
            case R.id.guideButton:
                intent = new Intent(this, PayGuiActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADDRESS_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    addressBean = (AddressBean) data.getSerializableExtra("addressBean");
                    updateAddressView(addressBean);
                }
                break;
            default:
                break;
        }
    }

    class GetDefAddressAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", strings[1]);
                map = HttpUtils.post(strings[0], jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            boolean flag = (boolean) map.get("flag");
            if (flag) {
                try {
                    String s = (String) map.get("data");

                    JSONObject jsonObject = new JSONObject(s);

                    addressBean.setPhone(jsonObject.getString("phone"));
//                    commodityBean.setCost(Double.parseDouble(jsonObject.getString("cost")));
                    addressBean.setId(jsonObject.getString("id"));
                    addressBean.setDef(jsonObject.getString("def"));
                    addressBean.setContact(jsonObject.getString("contact"));
                    addressBean.setAddress(jsonObject.getString("address"));

                    updateAddressView(addressBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
        }
    }

    class GetTransportAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();
            try {
                map = HttpUtils.get(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            boolean flag = (boolean) map.get("flag");
            if (flag) {
                try {
                    String s = (String) map.get("data");

                    JSONObject jsonObject = new JSONObject(s);

                    transportBean.setId(jsonObject.getString("id"));
                    transportBean.setDef(jsonObject.getString("def"));
                    transportBean.setEnable(jsonObject.getString("enable"));
                    transportBean.setName(jsonObject.getString("name"));
                    transportBean.setPrice(jsonObject.getString("price"));

                    setMoney();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
        }
    }

    class AddOrderAsyncTask extends AsyncTask<String, Integer, Map> {
        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();
            if (!transportBean.getDef().equals("")) {
                int bound = 1;
                Random random = new Random();
                for (int i = 0; i < 10 - name.length(); i++) {
                    bound *= 10;
                }
                orderNumber = name + random.nextInt(bound);
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("name", strings[1]);

                    JSONArray jsonArray = new JSONArray();

                    for (CommodityBean commodityBean : commodityBeans) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("contact", addressBean.getContact());
                        jsonObject1.put("phone", addressBean.getPhone());
                        jsonObject1.put("address", addressBean.getAddress());
                        jsonObject1.put("postalCode", "");
                        jsonObject1.put("commodity", commodityBean.getCommodityName());
                        jsonObject1.put("quantity", commodityBean.getQuantity());
                        jsonObject1.put("orderNumber", orderNumber);
                        jsonObject1.put("payment", payment);
                        jsonArray.put(jsonObject1);
                    }

                    jsonObject.put("orders", jsonArray);
                    map = HttpUtils.post(strings[0], jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else
                map.put("flag", false);

            return map;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingPopupView.show();
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            if (mLoadingPopupView.isShow())
                mLoadingPopupView.dismiss();

            boolean flag = (boolean) map.get("flag");
            if (flag) {
                try {
                    String s = (String) map.get("data");

                    if (s.equals("true")) {
                        orderNumberTextView.setText("订单号：" + orderNumber);
                        Toast.makeText(getBaseContext(), R.string.commit_success, Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
        }
    }

    class IsNewAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", SharedPreferencesUtils.sharedPreferencesRead(getApplicationContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, ""));
                map = HttpUtils.post(strings[0], jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            boolean flag = (boolean) map.get("flag");
            if (flag) {
                try {
                    String s = (String) map.get("data");

                    preferential = Integer.parseInt(s);

                    setMoney();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
        }
    }

    class PayAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            orderNumber = OrderInfoUtil2_0.getOutTradeNo();
            Map map = PayUtils.payV2(BuyListActivity.this, String.valueOf(payment), getString(R.string.app_name), orderNumber);
            return map;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            @SuppressWarnings("unchecked")
            PayResult payResult = new PayResult(map);
            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                showAlert(BuyActivity.this, "成功:" + payResult);
                new AddOrderAsyncTask().execute(HttpUtils.SERVLET_ADD_ORDER_REQUEST, name);
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                showAlert(BuyListActivity.this, "支付失败:" + payResult);
            }
        }
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton(R.string.confirm, null)
                .setOnDismissListener(onDismiss)
                .show();
    }
}
