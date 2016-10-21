package com.peopleNet.sotp.drools;

import com.peopleNet.sotp.vo.*;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Created by wangxin on 16/7/18.
 */
public class RuleEngine {
	static KieServices ks;
	static KieContainer kc;

	static {
		ks = KieServices.Factory.get();
		// From the kie services, a container is created from the classpath
		kc = ks.getKieClasspathContainer();
	}

	public static void execute(UserRequestMsgFido msgFido) {
		KieSession ksession = kc.newKieSession("SOTPKS");
		ksession.insert(msgFido);
		ksession.fireAllRules();
		ksession.dispose();
	}

	public static void execute(UserAuthMsg authMsg, RiskControlResult riskResult, UserRuleInfo ruleInfo, UserTrustLibInfo trustLibInfo) {
		KieSession ksession = kc.newKieSession("SOTPKS");
		ksession.insert(authMsg);
        ksession.insert(riskResult);
		ksession.insert(ruleInfo);
        ksession.insert(trustLibInfo);
		ksession.fireAllRules();
		ksession.dispose();
	}

	public static void execute(UserRequestMsgV2 msg) {
		KieSession ksession = kc.newKieSession("SOTPV2_KS");
		ksession.insert(msg);
		ksession.fireAllRules();
		ksession.dispose();
	}

	public static void execute(UserRequestMsgV2 msg, UserRuleInfo ruleInfo) {
		KieSession ksession = kc.newKieSession("SOTPV2_KS");
		ksession.insert(msg);
		ksession.insert(ruleInfo);
		ksession.fireAllRules();
		ksession.dispose();
	}
}
