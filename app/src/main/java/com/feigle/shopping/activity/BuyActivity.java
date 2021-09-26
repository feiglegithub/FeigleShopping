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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.feigle.shopping.R;
import com.feigle.shopping.bean.AddressBean;
import com.feigle.shopping.bean.CommodityBean;
import com.feigle.shopping.bean.TransportBean;
import com.feigle.shopping.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.feigle.shopping.numberbutton.NumberButton;
import com.feigle.shopping.utils.OrderInfoUtil2_0;
import com.feigle.shopping.utils.PayResult;
import com.feigle.shopping.utils.PayUtils;
import com.feigle.shopping.utils.SharedPreferencesUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

public class BuyActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ADDRESS_ACTIVITY = 1;

    private String id;
    private String name;
    private int quantity = 1;
    private double total = 0;
    private int preferential = 0;
    private double transport = 8;
    private double payment;
    private String orderNumber;

    private CommodityBean commodityBean = new CommodityBean();
    private AddressBean addressBean = new AddressBean();
    private TransportBean transportBean = new TransportBean();

    private TextView contactTextView;
    private TextView phoneTextView;
    private TextView addressTextView;
    private TextView commodityTextView;
    private TextView inventoryTextView;
    private TextView priceTextView;
    private TextView ticketTextView;
    private TextView totalTextView;
    private TextView preferentialTextView;
    private TextView transportTextView;
    private TextView paymentTextView;
    private TextView orderNumberTextView;
    private TextView chooseAddressTextView;
    private ImageView chooseAddressImageView;
    private ImageView imageView;
    private NumberButton number_button;
    private Button commitButton;
    private Button guideButton;
    private ConstraintLayout addressConstraintLayout;
    private LinearLayout ticketLinearLayout;

    private XPopup.Builder mXPopup;
    private LoadingPopupView mLoadingPopupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        commodityBean = (CommodityBean) intent.getSerializableExtra("commodityBean");

        initView();

        new GetTransportAsyncTask().execute(HttpUtils.SERVLET_GET_TRANSPORT_REQUEST);
        new GetDefAddressAsyncTask().execute(HttpUtils.SERVLET_GET_DEF_ADDRESS_REQUEST, name);
        new IsNewAsyncTask().execute(HttpUtils.SERVLET_PREFERENTIAL_REQUEST);
