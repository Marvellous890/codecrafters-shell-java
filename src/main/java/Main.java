import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        print$();

        Scanner scanner = new Scanner(System.in);

        String[] commands = {"echo", "exit", "type"};
        String typeArg;

        while (true) {
            String input = scanner.nextLine();

            String[] parts = input.split(" +");

            if (parts.length == 0 || parts[0].isEmpty()) {
                print$();
                continue;
            }

            switch (parts[0]) {
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
                    typeArg = input.substring(5);
                    if (Arrays.asList(commands).contains(typeArg)) {
                        System.out.println(typeArg + " is a shell builtin");
                    } else {
                        System.out.println(typeArg + " not found");
                    }
                    break;
                default:
                    System.out.println(input + ": command not found");
                    break;
            }

            print$();
        }
    }

    public static void print$() {
        System.out.print("$ ");
    }
}
