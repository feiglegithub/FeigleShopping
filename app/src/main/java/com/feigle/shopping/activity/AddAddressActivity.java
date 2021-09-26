package com.feigle.shopping.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.http.HttpUtils;
import com.feigle.shopping.utils.SharedPreferencesUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText contactEditText;
    private EditText phoneEditText;
    private EditText addressEditText;
    private Switch defSwitch;
    private Button commitButton;
    private XPopup.Builder mXPopup;
    private LoadingPopupView mLoadingPopupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        initView();
    }

    private void initView() {
        mXPopup = new XPopup.Builder(this);
        mLoadingPopupView = mXPopup.asLoading(getString(R.string.upload));

        contactEditText = findViewById(R.id.contactEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        defSwitch = findViewById(R.id.defSwitch);
        commitButton = findViewById(R.id.commitButton);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commitButton:
                String contact = contactEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String address = addressEditText.getText().toString();
                boolean def = defSwitch.isChecked();
                if (contact.equals("")) {
                    contactEditText.setError(getString(R.string.error_field_required));
                    return;
                }
                if (phone.equals("")) {
                    phoneEditText.setError(getString(R.string.error_field_required));
                    return;
                }
                if (address.equals("")) {
                    addressEditText.setError(getString(R.string.error_field_required));
                    return;
                }

                new AddAddressAsyncTask().execute(HttpUtils.SERVLET_ADD_ADDRESS_REQUEST, contact, phone, address, String.valueOf(def == true ? 1 : 0));
                break;
            default:
                break;
        }
    }

    class AddAddressAsyncTask extends AsyncTask<String, Integer, Map> {

        @Override
        protected Map doInBackground(String... strings) {
            String name = SharedPreferencesUtils.sharedPreferencesRead(getApplicationContext(), SharedPreferencesUtils.USER, SharedPreferencesUtils.KEY_Name, "");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", name);
                jsonObject.put("contact", strings[1]);
                jsonObject.put("phone", strings[2]);
                jsonObject.put("address", strings[3]);
                jsonObject.put("def", strings[4]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Map map = HttpUtils.post(strings[0], jsonObject.toString());
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
            boolean flag = Boolean.parseBoolean(map.get(HttpUtils.RESPONSE_PARAM_IS_SUCCESS_FLAG).toString());
            if (flag) {
                String result = (String) map.get(HttpUtils.RESPONSE_PARAM_DATA);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getApplicationContext(), R.string.error_network, Toast.LENGTH_LONG).show();
        }
    }
}
