package dk.ralu.k8s.fragments.cli.output;

import java.util.function.Consumer;

public abstract class ConsoleWriter {

    protected void writeToConsole(Consumer<Out> writer) {
        writeToConsole(writer, "");
    }

    protected void writeLineToConsole(Consumer<Out> writer) {
        writeToConsole(writer, "\n");
    }

    private void writeToConsole(Consumer<Out> writer, String suffix) {
        Out out = new Out();
        writer.accept(out);
        out.append(suffix);
        out.writeToConsole();
    }
}
