package com.mobilepearls.sokoban.io;

public class RunlengthEncoding {

	public static String decode(String s) {
		int repeat = 0;
		StringBuilder result = null;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c)) {
				repeat = repeat * 10 + c - '0';
				if (result == null) {
					result = new StringBuilder(s.substring(0, i));
				}
			} else if (result != null) {
				do {
					result.append(c);
					repeat--;
				}
				while (repeat > 0);
				repeat = 0;
			}
		}
		return (result != null ? result.toString() : s);
	}

	public static String encode(String s) {
		char lastChar = '\0';
		int count = 1;
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == lastChar) {
				count++;
			} else {
				if (count > 1) {
					buffer.append(count);
				}
				buffer.append(lastChar);
				count = 1;
			}
			lastChar = c;
		}
		if (count > 1) {
			buffer.append(count);
		}
		buffer.append(lastChar);
		return buffer.toString();
	}
}
