package com.peopleNet.sotp.dal.model;

public class pluginStatisticDto {

	private Integer id;

	private Integer type;

	private Integer totalNum;

	private Integer usedNum;

	private Integer invalidNum;

	public pluginStatisticDto() {
		this.type = 0;
		this.totalNum = 0;
		this.usedNum = 0;
		this.invalidNum = 0;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(Integer totalNum) {
		this.totalNum = totalNum;
	}

	public Integer getUsedNum() {
		return usedNum;
	}

	public void setUsedNum(Integer usedNum) {
		this.usedNum = usedNum;
	}

	public Integer getInvalidNum() {
		return invalidNum;
	}

	public void setInvalidNum(Integer invalidNum) {
		this.invalidNum = invalidNum;
	}
}