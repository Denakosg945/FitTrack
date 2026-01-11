package gr.hua.fitTrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "gr.hua.fitTrack.core.model")
@EnableJpaRepositories(basePackages = "gr.hua.fitTrack.core.repository")
public class FitTrackApplication {

	public static void main(String[] args) {

        SpringApplication.run(FitTrackApplication.class, args);
	}

}
