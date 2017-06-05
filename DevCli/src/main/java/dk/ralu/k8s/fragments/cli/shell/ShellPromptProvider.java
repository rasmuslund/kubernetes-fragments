package dk.ralu.k8s.fragments.cli.shell;

import dk.ralu.k8s.fragments.cli.core.CurrentContext;
import dk.ralu.k8s.fragments.cli.output.Out;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ShellPromptProvider implements PromptProvider {

    @Autowired
    private CurrentContext currentContext;

    @Override
    public String getProviderName() {
        return "DevCli";
    }

    @Override
    public String getPrompt() {
        return new Out()
                .styleBold().colorYellow().append(getProviderName()).append(" ")
                .colorBlue().append(currentContext.getCurrentContextPath()).append(" > ")
                .toString();
    }
}
