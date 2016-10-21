package autonomousApp;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class JsonTest {
	public static void main(String[] args){
		Map<String,String> map = new HashMap<String, String>();
		map.put("sb1", "sb1");
		map.put("sb2", "sb2");
		map.put("sb3", "sb3");
		map.put("sb4", "sb4");
		System.out.println(map);
		System.out.println(JSON.toJSONString(map));
		
	}
}
