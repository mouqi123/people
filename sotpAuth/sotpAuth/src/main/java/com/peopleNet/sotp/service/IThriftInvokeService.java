package com.peopleNet.sotp.service;

import org.apache.thrift.TException;

import com.peopleNet.sotp.thrift.service.SotpPlugin;
import com.peopleNet.sotp.thrift.service.SotpRet;

/**
 * 
 * @描述 thrift调用接口
 * @author wangxin
 * @created_at 2015年10月14日
 */
public interface IThriftInvokeService {

	public SotpRet encryptNew(String seed, String plain) throws TException;

	public SotpRet decryptNew(String seed, String cipher) throws TException;

	public SotpPlugin genSotpNew(int type, String phone, String hw) throws TException;

	public int verifyNew(int type, String seed, int time, int window, String pin, String challenge, String verifycode)
	        throws TException;

	public SotpPlugin genSotpV2(int type, String merchant_sn, String merchant_seed, String appSign, String hw)
	        throws org.apache.thrift.TException;

	public SotpRet sotpEncryptV2(String sn, String seed, String plain) throws org.apache.thrift.TException;

	public SotpRet sotpDecryptV2(String sn, String seed, String cipher) throws org.apache.thrift.TException;

	public int sotpVerifyV2(int type, String sn, String seed, int time, int window, String pin, String challenge,
	        String verifycode) throws org.apache.thrift.TException;

	public SotpRet shareKey(int type, String sharekey) throws TException;

	public SotpRet transEncry(int type, String seed, String data) throws TException;

}
