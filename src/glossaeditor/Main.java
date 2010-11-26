/*
 *  Copyright 2010 Georgios Migdos <cyberpython@gmail.com>.
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package glossaeditor;

import glossa.ui.cli.CLI;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

/**
 *
 * @author Georgios Migdos <cyberpython@gmail.com>
 */
public class Main {
    
    private static final String APP_NAME = "Slang - Διερμηνευτής για τη ΓΛΩΣΣΑ";
    private static final String VERSION = "Έκδοση: 0.9";
    private static final String SOURCE_FILE_NOT_DEFINED = "Δεν καθορίσατε το αρχείο πηγαίου κώδικα.";
    private static final String WRONG_USAGE = "Λάθος τρόπος χρήσης.";

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {

        String[] arguments = args;

        OptionParser parser = new OptionParser("hVcif:");

        try {
            OptionSet options = parser.parse(arguments);
            if (options.has("h") || options.has("V")) {
                if (options.has("V")) {
                    printVersionInfo(System.out);
                    printLicense(System.out);
                }
                if (options.has("h")) {
                    printHelpMessage(System.out);
                }
            } else {
                List<String> remainingArgs = options.nonOptionArguments();
                if(options.has("c")){
                    if (remainingArgs.size() > 0) {
                        boolean interactive = false;
                        File inputFile = null;
                        if (options.has("i")) {
                            interactive = true;
                        }
                        if (options.has("f")) {
                            inputFile = new File((String) options.valueOf("f"));
                        }
                        File sourceCodeFile = new File(remainingArgs.get(0));
                        CLI cli = new CLI(interactive);
                        if (inputFile != null) {
                            cli.execute(sourceCodeFile, inputFile);
                        } else {
                            cli.execute(sourceCodeFile);
                        }

                    } else {
                        System.out.println(SOURCE_FILE_NOT_DEFINED);
                        System.out.println();
                        printHelpMessage(System.out);
                    }
                }else{
                    Slang.launch(Slang.class, remainingArgs.toArray(new String[0]));
                }
            }
        } catch (OptionException uoe) {
            System.out.println(WRONG_USAGE);
            System.out.println();
            printHelpMessage(System.out);
        }
    }

    private static void printHelpMessage(PrintStream out) {
        printFile("/glossaeditor/resources/usage.txt", out);
    }

    private static void printLicense(PrintStream out) {
        printFile("/glossaeditor/resources/license.txt", out);
    }

    private static void printFile(String fileURL, PrintStream out) {
        BufferedReader r = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(fileURL), Charset.forName("UTF-8")));
        String line = "";
        try {
            while ((line = r.readLine()) != null) {
                out.println(line);
            }
        } catch (IOException ioe) {
        }
    }

    private static void printVersionInfo(PrintStream out) {
        out.println();
        out.println(APP_NAME);
        out.println(VERSION);
        out.println();
    }

}
