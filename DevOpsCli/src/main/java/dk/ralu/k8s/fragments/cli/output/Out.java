package dk.ralu.k8s.fragments.cli.output;

import org.springframework.boot.ansi.AnsiBackground;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;

/**
 * Used for building strings that may include ANSI formatting (style, color, and background color). Used same way as StringBuilder.
 * <p>
 * When {@link Out#toString()} is called, it will include ANSI codes to reset style, color, and background, if they have been set
 * to not default/normal values.
 * <p>
 * May require invocation of {@code AnsiOutput.setConsoleAvailable(true)} to enable.
 * <p>
 * Example:
 * <pre>
 * public static void main(String[] args) {
 *      AnsiOutput.setConsoleAvailable(true);
 *      System.out.println(new Out().append("Hi ").color(AnsiColor.RED).append("my").colorDefault().append(" friend"));
 * }
 * </pre>
 */
public class Out {

    private StringBuilder stringBuilder = new StringBuilder();
    private boolean ignoreOutput = false;

    public Out() {
        reset();
    }

    public Out append(Object o) {
        stringBuilder.append(o);
        return this;
    }

    public Out appendLine(Object o) {
        stringBuilder.append(o).append('\n');
        return this;
    }

    public Out appendLine() {
        stringBuilder.append('\n');
        return this;
    }

    public Out colorGray() {
        stringBuilder.append(AnsiOutput.encode(AnsiColor.BRIGHT_BLACK));
        return this;
    }

    public Out colorRed() {
        stringBuilder.append(AnsiOutput.encode(AnsiColor.BRIGHT_RED));
        return this;
    }

    public Out colorGreen() {
        stringBuilder.append(AnsiOutput.encode(AnsiColor.GREEN)); // color normally used by Spring Shell for result rendering
        return this;
    }

    public Out colorYellow() {
        stringBuilder.append(AnsiOutput.encode(AnsiColor.BRIGHT_YELLOW));
        return this;
    }

    public Out colorBlue() {
        stringBuilder.append(AnsiOutput.encode(AnsiColor.BRIGHT_BLUE));
        return this;
    }

    public Out colorMagenta() {
        stringBuilder.append(AnsiOutput.encode(AnsiColor.BRIGHT_MAGENTA));
        return this;
    }

    public Out colorCyan() {
        stringBuilder.append(AnsiOutput.encode(AnsiColor.BRIGHT_CYAN));
        return this;
    }

    public Out colorWhite() {
        stringBuilder.append(AnsiOutput.encode(AnsiColor.BRIGHT_WHITE));
        return this;
    }

    public Out background(AnsiBackground background) {
        stringBuilder.append(AnsiOutput.encode(background));
        return this;
    }

    public Out styleBold() {
        stringBuilder.append(AnsiOutput.encode(AnsiStyle.BOLD));
        return this;
    }

    public Out styleItalic() {
        stringBuilder.append(AnsiOutput.encode(AnsiStyle.ITALIC));
        return this;
    }

    public Out styleUnderline() {
        stringBuilder.append(AnsiOutput.encode(AnsiStyle.UNDERLINE));
        return this;
    }

    public Out reset() {
        stringBuilder.append(AnsiOutput.encode(AnsiStyle.NORMAL));
        colorGreen();
        return this;
    }

    /**
     * Will make not write/return any output no matter what other methods has been or will be called on this Out object.
     */
    public Out ignoreOutput() {
        this.ignoreOutput = true;
        return this;
    }

    void writeToConsole() {
        if (!ignoreOutput) {
            stringBuilder.append(AnsiOutput.encode(AnsiStyle.NORMAL));
            System.out.print(stringBuilder.toString());
        }
    }

    @Override
    public String toString() {
        if (ignoreOutput) {
            return "";
        } else {
            stringBuilder.append(AnsiOutput.encode(AnsiStyle.NORMAL));
            return stringBuilder.toString();
        }
    }
}
