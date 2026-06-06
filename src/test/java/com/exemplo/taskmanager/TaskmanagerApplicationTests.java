package com.exemplo.taskmanager;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Integration test — requires full infrastructure (Kafka, PostgreSQL)")
@SpringBootTest
class TaskmanagerApplicationTests {

	@Test
	void contextLoads() {
	}

}
