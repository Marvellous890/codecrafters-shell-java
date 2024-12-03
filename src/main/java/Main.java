import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        print$();

        Scanner scanner = new Scanner(System.in);

        while(true) {
            String input = scanner.nextLine();

            if (input.startsWith("exit ")) {
                break;
            }

            System.out.println(input + ": not found");
            print$();
        }
    }

    public static void print$() {
        System.out.print("$ ");
    }
}
