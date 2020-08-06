package com.leetcode;

public class Palindrome {

	
    public static boolean isPalindrome(ListNode head) {
        if (head == null || head.next == null) {
            return true;
        }
        ListNode prev = null;
        ListNode curr = head;
        ListNode fast = head;
        ListNode slow = head;
        ListNode secondHalf = null;
        boolean oushu = false;
        while (fast.next != null) {
            if (fast.next.next == null) {
                fast = fast.next;
                oushu = true;
            } else {
                slow = slow.next;
                fast = fast.next.next;
            }
        }
        if (oushu) {
        	secondHalf = slow.next;
        }
        else {
        	secondHalf = slow;
        }
        while (curr != secondHalf) {
            ListNode nextNode = curr.next;
            curr.next = prev;
            prev = curr;
            curr = nextNode;
        }
        if (!oushu) {
        	secondHalf = secondHalf.next;
        }
        while (secondHalf != null) {
            if (prev.val != secondHalf.val) {
                return false;
            }
            prev = prev.next;
            secondHalf = secondHalf.next;
        }
        return true;
    }
    
    public static void main(String[] args) {
		ListNode l1 = new ListNode(1);
		ListNode l2 = new ListNode(0);
		ListNode l3 = new ListNode(0);
		ListNode l4 = new ListNode(1);
		l1.next = l2;
		l2.next = l3;
		l3.next = l4;
		System.out.println(isPalindrome(l1));
	}
	
}
