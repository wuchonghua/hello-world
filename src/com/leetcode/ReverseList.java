package com.leetcode;

import java.util.Stack;

public class ReverseList {
	 
	class Solution {
		public ListNode reverseList(ListNode head) {
			Stack<ListNode> s = new Stack<>();
			while (head != null) {
				ListNode nextItem = head.next;
				head.next = null;
				s.push(head);
				head = nextItem;
			}
			ListNode l = new ListNode(-1);
			ListNode p = l;
			while (!s.empty()) {
				l.next = s.pop();
				l = l.next;
			}
			return p.next;
		}
	}
}
