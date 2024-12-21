import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        print$();

        Scanner scanner = new Scanner(System.in);

        String[] commands = {"echo", "exit", "type"};
        String parameter, command;

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
                    if (parts.length > 1) {
                        System.out.println(String.join(" ", parts).substring(5));
                    }
                    break;
                case "type":
                    parameter = input.substring(5);
                    if (Arrays.asList(commands).contains(parameter)) {
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
}
