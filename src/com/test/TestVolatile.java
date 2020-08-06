package com.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class TestVolatile {
	
	static {
		System.out.println("1231231");
	}
	private volatile int result;

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	public static void main(String[] args) {
		TestVolatile threadSafeCache = new TestVolatile();
		for (int i = 0; i < 8; i++) {
			new Thread(() -> {
				int x = 0;
				while (threadSafeCache.getResult() < 100) {
					// Thread.yield();
					x++;
				}
				System.out.println(x);
			}).start();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		threadSafeCache.setResult(200);
	}

//	 final V putVal(K key, V value, boolean onlyIfAbsent) {
//		 // 不允许null key 和 null value
//	        if (key == null || value == null) throw new NullPointerException();
//	        // 计算hash 类似HashMap的hash()方法
//	        int hash = spread(key.hashCode());
//	        
//	        int binCount = 0;
//	        for (Node<K,V>[] tab = table;;) {
//	        	// f 节点  该key对应的节点
//	        	// n tab大小
//	        	// i 数组下标
//	        	// fh
//	            Node<K,V> f; int n, i, fh;
//	            if (tab == null || (n = tab.length) == 0)
//	            	// 第一次调用put 进行Node
//	                tab = initTable();
//	            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
//	                if (casTabAt(tab, i, null,
//	                             new Node<K,V>(hash, key, value, null)))
//	                    break;                   // no lock when adding to empty bin
//	            }
//	            else if ((fh = f.hash) == MOVED)
//	                tab = helpTransfer(tab, f);
//	            else {
//	                V oldVal = null;
//	                synchronized (f) {
//	                    if (tabAt(tab, i) == f) {
//	                        if (fh >= 0) {
//	                            binCount = 1;
//	                            for (Node<K,V> e = f;; ++binCount) {
//	                                K ek;
//	                                if (e.hash == hash &&
//	                                    ((ek = e.key) == key ||
//	                                     (ek != null && key.equals(ek)))) {
//	                                    oldVal = e.val;
//	                                    if (!onlyIfAbsent)
//	                                        e.val = value;
//	                                    break;
//	                                }
//	                                Node<K,V> pred = e;
//	                                if ((e = e.next) == null) {
//	                                    pred.next = new Node<K,V>(hash, key,
//	                                                              value, null);
//	                                    break;
//	                                }
//	                            }
//	                        }
//	                        else if (f instanceof TreeBin) {
//	                            Node<K,V> p;
//	                            binCount = 2;
//	                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
//	                                                           value)) != null) {
//	                                oldVal = p.val;
//	                                if (!onlyIfAbsent)
//	                                    p.val = value;
//	                            }
//	                        }
//	                    }
//	                }
//	                if (binCount != 0) {
//	                    if (binCount >= TREEIFY_THRESHOLD)
//	                        treeifyBin(tab, i);
//	                    if (oldVal != null)
//	                        return oldVal;
//	                    break;
//	                }
//	            }
//	        }
//	        addCount(1L, binCount);
//	        return null;
//	    }

//	public int lengthOfLongestSubstring(String s) {
//		StringBuilder sb = new StringBuilder();
//        for(int i=0;i<s.length();i++){
//            
//            for(int j=i+1;j<s.length();j++) {
//            	char c = s.charAt(i);
//                sb.append(c);
//            }
//        }
//    }

}
