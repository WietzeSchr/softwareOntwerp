import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @param line 0-based
 * @param column 0-based
 */
record TextLocation(int line, int column) {}

class SimpleJsonParserException extends RuntimeException {
    TextLocation location;
    String innerMessage;
    public SimpleJsonParserException(TextLocation location, String message) {
        super(location + ": " + message);
        this.location = location;
        this.innerMessage = message;
    }
}

class SimpleJsonParser {

    String text;
    /**
     * Zero-based char offset
     */
    int offset;
    /**
     * Zero-based line number
     */
    int line;
    /**
     * Zero-based column number
     */
    int column;
    int objectNestingDepth;

    SimpleJsonParser(String text) {
        this.text = text;
    }

    TextLocation location() { return new TextLocation(line, column); }

    int peek() {
        if (offset == text.length())
            return -1;
        return text.charAt(offset);
    }

    void eat() {
        int c = text.charAt(offset);
        if (c == '\r') {
            // We assume the text has either CRLF or LF line separators.
            offset += 2;
            line++;
            column = 0;
        } else if (c == '\n') {
            offset++;
            line++;
            column = 0;
        } else {
            offset++;
            column++;
        }
    }

    String charToString(int c) {
        return switch (c) {
            case -1 -> "end of text";
            case '\r' -> "carriage return";
            case '\n' -> "newline";
            default -> "'" + (char)c + "'";
        };
    }

    void expect(int c) {
        if (peek() != c)
            throw new SimpleJsonParserException(location(), "Expected " + charToString(c) + " but found " + charToString(peek()));
        eat();
    }

    void expectLineSeparator() {
        if (peek() == '\r') {
            eat();
        } else
            expect('\n');
    }

    void expectIndentation() {
        int n = 2 * objectNestingDepth;
        for (int i = 0; i < n; i++)
            expect(' ');
    }

    void expectLineBreak() {
        expectLineSeparator();
        expectIndentation();
    }

    FileSystemEntry parseSimpleJsonValue(String name) {
        return switch (peek()) {
            case '"' -> parseSimpleJsonString(name);
            case '{' -> parseSimpleJsonObject(name);
            default -> throw new SimpleJsonParserException(location(), "Value expected");
        };
    }


    JsonValue parseSimpleJsonString() {
        TextLocation start = location();
        expect('"');
        StringBuilder builder = new StringBuilder();
        for (;;) {
            int c = peek();
            switch (c) {
                case '"' -> {
                    eat();
                    return new JsonValue(null, builder.toString(), new Point(start.line() + 1, start.column() + 1));
                }
                case '\\' -> {
                    eat();
                    switch (peek()) {
                        case '\\' -> {
                            eat();
                            builder.append('\\');
                        }
                        case '"' -> {
                            eat();
                            builder.append('"');
                        }
                        case 'r' -> {
                            eat();
                            builder.append('\r');
                        }
                        case 'n' -> {
                            eat();
                            builder.append('\n');
                        }
                        default ->
                                throw new SimpleJsonParserException(location(), "Invalid escape sequence");
                    }
                }
                default -> {
                    if (32 <= c && c <= 126) {
                        eat();
                        builder.append((char)c);
                    } else
                        throw new SimpleJsonParserException(location(), "Invalid character in string value");
                }
            }
        }
    }

    JsonValue parseSimpleJsonString(String name) {
        TextLocation start = location();
        expect('"');
        StringBuilder builder = new StringBuilder();
        for (;;) {
            int c = peek();
            switch (c) {
                case '"' -> {
                    eat();
                    return new JsonValue(name, builder.toString(), new Point(start.line() + 1, start.column() + 1));
                }
                case '\\' -> {
                    eat();
                    switch (peek()) {
                        case '\\' -> {
                            eat();
                            builder.append('\\');
                        }
                        case '"' -> {
                            eat();
                            builder.append('"');
                        }
                        case 'r' -> {
                            eat();
                            builder.append('\r');
                        }
                        case 'n' -> {
                            eat();
                            builder.append('\n');
                        }
                        default ->
                                throw new SimpleJsonParserException(location(), "Invalid escape sequence");
                    }
                }
                default -> {
                    if (32 <= c && c <= 126) {
                        eat();
                        builder.append((char)c);
                    } else
                        throw new SimpleJsonParserException(location(), "Invalid character in string value");
                }
            }
        }
    }

