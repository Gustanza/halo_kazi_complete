package tz.co.halotel.halokazi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HalokaziBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(HalokaziBackendApplication.class, args);
	}

}
