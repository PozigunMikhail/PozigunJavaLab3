package invidx;

import org.apache.commons.cli.*;

import javax.xml.crypto.Data;
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
                        invIdxMap.get(wordFromStr).sort(DataSource::compareTo);
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

    private static <T> List<T> intersectSortedLists(List<T> list1, List<T> list2, Comparator<T> cmp) {
        List<T> intersectionList = new ArrayList<>();
        if (list1.isEmpty() || list2.isEmpty()) {
            return intersectionList;
        }
        Iterator<T> it1 = list1.iterator();
        Iterator<T> it2 = list2.iterator();
        T curList1Elem = it1.next();
        T curList2Elem = it2.next();
        boolean isBothListsNotEnd = true;
        while (isBothListsNotEnd) {
            isBothListsNotEnd = false;
            if (cmp.compare(curList1Elem, curList2Elem) > 0 && it2.hasNext()) {
                curList2Elem = it2.next();
                isBothListsNotEnd = true;
            } else if (cmp.compare(curList1Elem, curList2Elem) < 0 && it1.hasNext()) {
                curList1Elem = it1.next();
                isBothListsNotEnd = true;
            } else {
                intersectionList.add(curList1Elem);
                if (it1.hasNext() && it2.hasNext()) {
                    curList1Elem = it1.next();
                    curList2Elem = it2.next();
                    isBothListsNotEnd = true;
                }
            }
        }
        return intersectionList;
    }

    public List<DataSource> getQueryResult(String[] words) {
        List<DataSource> intersectionDataSrcList = new ArrayList<>();
        if (containsAllWords(words)) {
            List<String> wordsList = new ArrayList<>(Arrays.asList(words));
            wordsList.sort((o1, o2) -> getDataSourceList(o1.toLowerCase()).size() - getDataSourceList(o2.toLowerCase()).size());
            for (String word : wordsList) {
                word = word.toLowerCase();
                if (intersectionDataSrcList.isEmpty()) {
                    intersectionDataSrcList.addAll(getDataSourceList(word));
                } else {
                    List<DataSource> curIntersList = intersectSortedLists(intersectionDataSrcList, getDataSourceList(word), DataSource::compareTo);
                    intersectionDataSrcList.clear();
                    intersectionDataSrcList.addAll(curIntersList);
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
