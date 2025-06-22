import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Commands {

    public static void runBuiltInCommand(String input) throws IOException {

        String command;
        String args = null;

        if (input.contains(" ")){
            String[] text = input.trim().split(" ", 2);
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
                runOtherCommand(command, args);
        }
    }

    private static String runOtherCommand(String command, String args) throws IOException {

        try{
            runFile(command, args);
        }catch (Exception e){
            return args + ": not found";
        }

        return null;
    }

    private static void runFile(String command, String args) throws IOException {
        String[] commandsPath = getCommandsPath(command);
        args = command + " " + args;

        for (String path : commandsPath){
            File file = new File(path, command);

            if (file.exists() && file.canExecute()) {
                
                path = path+command;
                ProcessBuilder processBuilder = new ProcessBuilder(args);
                processBuilder.start();
            }
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

            String[] pathList = getCommandsPath(args);

            for (String directory : pathList) { // Loops in the dirs

                if(System.getProperty("os.name").toLowerCase().contains("win")){
                    File file = new File(directory, args+".exe"); // Creates a file in the dir
                    if (file.exists() && file.canExecute()) { // Check if exists and if it's executable
                        output = args + " is " + file.getAbsolutePath();
                        System.out.println(output);
                        return output;
                    }
                } else{
                    File file = new File(directory, args); // Creates a file in the dir
                    if (file.exists() && file.canExecute()) { // Check if exists and if it's executable
                        output = args + " is " + file.getAbsolutePath();
                        System.out.println(output);
                        return output;
                    }
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

    private static String[] getCommandsPath(String command){
        String[] commandPaths;
        String pathEnv = System.getenv("PATH"); // Draw the list of the paths where the commands are usually located
        String separator = "";

        if(System.getProperty("os.name").toLowerCase().contains("win")){ //if it's on win will use → ;
            separator = ";";
        } else{
            separator = ":";
        }
        commandPaths = pathEnv.split(separator); // Splits the list in directories
        return commandPaths;
    }

}
