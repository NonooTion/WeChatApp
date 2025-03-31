package com.backend;

import com.backend.entity.User;
import com.backend.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.List;


@SpringBootTest
class BackendApplicationTests {
	@Value("${file.save.path}")
	private String fileSavePath;

	@Test
	void test1() {
		File dir = new File(fileSavePath);
		System.out.println(dir.exists());
		if (!dir.exists()) {
			dir.mkdirs();
		}

	}
}