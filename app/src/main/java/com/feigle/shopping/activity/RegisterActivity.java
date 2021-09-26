package com.feigle.shopping.activity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.http.HttpUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText pswEditText;
    private EditText confirmEditText;
    private EditText phoneEditText;
    private Button commitButton;
    private XPopup.Builder builder;
    private LoadingPopupView mLoadingPopupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
    }

    private void initView() {
        nameEditText = findViewById(R.id.nameEditText);
        pswEditText = findViewById(R.id.pswEditText);
        confirmEditText = findViewById(R.id.confirmEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        commitButton = findViewById(R.id.commitButton);

        builder = new XPopup.Builder(this);
        mLoadingPopupView = builder.asLoading(getString(R.string.loading));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.commitButton:
                String name = nameEditText.getText().toString();
                String psw = pswEditText.getText().toString();
                String confirm = confirmEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String problem = phoneEditText.getText().toString();
                String solution = phoneEditText.getText().toString();

                if (name.equals("")) {
                    nameEditText.setError(getString(R.string.error_field_required));
                    return;
                }
                if (name.length()>6){
                    nameEditText.setError(getString(R.string.error_field_exceed));
                    return;
                }
                if (psw.equals("")) {
                    pswEditText.setError(getString(R.string.error_field_required));
                    return;
                }
                if (!psw.equals(confirm)){
                    confirmEditText.setError(getString(R.string.not_the_same));
                    return;
                }
                if (phone.equals("")) {
                    phoneEditText.setError(getString(R.string.error_field_required));
                    return;
                }

                new RegisterAsyncTask().execute(HttpUtils.SERVLET_REGISTER_REQUEST, name, psw, phone);
                break;
            default:
                break;
        }
    }

    class RegisterAsyncTask extends AsyncTask<String, Integer, Map> {
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

        @Override
        protected Map doInBackground(String... strings) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", strings[1]);
                jsonObject.put("psw", strings[2]);
                jsonObject.put("phone", strings[3]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Map map = HttpUtils.post(strings[0], jsonObject.toString());
            return map;
        }
    }
}
