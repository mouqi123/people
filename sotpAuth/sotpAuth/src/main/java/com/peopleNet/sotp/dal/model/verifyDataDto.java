package com.peopleNet.sotp.dal.model;

import java.io.Serializable;

/**
 * 验证的数据项信息
 * 
 * @author tianchk
 * 
 */
public class verifyDataDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer pid;// 父级id

	private String code;// 数据项编码

	private String name;// 数据项名称

	private Integer nodelevel;// 层级

	private String verifyDesc;

	private String matchingRate;

	public String getMatchingRate() {
		return matchingRate;
	}

	public void setMatchingRate(String matchingRate) {
		this.matchingRate = matchingRate;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNodelevel() {
		return nodelevel;
	}

	public void setNodelevel(Integer nodelevel) {
		this.nodelevel = nodelevel;
	}

	public String getVerifyDesc() {
		return verifyDesc;
	}

	public void setVerifyDesc(String verifyDesc) {
		this.verifyDesc = verifyDesc;
	}
}
