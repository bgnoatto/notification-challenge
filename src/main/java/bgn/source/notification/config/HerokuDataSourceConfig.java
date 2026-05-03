package bgn.source.notification.config;

import java.net.URI;
import java.net.URISyntaxException;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class HerokuDataSourceConfig {

	@Bean
	@Primary
	@ConditionalOnExpression("#{systemEnvironment.containsKey('DATABASE_URL')}")
	public DataSource dataSource() throws URISyntaxException {
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		String[] userInfo = dbUri.getUserInfo().split(":");
		String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort() + dbUri.getPath()
				+ "?sslmode=require";
		return DataSourceBuilder.create().url(jdbcUrl).username(userInfo[0]).password(userInfo[1]).build();
	}

}
