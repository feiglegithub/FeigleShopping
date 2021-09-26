package com.feigle.shopping.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.activity.MainActivity;
import com.feigle.shopping.adapter.MyAddressRecyclerViewAdapter;
import com.feigle.shopping.adapter.MyShoppingCartRecyclerViewAdapter;
import com.feigle.shopping.bean.CommodityBean;
import com.feigle.shopping.fragment.dummy.DummyContent.DummyItem;
import com.feigle.shopping.http.HttpUtils;
import com.feigle.shopping.utils.SharedPreferencesUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ShoppingCartFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<CommodityBean> list;
    private XPopup.Builder builder;
    private BasePopupView basePopupView;
    public RefreshLayout refreshLayout;
    private MyShoppingCartRecyclerViewAdapter myShoppingCartRecyclerViewAdapter;
    private String name = "";
    private TextView noDataTextView;
    private Button goToPayButton;

    private int page = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShoppingCartFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShoppingCartFragment newInstance(int columnCount) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        list = new ArrayList<CommodityBean>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_cart_list, container, false);
        View lsitView = view.findViewById(R.id.list);

        refreshLayout = (RefreshLayout) view.findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                name = SharedPreferencesUtils.sharedPreferencesRead(getContext(), SharedPreferencesUtils.USER, "name", "");
                if (!name.equals("")) {
                    page = 1;
                    new GetShoppingListAsyncTask().execute(HttpUtils.SERVLET_GET_SHOPPING_CART_REQUEST, name);
                } else {
                    list.clear();
                    refreshlayout.finishRefresh(false);
                    Toast.makeText(getContext(), "你还没有登录，请先登录！", Toast.LENGTH_LONG).show();
                }
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                name = SharedPreferencesUtils.sharedPreferencesRead(getContext(), SharedPreferencesUtils.USER, "name", "");
                if (!name.equals("")) {
                    page += 1;
                    new GetMoreShoppingListAsyncTask().execute(HttpUtils.SERVLET_GET_SHOPPING_CART_REQUEST, name);
                } else {
                    list.clear();
                    refreshlayout.finishLoadMore(false);
                    Toast.makeText(getContext(), "你还没有登录，请先登录！", Toast.LENGTH_LONG).show();
                }
            }
        });

        myShoppingCartRecyclerViewAdapter = new MyShoppingCartRecyclerViewAdapter(list, mListener);
        // Set the adapter
        if (lsitView instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) lsitView;
            recyclerView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(myShoppingCartRecyclerViewAdapter);
        }

        if (!SharedPreferencesUtils.sharedPreferencesRead(getContext(), SharedPreferencesUtils.USER, "name", "").equals("")) {
            refreshLayout.autoRefresh();
        }

        noDataTextView = view.findViewById(R.id.noDataTextView);

        goToPayButton = view.findViewById(R.id.goToPayButton);
        goToPayButton.setOnClickListener(new MyOnClickListener());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
            ((MainActivity)context).commodityBeans.clear();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCommodityChecked(boolean checked, CommodityBean commodityBean);

        void onGoToPayClick();

        void onDeleteClick(String id);
    }

    private void updateList(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            CommodityBean commodityBean = new CommodityBean();

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            commodityBean.setCommodityName(jsonObject.getString("commodityName"));
//                    commodityBean.setCost(Double.parseDouble(jsonObject.getString("cost")));
            commodityBean.setCreateTime(jsonObject.getString("createTime"));
            commodityBean.setDetail(jsonObject.getString("detail"));
            commodityBean.setId(Integer.parseInt(jsonObject.getString("id")));
            commodityBean.setImgPath(jsonObject.getString("imgPath"));
            commodityBean.setPrice(Double.parseDouble(jsonObject.getString("price")));
            commodityBean.setQuantity(Integer.parseInt(jsonObject.getString("quantity")));
            commodityBean.setWeight(Double.parseDouble(jsonObject.getString("weight")));
            list.add(commodityBean);
        }
    }

    class GetShoppingListAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", strings[1]);
                jsonObject.put("page",page);
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
                        goToPayButton.setVisibility(View.VISIBLE);
                        noDataTextView.setVisibility(View.GONE);
                        updateList(jsonArray);
                    } else {
                        noDataTextView.setVisibility(View.VISIBLE);
                        goToPayButton.setVisibility(View.INVISIBLE);
                    }
                    myShoppingCartRecyclerViewAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    refreshLayout.finishRefresh(false);
                    e.printStackTrace();
                }
            } else {
                refreshLayout.finishRefresh(false);
                Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }


        }
    }

    class GetMoreShoppingListAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            Map map = new HashMap();

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", strings[1]);
                jsonObject.put("page",page);
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
                        noDataTextView.setVisibility(View.GONE);
                        updateList(jsonArray);
                        myShoppingCartRecyclerViewAdapter.notifyDataSetChanged();
                    } else {
                        page -= 1;
                    }

                } catch (Exception e) {
                    page -= 1;
                    refreshLayout.finishLoadMore(false);
                    e.printStackTrace();
                }
            } else {
                page -= 1;
                refreshLayout.finishLoadMore(false);
                Toast.makeText(getContext(), R.string.error_network, Toast.LENGTH_LONG).show();
            }


        }
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.goToPayButton:
                    mListener.onGoToPayClick();
                    break;
                default:
                    break;
            }
        }
    }
}
