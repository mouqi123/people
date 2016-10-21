package com.people.sotp.commons.base;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 公共Action抽象类
 */
public class BaseController {

	private long pagesize;
	private long rows = 10;
	private long page = 1;
	private long total = 0;

	protected String errorMessage = new String();

	private String id;// 主键
	private String ids;// 主键集合，逗号分割

	protected String resultjson = new String();

	/**
	 * 获取web工程跟路径
	 * 
	 * @return
	 */
	public String getWebRootPath() {
		String rootPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (rootPath.indexOf("WEB-INF") > 0) {
			rootPath = rootPath.substring(0, rootPath.indexOf("/WEB-INF/classes"));//
		}
		if (rootPath.indexOf("%20") > 0) {
			rootPath = rootPath.replace("%20", " ");
		}
//		if (rootPath.startsWith("/")) {
//			rootPath = rootPath.substring(1);
//		}
		return rootPath;
	}

	/**
	 * 获取服务器IP
	 * 
	 * @return
	 */
	public String getServerIP() {
		String ip = "127.0.0.1";
		try {
			InetAddress addr;
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * 将对象转换成JSON字符串，并响应回前台
	 * 
	 * @param object
	 * @param includesProperties
	 *            需要转换的属性
	 * @param excludesProperties
	 *            不需要转换的属性
	 */
	public String writeJsonByFilter(Object object, String[] includesProperties, String[] excludesProperties) {
		/* try { */
		FastjsonFilter filter = new FastjsonFilter();// excludes优先于includes
		PropertyValueFilter valueFilter = new PropertyValueFilter();//用于过滤null属性，当属性为Null时返回 “”，
		
		if (excludesProperties != null && excludesProperties.length > 0) {
			filter.getExcludes().addAll(Arrays.<String> asList(excludesProperties));
		}
		if (includesProperties != null && includesProperties.length > 0) {
			filter.getIncludes().addAll(Arrays.<String> asList(includesProperties));
		}
		// TODO logger.info("对象转JSON：要排除的属性[" + excludesProperties + "]要包含的属性["
		// + includesProperties + "]");
		String json;
		String User_Agent = getRequest().getHeader("User-Agent");
		
		SerializeFilter[] filters ={filter, valueFilter};
		if (StringUtils.indexOfIgnoreCase(User_Agent, "MSIE 6") > -1) {
			// 使用SerializerFeature.BrowserCompatible特性会把所有的中文都会序列化为\\uXXXX这种格式，字节数会多一些，但是能兼容IE6
			json = JSON.toJSONString(object, filters, SerializerFeature.WriteDateUseDateFormat,
					SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.BrowserCompatible, SerializerFeature.WriteNullStringAsEmpty);
		} else {
			// 使用SerializerFeature.WriteDateUseDateFormat特性来序列化日期格式的类型为yyyy-MM-dd
			// hh24:mi:ss
			// 使用SerializerFeature.DisableCircularReferenceDetect特性关闭引用检测和生成
			json = JSON.toJSONString(object, filters, SerializerFeature.WriteDateUseDateFormat,
					SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteNullStringAsEmpty);
		}
		return json;
		// TODO logger.info("转换后的JSON字符串：" + json);
		/*
		 * getResponse().setContentType("text/html;charset=utf-8");
		 * getResponse().getWriter().write(json);
		 * getResponse().getWriter().flush(); getResponse().getWriter().close();
		 */
		/*
		 * } catch (IOException e) { e.printStackTrace(); }
		 */
	}

	/**
	 * 将对象转换成JSON字符串，并响应回前台
	 * 
	 * @param object
	 * @throws IOException
	 */
	public String writeJson(Object object) {
		return writeJsonByFilter(object, null, null);
	}

	/**
	 * 获得request
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

	/**
	 * 获得response
	 * 
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return ((ServletWebRequest) RequestContextHolder.getRequestAttributes()).getResponse();
	}

	public long getRows() {
		return rows;
	}

	public void setRows(long rows) {
		this.rows = rows;
	}

	public long getPage() {
		return page;
	}

	public void setPage(long page) {
		this.page = page;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public long getPagesize() {
		return pagesize;
	}

	public void setPagesize(long pagesize) {
		this.pagesize = pagesize;
	}

	public String getResultjson() {
		return resultjson;
	}

	public void setResultjson(String resultjson) {
		this.resultjson = resultjson;
	}

}
