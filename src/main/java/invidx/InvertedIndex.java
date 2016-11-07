package invidx;

import org.apache.commons.cli.*;

import java.io.*;
import java.util.*;

public class InvertedIndex {
    private Map<String, List<DataSource>> invIdxMap = new HashMap<>();

    public void indexDataSource(DataSource dataSrc) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(dataSrc.getInputStream()))) {
            String str;
            while ((str = in.readLine()) != null) {
                for (String wordFromStr : str.trim().split("\\W+")) {
                    wordFromStr = wordFromStr.toLowerCase();
                    invIdxMap.putIfAbsent(wordFromStr, new ArrayList<>());
                    if (!invIdxMap.get(wordFromStr).contains(dataSrc)) {
                        invIdxMap.get(wordFromStr).add(dataSrc);
                    }
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

    public List<DataSource> getDataSourceList(String word) {
        return !invIdxMap.containsKey(word) ? null : Collections.unmodifiableList(invIdxMap.get(word));
    }

    private static <T> void intersectSortedLists(List<T> list1, List<T> list2, Comparator<T> cmp) {
        List<T> intersectionList = new ArrayList<>();
        int idx1 = 0;
        int idx2 = 0;
        while (idx1 != list1.size() && idx2 != list2.size()) {
            if (cmp.compare(list1.get(idx1), list2.get(idx2)) > 0) {
                idx2++;
            } else if (cmp.compare(list1.get(idx1), list2.get(idx2)) < 0) {
                idx1++;
            } else {
                intersectionList.add(list1.get(idx1));
                idx1++;
                idx2++;
            }
        }
        list1.clear();
        list1.addAll(intersectionList);
    }

    public List<DataSource> getQueryResult(String[] words) {
        List<DataSource> intersectionDataSrcList = new ArrayList<>();
        if (containsAllWords(words)) {
            List<String> wordsList = new ArrayList<>(Arrays.asList(words));
            wordsList.sort((o1, o2) -> getDataSourceList(o1.toLowerCase()).size() - getDataSourceList(o2.toLowerCase()).size());
            Comparator<DataSource> cmp = (o1, o2) -> o1.getId().compareTo(o2.getId());
            for (String word : wordsList) {
                word = word.toLowerCase();
                invIdxMap.get(word).sort(cmp);
                if (intersectionDataSrcList.isEmpty()) {
                    intersectionDataSrcList.addAll(invIdxMap.get(word));
                } else {
                    intersectSortedLists(intersectionDataSrcList, invIdxMap.get(word), cmp);
                    if (intersectionDataSrcList.isEmpty()) {
                        break;
                    }
                }
            }
        }
        return intersectionDataSrcList;
    }

    private void queryProcessing(InputStream inStream) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inStream))) {
            String str;
            while ((str = in.readLine()) != null) {
                List<DataSource> queryResult = getQueryResult(str.trim().split("\\W+"));
                if (queryResult.isEmpty()) {
                    System.out.println("Not found in any data source.");
                } else {
                    for (DataSource dataSource : queryResult) {
                        System.out.println(dataSource.getId());
                    }
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
