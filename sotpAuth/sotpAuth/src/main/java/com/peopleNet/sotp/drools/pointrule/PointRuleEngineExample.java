package com.peopleNet.sotp.drools.pointrule;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/*
现在我们模拟一个应用场景：网站伴随业务产生而进行的积分发放操作。比如支付宝信用卡还款奖励积分等。
发放积分可能伴随不同的运营策略和季节性调整，发放数目和规则完全不同，如果使用硬编码的方式去伴随业务调整而修改，代码的修改、管理、优化、测试、上线将是一件非常麻烦的事情，所以，将发放规则部分提取出来，交给Drools管理，可以极大程度的解决这个问题。

（注意一点的是，并非所有的规则相关内容都建议使用Drools，这其中要考虑系统会运行多久，规则变更频率等一系列条件，如果你的系统只会在线上运行一周，那根本没必要选择Drools来加重你的开发成本，java硬编码的方式则将是首选）

我们定义一下发放规则：
积分的发放参考因素有：交易笔数、交易金额数目、信用卡还款次数、生日特别优惠等。
定义规则：
// 过生日，则加10分，并且将当月交易比数翻倍后再计算积分
// 2011-01-08 - 2011-08-08每月信用卡还款3次以上，每满3笔赠送30分
// 当月购物总金额100以上，每100元赠送10分
// 当月购物次数5次以上，每五次赠送50分
// 特别的，如果全部满足了要求，则额外奖励100分
// 发生退货，扣减10分
// 退货金额大于100，扣减100分
*/
/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class PointRuleEngineExample {

	public static final void main(final String[] args) {
		// KieServices is the factory for all KIE services
		KieServices ks = KieServices.Factory.get();

		// From the kie services, a container is created from the classpath
		KieContainer kc = ks.getKieClasspathContainer();

		execute(kc);
	}

	public static void execute(KieContainer kc) {
		// From the container, a session is created based on
		// its definition and configuration in the META-INF/kmodule.xml file
		KieSession ksession = kc.newKieSession("PointRuleKS");

		// Once the session is created, the application can interact with it
		// In this case it is setting a global as defined in the
		// org/drools/examples/helloworld/HelloWorld.drl file

		System.setProperty("yyyy-MM-dd HH:mm:ss", "drools.dateformat");

		// The application can also setup listeners
		// ksession.addEventListener( new DebugAgendaEventListener() );
		// ksession.addEventListener( new DebugRuleRuntimeEventListener() );

		// To setup a file based audit logger, uncomment the next line
		// KieRuntimeLogger logger = ks.getLoggers().newFileLogger( ksession,
		// "./helloworld" );

		// To setup a ThreadedFileLogger, so that the audit view reflects events
		// whilst debugging,
		// uncomment the next line
		// KieRuntimeLogger logger = ks.getLoggers().newThreadedFileLogger(
		// ksession, "./helloworld", 1000 );

		// The application can insert facts into the session
		final PointDomain pointDomain = new PointDomain();
		pointDomain.setUserName("hello kity");
		pointDomain.setBackMondy(100d);
		pointDomain.setBuyMoney(500d);
		pointDomain.setBackNums(1);
		pointDomain.setBuyNums(5);
		pointDomain.setBillThisMonth(5);
		pointDomain.setBirthDay(true);
		pointDomain.setPoint(0l);

		ksession.insert(pointDomain);

		// and fire the rules
		ksession.fireAllRules();

		System.out.println("执行完毕BillThisMonth：" + pointDomain.getBillThisMonth());
		System.out.println("执行完毕BuyMoney：" + pointDomain.getBuyMoney());
		System.out.println("执行完毕BuyNums：" + pointDomain.getBuyNums());

		System.out.println("执行完毕规则引擎决定发送积分：" + pointDomain.getPoint());
		// Remove comment if using logging
		// logger.close();

		// and then dispose the session
		ksession.dispose();
	}
}
