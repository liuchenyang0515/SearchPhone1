package com.example.searchphone.mvp.impl;

import android.util.Log;

import com.example.searchphone.business.HttpUtils;
import com.example.searchphone.model.JsonparsePhone;
import com.example.searchphone.model.GsonParsePhone;
import com.example.searchphone.mvp.MvpMainView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainPresenter extends BasePresenter {
    //String mUrl = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
    String mUrl = "http://mobsec-dianhua.baidu.com/dianhua_api/open/location";
    MvpMainView mvpMainView;
    JsonparsePhone mPhone;
    GsonParsePhone mPhone1;
    String phoneNum;
    private static final String TAG = "MainPresenter";

    public JsonparsePhone getPhoneInfo() {
        return mPhone;
    }

    public GsonParsePhone getPhoneInfo1() {
        return mPhone1;
    }

    public MainPresenter(MvpMainView mainView) {
        mvpMainView = mainView;
    }

    public void searchPhoneInfo(String phone) {
        if (phone.length() != 11) {
            mvpMainView.showToast("请输入正确的手机号");
            return;
        }
        phoneNum = phone;
        mvpMainView.showLoading();
        // 写上http请求处理逻辑
        sendHttp(phone);
    }

    private void sendHttp(String phone) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("tel", phone);
        HttpUtils httpUtils = new HttpUtils(new HttpUtils.HttpResponse() {
            @Override
            public void onSuccess(Object object) {
                String json = object.toString();
                // 使用Gson时需要下面2句，使用JSONObject时注释下面2句
                StringBuilder str = new StringBuilder(json);
                json = str.substring(0, 14) + "phoneNumber" + str.substring(25);


                // 使用JSONObject
                //mPhone = parseModelWithOrgJson(json);
                // Gson
                mPhone1 = parseModelWithGson(json);
                // FastJson
                // mPhone = parseModelWithFastJson(json);
                mvpMainView.hidenLoading();
                mvpMainView.updateView();
            }

            @Override
            public void onFail(String error) {
                mvpMainView.showToast(error);
                mvpMainView.hidenLoading();
            }
        });
        httpUtils.sendGetHttp(mUrl, map);
    }

    // 手动练习json解析，根据自己选择需要来修改,这里写的是未修改json的手机号
    // 还是动态手机号解析
    private JsonparsePhone parseModelWithOrgJson(String json) {
        JsonparsePhone phone = new JsonparsePhone();
        Log.d(TAG, "=====================" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject response = jsonObject.getJSONObject("response");
            // Log.d(TAG, "response=====================" + response);
            JSONObject num = response.getJSONObject(phoneNum);// 外面传进来的手机号，json可以一步步处理动态key
            phone.setTelString(phoneNum);
            // Log.d(TAG, "num======================" + num);
            JSONObject detail = num.getJSONObject("detail");

            String province = detail.getString("province");
            phone.setProvince(province);

            String operator = detail.getString("operator");
            phone.setOperator(operator);

            JSONArray areaArr = detail.getJSONArray("area");
            JSONObject obj = areaArr.getJSONObject(0);
            String city = obj.getString("city");
            phone.setCity(city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return phone;
    }

    private GsonParsePhone parseModelWithGson(String json) {
        Gson gson = new Gson();
        GsonParsePhone phone = gson.fromJson(json, GsonParsePhone.class);
        return phone;
    }

    private JsonparsePhone parseModelWithFastJson(String json) {
        JsonparsePhone phone = com.alibaba.fastjson.JSONObject.parseObject(json, JsonparsePhone.class);
        return phone;
    }
}
