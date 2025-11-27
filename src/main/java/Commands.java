import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commands {

    public static void runBuiltInCommand(String input){
        String command;
        String args = null;

        if (input.contains(" ")) {
            String[] text = input.trim().split(" ", 2);
            command = text[0];
            args = text[1];
        } else {
            command = input;
        }


        switch (command.toLowerCase()) {

            case "exit":
                exitCommand();
                break;
            case "echo":
                echoCommand(args);
                break;
            case "type":
                typeCommand(args);
                break;
            case "pwd":
                pwdCommand();
                break;
            case "cd":
                cdCommand(args);
                break;

            default:
                runOtherCommand(command, args);
        }
    }

    public static void runOtherCommand(String command, String args) {

        if (!runFile(command, args)) {
            System.out.println(command + ": not found");
        }
    }

    public static boolean runFile(String command, String args) {
        String[] commandsPath = getCommandsPath();

        List<String> commandList = listArgs(command, args);

        for (String path : commandsPath) {
            File file = new File(path, command);
            if (file.canExecute() && file.exists()) {
                ProcessBuilder processBuilder = new ProcessBuilder(commandList);
                try {
                    Process process = processBuilder.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            process.getInputStream()));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }

                    reader.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;

    }

    public static ArrayList<String> listArgs(String command, String args) {
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add(command);
        StringBuilder builder = new StringBuilder();


        boolean isInSingleQuotes = false;
        boolean isInDoubleQuotes = false;
        boolean lastWasSpace = false;

        for (int i = 0 ; i < args.length(); i++){
            String c = String.valueOf(args.charAt(i));
            if (!isInSingleQuotes && !isInDoubleQuotes){
                if (c.equals("\\") && i+1 < args.length()){
                    builder.append(args.charAt(i+1));
                    i++;
                }
                else if (c.equals("'")){
                    isInSingleQuotes = true;
                }
                else if (c.equals("\"")){
                    isInDoubleQuotes = true;
                }
                else if (c.equals(" ") && lastWasSpace){
                    continue;
                }
                else if (c.equals(" ")){
                    lastWasSpace = true;
                    builder.append(c);
                }
                else {
                    builder.append(c);
                }
                if (!c.equals(" ") && lastWasSpace) {
                    lastWasSpace = false;
                }
            }
            else if (isInSingleQuotes && !c.equals("'")){
                if (c.equals("\\") && i+1 < args.length()){
                    builder.append(args.charAt(i+1));
                    i++;
                }else builder.append(c);
            }
            else if (c.equals("'") && isInSingleQuotes) {
                isInSingleQuotes = false;
            }
            else if (isInDoubleQuotes && !c.equals("\"")) {
                if (c.equals("\\") && i+1 < args.length()){
                    builder.append(args.charAt(i+1));
                    i++;
                }else builder.append(c);
            }
            else if (c.equals("\"") && isInDoubleQuotes) {
                isInDoubleQuotes = false;
            }
        }
        String[] splitter = builder.toString().split(" ");
        System.out.println(Arrays.toString(splitter));
        commandList.add(Arrays.toString(splitter));

        return commandList;
/*
        if (args == null || args.isEmpty()) return commandList;

        Matcher matcher = Pattern.compile("'([^']*)'|\"([^\"]*)\"|(\\S+)").matcher(args);

        while (matcher.find()) {
            String arg = matcher.group(1);
            if (arg == null) arg = matcher.group(2);
            if (arg == null) arg = matcher.group(3);
            commandList.add(arg);
        }
*/


    }

    public static String typeCommand(String args) {
        String output;

        try {

            for (CommandList commandList : CommandList.values()) {
                if (commandList.name().equalsIgnoreCase(args)) {
                    output = args + " is a shell builtin";
                    System.out.println(output);
                    return output;
                }
            }

            String[] pathList = getCommandsPath();

            for (String directory : pathList) { // Loops in the dirs

                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    File file = new File(directory, args + ".exe"); // Creates a file in the dir
                    if (file.exists() && file.canExecute()) { // Check if exists and if it's executable
                        output = args + " is " + file.getAbsolutePath();
                        System.out.println(output);
                        return output;
                    }
                } else {
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

        } catch (IllegalArgumentException e) {
            output = args + ": not found";
            System.out.println(output);
            return output;
        }
    }

    public static String pwdCommand() {
        String path = System.getProperty("user.dir");
        System.out.println(path);
        return path;
    }

    public static void cdCommand(String path) {
        try {
            String currentPath = System.getProperty("user.dir");
            String newPath = "";

            if (path.contains("../")) {
                int x  = (path.splitWithDelimiters("../", 0).length)/2; //Ammount of ../ that were input
                String[] pathDirs = currentPath.trim().split("/");
                int y = pathDirs.length; //The lenght of the current dir
                if (x>=y){
                    System.out.println("cd: " + path + ": No such file or directory");
                }else {
                    for (int i = 0; i < y-x; i++) {
                        newPath = newPath + "/" + pathDirs[i];
                    }
                    System.setProperty("user.dir", newPath.substring(1));
                }


            } else if (path.contains("./")) {

                path = currentPath + path.substring(1);
                if (checkDirExistence(path)){
                    System.setProperty("user.dir", path);
                }

            } else if (path.contains("~")) {
                System.setProperty("user.dir", System.getenv("HOME"));
            } else {

                if (checkDirExistence(path)){
                    System.setProperty("user.dir", path);
                }
            }
        }catch (NullPointerException e){
            System.out.println("cd: " + path + ": No such file or directory");
        }

    }

    public static boolean checkDirExistence(String path){
        try {
            File file = new File(path);
            if (!file.createNewFile()) {
                file.delete();
                return true;
            } else {
                System.out.println("cd: " + path + ": No such file or directory");
                return false;
            }

        } catch (NullPointerException e) {
            System.out.println("cd: " + path + ": No such file or directory");
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String echoCommand(String args) {
        StringBuilder builder = new StringBuilder();

        boolean isInSingleQuotes = false;
        boolean isInDoubleQuotes = false;
        boolean lastWasSpace = false;

        for (int i = 0 ; i < args.length(); i++){
            String c = String.valueOf(args.charAt(i));
            if (!isInSingleQuotes && !isInDoubleQuotes){
                if (c.equals("\\") && i+1 < args.length()){
                    builder.append(args.charAt(i+1));
                    i++;
                }
                else if (c.equals("'")){
                    isInSingleQuotes = true;
                }
                else if (c.equals("\"")){
                    isInDoubleQuotes = true;
                }
                else if (c.equals(" ") && lastWasSpace){
                    continue;
                }
                else if (c.equals(" ")){
                    lastWasSpace = true;
                    builder.append(c);
                }
                else {
                    builder.append(c);
                }
                if (!c.equals(" ") && lastWasSpace) {
                    lastWasSpace = false;
                }
            }
            else if (isInSingleQuotes && !c.equals("'")){
                if (c.equals("\\") && i+1 < args.length()){
                    builder.append(args.charAt(i+1));
                    i++;
                }else builder.append(c);
                }
            else if (c.equals("'") && isInSingleQuotes) {
                isInSingleQuotes = false;
            }
            else if (isInDoubleQuotes && !c.equals("\"")) {
                if (c.equals("\\") && i+1 < args.length()){
                    builder.append(args.charAt(i+1));
                    i++;
                }else builder.append(c);
                }
            else if (c.equals("\"") && isInDoubleQuotes) {
                isInDoubleQuotes = false;
            }
        }

        System.out.println(builder);
        return builder.toString();
    }

    public static void exitCommand() {
        System.exit(0);
    }

    public static String[] getCommandsPath() {
        String[] commandPaths;
        String pathEnv = System.getenv("PATH"); // Draw the list of the paths where the commands are usually located
        String separator;

        if (System.getProperty("os.name").toLowerCase().contains("win")) { //if it's on win will use → ;
            separator = ";";
        } else {
            separator = ":";
        }

        commandPaths = pathEnv.split(separator); // Splits the list in directories
        return commandPaths;
    }
}
