import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @param line 0-based
 * @param column 0-based
 */
record TextLocation(int line, int column) {}

class SimpleJsonProperty {
    SimpleJsonObject object;
    String name;
    SimpleJsonValue value;
    SimpleJsonProperty(String name, SimpleJsonValue value) {
        this.name = name;
        this.value = value;
        value.property = this;
    }
}

sealed abstract class SimpleJsonValue permits SimpleJsonString, SimpleJsonObject {
    /**
     * The property for which this is the value, or null if this is a toplevel value.
     */
    SimpleJsonProperty property;
}

final class SimpleJsonString extends SimpleJsonValue {
    TextLocation start;
    int length;
    String value;
    SimpleJsonString(TextLocation start, int length, String value) {
        this.start = start;
        this.length = length;
        this.value = value;
    }
}

final class SimpleJsonObject extends SimpleJsonValue {
    LinkedHashMap<String, SimpleJsonProperty> properties;
    SimpleJsonObject(LinkedHashMap<String, SimpleJsonProperty> properties) {
        this.properties = properties;
        this.properties.values().forEach(p -> p.object = this);
    }
}

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

    static JsonObject parseJsonObject(String text) {
        SimpleJsonParser parser = new SimpleJsonParser(text);
        JsonObject result = parser.parseSimpleJsonObject("/root");
        if (parser.peek() != -1)
            throw new SimpleJsonParserException(parser.location(), "End of text expected");
        return result;
    }

}

/*
class SimpleJsonGenerator {
    StringBuilder builder = new StringBuilder();
    int objectNestingDepth;
    String lineSeparator;

    SimpleJsonGenerator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    void generateLineBreak() {
        builder.append(lineSeparator);
        int n = 2 * objectNestingDepth;
        for (int i = 0; i < n; i++)
            builder.append(' ');
    }

    void generate(SimpleJsonValue value) {
        switch (value) {
            case SimpleJsonString s -> generate(s);
            case SimpleJsonObject o -> generate(o);
        }
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

    void generate(SimpleJsonString string) {
        generate(string.value);
    }

    void generate(SimpleJsonObject object) {
        builder.append('{');
        objectNestingDepth++;
        generateLineBreak();
        int i = 0;
        for (SimpleJsonProperty property : object.properties.values()) {
            generate(property.name);
            builder.append(": ");
            generate(property.value);
            i++;
            if (i < object.properties.size()) {
                builder.append(',');
                generateLineBreak();
            }
        }
        objectNestingDepth--;
        generateLineBreak();
        builder.append('}');
    }

    static String generate(String lineSeparator, SimpleJsonValue value) {
        SimpleJsonGenerator generator = new SimpleJsonGenerator(lineSeparator);
        generator.generate(value);
        return generator.builder.toString();
    }

    static String generateStringLiteral(String content) {
        SimpleJsonGenerator generator = new SimpleJsonGenerator(null);
        generator.generate(content);
        return generator.builder.toString();
    }
}*/
