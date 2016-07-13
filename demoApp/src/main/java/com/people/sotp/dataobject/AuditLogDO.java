package com.people.sotp.dataobject;

import com.people.sotp.commons.base.BaseDO;

/**
 * 审计日志对象
 * 
 * @author tianchk
 */
public class AuditLogDO extends BaseDO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private long masterid;// 用户编号
	private String mastername;
	private String username;// 操作人姓名
	private String srcip;// 源ip
	private String serverip;// 目的ip
	private Integer serverport;// 目的端口
	private String operation;// 操作名称
	private String opttime;// 操作时间
	private String status;// 操作结果1=操作成功2=操作失败
	private String description;// 操作描述




	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public long getMasterid() {
		return masterid;
	}

	public void setMasterid(long masterid) {
		this.masterid = masterid;
	}

	public String getMastername() {
		return mastername;
	}

	public void setMastername(String mastername) {
		this.mastername = mastername;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSrcip() {
		return srcip;
	}

	public void setSrcip(String srcip) {
		this.srcip = srcip;
	}

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public Integer getServerport() {
		return serverport;
	}

	public void setServerport(Integer serverport) {
		this.serverport = serverport;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getOpttime() {
		return opttime;
	}

	public void setOpttime(String opttime) {
		this.opttime = opttime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
