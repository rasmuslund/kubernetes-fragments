package dk.ralu.k8s.fragments.cli.commmand;

import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

@Component
public class DeployCommands extends BaseCommand {

    private static final String DEPLOY_SINGLE_APP = "deploy app";
    private static final String DEPLOY_MULTIPLE_APPS = "deploy apps";

    @CliAvailabilityIndicator({DEPLOY_SINGLE_APP, DEPLOY_MULTIPLE_APPS})
    public boolean isCommandAvailable() {
        return true;
    }

    @CliCommand(value = DEPLOY_MULTIPLE_APPS, help = "(Re-)deploys a set of applications (all are pre-built versions fetched from the repository)")
    public void deployMultipleApps() {

    }

    @CliCommand(value = DEPLOY_SINGLE_APP, help = "(Re-)deploys a single application (either build locally or a pre-built version fetched from the repository)")
    public void deploySingleApp() {

    }
}
