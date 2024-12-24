import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

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
                    if (parts.length > 1) {
                        if (parts[1].startsWith("'")) {
                            System.out.println(input.substring(6, input.length() - 1));
                        } else if (parts[1].startsWith("\"")) {
                            System.out.println(input.substring(6, input.length() - 1));
                        } else {
                            System.out.println(String.join(" ", parts).substring(5));
                        }
                    }
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
