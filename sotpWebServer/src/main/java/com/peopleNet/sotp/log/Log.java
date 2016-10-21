package com.peopleNet.sotp.log;

import org.apache.log4j.Logger;

import com.peopleNet.sotp.context.UserThreadContext;

public class Log {
	// 将Log类封装成单实例的模式，独立于其他类。以后要用到日志的地方只要获得Log的实例就可以方便使用
	private static Log log;
	protected Logger loger;

	private Log(Logger log4jLogger) {
		loger = log4jLogger;
	}

	@SuppressWarnings("rawtypes")
	public static Log getLogger(Class classObject) {
		if (log != null)
			return log;
		else
			return new Log(org.apache.log4j.Logger.getLogger(classObject));
	}

	public static Log getLogger(String className) {
		if (log != null)
			return log;
		else
			return new Log(org.apache.log4j.Logger.getLogger(className));
	}

	public void debug(String msg) {
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.debug(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void info(String msg) {
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.info(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void warn(String msg) {
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.warn(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void error(String msg) {
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.error(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void fatal(String msg) {
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.fatal(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void debug(String format, Object... args) {
		String msg = String.format(format, args);
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.debug(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void info(String format, Object... args) {
		String msg = String.format(format, args);
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.info(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void warn(String format, Object... args) {
		String msg = String.format(format, args);
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.warn(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void error(String format, Object... args) {
		String msg = String.format(format, args);
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.error(msg + (contextInfo == null ? "" : contextInfo));
	}

	public void fatal(String format, Object... args) {
		String msg = String.format(format, args);
		String contextInfo = "";
		if (null != UserThreadContext.getContext())
			contextInfo = UserThreadContext.getContext().toString();
		loger.fatal(msg + (contextInfo == null ? "" : contextInfo));
	}
}
