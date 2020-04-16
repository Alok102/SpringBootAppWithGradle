package com.alok.app;

import java.util.List;
import java.util.stream.IntStream;

public class Test {
	public void sum() {
		int a = 20;
		int b = 3;
		System.out.println("summ" + (a + b));
	}

	public void sub() {
		int a = 2;
		int b = 3;
		System.out.println("Subs" + (a - b));
	}

	public void printRandomNumber() {
		IntStream stream = IntStream.generate(() -> {
			return (int) (Math.random() * 10000);
		});

		stream.limit(10).forEach(System.out::println);

	}

	public void div() {
		int a = 21;
		int b = 3;
		// dev
		System.out.println("Div" + (a / b));
		// mul
		System.out.println("Mul" + (a * b));
	}
}
