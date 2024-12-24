import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
        print$();

        Scanner scanner = new Scanner(System.in);

        List<String> commands = List.of("exit", "echo", "type", "pwd", "cd");
        String parameter, command;

        String cwd = System.getProperty("user.dir"); // eqv to Path.of("").toAbsolutePath().toString();

        while (true) {
            String input = scanner.nextLine();

            String[] parts = input.split(" +");

            if (parts.length == 0 || parts[0].isEmpty()) {
                print$();
                continue;
            }

            switch (command = parts[0]) {
                case "exit":
                    if (parts.length > 1) {
                        System.exit(Integer.parseInt(parts[1]));
                    }
                    break;
                case "echo":
                    Pattern pattern = Pattern.compile("\\w+((\\\\ )+\\w+)");
                    Matcher matcher = pattern.matcher(input);
                    if (matcher.find()) {
                        System.out.println(matcher.group(0).replace("\\ ", " "));
                        break;
                    }
                    List<String> _parsedArgs = parseArguments(input);
                    _parsedArgs.removeFirst();
                    System.out.println(_parsedArgs.stream().reduce((a, b) -> a + " " + b).orElse(""));
                    break;
                case "type":
                    parameter = input.substring(5);
                    if (commands.contains(parameter)) {
                        System.out.println(parameter + " is a shell builtin");
                    } else {
                        String path = getPath(parameter);
                        if (path != null) {
                            System.out.println(parameter + " is " + path);
                        } else {
                            System.out.println(parameter + ": not found");
                        }
                    }
                    break;
                default:
                    String path = getPath(command);
                    if (path == null) {
                        System.out.printf("%s: command not found%n", command);
                    } else {
                        String fullPath = path + input.substring(command.length());
                        Process p = Runtime.getRuntime().exec(fullPath.split(" "));
                        p.getInputStream().transferTo(System.out);
                    }
                    break;
                case "pwd":
                    System.out.println(cwd);
                    break;
                case "cd":
                    String dir = parts[1];

                    if (dir.equals("~")) {
                        if ((cwd = System.getenv("HOME")) == null) {
                            cwd = System.getenv("HOMEPATH");
                        }
                        break;
                    }

                    if (!dir.startsWith("/")) {
                        dir = cwd + "/" + dir;
                    }

                    Path _path = Path.of(dir);
                    if (Files.isDirectory(_path)) {
                        cwd = _path.normalize().toString();
                    } else {
                        System.out.printf("cd: %s: No such file or directory%n", dir);
                    }
                    break;
                case "cat":
                    List<String> parsedArgs = parseArguments(input);
                    parsedArgs.removeFirst();

                    for (String p : parsedArgs) {
                        Path file = Path.of(p);
                        if (Files.isRegularFile(file)) {
                            Files.lines(file).forEach(System.out::print);
                        } else {
                            System.out.printf("cat: %s: No such file or directory", file);
                        }
                    }
                    System.out.println();
                    break;
            }

            print$();
        }
    }

    public static void print$() {
        System.out.print("$ ");
    }

    private static String getPath(String parameter) {
        for (String path : System.getenv("PATH").split(":")) {
            Path fullPath = Path.of(path, parameter);
            if (Files.isRegularFile(fullPath)) {
                return fullPath.toString();
            }
        }
        return null;
    }

    public static List<String> parseArguments(String input) {
        List<String> arguments = new ArrayList<>();
        // Regex to handle quoted and unquoted arguments, including escape characters
        Pattern pattern = Pattern.compile("'((?:\\\\.|[^'\\\\])*)'|\"((?:\\\\.|[^\"\\\\])*)\"|(\\S+)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Single-quoted argument
                arguments.add(unescape(matcher.group(1)));
            } else if (matcher.group(2) != null) {
                // Double-quoted argument
                arguments.add(unescape(matcher.group(2)));
            } else if (matcher.group(3) != null) {
                // Unquoted argument
                arguments.add(matcher.group(3));
            }/* else if (matcher.group(4) != null) {
                // Unquoted argument with backslashes followed by spaces
                System.out.println("matcher.group(4)");
                arguments.add(unescape(matcher.group(4)));
            }*/
        }

        return arguments;
    }

    public static String unescape(String str) {
        // Replace backslashes followed by spaces with actual spaces
        str = str.replaceAll("\\\\ ", " ");
        // Replace other escaped characters with their actual representations
        return str.replace("\\n", "\n")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\'", "'")
                .replace("\\\\", "\\");
    }
}
