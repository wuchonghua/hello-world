package com.leetcode;

public class RemoveNthNodeFromEndOfList {

	public ListNode removeNthFromEnd(ListNode head, int n) {
		if (head == null) {
			return null;
		}
        ListNode fast = new ListNode(-1);
        ListNode slow = new ListNode(-1);
        fast.next = head;
        slow.next = head;
        ListNode result = slow;
        while (n != 0) {
        	fast = fast.next;
        	n--;
        }
        while (fast.next != null) {
        	fast = fast.next;
        	slow = slow.next;
        }
        slow.next = slow.next.next;
        return result.next;
    }
}
