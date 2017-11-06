package com.terran4j.commons.jfinger.impl;

import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class CommandLineTask extends Thread {

	private static final Logger log = LoggerFactory.getLogger(CommandLineTask.class);

	private final CommandLineService service;

	public CommandLineTask(CommandLineService service) {
		super();
		this.service = service;
	}

	public void run() {
		if (log.isInfoEnabled()) {
			log.info("start Command Line Task.");
		}
		Scanner sc = new Scanner(System.in);
		PrintStream out = System.out;
		try {
			boolean running = true;
			
			// 等待程序彻底启动了，再显示提示符。
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// ignore.
			}
			out.println();
			out.println("JFinger Command Line Service is starting...");
			service.printPrompt(out);

			while (running) {
				String commandLine = null;
				try {
					commandLine = sc.nextLine();
				} catch (NoSuchElementException e) {
				}
				if (StringUtils.hasText(commandLine)) {
					running = service.execute(commandLine, out);
				}
				service.printPrompt(out);
			}
		} finally {
			if (sc != null) {
				sc.close();
			}
			out.println("Command Line Console Closed.");
			if (log.isInfoEnabled()) {
				log.info("stoped Command Line Task.");
			}
		}

	}
}
