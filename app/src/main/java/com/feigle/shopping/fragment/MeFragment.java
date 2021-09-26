package com.feigle.shopping.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.feigle.shopping.R;
import com.feigle.shopping.activity.AddressActivity;
import com.feigle.shopping.activity.LoginActivity;
import com.feigle.shopping.activity.NegotiateActivity;
import com.feigle.shopping.activity.OrderListActivity;
import com.feigle.shopping.activity.PayGuiActivity;
import com.feigle.shopping.activity.ServiceActivity;
import com.feigle.shopping.activity.SettingActivity;
import com.feigle.shopping.utils.SharedPreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String name = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView loginTextView;
    private TextView nameTextView;
    private LinearLayout settingLinearLayout;
    private LinearLayout addressLinearLayout;
    private LinearLayout payGuideLinearLayout;
    private LinearLayout negotiateLinearLayout;
    private LinearLayout serviceLinearLayout;
    private TextView orderTextView;
    private TextView payTextView;
    private TextView receiveTextView;

    private OnFragmentInteractionListener mListener;

    public MeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeFragment newInstance(String param1, String param2) {
        MeFragment fragment = new MeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        loginTextView = view.findViewById(R.id.loginTextView);
        nameTextView = view.findViewById(R.id.contactTextView);
        settingLinearLayout = view.findViewById(R.id.settingLinearLayout);
        addressLinearLayout = view.findViewById(R.id.addressLinearLayout);
        payGuideLinearLayout = view.findViewById(R.id.payGuideLinearLayout);
        negotiateLinearLayout = view.findViewById(R.id.negotiateLinearLayout);
        serviceLinearLayout = view.findViewById(R.id.serviceLinearLayout);
        orderTextView = view.findViewById(R.id.orderTextView);
        payTextView = view.findViewById(R.id.payTextView);
        receiveTextView = view.findViewById(R.id.receiveTextView);

        loginTextView.setOnClickListener(new MyOnClickListener());
        settingLinearLayout.setOnClickListener(new MyOnClickListener());
        addressLinearLayout.setOnClickListener(new MyOnClickListener());
        orderTextView.setOnClickListener(new MyOnClickListener());
        payTextView.setOnClickListener(new MyOnClickListener());
        receiveTextView .setOnClickListener(new MyOnClickListener());
        payGuideLinearLayout .setOnClickListener(new MyOnClickListener());
        negotiateLinearLayout .setOnClickListener(new MyOnClickListener());
        serviceLinearLayout .setOnClickListener(new MyOnClickListener());

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        name = SharedPreferencesUtils.sharedPreferencesRead(getContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, "");
        if (name.equals("")) {
            loginTextView.setVisibility(View.VISIBLE);
            nameTextView.setVisibility(View.GONE);
        } else {
            nameTextView.setVisibility(View.VISIBLE);
            nameTextView.setText(name);
            loginTextView.setVisibility(View.GONE);
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private boolean isLogin() {
        name = SharedPreferencesUtils.sharedPreferencesRead(getContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, "");
        if (name.equals("")) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            return false;
        } else
            return true;
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent;
            if (!isLogin()) {
                return;
            }
            switch (v.getId()) {
                case R.id.loginTextView:
                    intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    break;
                case R.id.settingLinearLayout:
                    intent = new Intent(getContext(),SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.addressLinearLayout:
                    intent = new Intent(getContext(), AddressActivity.class);
                    startActivity(intent);
                    break;
                case R.id.orderTextView:
                    intent = new Intent(getContext(), OrderListActivity.class);
                    intent.putExtra("title",getString(R.string.order_all));
                    intent.putExtra("status",OrderListActivity.STATUS_ALL);
                    startActivity(intent);
                    break;
                case R.id.payTextView:
                    intent = new Intent(getContext(), OrderListActivity.class);
                    intent.putExtra("title",getString(R.string.order_wait_pay));
                    intent.putExtra("status",OrderListActivity.STATUS_WAIT_PAY);
                    startActivity(intent);
                    break;
                case R.id.receiveTextView:
                    intent = new Intent(getContext(), OrderListActivity.class);
                    intent.putExtra("title",getString(R.string.order_wait_receive));
                    intent.putExtra("status",OrderListActivity.STATUS_SEND);
                    startActivity(intent);
                    break;
                case R.id.payGuideLinearLayout:
                    intent = new Intent(getContext(), PayGuiActivity.class);
                    startActivity(intent);
                    break;
                case R.id.negotiateLinearLayout:
                    intent = new Intent(getContext(), NegotiateActivity.class);
                    startActivity(intent);
                    break;
                case R.id.serviceLinearLayout:
                    intent = new Intent(getContext(), ServiceActivity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    }
}
