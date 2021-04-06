package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing // SpringDataJpa에서 Auditing(등록일, 수정일, 등록자, 수정자 등 공통사항 등을 관리) + (modifyOnCreate = false)를 해주면 update 가 null 컬럼으로 들어감 (create는 값이 들어가고)
@SpringBootApplication
// @EnableJpaRepositories(basePackages = "study.datajpa.repository") -> springBoot를 쓰기 때문에 이게 없어도 되고 study.datajpa가 포함되어있는
// 상위부터 하위 패키지까지는 자동으로 끌어올 수 있음
public class DataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataJpaApplication.class, args);
    }

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.of(UUID.randomUUID().toString());
    }

/*	위에서 return에서 나온 것이 아래의 코드에서 random을 이용하여 만들어진 코드이다다 - 인터페이스에서 메소드가 하나면 random으로 바꿀 수 있다
   public AuditorAware<String> auditorProvider() {
		return new AuditorAware<String>() {
			@Override
			public Optional<String> getCurrentAuditor() {
				return Optional.of(UUID.randomUUID().toString());
			}
		};
	}*/
}
