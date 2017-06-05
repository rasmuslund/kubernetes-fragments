package dk.ralu.k8s.fragments.cli.shell;

import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ImportResource;
import org.springframework.shell.CommandLine;
import org.springframework.shell.commands.DateCommands;
import org.springframework.shell.commands.SystemPropertyCommands;
import org.springframework.shell.commands.VersionCommands;
import org.springframework.shell.core.JLineShellComponent;

@Configuration
@ImportResource("classpath*:/META-INF/spring/spring-shell-plugin.xml")
@ComponentScan(
        value = {
                "org.springframework.shell.converters",
                "org.springframework.shell.plugin.support",
                "org.springframework.shell.commands"
        },
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        value = {
                                DateCommands.class,
                                SystemPropertyCommands.class,
                                VersionCommands.class
                        }
                )
        }
)

public class ShellConfiguration {

    @Bean
    public ShellCommandLineParser shellCommandLineParser() {
        return new ShellCommandLineParser();
    }

    @Bean
    public CommandLine commandLine(ApplicationArguments applicationArguments, ShellProperties shellProperties) throws Exception {
        return shellCommandLineParser().parse(shellProperties, applicationArguments.getSourceArgs());
    }

    @Bean
    public JLineShellComponent shell() {
        return new JLineShellComponent();
    }

    @Bean
    public ShellCommandLineRunner commandLineRunner() {
        return new ShellCommandLineRunner();
    }

}
