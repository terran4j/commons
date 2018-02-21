package com.terran4j.commons.http;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/calculator")
@RestController
public class CalculatorController {

	@RequestMapping("/plus")
	@ResponseBody
	public Calculator plus(@RequestParam("a") long a, @RequestParam("b") long b) {
		return new Calculator(a, b).plus();
	}
	
}
