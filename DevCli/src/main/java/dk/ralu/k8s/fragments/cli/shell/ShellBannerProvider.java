package dk.ralu.k8s.fragments.cli.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.BannerProvider;
import org.springframework.shell.support.util.FileUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShellBannerProvider implements BannerProvider {

    @Override
    public String getProviderName() {
        return "devcli";
    }

    @Override
    public String getBanner() {
        return FileUtils.readBanner(ShellBannerProvider.class, "/DevCliAsciiBanner.txt") + "\n" + getVersion() + "\n";
    }

    @Override
    public String getVersion() {
        return "1.0-SNAPSHOT";
    }

    @Override
    public String getWelcomeMessage() {
        return "DevCli - the Ivan Development and Operations Command Line Interface. "
                + "For assistance hit TAB or type help.";
    }
}
