package com.faraz.authservice;

import com.netflix.discovery.provider.Serializer;
import io.jaegertracing.Configuration;
import io.jaegertracing.internal.metrics.NoopMetricsFactory;
import io.opentracing.util.GlobalTracer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAuthorizationServer
@EnableFeignClients
@Serializer
public class AuthServiceApplication {


	@Bean
	public io.opentracing.Tracer initTracer() {
		Configuration config = io.jaegertracing.Configuration.fromEnv("auth-service")
				.withSampler(
						io.jaegertracing.Configuration.SamplerConfiguration.fromEnv()
								.withType("const")
								.withParam(1))
				.withReporter(
						io.jaegertracing.Configuration.ReporterConfiguration.fromEnv()
								.withLogSpans(true)
								.withFlushInterval(1000)
								.withMaxQueueSize(10000)
								.withSender(
										io.jaegertracing.Configuration.SenderConfiguration.fromEnv()
												.withAgentHost("192.168.1.5")
												.withAgentPort(6831)
                                                .withEndpoint("http://192.168.1.5:14268/api/traces")
								));
		GlobalTracer.register(config.getTracer());
		return GlobalTracer.get();

/*
		Configuration.SamplerConfiguration samplerConfig = new Configuration.SamplerConfiguration().withType("const").withParam(1);
		Configuration.ReporterConfiguration reporterConfig = new Configuration.ReporterConfiguration().withLogSpans(true);
		Configuration config = new Configuration("Auth-Server").withSampler(samplerConfig).withReporter(reporterConfig).withMetricsFactory(new NoopMetricsFactory());
		GlobalTracer.register(config.getTracer());
		return GlobalTracer.get();

 */
	}


	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder){
		return restTemplateBuilder
				.setConnectTimeout(Duration.ofSeconds(3))
				.setReadTimeout(Duration.ofSeconds(3))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
