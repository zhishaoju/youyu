package com.youyu.gao.xiao.activity;

import static com.youyu.gao.xiao.utils.Contants.Net.BASE_URL;
import static com.youyu.gao.xiao.utils.Contants.Net.CODE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.youyu.gao.xiao.R;
import com.youyu.gao.xiao.net.NetInterface.RequestResponse;
import com.youyu.gao.xiao.utils.LogUtil;
import com.youyu.gao.xiao.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author zhisiyi
 * @Date 2020.04.28 13:42
 * @Comment
 */
public class FindPasswordActivity extends BaseActivity {

    private String TAG = FindPasswordActivity.class.getSimpleName();

    @BindView(R.id.bt_find_password)
    Button btFindPassword;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.fl_chat)
    FrameLayout flChat;
    @BindView(R.id.fl_qq)
    FrameLayout flQq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
        initListener();
    }

    @OnClick(R.id.bt_find_password)
    public void onViewClicked() {
        String url = BASE_URL + CODE;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mobile", etPhone.getText().toString());
            jsonObject.put("imei", Utils.getIMEI());
        } catch (JSONException e) {
            LogUtil.showELog(TAG, "R.id.bt_login e :" + e.getLocalizedMessage());
        }
        String param = jsonObject.toString();
        post(url, param);
    }

    private void initListener() {
        setNetListener(new RequestResponse() {
            @Override
            public void failure(Exception e) {
                LogUtil.showELog(TAG, "setNetListener check code e :" + e.getLocalizedMessage());
            }

            @Override
            public void success(String data) {
                Intent intent = new Intent(FindPasswordActivity.this, RegisterSetPassActivity.class);
                intent.putExtra("mobile", etPhone.getText().toString());
                startActivity(intent);
            }
        });
    }
}
