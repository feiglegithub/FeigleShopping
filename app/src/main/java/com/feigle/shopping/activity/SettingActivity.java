package com.feigle.shopping.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.feigle.shopping.R;
import com.feigle.shopping.http.HttpUtils;
import com.feigle.shopping.utils.SharedPreferencesUtils;
import com.feigle.shopping.utils.Utils;

import java.util.Map;

public class SettingActivity extends AppCompatActivity {

    private Button exitButton;
    private Activity mActivity;
    private TextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mActivity = this;
        initView();
    }

    private void initView() {
        exitButton = findViewById(R.id.exitButton);
        versionTextView = findViewById(R.id.versionTextView);
        versionTextView.setText(Utils.getAppVersionName(getApplicationContext()));
    }

    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.exitButton:
                SharedPreferencesUtils.sharedPreferencesWrite(this,SharedPreferencesUtils.USER,SharedPreferencesUtils.KEY_Name,"");
                finish();
                break;
            case R.id.updatePswConstraintLayout:
                intent =new Intent(this,UpdatePswActivity.class);
                startActivity(intent);
                break;
            case R.id.updatePhoneConstraintLayout:
                intent =new Intent(this,UpdatePhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.versionCheckConstrainLayout:
                new UpdateAppAsyncTask().execute();
                break;
            default:
                break;
        }
    }

    class UpdateAppAsyncTask extends AsyncTask<String,Integer,Map> {

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
