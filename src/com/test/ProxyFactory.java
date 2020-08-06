package com.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {

	/**
	 * @param 要代理的类
	 * @return 代理类
	 */
	//代理
	//委托代理完成真实对象的业务逻辑   在代理实际逻辑前后加上代理所要做的业务逻辑
	//代理类需要知道：1.代理哪个类   2.要另外做的事
 	public static Object createProxy(Object o) {
 		//ClassLoader.getSystemClassLoader()
		return Proxy.newProxyInstance(o.getClass().getClassLoader(), o.getClass().getInterfaces(), new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				System.out.println("做饭");
				//用真实对象来执行业务逻辑
				Object methodReturn = null;
				methodReturn = method.invoke(o, args);
				System.out.println("洗碗");
				return methodReturn;
			}
		});
	}
 	
}