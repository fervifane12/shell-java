import java.util.Objects;

public class Commands {

    public static void exitCommand(String command){
        if (Objects.equals(command, "exit 0")){
            System.exit(0);
        }
    }
}
