package CS143B.josepdm1;

import CS143B.josepdm1.Exceptions.RCBException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
public class Shell {
    public static void Main(String[] args) throws IOException {
        //pass in file name as first arg
        //reading input
        File file = new File(args[0]);
        Scanner scanner = new Scanner(file);
        //writing to file
        final String UCIiD = "70398647";
        PrintStream out = new PrintStream(new FileOutputStream(UCIiD));
        System.setOut(out);
        BasicManager processManager = new BasicManager();
        while ( scanner.hasNextLine() ) {
            String line = scanner.nextLine();
            //todo check new line too
            if (line.equals("") || line.equals("\n")) {
                continue;
            }
            String[] tokens = line.split(" ");
            String cmd = tokens[0];
            //process input
            if (tokens.length == 1) {
                if (cmd.equals("to")) {
                    System.out.print( processManager.timeout() );
                }
                //todo clarify - 1) will in always be called first 2)will in always be after an explicit error? or do we have to call in ourselves after an error
                else if (cmd.equals("in")) {
                    System.out.print( processManager.init() );
                }
                else {
                    System.out.println(-1);
                }

            }
            else if (tokens.length == 2) {
                int value;
                //todo handle error
                try {
                    value = getInteger(tokens[1]);
                } catch (NumberFormatException e) {
                    System.out.println(-1);
                    continue;
                }
                if (cmd.equals("de"))  {
                    try {
                        System.out.print( processManager.destroy(value));
                    } catch (RCBException e) {
                        e.printStackTrace();
                        System.out.println(-1);
                    }
                }
                else if (cmd.equals("cr")) {

                }
                else {
                    System.out.println(-1);
                }
            }
            else if (tokens.length == 3) {
                if (cmd.equals("rq")) {

                }
                else if (cmd.equals("rl")) {

                }

            }
            //bad input?
            else {

            }
        }
        return;
    }
    private static int getInteger(String token) throws NumberFormatException{
        return Integer.parseInt(token);
    }
}
