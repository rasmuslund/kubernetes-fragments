package dk.ralu.k8s.fragments.cli.tool;


import com.github.zafarkhaja.semver.Version;
import dk.ralu.k8s.fragments.cli.output.ConsoleWriter;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Tool extends ConsoleWriter {

    @Autowired
    private Supplier<ExternalCommand> externalCommandSupplier;

    private final String name;
    private final Version requiredVersion;
    private Version version;

    protected Tool(String name, String requiredVersion) {
        this.name = name;
        this.requiredVersion = toVersion(requiredVersion);
    }

    protected ExternalCommand createExternalCommand() {
        return externalCommandSupplier.get();
    }

    public String getName() {
        return name;
    }

    public abstract String getPreciseVersionAsString();

    public final void writeDetails() {
        writeLineToConsole(out -> out
                .styleBold()
                .append(getName())
        );
        writeDetailLine("version", () -> getVersion());
        writeDetailLine("preciseVersion", () -> getPreciseVersionAsString());
        writeDetailLine("requiredVersion", () -> getRequiredVersion());
        writeDetailLine("which", () -> createExternalCommand().command("which", getName()).executeExpectingSingleStdOutLine());
        writeDetailExtraLines();
    }

    public void writeDetailExtraLines() {
    }

    protected final void writeDetailLine(String key, Supplier<Object> valueRetriever) {
        String lineStart = "- " + key + ": ";
        try {
            writeLineToConsole(out -> out
                    .append(lineStart)
                    .colorBlue()
                    .append(valueRetriever.get())
            );
        } catch (RuntimeException e) {
            writeLineToConsole(out -> out
                    .append(lineStart)
                    .colorBlue()
                    .append("?")
            );
        }
    }

    public void writeVerifyInfo() {
        if (!isInstalled()) {
            writeLineToConsole(out -> out
                    .colorRed().styleBold()
                    .append("[FAIL] ")
                    .reset()
                    .append(getName())
                    .append(" is not installed")
            );

        } else if (!isRequiredVersion()) {
            writeLineToConsole(out -> out
                    .colorRed().styleBold()
                    .append("[FAIL] ")
                    .reset()
                    .append(getName())
                    .append(" ")
                    .append(getPreciseVersionAsString())
                    .append(" (at least ")
                    .append(getRequiredVersion())
                    .append(" is required)")
            );

        } else {
            writeLineToConsole(out -> out
                    .styleBold()
                    .append("[ OK ] ")
                    .reset()
                    .append(getName())
            );
        }
    }

    @FunctionalInterface
    public static interface ValueSupplier {

        Object getValue() throws Exception;
    }

    public Version getVersion() {
        if (version == null) {
            version = toVersion(getPreciseVersionAsString());
        }
        return version;
    }

    Version getRequiredVersion() {
        return requiredVersion;
    }

    boolean isRequiredVersion() {
        return getRequiredVersion().lessThanOrEqualTo(getVersion());
    }

    boolean isInstalled() {
        try {
            getVersion();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private static Version toVersion(String versionString) {
        try {
            Matcher matcher;

            matcher = Pattern.compile("(?<simpleVersion>\\d+\\.\\d+\\.\\d+).*").matcher(versionString);
            if (matcher.matches()) {
                return Version.valueOf(matcher.group("simpleVersion"));
            }

            matcher = Pattern.compile("(?<simpleVersion>\\d+\\.\\d+).*").matcher(versionString);
            if (matcher.matches()) {
                return Version.valueOf(matcher.group("simpleVersion") + ".0");
            }

            matcher = Pattern.compile("(?<simpleVersion>\\d+).*").matcher(versionString);
            if (matcher.matches()) {
                return Version.valueOf(matcher.group("simpleVersion") + ".0.0");
            }

            throw new IllegalArgumentException("The versionString [" + versionString + "] cannot be converted into X.Y.Z version");

        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Unable to parse " + versionString + " as a version");
        }
    }
}
