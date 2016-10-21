package com.people.sotp.commons.base;

import java.util.Arrays;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.people.sotp.dataobject.MemberDO;

public class BaseControllerTest {
	public static void main(String[] args){
		MemberDO member = new MemberDO();
		member.setPhoneNum("15115771640");
		member.setId(10);
		member.setPassword("123456");
		String[] includesProperties ={"password","phoneNum","userName","picPath"};
		
		FastjsonFilter filter = new FastjsonFilter();
		filter.getIncludes().addAll(Arrays.<String> asList(includesProperties));
		
		ValueFilter valueFilter = new ValueFilter() {	
			@Override
			public Object process(Object obj, String s, Object v) {
				if(v == null) 
					return "";
				return v;
			}
		};
		SerializeFilter[] filters = {filter, valueFilter};
		System.out.println(JSON.toJSONString(member, filters, SerializerFeature.WriteNullStringAsEmpty, SerializerFeature.WriteNonStringKeyAsString));
	}
}
