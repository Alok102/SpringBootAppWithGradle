package com.alok.app;

import java.util.stream.IntStream;

public class Test {
	
	public int sum(int a, int b) {
		return (a+b);
	}

	public void sub(int a , int b) {
		System.out.println("Subs" + (a - b));
	}

	public void printRandomNumber() {
		IntStream stream = IntStream.generate(() -> {
			return (int) (Math.random() * 10000);
		});

		stream.limit(10).forEach(System.out::println);
	}

	public void div(int a , int b) {
		System.out.println("Div" + (a / b));
	}
}
