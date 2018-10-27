package task.table_renderer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import task.table_renderer.domain.Table;

/**
 * Console reporting app.
 * 
 */
public class Generator {
    public static void main( String[] args ) throws IOException {
    	args = new String[3];
    	args[0] = "settings.xml";
    	args[1] = "source-data.tsv";
    	args[2] = "out.txt";
    	
    	if (args.length != 3) {
    		Logger.getGlobal().log(Level.INFO, "Not enough arguments, examlple:"
    				+ "\njava -jar reportApp.jar settings.xml source-data.tsv out.txt");
    		System.exit(0);
    	}
        
        String output = Table.builder().withSettingsFromXML(new File(args[0]))
            	.withRowsFromTSV(new File(args[1]))
            	.build().toString();
        
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
    			new FileOutputStream(new File(args[2])), StandardCharsets.UTF_16)))) {
            out.println(output);
        }
        
    }
}

