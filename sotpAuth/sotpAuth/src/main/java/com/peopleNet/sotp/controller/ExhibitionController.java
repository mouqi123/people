package com.peopleNet.sotp.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.peopleNet.sotp.constant.Constant;
import com.peopleNet.sotp.exception.BusinessException;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.ICacheService;
import com.peopleNet.sotp.service.IExhibitionService;
import com.peopleNet.sotp.util.StringUtils;
import com.peopleNet.sotp.vo.ExhibitionLog;

@Controller
@RequestMapping("/exhibition")
public class ExhibitionController {
	private static LogUtil logger = LogUtil.getLogger(ExhibitionController.class);
	private static String CODE = "people2000";

	@Autowired
	private IExhibitionService exhibitionService;

	@Autowired
	private ICacheService cacheService;

	/**
	 * 设置各中心的交易量
	 */
	@RequestMapping(value = { "/setTradeAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void setTradeAmount(@RequestParam(value = "bjTrade", required = false) Long bjTrade,
	        @RequestParam(value = "shTrade", required = false) Long shTrade,
	        @RequestParam(value = "gzTrade", required = false) Long gzTrade) {
		if (null != bjTrade) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.BEIJING_TRADE, bjTrade, -1);
			} catch (BusinessException e) {
				logger.error("set beijing trade error. " + e.toString());
			}
		}

		if (null != shTrade) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.SHANGHAI_TRADE, shTrade, -1);
			} catch (BusinessException e) {

				logger.error("set shanghai trade error. " + e.toString());
			}
		}

		if (null != gzTrade) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.GUANGZHOU_TRADE, gzTrade, -1);
			} catch (BusinessException e) {

				logger.error("set guangzhou trade error. " + e.toString());
			}
		}
	}

	/**
	 * 设置各业务的交易量
	 */
	@RequestMapping(value = { "/setBusinessAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void setBusinessAmount(@RequestParam(value = "ePay", required = false) Long ePay,
	        @RequestParam(value = "onePay", required = false) Long onePay,
	        @RequestParam(value = "quickPay", required = false) Long quickPay,
	        @RequestParam(value = "hce", required = false) Long hce,
	        @RequestParam(value = "epos", required = false) Long epos) {
		if (null != ePay) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.E_PAY, ePay, -1);
			} catch (BusinessException e) {

				logger.error("set ePay trade error. " + e.toString());
			}
		}

		if (null != onePay) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.ONE_PAY, onePay, -1);
			} catch (BusinessException e) {

				logger.error("set onePay trade error. " + e.toString());
			}
		}

		if (null != quickPay) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.QUICK_PAY, quickPay, -1);
			} catch (BusinessException e) {

				logger.error("set quickPay trade error. " + e.toString());
			}
		}

		if (null != hce) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.HCE, hce, -1);
			} catch (BusinessException e) {

				logger.error("set hce trade error. " + e.toString());
			}
		}

		if (null != epos) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.EPOS, epos, -1);
			} catch (BusinessException e) {

				logger.error("set epos trade error. " + e.toString());
			}
		}
	}

	/**
	 * 设置各中心的认证用户量
	 */
	@RequestMapping(value = { "/setCenterVerifyUserAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void setCenterVerifyUserAmount(@RequestParam(value = "bjUser", required = false) Long bjUser,
	        @RequestParam(value = "shUser", required = false) Long shUser,
	        @RequestParam(value = "gzUser", required = false) Long gzUser) {
		if (null != bjUser) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.BEIJING_USER, bjUser, -1);
			} catch (BusinessException e) {

				logger.error("set bjUser trade error. " + e.toString());
			}
		}

		if (null != shUser) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.SHANGHAI_USER, shUser, -1);
			} catch (BusinessException e) {

				logger.error("set shUser trade error. " + e.toString());
			}
		}

		if (null != gzUser) {
			try {
				this.cacheService.setStatisticNum(Constant.EXHIBITION_NAME.GUANGZHOU_USER, gzUser, -1);
			} catch (BusinessException e) {

				logger.error("set gzUser trade error. " + e.toString());
			}
		}
	}

	/**
	 * 设置各省市的认证用户量
	 */
	@RequestMapping(value = { "/setProvinceUserAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void setProvinceUserAmount(@RequestParam("province") String province,
	        @RequestParam("userAmount") Long amount) {
		String source = null;
		try {
			String srcEncode = StringUtils.getEncoding(province);
			source = new String(province.getBytes(srcEncode), "utf-8");
		} catch (UnsupportedEncodingException e) {

			logger.error("encode source city error!" + e.toString());
			source = province;
		}

		try {
			this.cacheService.setAmountMap(Constant.EXHIBITION_NAME.PROVINCE_USER_AMOUNT, source, amount);
		} catch (BusinessException e) {

			logger.error("set province user amount error. " + e.toString());
		}
	}

	/**
	 * 设置各中心的实时业务数
	 */
	@RequestMapping(value = { "/setRTCenterBusinessAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void setRTCenterBusinessAmount(@RequestParam("center") String center, @RequestParam("amount") Long amount) {
		String source = null;
		try {
			String srcEncode = StringUtils.getEncoding(center);
			source = new String(center.getBytes(srcEncode), "utf-8");
		} catch (UnsupportedEncodingException e) {

			logger.error("encode source city error!" + e.toString());
			source = center;
		}

		try {
			this.cacheService.setAmountMap(Constant.EXHIBITION_NAME.RT_CENTER_BUSINESS_AMOUNT, source, amount);
		} catch (BusinessException e) {

			logger.error("set realtime center business amount error. " + e.toString());
		}
	}

	/**
	 * 设置各省市的实时交易信息
	 */
	@RequestMapping(value = { "/setRealtimeAuthInfo" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void setRealtimeAuthInfo(@RequestParam("centerCity") String centerCity,
	        @RequestParam("sourceCity") String sourceCity, @RequestParam("realtimeAmount") Long realtimeAmount) {

		String source = null;
		try {
			String srcEncode = StringUtils.getEncoding(sourceCity);
			source = new String(sourceCity.getBytes(srcEncode), "utf-8");
		} catch (UnsupportedEncodingException e) {

			logger.error("encode source city error!" + e.toString());
			source = sourceCity;
		}

		if ("beijing".equals(centerCity)) {
			try {

				this.cacheService.setRealTimeMap(Constant.EXHIBITION_NAME.BEIJING_REALTIME, source, realtimeAmount);
			} catch (BusinessException e) {

				logger.error("set BEIJING_REALTIME trade error. " + e.toString());
			}
		}

		if ("shanghai".equals(centerCity)) {
			try {
				this.cacheService.setRealTimeMap(Constant.EXHIBITION_NAME.SHANGHAI_REALTIME, source, realtimeAmount);
			} catch (BusinessException e) {

				logger.error("set SHANGHAI_REALTIME trade error. " + e.toString());
			}
		}

		if ("guangzhou".equals(centerCity)) {
			try {
				this.cacheService.setRealTimeMap(Constant.EXHIBITION_NAME.GUANGZHOU_REALTIME, source, realtimeAmount);
			} catch (BusinessException e) {

				logger.error("set GUANGZHOU_REALTIME trade error. " + e.toString());
			}
		}
	}

	/**
	 * 设置实时交易log
	 */
	@RequestMapping(value = { "/setTradeLog" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public void setTradeLog(@ModelAttribute ExhibitionLog tradeLog, HttpServletRequest request) {

		try {
			this.cacheService.setRealTimeLog(Constant.EXHIBITION_NAME.TRADE_LOG, tradeLog);
		} catch (BusinessException e) {

			logger.error("set BEIJING_REALTIME trade error. " + e.toString());
		}
	}

	/**
	 * 获取各中心的交易量
	 */
	@RequestMapping(value = { "/getTradeAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getTradeAmount(@RequestParam("callback") String callback) {
		JSONArray list = this.exhibitionService.getCenterTradeAmount();
		return callback + "(" + list.toString() + ")";
	}

	/**
	 * 获取各业务的交易量
	 */
	@RequestMapping(value = { "/getBusinessAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getBusinessAmount(@RequestParam("callback") String callback) {
		JSONArray list = this.exhibitionService.getBusinessAmount();
		return callback + "(" + list.toString() + ")";
	}

	/**
	 * 获取各中心的认证用户量
	 */
	@RequestMapping(value = { "/getCenterVerifyUserAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getCenterVerifyUserAmount(@RequestParam("callback") String callback) {
		JSONArray list = this.exhibitionService.getCenterVerifyUserAmount();
		return callback + "(" + list.toString() + ")";
	}

	/**
	 * 获取各省市的认证用户量，并按照人数多少排序
	 */
	@RequestMapping(value = { "/getProvinceUserAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getProvinceUserAmount(@RequestParam("callback") String callback) {
		JSONArray list = this.exhibitionService.getProvinceUserAmount();
		return callback + "(" + list.toString() + ")";
	}

	/**
	 * 获取各中心的实时业务数
	 */
	@RequestMapping(value = { "/getRTCenterBusinessAmount" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getRTCenterBusinessAmount(@RequestParam("callback") String callback) {
		JSONArray list = this.exhibitionService.getRTCenterBusinessAmount();
		return callback + "(" + list.toString() + ")";
	}

	/**
	 * 获取各省市的实时交易信息
	 */
	@RequestMapping(value = { "/getRealtimeAuthInfo" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getRealtimeAuthInfo(@RequestParam("centerCity") String centerCity,
	        @RequestParam("callback") String callback) {
		JSONArray list = this.exhibitionService.getRealtimeAuthInfo(centerCity);
		return callback + "(" + list.toString() + ")";
	}

	/**
	 * 设置实时交易log
	 */
	@RequestMapping(value = { "/getTradeLog" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getTradeLog(@RequestParam("callback") String callback, HttpServletRequest request) {
		JSONArray list = this.exhibitionService.getTradeLog();
		return callback + "(" + list.toString() + ")";
	}

	/**
	 * 获取各接口的访问信息
	 */
	@RequestMapping(value = { "/getApiVisitorNum" }, method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getApiVisitorAmount(@RequestParam("callback") String callback) {
		JSONArray list = this.exhibitionService.getApiVisitorInfo();
		return callback + "(" + list.toString() + ")";
	}
}
