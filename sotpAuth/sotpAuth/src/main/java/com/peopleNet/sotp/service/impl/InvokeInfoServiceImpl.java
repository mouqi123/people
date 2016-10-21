package com.peopleNet.sotp.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peopleNet.sotp.dal.dao.invokeInfoDtoMapper;
import com.peopleNet.sotp.dal.model.invokeInfoDto;
import com.peopleNet.sotp.log.LogUtil;
import com.peopleNet.sotp.service.IInvokeInfoService;
import com.peopleNet.sotp.util.CommonConfig;

@Service
public class InvokeInfoServiceImpl implements IInvokeInfoService {
	private static LogUtil logger = LogUtil.getLogger(InvokeInfoServiceImpl.class);
	private Vector<invokeInfoDto> vectorInfo = new Vector<invokeInfoDto>();

	private int LOG_BATCH_INSERT_NUM = Integer.parseInt(CommonConfig.get("LOG_BATCH_INSERT_NUM").trim());

	@Autowired
	private invokeInfoDtoMapper invokeInfoMapper;

	@SuppressWarnings("unchecked")
	@Override
	public int insertBatch(invokeInfoDto info) {
		int ret = 0;
		vectorInfo.add(info);
		// System.out.println("************ vector.size():"+vectorInfo.size());

		int endIndex = vectorInfo.size();
		if (0 == endIndex % LOG_BATCH_INSERT_NUM && LOG_BATCH_INSERT_NUM <= endIndex) {
			List<invokeInfoDto> vectorInsert = new Vector<invokeInfoDto>();
			synchronized (vectorInfo) {
				if (vectorInfo.size() >= LOG_BATCH_INSERT_NUM) {
					invokeInfoDto info1 = vectorInfo.remove(0);
					String tableName = info1.getTableName();
					vectorInsert.add(info1);
					for (int i = 0; i < LOG_BATCH_INSERT_NUM - 1; i++) {
						invokeInfoDto info2 = vectorInfo.remove(0);
						if (tableName.equals(info2.getTableName())) {
							vectorInsert.add(info2);
						} else {
							vectorInfo.add(info2);
						}
					}
				}
			}
			batchInsert insertThread = new batchInsert(vectorInsert, invokeInfoMapper,
			        vectorInsert.get(0).getTableName());
			Thread tp = new Thread(insertThread);
			tp.start();
		}
		return ret;
	}

	class batchInsert implements Runnable {
		private final LogUtil logger = LogUtil.getLogger(batchInsert.class);

		private List<invokeInfoDto> logData = new Vector<invokeInfoDto>();
		private invokeInfoDtoMapper invokeInfoMapper;
		private String tableName;

		public batchInsert(List<invokeInfoDto> data, invokeInfoDtoMapper invokeInfoMapper, String tableName) {
			this.logData = data;
			this.invokeInfoMapper = invokeInfoMapper;
			this.tableName = tableName;
		}

		public void run() {
			logger.debug("----- logData.size :" + logData.size());
			if (null != logData && logData.size() == 0) {
				return;
			}
			try {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				paramMap.put("tableName", tableName);
				paramMap.put("list", logData);
				// this.invokeInfoMapper.insertBatch(logData);
				this.invokeInfoMapper.insertBatch(paramMap);
				logData.clear();
			} catch (SQLException e) {
				logger.error("invokeInfoMapper insertBatch sql error" + e.toString());
			}
		}
	}

}
