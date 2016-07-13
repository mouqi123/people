package com.people.sotp.commons.util;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import net.sf.json.JSONObject;

public class AlidayuNote {
	
	
	
	
	public boolean sendNoteCode(String phoneNum,String code){
		
		boolean falg=false;
		String url = "	http://gw.api.taobao.com/router/rest";
		String appkey = "23397599";
		String secret = "20faa13eef5bfd06d016d34523c1cd81";
		
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend("123456");
		req.setSmsType("normal");
		req.setSmsFreeSignName("S盾");
		req.setSmsParamString("{\"code\":\""+code+"\"}");
		req.setRecNum(phoneNum);
		req.setSmsTemplateCode("sms_11515765");
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonsotp = JSONObject.fromObject(rsp.getBody());
		JSONObject alibaba_aliqin_fc_sms_num_send_response=jsonsotp.getJSONObject("alibaba_aliqin_fc_sms_num_send_response");
		JSONObject result=alibaba_aliqin_fc_sms_num_send_response.getJSONObject("result");
		 falg = result.getBoolean("success");
		return falg;
		
	}

}
