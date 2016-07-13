package com.people.sotp.interfaces.controller;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class text {

	
	
	public static void main(String[] args) {
		String str ="{\"alibaba_aliqin_fc_sms_num_send_response\":{\"result\":{\"err_code\":\"0\",\"model\":\"102136230133^1102774452997\",\"success\":true},\"request_id\":\"zt99b7zfmqva\"}}";
		JSONObject jsonsotp = JSONObject.fromObject(str);
		JSONObject alibaba_aliqin_fc_sms_num_send_response=jsonsotp.getJSONObject("alibaba_aliqin_fc_sms_num_send_response");
		JSONObject result=alibaba_aliqin_fc_sms_num_send_response.getJSONObject("result");
		boolean falg = result.getBoolean("success");
		System.out.println(falg);
		
		
	}
	
}
