package com.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class BraceMatch {
	
	static {
		System.out.println("我被初始化拉");
	}
	
	public static void main(String[] args) {
		/*
		 * BraceMatch b = new BraceMatch(); System.out.println(b.isValid("([)]"));
		 */
		Map<Character,char[]> m = new HashMap<>();
		String s = "<text id=\"text3\" key_id=\"3970440000375\" x=\"232.803\" y=\"351.684\">Uc:[3970440000375]kV</text>";
		String regex = "<text id=\"[a-z]+[0-9]+\" key_id=\"[0-9]+\" x=\"[0-9]+.[0-9]+\" y=\"[0-9]+.[0-9]+\">[a-zA-Z]+\\S*:\\S*[0-9]+\\S*[a-zA-Z]*</text>";
		System.out.println(s.matches(regex));
	}
	public boolean isValid(String s) {
        Map<Character, Character> m = new HashMap<>();
        m.put('(', ')');
        m.put('{', '}');
        m.put('[', ']');
        Stack<Character> stack = new Stack<>();
        stack.pop();
        for(int i=0; i<s.length(); i++){
            char c = s.charAt(i);
            if(!stack.empty() && m.get(stack.peek()) != null &&  m.get(stack.peek()) == c){
                stack.pop();
            }else{
                stack.push(c);
            }
        }
        return stack.empty();
    }
	
}
