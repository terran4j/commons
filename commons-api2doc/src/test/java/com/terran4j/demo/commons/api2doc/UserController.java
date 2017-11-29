package com.terran4j.demo.commons.api2doc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.terran4j.commons.api2doc.annotations.Api2Doc;
import com.terran4j.commons.api2doc.annotations.ApiComment;

@Api2Doc(id = "user", name = "用户相关接口", order = 0)
@RestController
@RequestMapping(value = "/user")
public class UserController {

	@Api2Doc(order = 10)
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	public User getUser(@PathVariable("id") Long id) {
		User user = new User();
		user.setId(id);
		user.setPassword("abc");
		user.setUsername("12345");
		return user;
	}

	@Api2Doc(order = 2)
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<User> getUserList() {

		List<User> list = new ArrayList<>();
		User user = new User();
		user.setId(15L);
		user.setPassword("ricky");
		user.setUsername("root");

		list.add(user);

//		return ResponseMocker.mockObject(new ArrayList<User>());
		return list;
	}

	/**
	 * http://localhost:8080/api2doc/apidoc/api2DocController/apidoc.html
	 * 
	 * @param id
	 * @param name
	 * @return
	 */
	@Api2Doc(order = 3)
	@RequestMapping(value = "/user/{id}", method = RequestMethod.POST)
	public User insert(@PathVariable("id") Long id, //
			@ApiComment(value = "用户名", sample = "张三")
			@RequestParam("name") String name) {
		User user = new User();
		user.setId(id);
		user.setUsername(name);
		return user;
	}

	@Api2Doc(order = 4)
	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable("id") Long id) {
		System.out.println("delete user:" + id);
		return "OK";
	}
}
