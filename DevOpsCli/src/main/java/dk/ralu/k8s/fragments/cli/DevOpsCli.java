package dk.ralu.k8s.fragments.cli;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DevOpsCli {

    public static void main(String[] args) throws Exception {
        new SpringApplicationBuilder().sources(DevOpsCli.class).bannerMode(Banner.Mode.OFF).run(args);
    }
}
