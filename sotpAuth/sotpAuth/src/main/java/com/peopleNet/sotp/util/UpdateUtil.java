package com.peopleNet.sotp.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.security.PeopleProtect;

public class UpdateUtil {
	private Map<String, Object> UpdateInfomap = new HashMap<String, Object>();

	private List<Object> fieldList = new ArrayList<Object>();

	public void addField(String name, Object value) {
		Map<String, Object> field = new HashMap<String, Object>();
		if (Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PLUGIN_KEY.equals(name)
		        || Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.HOLD_INFO.equals(name)
		        || Constant.COL_NAME_OF_T_PLUGIN_INFO_TABLE.PROTECT_CODE.equals(name)) {
			String encryptVal = PeopleProtect.transform(String.valueOf(value));
			field.put("key", name);
			field.put("value", encryptVal);
		} else {
			field.put("key", name);
			field.put("value", value);
		}
		fieldList.add(field);
	}

	public void addAbsoluteKey(String name, Object value) {
		UpdateInfomap.put(name, value);
	}

	public Map<String, Object> getUpdateInfomap() {
		UpdateInfomap.put("fields", fieldList);
		return UpdateInfomap;
	}

	public void setUpdateInfomap(Map<String, Object> updateInfomap) {
		UpdateInfomap = updateInfomap;
	}

	public List<Object> getFieldList() {
		return fieldList;
	}

	public void setFieldList(List<Object> fieldList) {
		this.fieldList = fieldList;
	}
}