    JsonObject parseSimpleJsonObject(String name) {
        expect('{');
        objectNestingDepth++;
        expectLineBreak();
        String path = name + "/";
        ArrayList<FileSystemEntry> properties = new ArrayList<>();
        for (;;) {
            String propertyName = parseSimpleJsonString().getValue();
            String propertyPath = path + propertyName;
            for (FileSystemEntry property : properties) {
                if (property.getName().equals(propertyName))
                    throw new SimpleJsonParserException(location(), "Duplicate property");
            }
            expect(':');
            expect(' ');
            FileSystemEntry propertyValue = parseSimpleJsonValue(propertyPath);
            properties.add(propertyValue);
            if (peek() != ',')
                break;
            expect(',');
            expectLineBreak();
        }
        objectNestingDepth--;
        expectLineBreak();
        expect('}');
        return new JsonObject(path, properties.toArray(new FileSystemEntry[0]));
    }

    static JsonObject parseJsonObject(String text, Buffer buffer) {
        SimpleJsonParser parser = new SimpleJsonParser(text);
        JsonObject result = parser.parseSimpleJsonObject("/root");
        result.setBuffer(buffer);
        if (parser.peek() != -1)
            throw new SimpleJsonParserException(parser.location(), "End of text expected");
        return result;
    }

}


class SimpleJsonGenerator {

    ArrayList<String> lines = new ArrayList<>();

    StringBuilder builder = new StringBuilder();
    int objectNestingDepth;

    void generateLineBreak() {
        lines.add(builder.toString());
        builder = new StringBuilder();
        generateIndentation();
    }

    void generateIndentation() {
        builder.append(" ".repeat(Math.max(0, objectNestingDepth * 2)));
    }

    void generate(FileSystemEntry value) {
        value.generate(this);
    }

    void generate(String text) {
        builder.append('"');
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '\r' -> builder.append("\\r");
                case '\n' -> builder.append("\\n");
                case '"' -> builder.append("\\\"");
                case '\\' -> builder.append("\\\\");
                default -> {
                    if (32 <= c && c <= 126)
                        builder.append(c);
                    else
                        throw new RuntimeException("Invalid character in string literal at offset " + i);
                }
            }
        }
        builder.append('"');
    }

    void generateJsonValue(JsonValue string) {
        generate(string.getValue());
    }

    void generateJsonObject(JsonObject object) {
        builder.append('{');
        objectNestingDepth++;
        generateLineBreak();
        int i = 0;
        for (FileSystemEntry property : object.getEntries()) {
            if (property != object.getParent()) {
                String name = property.getName().split("/")[0];
                generate(name);
                builder.append(": ");
                generate(property);
                if (i < object.getEntries().length - 1) {
                    builder.append(',');
                    generateLineBreak();
                }
            }
            i++;
        }
        objectNestingDepth--;
        generateLineBreak();
        builder.append('}');
    }

    void generateFile(File file) {
        throw new RuntimeException("File can't be generated as json");
    }

    void generateDir(Directory dir) {
        throw new RuntimeException("Directory can't be generated as json");
    }

    static String[] generateJson(FileSystemEntry value) {
        SimpleJsonGenerator generator = new SimpleJsonGenerator();
        generator.generate(value);
        generator.lines.add(generator.builder.toString());
        return generator.lines.toArray(new String[0]);
    }

    static String generateStringLiteral(String content) {
        SimpleJsonGenerator generator = new SimpleJsonGenerator();
        generator.generate(content);
        return generator.builder.toString();
    }
}
