package com.feigle.shopping.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.adapter.MyOrderListRecyclerViewAdapter;
import com.feigle.shopping.bean.OrderBean;
import com.feigle.shopping.http.HttpUtils;
import com.feigle.shopping.utils.SharedPreferencesUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListActivity extends AppCompatActivity implements MyOrderListRecyclerViewAdapter.OnItemClickListener {

    private String status;
    private int page = 1;

    public static final int STATUS_ALL = -1;
    public static final int STATUS_WAIT_PAY = 0;
    public static final int STATUS_WAIT_SEND = 1;
    public static final int STATUS_SEND = 2;
    public static final int STATUS_RETURN = 3;
    public static final int STATUS_CANCEL = 4;

    private SmartRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private MyOrderListRecyclerViewAdapter myOrderListRecyclerViewAdapter;

    private List<OrderBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        status = String.valueOf(intent.getIntExtra("status", -1));

        setTitle(title);

        initView();
    }

    private void initView() {
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                new GetOrderListAsyncTask().execute(HttpUtils.SERVLET_GET_ORDER_LIST_REQUEST, status);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                page += 1;
                new GetMoreOrderListAsyncTask().execute(HttpUtils.SERVLET_GET_ORDER_LIST_REQUEST, status);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myOrderListRecyclerViewAdapter = new MyOrderListRecyclerViewAdapter(list, this);
        recyclerView.setAdapter(myOrderListRecyclerViewAdapter);

        refreshLayout.autoRefresh();
    }

    @Override
    public void onItemClick(OrderBean orderBean) {

    }

    private void updateList(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            OrderBean orderBean = new OrderBean();

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            orderBean.setId(jsonObject.getString("id"));
            orderBean.setContact(jsonObject.getString("name"));
            orderBean.setPhone(jsonObject.getString("phone"));
            orderBean.setAddress(jsonObject.getString("address"));
            orderBean.setPostalCode(jsonObject.getString("postalCode"));
            orderBean.setCommodity(jsonObject.getString("commodityName"));
            orderBean.setQuantity(jsonObject.getString("quantity"));
            orderBean.setCreateTime(jsonObject.getString("createTime"));
            orderBean.setExpressNmber(jsonObject.getString("expressNumber"));
            orderBean.setExpressTime(jsonObject.getString("expressTime"));
            orderBean.setExpressCompany(jsonObject.getString("expressCompany"));
            orderBean.setCourierFee(jsonObject.getString("courierFee"));
            orderBean.setUserName(jsonObject.getString("userName"));
            orderBean.setOrderNumber(jsonObject.getString("orderNumber"));
            orderBean.setPayment(jsonObject.getString("payment"));
            orderBean.setStatus(jsonObject.getString("status"));
            list.add(orderBean);
        }
    }

    class GetOrderListAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", SharedPreferencesUtils.sharedPreferencesRead(getApplicationContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, ""));
                jsonObject.put("status", status);
                jsonObject.put("page", page);
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
                    refreshLayout.finishRefresh(true);
                    list.clear();

                    JSONArray jsonArray = new JSONArray(s);
                    if (jsonArray.length() > 0) {
                        updateList(jsonArray);
                        myOrderListRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_data, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    refreshLayout.finishRefresh(false);
                    e.printStackTrace();
                }
            } else {
                refreshLayout.finishRefresh(false);
                Toast.makeText(getApplicationContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
        }
    }

    class GetMoreOrderListAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", SharedPreferencesUtils.sharedPreferencesRead(getApplicationContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, ""));
                jsonObject.put("status", status);
                jsonObject.put("page", page);
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
                    refreshLayout.finishLoadMore(true);

                    JSONArray jsonArray = new JSONArray(s);
                    if (jsonArray.length() > 0) {
                        updateList(jsonArray);
                        myOrderListRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        page -= 1;
                        Toast.makeText(getApplicationContext(), R.string.no_more_data, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    page -= 1;
                    refreshLayout.finishLoadMore(false);
                    e.printStackTrace();
                }
            } else {
                page -= 1;
                refreshLayout.finishLoadMore(false);
                Toast.makeText(getApplicationContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
        }
    }
}
