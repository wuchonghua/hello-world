package com.test;

public class ChifanProxy implements IChifan {

	private IChifan realObject;
	public ChifanProxy(IChifan realObject) {
		this.realObject = realObject;
	}

	@Override
	public void chifan() {
		System.out.println("做饭");
		realObject.chifan();
		System.out.println("洗碗");
	}

}

