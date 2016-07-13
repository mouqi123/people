package com.people.sotp.urlcontrollers;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.people.sotp.commons.util.Base64;
import com.people.sotp.dataobject.ApplyDO;
import com.people.sotp.dataobject.SotpAuthDO;
import com.people.sotp.payment.dao.ApplyDAO;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/genurl")
public class GenTestUrl {
	
	@Resource
	private ApplyDAO applyDao;
	@Resource
	private SotpDao sotpDao;
	
	public static String appId="884aee78ca8eb8be6bccf96f716064691ac6b226";
	
	@RequestMapping(value = "/genTest.do", method = RequestMethod.GET)
	public void entry(HttpServletRequest request, HttpServletResponse response){
		String phoneNum = request.getParameter("phoneNum");
		String number = request.getParameter("num");
		long num = Long.parseLong(number);		
		Long one = Long.parseLong(phoneNum);
		
		for (int i = 0; i < num; i++) {
			
			
			applyPlugin( (one+i));
			
		}
		
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public  void applyPlugin(Long phoneNum){
		
		
		
//		String num = request.getParameter("num");
		
		String sotp = genPluginSotp(phoneNum);
		StringBuffer applyPlugin = new StringBuffer();
		StringBuffer encryption = new StringBuffer();
		
		
		Map<String, Object> sotpMap = new HashMap<String, Object>();
		sotpMap = sotpAuthUtil.getUrlParams(sotp);
		
		String appKey = getAppKey(String.valueOf(sotpMap.get("appId")));
		if (appKey == null) {
			System.out.println("appKey不存在");
			return;
		}
		SotpAuthDO service = new SotpAuthDO();
		try {
			service = sotpDao.selectService();
			applyPlugin.append("http://" + service.getIp() + ":" + service.getPost() + "");
			applyPlugin.append("/" + SotpService.project + "?");
			applyPlugin.append("" + sotpAuthUtil.genRequestSotpAuthMsg(sotpMap, sotp, appKey) + "");
			
			
			String info = sendRequest.sendGet(applyPlugin.toString());
			
			JSONObject jsonsotp = JSONObject.fromObject(info);
			appKey = getAppKey(jsonsotp.getString("appId"));
			if (appKey == null) {
				System.out.println("认证:appKey不存在");
				return;
			}
			boolean falg = sotpAuthUtil.verifySotp(jsonsotp,jsonsotp.getString("appId"),appKey);
			if (!falg) {
				System.out.println("认证：sign不通过");
				return;
			}
			if (jsonsotp.getInt("status") == 0) {
				write(applyPlugin.toString(), false);
//				JSONObject sotpJson = (JSONObject) jsonsotp.get("message");
//				String sotpId =sotpJson.getString("sotpId");
//					
//				
//				String jiaSotp=genEncryption(phoneNum, sotpId);
//				Map<String, Object> jiaSotpMap = new HashMap<String, Object>();
//				jiaSotpMap = sotpAuthUtil.getUrlParams(jiaSotp);
//				
//				String jiaAppKey = getAppKey(String.valueOf(jiaSotpMap.get("appId")));
//				
//				
//				encryption.append("http://" + service.getIp() + ":" + service.getPost() + "");
//				encryption.append("/" + SotpService.project + "?");
//				encryption.append("" + sotpAuthUtil.genRequestSotpAuthMsg(jiaSotpMap, jiaSotp, jiaAppKey) + "");
//				
//				String jiaInfo = sendRequest.sendGet(encryption.toString());
//				
//				
//
//				JSONObject jiasotp = JSONObject.fromObject(jiaInfo);
//				if(jiasotp.getInt("status")==0){
//					write(encryption.toString(), false);
//				}
				
				
				
				
			}else{
				System.out.println("认证：申请插件错误");
				return;
			}
			
		} catch (Exception e) {
		}
		
		
	}

	
	public  String genPluginSotp(Long phoneNum){
		StringBuffer sotp = new StringBuffer();
		
		sotp.append("appId=");
		sotp.append(""+appId+"&");
		sotp.append("pin=");
		sotp.append("147258&");
		sotp.append("phoneNum=");
		sotp.append(""+phoneNum+"&");
		sotp.append("service=");
		sotp.append("zrauth.plugin.apply2&");
		sotp.append("version=");
		sotp.append("2.0&");
		sotp.append("devInfo=");
		
		JSONObject devinfo = new JSONObject();
		devinfo.put("imei","866960021585722" );
		devinfo.put("product_type", "HUAWEI P8max");
		devinfo.put("phoneNum", phoneNum);
		devinfo.put("manufacturer", "HUAWEI"); //手机型号
		devinfo.put("system_version", "6.0");//sdk版本
		devinfo.put("sdk_version","23" );//cpu构架
		devinfo.put("cpu","armeabi-v7a" );//手机厂商
		devinfo.put("mac","50:a7:2b:57:46:48");//ip地址
		devinfo.put("dev_type", "Android");
		devinfo.put("ip", "192.168.99.190");
		
		
		sotp.append(Base64.encode(devinfo.toString()));
		
		
		
		return sotp.toString();
	}
	
	public String genEncryption(Long phoneNum,String sotpId){
		
		StringBuffer encryption = new StringBuffer();
		encryption.append("phoneNum=");
		encryption.append(""+phoneNum+"&");
		encryption.append("sotpId=");
		encryption.append(""+sotpId+"&");
		encryption.append("dataInfo=");
		encryption.append("123456&");
		encryption.append("appId=");
		encryption.append(""+appId+"&");
		encryption.append("service=");
		encryption.append("zrauth.business.dataencryption&");
		encryption.append("version=");
		encryption.append("2.0");
		
		return encryption.toString();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (apply != null ) {
			return apply.getAppKey();
		} else {
			return null;
		}
	}
	
	public void write(String str,boolean falg){
		
		 try {
			FileWriter fileWriter=new FileWriter("c:\\url.txt", true);
			
			if(falg){
			    fileWriter.write(str);
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
	            fileWriter.write("\t");
			}else{
			    fileWriter.write(str);
			    fileWriter.write("\r\n");
			    fileWriter.write("\r\n");
			    fileWriter.write("\r\n");
			}
			  fileWriter.close();
		 } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	
}
