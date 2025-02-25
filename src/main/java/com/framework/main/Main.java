package com.framework.main;

import java.io.File;

import org.testng.Reporter;

import com.framework.core.FrameworkOperations;

public class Main {

	public static void main(String[] args) {
		System.out.println("Started Execution");
		FrameworkOperations frameworkOperations = new FrameworkOperations();
		if (args.length == 1) {
			frameworkOperations.TriggerTestNGWithOptParam(args[0]);
		} else {
			frameworkOperations.TriggerTestNG();
		}
	}

}
