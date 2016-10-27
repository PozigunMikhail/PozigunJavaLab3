package inv_idx;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class InvertedIndexTest extends InvertedIndex {
    private static Set<StringDataSource> dSrcSet = new HashSet<>();

    private Map<String, HashSet<String>> getInvIdxMap() {
        return invIdxMap;
    }

    private void checkWrongWordsInInvIdxMap() throws Exception {
        for (String word : invIdxMap.keySet()) {
            boolean isAtLeastInOneSrc = false;
            for (StringDataSource dSrc : dSrcSet) {
                for (String wordFromStr : dSrc.getID().trim().split("\\W+")) {
                    if (word.equals(wordFromStr.toLowerCase())) {
                        isAtLeastInOneSrc = true;
                        break;
                    }
                }
                if (isAtLeastInOneSrc) {
                    break;
                }
            }
            assertTrue(isAtLeastInOneSrc);
        }
    }

    private void checkWrongDataSourcesInInvIdxMap() throws Exception {
        for (String word : invIdxMap.keySet()) {
            for (String str : invIdxMap.get(word)) {
                boolean isInDSrcSet = false;
                for (StringDataSource dSrc : dSrcSet) {
                    if (dSrc.getID().equals(str)) {
                        isInDSrcSet = true;
                        break;
                    }
                }
                assertTrue(isInDSrcSet);
            }
        }
    }

    private void checkIsAllDataSourcesIndexed() throws Exception {
        for (StringDataSource dSrc : dSrcSet) {
            for (String wordFromStr : dSrc.getID().trim().split("\\W+")) {
                wordFromStr = wordFromStr.toLowerCase();
                assertTrue(invIdxMap.containsKey(wordFromStr));
                assertTrue(invIdxMap.get(wordFromStr).contains(dSrc.getID()));
            }
        }
    }

    private void checkInvertedIdxMap() throws Exception {
        checkIsAllDataSourcesIndexed();
        checkWrongDataSourcesInInvIdxMap();
        checkWrongWordsInInvIdxMap();
    }

    @Test
    public void indexStringDataSourcesTest() throws Exception {
        InvertedIndexTest invIdx = new InvertedIndexTest();
        dSrcSet.add(new StringDataSource("data structure is a central component of a typical search " +
                "of the query: find the documents where word X occurs. Once a forward index is developed, which " +
                "stores lists of words per document, it is next inverted to develop an inverted index. Querying "));
        dSrcSet.add(new StringDataSource("are not always technically realistic. Instead of listing the words" +
                " per document in the forward index, the inverted index data structure is developed which lists the " +
                "documents per word."));
        dSrcSet.add(new StringDataSource(" data structure that improves the speed of data retrieval operations " +
                "on a database table at the cost of additional writes and storage space to maintain the index data structure." +
                " a database table is accessed. Indices can be created using one or more columns of a database table, providing " +
                "the basis for both"));
        dSrcSet.add(new StringDataSource(" There are many different data structures used for this purpose. " +
                "There are complex design trade-offs "));
        dSrcSet.add(new StringDataSource("   complex constraints, like ensuring that no overlapping time ranges or " +
                "no intersecting geometry objects would be stored in the table. An index supporting fast searching for records " +
                "satisfying the predicate is required to police such a"));
        for (StringDataSource dSrc : dSrcSet) {
            invIdx.indexDataSource(dSrc);
        }
        assertTrue(!invIdx.getInvIdxMap().isEmpty());
        invIdx.checkInvertedIdxMap();
        invIdx.resetInvertedIdxMap();
        dSrcSet.clear();
    }

    @Test
    public void indexStringEmptyDataSourcesTest() throws Exception {
        InvertedIndexTest invIdx = new InvertedIndexTest();
        for (int i = 0; i < 2; i++) {
            invIdx.indexDataSource(new StringDataSource(""));
        }
        assertTrue(invIdx.getInvIdxMap().isEmpty());
    }

    @Test
    public void indexStringDataSourcesWithSameWordsTest() throws Exception {
        InvertedIndexTest invIdx = new InvertedIndexTest();
        dSrcSet.add(new StringDataSource("Hello, world!"));
        dSrcSet.add(new StringDataSource("Hello,  World!"));
        dSrcSet.add(new StringDataSource("   HELLO  , WORLD."));
        for (StringDataSource dSrc : dSrcSet) {
            invIdx.indexDataSource(dSrc);
        }
        assertTrue(!invIdx.getInvIdxMap().isEmpty());
        invIdx.checkInvertedIdxMap();
        invIdx.resetInvertedIdxMap();
        dSrcSet.clear();
    }
}