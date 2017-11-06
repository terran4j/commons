package com.terran4j.demo.commons.api2doc;

import com.terran4j.commons.api2doc.codewriter.CodeConfig;
import com.terran4j.commons.api2doc.codewriter.FileCodeOutput;
import com.terran4j.commons.api2doc.codewriter.RetrofitCodeWriter;
import com.terran4j.commons.api2doc.domain.ApiDocObject;
import com.terran4j.commons.api2doc.domain.ApiFolderObject;
import com.terran4j.commons.api2doc.domain.ApiParamLocation;
import com.terran4j.commons.api2doc.domain.ApiParamObject;
import com.terran4j.commons.api2doc.impl.Api2DocService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.terran4j.commons.api2doc.config.EnableApi2Doc;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@EnableApi2Doc
@SpringBootApplication
public class Api2DocDemoApp {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Api2DocDemoApp.class, args);

		// 生成 Android 端代码。
		Api2DocService api2DocService = context.getBean(Api2DocService.class);
		List<ApiFolderObject> folders = api2DocService.getFolders();

		RetrofitCodeWriter writer = context.getBean(RetrofitCodeWriter.class);
		String folderPath = System.getProperty("java.io.tmpdir");
		folderPath = folderPath.replaceAll("\\\\", "/");
		if (!folderPath.endsWith("/")) {
			folderPath = folderPath + "/";
		}
		folderPath = folderPath + "demo-api2doc";
		File dir = new File(folderPath);
		if (dir.isDirectory()) {
			dir.delete();
		}
        dir.mkdir();
		System.out.println("gen doc to folder: " + folderPath);
		FileCodeOutput out = new FileCodeOutput(folderPath);

        CodeConfig config = new CodeConfig();
		config.setPkgName("demo.api2doc");
		config.setDeclaredComment("本类由 Api2Doc 自动生成，建议您不要修改它，" //
				+ "以便更新时重新生并覆盖掉之前代码即可。");

		writer.writeCode(folders, out, config);
		System.out.println("gen doc done");
	}

}