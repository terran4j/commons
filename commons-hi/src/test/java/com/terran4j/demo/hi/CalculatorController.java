package com.terran4j.demo.hi;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/calculator")
@RestController
public class CalculatorController {

	@RequestMapping("/plus")
	@ResponseBody
	public Calculator plus(@RequestParam("a") long a, @RequestParam("b") long b) {
		return new Calculator(a, b).plus();
	}
	
}
