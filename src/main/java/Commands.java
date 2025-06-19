import java.util.Objects;

public class Commands {


    public static void runCommand(String input){

        String command = null;
        String args = null;

        if (input.indexOf(" ") != -1){
            String[] text = input.split(" ", 2);
            command = text[0];
            args = text[1];
        } else {command = input;}

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
