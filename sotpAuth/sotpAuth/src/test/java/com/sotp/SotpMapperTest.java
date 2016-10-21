package com.sotp;

import java.sql.SQLException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;

import com.peopleNet.sotp.dal.dao.*;
import com.peopleNet.sotp.dal.model.userInfoDto;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.IEncryptPluginInfoService;
import com.peopleNet.sotp.service.impl.CacheServiceImpl;

@ContextConfiguration(locations = { "classpath:sotp-auth-test-servlet.xml" })
public class SotpMapperTest extends AbstractJUnit4SpringContextTests {

	private static LogUtil logger = LogUtil.getLogger(CacheServiceImpl.class);
	@Autowired
	private androidStatisticDtoMapper androidStatisticMapper;
	@Autowired
	private iosStatisticDtoMapper iosStatisticMapper;
	@Autowired
	private IEncryptPluginInfoService pluginInfoMapper;
	@Autowired
	private pluginUpdatePolicyDtoMapper pluginUpdatePolicyMapper;
	@Autowired
	private sotpVerifyPolicyDtoMapper sotpVerifyPolicyMapper;
	@Autowired
	private userInfoDtoMapper userInfoMapper;
	@Autowired
	private winphoneStatisticDtoMapper winphoneStatisticMapper;

	@Test
	@Transactional
	public void getStudentTest() {
		userInfoDto entity = null;
		try {
			entity = userInfoMapper.selectByPrimaryKey(1);
		} catch (SQLException e) {
			logger.error("selectByPrimaryKey sql error. msg:%s", e.toString());
		}
		System.out.println("" + entity.getUserName() + entity.getUserId());

		System.out.println("------------------------");
	}
}