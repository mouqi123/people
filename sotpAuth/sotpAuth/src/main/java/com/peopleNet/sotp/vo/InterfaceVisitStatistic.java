package com.peopleNet.sotp.vo;

import java.io.Serializable;

public class InterfaceVisitStatistic implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long visitNum;
	private Long successNum;
	private Long failNum;
	private Long visitTime;

	public InterfaceVisitStatistic() {
		successNum = (long) 0;
		failNum = (long) 0;
		visitNum = (long) 0;
		visitTime = (long) 0;
	}

	public Long getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(Long successNum) {
		this.successNum = successNum;
	}

	public Long getFailNum() {
		return failNum;
	}

	public void setFailNum(Long failNum) {
		this.failNum = failNum;
	}

	public Long getVisitNum() {
		return visitNum;
	}

	public void setVisitNum(Long visitNum) {
		this.visitNum = visitNum;
	}

	public Long getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(Long visitTime) {
		this.visitTime = visitTime;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{totalNum:" + visitNum + ",");
		sb.append("successNum:" + successNum + ",");
		sb.append("failNum:" + failNum + ",");
		sb.append("totalTime:" + visitTime + "}");
		return sb.toString();
	}

	public InterfaceVisitStatistic add(InterfaceVisitStatistic another) {
		// InterfaceVisitStatistic sum = new InterfaceVisitStatistic();
		this.setVisitNum(this.visitNum + another.getVisitNum());
		this.setSuccessNum(this.successNum + another.getSuccessNum());
		this.setFailNum(this.failNum + another.getFailNum());
		this.setVisitTime(this.visitTime + another.getVisitTime());
		return this;
	}
}
