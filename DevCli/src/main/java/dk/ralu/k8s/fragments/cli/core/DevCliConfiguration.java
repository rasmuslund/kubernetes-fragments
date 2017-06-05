package dk.ralu.k8s.fragments.cli.core;

import dk.ralu.k8s.fragments.cli.tool.ExternalCommand;
import java.util.function.Supplier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevCliConfiguration {

    @Bean
    public Supplier<ExternalCommand> externalCommandSupplier(ApplicationContext context) {
        return () -> context.getBean(ExternalCommand.class);
    }
}
