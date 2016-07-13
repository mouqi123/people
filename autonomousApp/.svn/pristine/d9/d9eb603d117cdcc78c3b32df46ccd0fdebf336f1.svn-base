package com.people.sotp.interfaces.service;

import java.sql.SQLException;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.people.sotp.commons.base.GlobalParam;
import com.people.sotp.commons.util.sendRequest;
import com.people.sotp.commons.util.sotpAuthUtil;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.AutoAppDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.payment.dao.ApplyDAO;
import com.people.sotp.payment.dao.SotpAuthDAO;
import net.sf.json.JSONObject;



@Service
public class SotpService {
	
	@Resource
	private SotpAuthDAO sotpDao;
	@Resource
	private ApplyDAO applyDao;
	
	private static Log log = LogFactory.getLog(SotpService.class);
	
	public static String project = "/fidoportal";
	
	
	
	/**
	 * 申请插件1
	 * @param json
	 * @return
	 */
	public String pluginApply(AutoAppDO auto) {
		
		StringBuffer jsondata = new StringBuffer();
		StringBuffer sotpGet = new StringBuffer();
		StringBuffer errorMsg = new StringBuffer();
		int status=-1;
		
		
		String sotpInfo = auto.getSotpInfo();
		sotpInfo=sotpInfo.replaceAll("(\r\n|\r|\n|\n\r)", "");
		Map<String, Object> sotpMap = new HashMap<String, Object>();
		sotpMap = sotpAuthUtil.getUrlParams(sotpInfo);
		
		if(auto.getBusinessName()!=null){
			sotpMap.put("businessName", auto.getBusinessName());
		}
		if(auto.getGrade()!=null){
			sotpMap.put("grade", auto.getGrade());
		}
		
		
		
		String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
		if (appKey == null) {
			jsondata.append("{}");
			return utilJson(auto.getService(),GlobalParam.AppIdError, jsondata.toString(),"appId not match");
		}
		
		
		SotpAuthDO service = new SotpAuthDO();
		try {
			service = sotpDao.querySotpAuthOne();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		sotpGet.append("http://" + service.getIp() + ":" + service.getPost() + "");
		sotpGet.append("/" + project + "");
		
		String sotpAuth=sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, sotpInfo, appKey);
		
		String info = sendRequest.sendPost(sotpGet.toString(),sotpAuth );
		log.info("terminal======" + auto);
		log.info("Pc======" + sotpGet.toString()+"?"+sotpAuth);
		log.info("Sotp======" + info);
		
		JSONObject jsonsotp = JSONObject.fromObject(info);
		
		appKey = getAppKey(jsonsotp.getString("appId"));
		if (appKey == null) {
			jsondata.append("{}");
			return utilJson(auto.getService(), GlobalParam.AppIdError, jsondata.toString(),"appId not match");
		}
		boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
		if (!falg) {
			jsondata.append("{}");
			return utilJson(auto.getService(),GlobalParam.SignMatch, jsondata.toString(), "sign not match");
		}
		
		JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
		status=jsonsotp.getInt("status");
		if (jsonsotp.getInt("status") == 0) {
			if(sotpJson.containsKey("hwInfo")){
				jsondata.append(" "+sotpJson.toString()+" ");
			}else
			{
				if(sotpJson.containsKey("data")){
					int num =sotpJson.getString("data").indexOf("{");
					if(num <0){
						jsondata.append("\""+sotpJson.getString("data")+"\"");
					}else{
						jsondata.append(sotpJson.getString("data"));
					}
				}else{
					jsondata.append("{}");
				}
				
			}
			
		} else {
			jsondata.append("{}");
			if(sotpJson.containsKey("errorMsg")){
				errorMsg.append(sotpJson.getString("errorMsg"));
			}
		}
		
		return utilJson(auto.getService(),status, jsondata.toString(), errorMsg.toString());
	}
	
	
	
	
	
	
	
	
		
	
	
	/**
	 * 通过appid获取appkey
	 * 
	 * @param appId
	 * @return
	 */
	public String getAppKey(String appId) {
		ApplyDO apply = new ApplyDO();
		apply.setAppId(appId);
		try {
			apply = applyDao.queryApplyOne(apply);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (apply != null ) {
			return apply.getAppKey();
		} else {
			return null;
		}
	}
	
	
	/**
	 * 返回json
	 * 
	 * @param type
	 *            业务类型
	 * @param status
	 *            结果
	 * @param data
	 *            返回信息
	 * @param errorMsg
	 *            错误信息
	 * @param printWriter
	 */
	public String utilJson(String service,int status, String data, String errorMsg) {
		StringBuffer jsondata = new StringBuffer();
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String serverTime = dateFormat.format(now);
		jsondata.append("{\"service\": \""+service+"\" ");
		jsondata.append(",\"status\":" + status + ",");
		jsondata.append("\"data\":" + data + "");
		jsondata.append(",\"errorMsg\":\"" + errorMsg + "\",");
		jsondata.append("\"serverTime\":\"" + serverTime + "\"}");
		return jsondata.toString();
	}
	
}
