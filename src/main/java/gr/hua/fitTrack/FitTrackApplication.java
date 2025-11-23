package gr.hua.fitTrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class }) // Delete later
public class FitTrackApplication {

	public static void main(String[] args) {

        SpringApplication.run(FitTrackApplication.class, args);
	}

}
