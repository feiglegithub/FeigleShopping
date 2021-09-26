package com.feigle.shopping.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.adapter.MyAddressRecyclerViewAdapter;
import com.feigle.shopping.bean.AddressBean;
import com.feigle.shopping.http.HttpUtils;
import com.feigle.shopping.utils.SharedPreferencesUtils;
import com.scwang.smartrefresh.header.WaveSwipeHeader;
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

public class ChooseAddressActivity extends AppCompatActivity implements MyAddressRecyclerViewAdapter.OnItemClickListener {

    private Button addButton;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    private MyAddressRecyclerViewAdapter myAddressRecyclerViewAdapter;

    private List<AddressBean> list;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLayout.autoRefresh();
    }

    private void initView() {
        addButton = findViewById(R.id.addButton);

        refreshLayout = (RefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                new GetAddressListAsyncTask().execute(HttpUtils.SERVLET_GET_ADDRESS_LIST_REQUEST);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                page += 1;
                new GetMoreAddressListAsyncTask().execute(HttpUtils.SERVLET_GET_ADDRESS_LIST_REQUEST);
            }
        });

        list = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAddressRecyclerViewAdapter = new MyAddressRecyclerViewAdapter(list);
        recyclerView.setAdapter(myAddressRecyclerViewAdapter);
        myAddressRecyclerViewAdapter.setOnItemClickListener(this);
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.addButton:
                intent = new Intent(getApplicationContext(), AddAddressActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AddressBean addressBean) {
        Intent intent = new Intent();
        intent.putExtra("addressBean", addressBean);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void updateList(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            AddressBean addressBean = new AddressBean();

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            addressBean.setAddress(jsonObject.getString("address"));
            addressBean.setContact(jsonObject.getString("contact"));
            addressBean.setDef(jsonObject.getString("def"));
            addressBean.setId(jsonObject.getString("id"));
            addressBean.setPhone(jsonObject.getString("phone"));
            list.add(addressBean);
        }
    }

    class GetAddressListAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            String name = SharedPreferencesUtils.sharedPreferencesRead(getApplicationContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, "");
            Map map = new HashMap();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", name);
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
                        myAddressRecyclerViewAdapter.notifyDataSetChanged();
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

    class GetMoreAddressListAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            String name = SharedPreferencesUtils.sharedPreferencesRead(getApplicationContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, "");
            Map map = new HashMap();
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", name);
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
                        myAddressRecyclerViewAdapter.notifyDataSetChanged();
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
