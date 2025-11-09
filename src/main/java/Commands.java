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

    private static void runOtherCommand(String command, String args) {



        if (!runFile(command, args)) {
            System.out.println(command + ": not found");
        }
    }

    private static boolean runFile(String command, String args) {
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

    private static ArrayList<String> listArgs(String command, String args) {
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add(command);

        if (args == null || args.isEmpty()) return commandList;

        Matcher matcher = Pattern.compile("'([^']*)'|\"([^\"]*)\"|(\\S+)").matcher(args);

        while (matcher.find()) {
            String arg = matcher.group(1);
            if (arg == null) arg = matcher.group(2);
            if (arg == null) arg = matcher.group(3);
            commandList.add(arg);
        }

        return commandList;
    }

    private static String typeCommand(String args) {
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

    public static void addNotQuoted (String content, StringBuilder builder){
        String contentClean = content.replaceAll("\s+", " ");
        builder.append(contentClean);
    }

    public static String echoCommand(String args) {
        StringBuilder builder = new StringBuilder();

        if ( args == null || args.isEmpty()){
            return args = "";
        }

        Matcher matcher = Pattern.compile("'([^']*)'|\"([^\"]*)\"|(\\S+)").matcher(args);

        while (matcher.find()){
            String arg = matcher.group(1);
            if (arg == null){
                arg = matcher.group(2);
            } else if (arg==null) {
                arg = matcher.group(3);
            }
            builder.append(arg);
        }
        return builder.toString();
        /*
        String[] argsSplit = args.splitWithDelimiters("'", 0);
        boolean isInsideQuotes = false;

        for (String content: argsSplit){
            if (content.equals("'") && !isInsideQuotes){
                isInsideQuotes = true;
            } else if (content.equals("'") && isInsideQuotes) {
                isInsideQuotes = false;
            } else if (!content.equals("'") && isInsideQuotes) {
                builder.append(content);
            } else {
                addNotQuoted(content, builder);
            }
        }
        System.out.println(builder.toString().trim());
        return builder.toString().trim();

         */
    }

    public static void exitCommand() {
        System.exit(0);
    }

    private static String[] getCommandsPath() {
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
