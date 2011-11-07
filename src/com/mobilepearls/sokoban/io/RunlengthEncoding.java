package com.mobilepearls.sokoban.io;

public class RunlengthEncoding {

	public static CharSequence decode(CharSequence s) {
		return decode(s, 0, -1);
	}

	public static CharSequence decode(CharSequence s, int start, int end) {
		if (start < 0) {
			start = s.length() + start;
		}
		if (end < 0) {
			end = s.length() + end + 1;
		}
		int repeat = 0;
		StringBuilder result = null;
		for (int i = 0; i < end; i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c)) {
				repeat = repeat * 10 + c - '0';
				if (result == null) {
					result = new StringBuilder(s.subSequence(0, i));
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
		return (result != null ? result : s);
	}

	public static CharSequence encode(CharSequence s) {
		return encode(s, 0, -1);
	}

	public static CharSequence encode(CharSequence s, int start, int end) {
		if (start < 0) {
			start = s.length() + start;
		}
		if (end < 0) {
			end = s.length() + end + 1;
		}
		char lastChar = '\0';
		int count = 1;
		StringBuilder buffer = new StringBuilder();
		for (int i = start; i < end; i++) {
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
		return buffer;
	}
}
