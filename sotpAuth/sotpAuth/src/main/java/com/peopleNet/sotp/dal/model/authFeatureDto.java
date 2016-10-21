package com.peopleNet.sotp.dal.model;

import java.io.Serializable;
import java.util.Vector;

import com.peopleNet.sotp.constant.Constant;

public class authFeatureDto implements Serializable {
	/**
	 * 
	 */
	private Integer id;

	private String plugin_id;

	private Integer dev_type;

	private String manufacturer;

	private String product_type;

	private String system_version;

	private String sdk_version;

	private String uuid;

	private String imei;

	private String imsi;

	private String mac;

	private String location;

	private String ip;

	private String phone_num;

	private String pin;

	private String touchId;

	private String irides;

	private String voice;

	private String gesture_code;

	private String cpu;

	private String dev_name;

	private String devId;

	private String join_id;
	
	private String dev_info;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPluginId() {
		return plugin_id;
	}

	public void setPluginId(String pluginId) {
		this.plugin_id = pluginId == null ? null : pluginId.trim();
	}

	public Integer getdevType() {
		return dev_type;
	}

	public void setdevType(Integer devtype) {
		this.dev_type = devtype;
	}

	public String getmanufacturer() {
		return manufacturer;
	}

	public void setmanufacturer(String manufacturer) {
		this.manufacturer = manufacturer == null ? null : manufacturer.trim();
	}

	public String getProductType() {
		return product_type;
	}

	public void setProductType(String product_type) {
		this.product_type = product_type == null ? null : product_type.trim();
	}

	public String getSystemVersion() {
		return system_version;
	}

	public void setSystemVersion(String system_version) {
		this.system_version = system_version == null ? null : system_version.trim();
	}

	public String getSdkVersion() {
		return sdk_version;
	}

	public void setSdkVersion(String sdk_version) {
		this.sdk_version = sdk_version == null ? null : sdk_version.trim();
	}

	public String getuuid() {
		return uuid;
	}

	public void setuuid(String uuid) {
		this.uuid = uuid == null ? null : uuid.trim();
	}

	public String getimei() {
		return imei;
	}

	public void setimei(String imei) {
		this.imei = imei == null ? "" : imei.trim();
	}

	public String getimsi() {
		return imsi;
	}

	public void setimsi(String imsi) {
		this.imsi = imsi == null ? "" : imsi.trim();
	}

	public String getmac() {
		return mac;
	}

	public void setmac(String mac) {
		this.mac = mac == null ? "" : mac.trim();
	}

	public String getlocation() {
		return location;
	}

	public void setlocation(String location) {
		this.location = location == null ? "" : location.trim();
	}

	public String getip() {
		return ip;
	}

	public void setip(String ip) {
		this.ip = ip == null ? "" : ip.trim();
	}

	public String getphone_num() {
		return phone_num;
	}

	public void setphone_num(String phone_num) {
		this.phone_num = phone_num == null ? "" : phone_num.trim();
	}

	public String getpin() {
		return pin;
	}

	public void setpin(String pin) {
		this.pin = pin == null ? "" : pin.trim();
	}

	public String gettouchId() {
		return touchId;
	}

	public void settouchId(String touchId) {
		this.touchId = touchId == null ? "" : touchId.trim();
	}

	public String getirides() {
		return irides;
	}

	public void setirides(String irides) {
		this.irides = irides == null ? "" : irides.trim();
	}

	public String getvoice() {
		return voice;
	}

	public void setvoice(String voice) {
		this.voice = voice == null ? "" : voice.trim();
	}

	public String getgesture_code() {
		return gesture_code;
	}

	public void setgesture_code(String gesture_code) {
		this.gesture_code = gesture_code == null ? "" : gesture_code.trim();
	}

	public String getcpu() {
		return cpu;
	}

	public void setcpu(String cpu) {
		this.cpu = cpu == null ? "" : cpu.trim();
	}

	public String getDevName() {
		return dev_name;
	}

	public void setDevName(String dev_name) {
		this.dev_name = dev_name == null ? "" : dev_name.trim();
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId == null ? "" : devId.trim();
	}

	public String getJoinId() {
		return join_id;
	}

	public void setJoinId(String join_id) {
		this.join_id = join_id == null ? "" : join_id.trim();
	}
	
	
	public String getDev_info() {
		return dev_info;
	}

	public void setDev_info(String dev_info) {
		this.dev_info = dev_info;
	}

	
	
}