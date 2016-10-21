package com.peopleNet.sotp.dal.model;

public class androidStatisticDto {

	private Integer id;

	private Integer totalNum;

	private Integer usedNum;

	private Integer invalidNum;

	public androidStatisticDto() {
		this.totalNum = 0;
		this.usedNum = 0;
		this.invalidNum = 0;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}