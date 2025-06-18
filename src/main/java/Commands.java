import java.util.Arrays;
import java.util.Objects;

public class Commands {


    public static void runCommand(String input){

        String[] text = input.split("$ ", 2);
        String text2 = Arrays.toString(text[1].split(" ", 2));
        String command = text[0];
        String args = text[1];

        switch (command.toLowerCase()){

            case "exit":
                exitCommand();
                break;
            case "echo":
                echoCommand(args);
                break;

            default:
                System.out.println(input + ": command not found");
        }

    }

    public static String echoCommand(String args){
        System.out.println(args);
        return args;
    }

    public static void exitCommand(){
            System.exit(0);
    }
}
