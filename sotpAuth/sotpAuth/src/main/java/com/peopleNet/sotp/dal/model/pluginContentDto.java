package com.peopleNet.sotp.dal.model;

public class pluginContentDto {
	private Integer id;

	private String pluginId;

	private Integer status;

	private byte[] pluginContent;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPluginId() {
		return pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId == null ? null : pluginId.trim();
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public byte[] getPluginContent() {
		return pluginContent;
	}

	public void setPluginContent(byte[] pluginContent) {
		this.pluginContent = pluginContent;
	}
}