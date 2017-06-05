package dk.ralu.k8s.fragments.cli;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DevCli {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder().sources(DevCli.class).bannerMode(Banner.Mode.OFF).run(args);
    }
}