//        new GetCommodityAsyncTask().execute(HttpUtils.SERVLET_GET_COMMODITY_BY_ID_REQUEST, name
//                , id);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {
        mXPopup = new XPopup.Builder(this);
        mLoadingPopupView = mXPopup.asLoading(getString(R.string.upload));

        contactTextView = findViewById(R.id.contactTextView);
        chooseAddressTextView = findViewById(R.id.chooseAddressTextView);
        phoneTextView = findViewById(R.id.phoneTextView);
        addressTextView = findViewById(R.id.addressTextView);
        commodityTextView = findViewById(R.id.commodityTextView);
        inventoryTextView = findViewById(R.id.inventoryTextView);
        priceTextView = findViewById(R.id.priceTextView);
        ticketTextView = findViewById(R.id.ticketTextView);
        totalTextView = findViewById(R.id.totalTextView);
        preferentialTextView = findViewById(R.id.preferentialTextView);
        transportTextView = findViewById(R.id.transportNumberTextView);
        paymentTextView = findViewById(R.id.paymentTextView);
        orderNumberTextView = findViewById(R.id.orderNumberTextView);
        chooseAddressImageView = findViewById(R.id.chooseAddressImageView);
        imageView = findViewById(R.id.imageView);
        number_button = findViewById(R.id.number_button);
        commitButton = findViewById(R.id.commitButton);
        guideButton = findViewById(R.id.guideButton);
        addressConstraintLayout = findViewById(R.id.addressConstraintLayout);
        ticketLinearLayout = findViewById(R.id.ticketLinearLayout);

        number_button.setCurrentNumber(1);
        number_button.setOnNumberChangedListener(new NumberButton.OnNumberChangedListener() {
            @Override
            public void onNumberChanger(int number) {
                quantity = number;
                setMoney();
            }
        });

    }

    private void setMoney() {
        try {
            setTransport(commodityBean.getWeight(), Integer.parseInt(transportBean.getDef()), Integer.parseInt(transportBean.getPrice()), quantity);
            setTotal(quantity, commodityBean.getPrice());
            setPayment(total, preferential, transport);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setTotal(int quantity, double price) {
        total = price * quantity;
        totalTextView.setText(String.valueOf(total));
    }

    private void setPayment(double total, int preferential, double transport) {
        payment = total - preferential + transport;
        paymentTextView.setText("实付：" + payment);
    }

    private void setTransport(double weight, int defPrice, int price, int quantity) {
        weight = Math.ceil(weight * quantity);
        transport = defPrice + (weight - 1) * price;
        transportTextView.setText(String.valueOf(transport));
    }

    private void updateCommodityView(CommodityBean commodityBean) {
        setTransport(commodityBean.getWeight(), Integer.parseInt(transportBean.getDef()), Integer.parseInt(transportBean.getPrice()), quantity);
        setTotal(quantity, commodityBean.getPrice());
        setPayment(total, preferential, transport);

        commodityTextView.setText(commodityBean.getCommodityName());
        inventoryTextView.setText("库存：" + String.valueOf(commodityBean.getQuantity()));
        priceTextView.setText("价格：" + String.valueOf(commodityBean.getPrice()));
        totalTextView.setText(String.valueOf(total));
        preferentialTextView.setText(String.valueOf(preferential));

        Glide.with(this).load(commodityBean.getImgPath()).placeholder(R.drawable.loading2).error(R.drawable.loading_error).into(imageView);

        if (commodityBean.getQuantity() > 0) {
            number_button.setInventory(commodityBean.getQuantity())
                    .setOnWarnListener(new NumberButton.OnWarnListener() {
                        @Override
                        public void onWarningForInventory(int inventory) {
                            Toast.makeText(BuyActivity.this, "当前库存:" + inventory, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onWarningForBuyMax(int buyMax) {
                            Toast.makeText(BuyActivity.this, "超过最大购买数:" + buyMax, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            number_button.setInventory(1);
            commitButton.setEnabled(false);
        }
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
            case R.id.ticketLinearLayout:
                break;
            default:
                break;
        }
    }

    class GetCommodityAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", strings[1]);
                jsonObject.put("id", strings[2]);
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

                    commodityBean.setCommodityName(jsonObject.getString("commodityName"));
//                    commodityBean.setCost(Double.parseDouble(jsonObject.getString("cost")));
                    commodityBean.setCreateTime(jsonObject.getString("createTime"));
                    commodityBean.setDetail(jsonObject.getString("detail"));
                    commodityBean.setId(Integer.parseInt(jsonObject.getString("id")));
                    commodityBean.setImgPath(jsonObject.getString("imgPath"));
                    commodityBean.setPrice(Double.parseDouble(jsonObject.getString("price")));
                    commodityBean.setQuantity(Integer.parseInt(jsonObject.getString("quantity")));
                    commodityBean.setWeight(Double.parseDouble(jsonObject.getString("weight")));

                    updateCommodityView(commodityBean);
                    setMoney();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
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

    class AddOrderAsyncTask extends AsyncTask<String, Integer, Map> {
        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();
            if (commodityBean.getPrice() > 0 && !transportBean.getDef().equals("")) {

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
                        orderNumberTextView.setText(orderNumber);
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

                    updateCommodityView(commodityBean);
                    setMoney();
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

                    updateCommodityView(commodityBean);
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
            Map map = PayUtils.payV2(BuyActivity.this, String.valueOf(payment), getString(R.string.app_name), orderNumber);
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
                showAlert(BuyActivity.this, "支付失败:" + payResult);
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
