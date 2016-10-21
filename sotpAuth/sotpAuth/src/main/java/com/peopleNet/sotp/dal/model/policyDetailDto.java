package com.peopleNet.sotp.dal.model;

import java.io.Serializable;

/**
 * 规则策略详情
 * 
 * @author hjh
 */
public class policyDetailDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id;// 编号
	private Integer policy_id;// 策略ID
	private Integer field_id;// 策略中域项ID
	private String field_name;// 策略中域项名称
	private String field_operator;// 策略中域项关联符号
	private String field_value;// 策略中域项值
	private String expression;// 策略中域项表达式

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPolicyId() {
		return policy_id;
	}

	public void setPolicyId(Integer policyid) {
		this.policy_id = policyid;
	}

	public Integer getFieldId() {
		return field_id;
	}

	public void setFieldId(Integer fieldid) {
		this.field_id = fieldid;
	}

	public String getFieldName() {
		return field_name;
	}

	public void setFieldName(String fieldname) {
		this.field_name = fieldname;
	}

	public String getFieldOperator() {
		return field_operator;
	}

	public void setFieldOperator(String fieldoperator) {
		this.field_operator = fieldoperator;
	}

	public String getFieldValue() {
		return field_value;
	}

	public void setFieldValue(String fieldvalue) {
		this.field_value = fieldvalue;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String Expression) {
		this.expression = Expression;
	}

}
