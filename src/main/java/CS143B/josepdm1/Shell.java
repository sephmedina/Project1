package CS143B.josepdm1;

import CS143B.josepdm1.Exceptions.PCBException;
import CS143B.josepdm1.Exceptions.RCBException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Shell {
    private final static Logger LOG = Logger.getLogger(Shell.class.getName());
    static {
        FileHandler fh = null;
        try {
            fh = new FileHandler("log.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        fh.setFormatter(new SimpleFormatter());
        LOG.addHandler(fh);
    }
    public static void main(String[] args) throws IOException {
        //pass in file name as first arg
        //reading input
        System.out.println(args[0]);
        File file = new File(args[0]);
        Scanner scanner = new Scanner(file);

        //writing to file
        final String UCIiD = "70398647.txt";
        PrintStream out = new PrintStream(new FileOutputStream(UCIiD));
        System.setOut(out);
        Manager processManager = new Manager();
        int value;
        int resource;
        while ( scanner.hasNextLine() ) {
            String line = scanner.nextLine();
            System.out.println("input: " + line);
            //todo check new line too
            if (line.equals("") || line.equals("\n")) {
                continue;
            }
            LOG.info("input: " + line);
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
                    catch (PCBException e) {
                        e.printStackTrace();
                        System.out.println(-1);
                    }
                }
                else if (cmd.equals("cr")) {
                    try {
                        System.out.print( processManager.create(value));
                    } catch (PCBException e) {
                        e.printStackTrace();
                        System.out.println(-1);
                    }
                }
                else {
                    System.out.println(-1);
                }
            }
            else if (tokens.length == 3) {
            	try {
                    resource = getInteger(tokens[1]);
                    value = getInteger(tokens[2]);
                } catch (NumberFormatException e) {
                    System.out.println(-1);
                    continue;
                }
                if (cmd.equals("rq")) {
                    System.out.print( processManager.request(resource, value));
                }
                else if (cmd.equals("rl")) {
                    try {
                        System.out.print( processManager.release(resource, value));
                    } catch (PCBException e) {
                        System.out.println(-1);
                        continue;
                    } catch (RCBException e) {
                        System.out.println(-1);
                        continue;
                    }
                }

            }
            //bad input?
            else {
                System.out.println(-1);
                continue;
            }
            System.out.println(processManager.toString() + "\n");
        }
        scanner.close();
        return;
    }
    private static int getInteger(String token) throws NumberFormatException{
        return Integer.parseInt(token);
    }
}
