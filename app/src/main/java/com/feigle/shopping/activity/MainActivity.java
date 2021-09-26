package com.feigle.shopping.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.bean.CommodityBean;
import com.feigle.shopping.fragment.CommodityFragment;
import com.feigle.shopping.fragment.MeFragment;
import com.feigle.shopping.fragment.ShoppingCartFragment;
import com.feigle.shopping.fragment.dummy.DummyContent;
import com.feigle.shopping.http.HttpUtils;
import com.feigle.shopping.utils.SharedPreferencesUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.lxj.xpopup.interfaces.OnCancelListener;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, CommodityFragment.OnListFragmentInteractionListener,
        ShoppingCartFragment.OnListFragmentInteractionListener, MeFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;
    private BottomNavigationView navigation;
    private ViewPager viewPager;
    private FragmentManager fragmentManager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private MenuItem menuItem;
    private XPopup.Builder builder;
    private LoadingPopupView mLoadingPopupView;
    public List<CommodityBean> commodityBeans = new ArrayList<>();
    private Activity mActivity;
    private long exitTime = System.currentTimeMillis();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_shopping_cart:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_me:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;
        initView();
        new UpdateAppAsyncTask().execute();
    }

    private void initView() {
        builder = new XPopup.Builder(this);
        mLoadingPopupView = builder.asLoading(getString(R.string.loading));

        fragmentManager = getSupportFragmentManager();
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager);

        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(myFragmentPagerAdapter);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            // System.currentTimeMillis()无论何时调用，肯定大于2000
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        menuItem = navigation.getMenu().getItem(i);
        menuItem.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onShoppingCartButtonClick(CommodityBean commodityBean) {
        String name = SharedPreferencesUtils.sharedPreferencesRead(this, SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, "");
        String commodityId = String.valueOf(commodityBean.getId());
        if (!name.equals(""))
            new ShoppingAddAsyncTask().execute(HttpUtils.SERVLET_SHOPPING_ADD_REQUEST, name, commodityId);
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBuyButtonClick(CommodityBean commodityBean) {
        String name = SharedPreferencesUtils.sharedPreferencesRead(this, SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, "");
        String commodityId = String.valueOf(commodityBean.getId());
        if (!name.equals("")) {
            Intent intent = new Intent(this, BuyActivity.class);
            intent.putExtra("name", name);
            intent.putExtra("commodityBean", commodityBean);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onCommodityChecked(boolean checked, CommodityBean commodityBean) {
        if (checked)
            commodityBeans.add(commodityBean);
        else
            commodityBeans.remove(commodityBean);
    }

    @Override
    public void onGoToPayClick() {
        if (commodityBeans.size() > 0) {
            String name = SharedPreferencesUtils.sharedPreferencesRead(this, SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, "");
            if (!name.equals("")) {
                Intent intent = new Intent(this, BuyListActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("commodityBeans", (Serializable) commodityBeans);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            }
        } else
            Toast.makeText(this, R.string.no_select, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteClick(final String id) {
        builder.asConfirm(getString(R.string.comfirm_waring), getString(R.string.comfirm_delete), new OnConfirmListener() {
            @Override
            public void onConfirm() {
                new ShoppingDeleteAsyncTask().execute(HttpUtils.SERVLET_SHOPPING_DELETE_REQUEST, id);
            }
        }, new OnCancelListener() {
            @Override
            public void onCancel() {

            }
        }).show();
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        public Fragment[] fragments = new Fragment[]{CommodityFragment.newInstance(1), ShoppingCartFragment.newInstance(1), MeFragment.newInstance("1", "2")};

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments[i];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }

    class ShoppingAddAsyncTask extends AsyncTask<String, Integer, Map> {

        Map map = new HashMap();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingPopupView.show();
        }

        @Override
        protected Map doInBackground(String... strings) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", strings[1]);
                jsonObject.put("commodityId", strings[2]);

                map = HttpUtils.post(strings[0], jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            if (mLoadingPopupView.isShow())
                mLoadingPopupView.dismiss();

            boolean flag = (boolean) map.get("flag");
            if (flag) {
                String s = (String) map.get("data");
                try {
                    Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
        }
    }

    class ShoppingDeleteAsyncTask extends AsyncTask<String, Integer, Map> {

        Map map = new HashMap();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingPopupView.show();
        }

        @Override
        protected Map doInBackground(String... strings) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", SharedPreferencesUtils.sharedPreferencesRead(getApplicationContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, ""));
                jsonObject.put("id", strings[1]);

                map = HttpUtils.post(strings[0], jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            if (mLoadingPopupView.isShow())
                mLoadingPopupView.dismiss();

            boolean flag = (boolean) map.get("flag");
            if (flag) {
                String s = (String) map.get("data");
                try {
                    Toast.makeText(getBaseContext(), s, Toast.LENGTH_LONG).show();
                    ShoppingCartFragment shoppingCartFragment = (ShoppingCartFragment) myFragmentPagerAdapter.fragments[1];
                    shoppingCartFragment.refreshLayout.autoRefresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getBaseContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }
        }
    }

    class UpdateAppAsyncTask extends AsyncTask<String,Integer,Map>{

        @Override
        protected Map doInBackground(String... strings) {
            Map map = HttpUtils.updateApp(mActivity);
            return map;
        }

        @Override
        protected void onPostExecute(Map map) {
            super.onPostExecute(map);
            boolean flag = (boolean)map.get("flag");
            if (!flag)
                Toast.makeText(getApplicationContext(),map.get("data").toString(),Toast.LENGTH_LONG).show();
        }
    }
}
