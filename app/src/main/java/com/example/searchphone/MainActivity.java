package com.example.searchphone;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.searchphone.model.GsonParsePhone;
import com.example.searchphone.model.JsonparsePhone;
import com.example.searchphone.mvp.MvpMainView;
import com.example.searchphone.mvp.impl.MainPresenter;

public class MainActivity extends AppCompatActivity implements MvpMainView {

    EditText input_phone;
    Button btn_search;
    TextView result_phone;
    TextView result_province;
    TextView result_type;
    TextView result_carrier;
    MainPresenter mainPresenter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        input_phone = findViewById(R.id.input_phone);
        btn_search = findViewById(R.id.btn_search);
        result_phone = findViewById(R.id.result_phone);
        result_province = findViewById(R.id.result_province);
        result_type = findViewById(R.id.result_type);
        result_carrier = findViewById(R.id.result_carrier);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPresenter.searchPhoneInfo(input_phone.getText().toString());
            }
        });
        mainPresenter = new MainPresenter(this);
        //mainPresenter.attach(this);
    }

    // mvpMainView接口的方法
    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

/*    // 用JSONObject解析时用这个
   @Override
    public void updateView() {
        JsonparsePhone phone = mainPresenter.getPhoneInfo();
        result_phone.setText("手机号：" + phone.getTelString());
        result_province.setText("省份：" + phone.getProvince());
        result_type.setText("城市：" + phone.getCity());
        result_carrier.setText("运营商：" + phone.getOperator());
    }*/

    // 用Gson解析时用这个
    @Override
    public void updateView() {
        GsonParsePhone phone = mainPresenter.getPhoneInfo1();
        result_phone.setText("手机号：" + input_phone.getText().toString()); // 手机号码直接在EditView获取就行
        result_province.setText("省份：" + phone.getResponse().getPhoneNumber().getDetail().getProvince());
        result_type.setText("城市：" + phone.getResponse().getPhoneNumber().getDetail().getArea().get(0).getCity());
        result_carrier.setText("运营商：" + phone.getResponse().getPhoneNumber().getDetail().getOperator());
    }

    @Override
    public void showLoading() {
        if (progressDialog == null) {
            progressDialog = ProgressDialog.show(this, "", "正在加载...", true, false);
        } else if (progressDialog.isShowing()) {
            progressDialog.setTitle("");
            progressDialog.setMessage("正在加载...");
        }
        progressDialog.show();
    }

    @Override
    public void hidenLoading() {
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
}
