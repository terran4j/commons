//package com.terran4j.demo.commons.api2doc;
//
//import com.google.gson.JsonObject;
//import com.terran4j.commons.util.Jsons;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.terran4j.commons.api2doc.annotations.Api2Doc;
//import com.terran4j.commons.api2doc.annotations.ApiComment;
//import com.terran4j.commons.api2doc.annotations.ApiError;
//import com.terran4j.commons.restpack.RestPackController;
//import com.terran4j.commons.util.error.BusinessException;
//import com.terran4j.commons.util.error.ErrorCodes;
//
//import java.util.Map;
//
////@Api2Doc("demo")
////@ApiComment("设备相关接口，包括绑定/解绑设备、切换主设备、设置设备名称等接口")
////@RestPackController
////@RequestMapping(name = "设备相关接口", value = "/api/demo/v1")
//public class DemoController {
//
//	private static final Logger log = LoggerFactory.getLogger(DemoController.class);
//
//	public DemoController() {
//		super();
//	}
//
//	/**
//	 * http://localhost:8080/api2doc/doc/demo/bind.html
//	 * @param code
//	 * @throws BusinessException
//	 */
//	@Api2Doc(order = 5)
//	@ApiComment("绑定设备到当前账号上。")
//	@ApiError(value = ErrorCodes.DUPLICATE_KEY, comment = "设备编号重复")
//	@ApiError(value = ErrorCodes.INVALID_PARAM,
//			comment = "没有传入参数：code，设备编号必须指定!")
//	@RequestMapping(name = "绑定设备", value = "/bind",
//			method = RequestMethod.POST)
//	public UserPage bind(
//			@ApiComment(value = "设备唯一编号，可以是设备 IMEI 等。",
//                    sample = "IMEI-SDFHIW2839482")
//			@RequestParam("code") String code)
//			throws BusinessException {
//		return null;
//	}
//
//
//	/**
//	 * http://localhost:8080/api/demo/v1/hello?name=neo
//	 * @param name
//	 * @throws BusinessException
//	 */
//	@Api2Doc
//	@ApiComment(value = "hello, world。")
//	@RequestMapping(name = "你好", value = "/hello", method = RequestMethod.GET)
//	public Map<String, Object> hello(
//            @ApiComment(value = "名称")
//			@RequestParam(value = "name") String name)
//			throws BusinessException {
//		log.info("hello json, name = {}", name);
//		JsonObject json = new JsonObject();
//		json.addProperty("name", name);
//		return Jsons.toMap(json);
//	}
//
//}