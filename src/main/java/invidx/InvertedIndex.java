package invidx;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class InvertedIndex {
    private Map<String, Set<String>> invIdxMap = new HashMap<>();

    public void indexDataSource(DataSource dataSrc) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(dataSrc.getInputStream()))) {
            String str;
            while ((str = in.readLine()) != null) {
                for (String wordFromStr : str.trim().split("\\W+")) {
                    wordFromStr = wordFromStr.toLowerCase();
                    if (!invIdxMap.containsKey(wordFromStr)) {
                        invIdxMap.put(wordFromStr, new HashSet<String>());
                    }
                    invIdxMap.get(wordFromStr).add(dataSrc.getId());
                }
            }
        }
    }

    public void clearInvertedIdxMap() {
        invIdxMap.clear();
    }

    public boolean containsWord(String word) {
        return invIdxMap.containsKey(word);
    }

    public boolean containsAllWords(String[] words) {
        for (String word : words) {
            word = word.toLowerCase();
            if (!containsWord(word)) {
                return false;
            }
        }
        return true;
    }

    public Set<String> getDataSourceSet(String word) {
        return invIdxMap.get(word) == null ? null : new HashSet<>(invIdxMap.get(word));
    }

    public Set<String> getQueryResult(String[] words) {
        Set<String> intersectionDataSrcSet = new HashSet<>();
        if (containsAllWords(words)) {
            for (String word : words) {
                word = word.toLowerCase();
                if (intersectionDataSrcSet.isEmpty()) {
                    intersectionDataSrcSet.addAll(getDataSourceSet(word));
                } else {
                    intersectionDataSrcSet.retainAll(getDataSourceSet(word));
                }
            }
        }
        return intersectionDataSrcSet;
    }

    private void printQueryResult(Set<String> dataSourceIdSet) {
        if (dataSourceIdSet.isEmpty()) {
            System.out.println("Not found in any data source.");
        } else {
            for (String dataSourceId : dataSourceIdSet) {
                System.out.println(dataSourceId);
            }
        }
    }

    private void queryProcessing(InputStream inStream) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inStream))) {
            String str;
            while ((str = in.readLine()) != null) {
                printQueryResult(getQueryResult(str.trim().split("\\W+")));
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
                    if (files != null) {
                        InvertedIndex invIdx = new InvertedIndex();
                        for (File file : files) {
                            invIdx.indexDataSource(new FileDataSource(file.getPath()));
                        }
                        invIdx.queryProcessing(System.in);
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
