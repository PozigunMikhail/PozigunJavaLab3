package inv_idx;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class InvertedIndex {
    protected Map<String, HashSet<String>> invIdxMap;

    public InvertedIndex() {
        invIdxMap = new HashMap<>();
    }

    private void printInvertedIdxByWordsInStr(String words) {
        for (String word : words.trim().split("\\W+")) {
            System.out.println(word + ":");
            if (invIdxMap.containsKey(word.toLowerCase())) {
                for (String dataSourceName : invIdxMap.get(word.toLowerCase())) {
                    System.out.println(dataSourceName);
                }
            } else {
                System.out.println("Not found in any data source.");
            }
        }
    }

    private void readWords(InputStream inStream) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inStream))) {
            String str;
            while ((str = in.readLine()) != null) {
                printInvertedIdxByWordsInStr(str);
            }
        }
    }

    public void resetInvertedIdxMap() {
        invIdxMap.clear();
    }

    public void printInvIdxMap() {
        for (String word : invIdxMap.keySet()) {
            System.out.println(word + ":");
            for (String dataSourceName : invIdxMap.get(word)) {
                System.out.println(dataSourceName);
            }
        }
    }

    public void indexDataSource(DataSource dataSrc) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(dataSrc.getInputStream()))) {
            String str;
            while ((str = in.readLine()) != null) {
                for (String wordFromStr : str.trim().split("\\W+")) {
                    wordFromStr = wordFromStr.toLowerCase();
                    if (!invIdxMap.containsKey(wordFromStr)) {
                        invIdxMap.put(wordFromStr, new HashSet<String>());
                    }
                    invIdxMap.get(wordFromStr).add(dataSrc.getID());
                }
            }
        }
    }

    public static void main(String[] args) {
        Option path = new Option("p", "path", true, "Path to folder with files");
        Option help = new Option("h", "help", false, "Help");
        path.setArgs(1);
        path.setArgName("folder path");
        Options options = new Options();
        options.addOption(path);
        options.addOption(help);
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("Inverted index", options, true);
            }
            if (commandLine.hasOption("p")) {
                File folder = new File(commandLine.getOptionValue("p"));
                if (!folder.exists()) {
                    System.out.println("Folder with this path doesn't exist.");
                } else {
                    File[] files = folder.listFiles();
                    InvertedIndex invIdx = new InvertedIndex();
                    if (files != null) {
                        for (File file : files) {
                            invIdx.indexDataSource(new FileDataSource(file.getPath()));
                        }
                        invIdx.readWords(System.in);
                    } else {
                        System.out.println("No files in folder.");
                    }
                }
            }
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error.");
        }
    }
}
