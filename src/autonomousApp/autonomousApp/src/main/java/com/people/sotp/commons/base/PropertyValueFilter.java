package com.people.sotp.commons.base;

import com.alibaba.fastjson.serializer.ValueFilter;

public class PropertyValueFilter implements ValueFilter{
	
	@Override
	public Object process(Object obj, String s, Object v) {
		if(v == null) 
			return "";
		return v;
	}

}
