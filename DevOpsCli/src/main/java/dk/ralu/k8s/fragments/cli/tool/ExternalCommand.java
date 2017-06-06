package dk.ralu.k8s.fragments.cli.tool;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dk.ralu.k8s.fragments.cli.core.DebugFlags;
import dk.ralu.k8s.fragments.cli.output.ConsoleWriter;
import dk.ralu.k8s.fragments.cli.output.Out;
import dk.ralu.k8s.fragments.cli.tool.ExternalCommand.OutputLine.Source;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * Helper to make it easier to run commands (processes) external to the Java program.
 */
@Service
@Scope(scopeName = BeanDefinition.SCOPE_PROTOTYPE)
public class ExternalCommand extends ConsoleWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalCommand.class);

    private ExternalCommand() {
    }

    @Autowired
    private DebugFlags debugFlags;

    private Map<String, String> envVariables = new LinkedHashMap<String, String>() {{
        putAll(System.getenv());
    }};

    private List<String> command;

    private File workingDirectory;

    private long timeout = 1;
    private TimeUnit timeoutUnit = TimeUnit.MINUTES;

    private Integer requiredExitValue = 0;

    private boolean acceptUserInput;

    @JsonIgnore
    private BiConsumer<OutputLine, Out> outputLineConverter; // null means don't show output
    private boolean collectOutput = true;

    public ExternalCommand command(String command) {
        return command(command.split(" "));
    }

    public ExternalCommand command(String... command) {
        this.command = Arrays.asList(command);
        return this;
    }

    public ExternalCommand command(List<String> command) {
        this.command = command;
        return this;
    }

    /**
     * By default inherits all the environment variables of this Java program.
     */
    public ExternalCommand clearEnvironmentVariables() {
        envVariables.putAll(System.getenv());
        return this;
    }

    public ExternalCommand removeEnvironmentVariable(String key) {
        envVariables.remove(key);
        return this;
    }

    /**
     * Adds an environment variable.
     */
    public ExternalCommand environmentVariable(String key, String value) {
        envVariables.put(key, value);
        return this;
    }

    /**
     * Sets the working dir.
     */
    public ExternalCommand workingDirectory(File dir) {
        this.workingDirectory = dir;
        return this;
    }

    /**
     * Sets the working dir.
     */
    public ExternalCommand workingDirectory(Path dir) {
        this.workingDirectory = dir.toFile();
        return this;
    }

    public ExternalCommand neverTimeout() {
        timeout(0, null);
        return this;
    }

    /**
     * ExternalCommand blocks until program has finished or timeout has been reached (after which it will kill the program).
     * <p>
     * Default timeout is 1 minute.
     */
    public ExternalCommand timeout(long timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.timeoutUnit = unit;
        return this;
    }

    /**
     * Ignores the exit code. Default is to fail with exception if exit code is not 0.
     */
    public ExternalCommand ignoreExitValue() {
        return requiredExitValue(null);
    }

    /**
     * Fails with an exception, if the exit code is not what is expected (default expectation is 0).
     */
    public ExternalCommand requiredExitValue(Integer requiredExitCode) {
        this.requiredExitValue = requiredExitCode;
        return this;
    }

    /**
     * Accepts input from the user - e.g. for password.
     */
    public ExternalCommand acceptUserInput() {
        this.acceptUserInput = true;
        return this;
    }

    /**
     * Enable echoing of the output from stdout and stderr in the CLI (default is to NOT reflect it).
     */
    public ExternalCommand showOutput() {
        return showOutputWithConversion((outputLine, out) -> out.append(outputLine.getContent()));
    }

    /**
     * Enable echoing of the output from stdout and stderr in the CLI (default is to NOT reflect it).
     *
     * Lines from stdout will be prefixed with [OUT], while lines from stderr will be prefixed with [ERR].
     */
    public ExternalCommand showOutputWithSourcePrefix() {
        return showOutputWithConversion((outputLine, out) -> {
            if (outputLine.fromOut()) {
                out.colorRed().styleBold().append("[err] ").reset().append(outputLine.getContent());
            } else {
                out.styleBold().append("[out] ").reset().append(outputLine.getContent());
            }
        });
    }

    /**
     * Enable echoing of the output from stdout and stderr in the CLI (default is to NOT reflect it).
     *
     * The lineConverter is called for each line, and must convert the original line to what should be echoed on the CLI.
     *
     * If lineConverter returns null for a given line, the line will not be shown at all.
     */
    public ExternalCommand showOutputWithConversion(BiConsumer<OutputLine, Out> lineConverter) {
        this.outputLineConverter = lineConverter;
        return this;
    }

    /**
     * Executes the command. Does not collect the output.
     */
    public void executeIgnoreOutput() {
        this.collectOutput = false;
        execute();
    }

    /**
     * Executes the command. Expects the output to be a <b>single</b> line written to <b>std out</b> - if that is not the case, then
     * fails with an {@link IllegalStateException}.
     */
    public String executeExpectingSingleStdOutLine() throws IllegalStateException {
        List<OutputLine> outputLines = execute();
        if (outputLines.size() != 1) {
            throw new IllegalStateException("Expected exactly 1 line of output - but got " + outputLines.size() + " lines:" +
                                                    outputLines.stream()
                                                            .map(outputLine -> "[" + outputLine.getSource() + "] " + outputLine
                                                                    .getContent())
                                                            .collect(Collectors.joining("\n", "\n", "\n"))
            );
        }
        OutputLine outputLine = outputLines.get(0);
        if (outputLine.fromErr()) {
            throw new IllegalStateException("Expected exactly 1 line of output from std out, but got 1 line from std err");
        }
        return outputLine.getContent();
    }

    /**
     * As {@link #executeExpectingSingleStdOutLine()}, but must also match the given regex.
     */
    public Matcher executeExpectingSingleStdOutLineMatchingRegEx(String regex) {
        Pattern pattern = Pattern.compile(regex);
        String outputLine = executeExpectingSingleStdOutLine();
        Matcher matcher = pattern.matcher(outputLine);
        if (!matcher.matches()) {
            throw new IllegalStateException(
                    "Out line did not match regex (regex=[" + regex + "], output=[" + outputLine + "]");
        }
        return matcher;
    }

    /**
     * Executes the command. Collects all output, and returns it as a list of lines.
     */
    public List<OutputLine> execute() {
        try {
            LOGGER.debug("Preparing to run:\n" + this);
            if (debugFlags.isForceShowCommands()) {
                printDebugInfo("cmd", command.stream().collect(Collectors.joining(" ")));
            }
            List<OutputLine> outputLines = new LinkedList<>();
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command);
            processBuilder.environment().clear();
            processBuilder.environment().putAll(envVariables);
            if (acceptUserInput) {
                processBuilder.redirectInput(Redirect.INHERIT);
            }
            if (workingDirectory == null) {
                workingDirectory = new File(".").getCanonicalFile();
            }
            processBuilder.directory(workingDirectory);
            Process process = processBuilder.start();
            Consumer<OutputLine> outputLineConsumer = new Consumer<OutputLine>() {
                @Override
                public synchronized void accept(OutputLine outputLine) {
                    if (collectOutput) {
                        if (debugFlags.isForceShowOutput()) {
                            printDebugInfo(outputLine.getSource().toString().toLowerCase(), outputLine.getContent());
                        }
                        outputLines.add(outputLine);
                        if (outputLineConverter != null) {
                            writeLineToConsole(out -> outputLineConverter.accept(outputLine, out));
                        }
                    }
                }
            };
            Thread stdOutHandlingThread = createInputStreamHandlingThread(process, Source.OUT, outputLineConsumer);
            Thread stdErrHandlingThread = createInputStreamHandlingThread(process, Source.ERR, outputLineConsumer);
            if (timeoutUnit != null) {
                process.waitFor(timeout, timeoutUnit);
                process.destroyForcibly();
            } else {
                process.waitFor();
            }
            stdOutHandlingThread.join(5_000); // Wait 5 secs for thread to finish up
            stdErrHandlingThread.join(5_000);
            if (requiredExitValue != null && requiredExitValue != process.exitValue()) {
                throw new IllegalStateException("Expected exit code " + requiredExitValue + " but was " + process.exitValue());
            }
            if (debugFlags.isForceShowExitCode()) {
                printDebugInfo("exi", process.exitValue());
            }
            return outputLines;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to execute command:\n" + this.toString(), e);
        }
    }

    private void printDebugInfo(String prefix, Object content) {
        writeLineToConsole(out -> out
                .colorRed()
                .append('[')
                .append(prefix)
                .append("] ")
                .append(content)
        );
    }

    private Thread createInputStreamHandlingThread(Process process, Source source, Consumer<OutputLine> outputLineConsumer) {
        Thread thread = new Thread(() -> {
            try (InputStream inputStream = (source == Source.OUT ? process.getInputStream() : process.getErrorStream());
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while (null != (line = bufferedReader.readLine())) {
                    outputLineConsumer.accept(new OutputLine(source, line));
                }
            } catch (IOException ignore) {
            }
        });
        thread.start();
        return thread;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper()
                    .setVisibility(PropertyAccessor.ALL, Visibility.NONE)
                    .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
                    .enable(SerializationFeature.INDENT_OUTPUT).writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to map object of type " + getClass().getCanonicalName() + " to JSON");
        }
    }

    /**
     * Represents a line of output from a command.
     */
    public static class OutputLine {

        /**
         * Where the output line came from - std out or std err.
         */
        public enum Source {
            /**
             * Represents std out.
             */
            OUT,
            /**
             * Represents std err.
             */
            ERR
        }

        private Source source;
        private String content;

        public OutputLine(Source source, String content) {
            this.source = source;
            this.content = content;
        }

        public Source getSource() {
            return source;
        }

        public boolean fromErr() {
            return Source.ERR == source;
        }

        public boolean fromOut() {
            return Source.OUT == source;
        }

        public String getContent() {
            return content;
        }
    }
}
