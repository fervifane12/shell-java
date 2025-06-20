import java.io.File;
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

            for (CommandList commandList : CommandList.values()){
                if (commandList.name().equalsIgnoreCase(args)){
                    output = args + " is a shell builtin";
                    System.out.println(output);
                    return output;
                }
            }

            String pathEnv = System.getenv("PATH"); // Draw the list of the paths where the commands are usually located
            String[] pathList = pathEnv.split(":"); // Splits the list in directories

            for (String directory : pathList) { // Loops in the dirs
                System.out.println(directory);
                File file = new File(directory, args); // Creates a file in the dir
                if (file.exists() && file.canExecute()) { // Check if exists and if it's executable
                    output = args + " is " + file.getAbsolutePath();
                    System.out.println(output);
                    return output;
                }
            }

            output = args + ": not found";
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
