package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
// @EnableJpaRepositories(basePackages = "study.datajpa.repository") -> springBoot를 쓰기 때문에 이게 없어도 되고 study.datajpa가 포함되어있는
// 상위부터 하위 패키지까지는 자동으로 끌어올 수 있음
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

}
