package tz.co.halotel.halokazi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HalokaziBackendApplication {

	public static void main(String[] args) {
		loadDotenv();
		SpringApplication.run(HalokaziBackendApplication.class, args);
	}

	private static void loadDotenv() {
		List<Path> candidates = List.of(
			Path.of(".env"),
			Path.of("back_resources/backend/.env"),
			Path.of("../.env"),
			Path.of("../../.env")
		);
		for (Path path : candidates) {
			if (Files.isRegularFile(path)) {
				try {
					List<String> lines = Files.readAllLines(path);
					for (String raw : lines) {
						String line = raw.trim();
						if (line.isEmpty() || line.startsWith("#")) {
							continue;
						}
						int eq = line.indexOf('=');
						if (eq > 0) {
							String key = line.substring(0, eq).trim();
							String val = line.substring(eq + 1).trim();
							if ((val.startsWith("\"") && val.endsWith("\"")) ||
							    (val.startsWith("'") && val.endsWith("'"))) {
								if (val.length() >= 2) {
									val = val.substring(1, val.length() - 1);
								}
							}
							if (System.getProperty(key) == null && System.getenv(key) == null) {
								System.setProperty(key, val);
							}
						}
					}
					break;
				} catch (IOException ignored) {
				}
			}
		}
	}

}
