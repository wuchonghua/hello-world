package com.test;

import org.junit.jupiter.api.Test;

public class TestProxy {

	@Test
	public void dynamicProxy() {
		((IChifan)ProxyFactory.createProxy(new ChifanImpl())).chifan();
	}
	
	@Test
	public void staticProxy() {
		new ChifanProxy(new ChifanImpl()).chifan();
	}
}
