import java.util.Objects;

public class Commands {


    public static void runCommand(String input){

        String command;
        String args = null;

        if (input.contains(" ")){
            String[] text = input.split(" ", 2);
            command = text[0];
            args = text[1];
        } else {
            command = input;
        }

        switch (command.toLowerCase()){

            case "exit":
                exitCommand();
                break;
            case "echo":
                echoCommand(args);
                break;
            case "type":
                typeCommand(args);
                break;

            default:
                System.out.println(input + ": command not found");
        }

    }

    private static String typeCommand(String args) {
        String output;
        try{
            CommandList c = CommandList.valueOf(args.toUpperCase());
            output = args + " is a shell builtin";
            System.out.println(output);
            return output;
        } catch (IllegalArgumentException e){
            output = args + ": not found";
            System.out.println(output);
            return output;
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
