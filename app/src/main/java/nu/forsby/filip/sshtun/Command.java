package nu.forsby.filip.sshtun;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by filip on 2018-04-09.
 */

public class Command {

    String command;
    List<String> output;
    Long timestamp;

    public Command (String command) {
        this.command = command;
        output = new ArrayList<>(5);
    }
}
