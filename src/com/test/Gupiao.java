package com.test;

public class Gupiao {
	
	 public int maxProfit(int[] prices) {
		int maxProfit = 0;
		int danqianProfit = 0;
		for (int i = 0; i < prices.length - 1; i++) {
			if (danqianProfit > 0) {
				danqianProfit += (prices[i + 1] - prices[i]);
			} else {
				danqianProfit = prices[i + 1] - prices[i];
			}
			maxProfit = maxProfit > danqianProfit ? maxProfit : danqianProfit;
		}
		return maxProfit;
	 }
	 
}
