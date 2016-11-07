package invidx;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class InvertedIndexTest {
    private static List<StringDataSource> dSrcList1 = new ArrayList<>();
    private static List<StringDataSource> dSrcList2 = new ArrayList<>();
    private static List<StringDataSource> dSrcList3 = new ArrayList<>();
    private static InvertedIndex invIdx1 = new InvertedIndex();
    private static InvertedIndex invIdx2 = new InvertedIndex();
    private static InvertedIndex invIdx3 = new InvertedIndex();

    private boolean isEqualElementsInDataSrcLists(List<DataSource> list1, List<DataSource> list2) {
        return list1.containsAll(list2) && list2.containsAll(list1);
    }

    private void checkIsAllDataSourcesIndexed(InvertedIndex invIdx, List<StringDataSource> dSrcList) throws Exception {
        for (StringDataSource dSrc : dSrcList) {
            for (String wordFromStr : dSrc.getId().trim().split("\\W+")) {
                wordFromStr = wordFromStr.toLowerCase();
                assertTrue(invIdx.containsWord(wordFromStr));
                assertTrue(invIdx.getDataSourceList(wordFromStr).contains(dSrc));
            }
        }
    }

    private void indexStringDataSourceList(InvertedIndex invIdx, List<StringDataSource> dSrcList) throws Exception {
        for (StringDataSource dSrc : dSrcList) {
            invIdx.indexDataSource(dSrc);
        }
    }

    @Before
    public void initInvIdxAndDataSrcList1() throws Exception {
        dSrcList1.add(new StringDataSource("data structure is a central component of a typical search " +
                "of the query: find the documents where word X occurs. Once a forward index is developed, which " +
                "stores lists of words per document, it is next inverted to develop an inverted index. Querying "));
        dSrcList1.add(new StringDataSource("are not always technically realistic. Instead of listing the words" +
                " per document in the forward index, the inverted index data structure is developed which lists the " +
                "documents per word."));
        dSrcList1.add(new StringDataSource(" data structure that improves the speed of data retrieval operations " +
                "on a database table at the cost of additional writes and storage space to maintain the index data structure." +
                " a database table is accessed. Indices can be created using one or more columns of a database table, providing " +
                "the basis for both"));
        dSrcList1.add(new StringDataSource(" There are many different data structures used for this purpose. " +
                "There are complex design trade-offs "));
        dSrcList1.add(new StringDataSource("   complex constraints, like ensuring that no overlapping time ranges or " +
                "no intersecting geometry objects would be stored in the table. An index supporting fast searching for records " +
                "satisfying the predicate is required to police such a"));
        indexStringDataSourceList(invIdx1, dSrcList1);
    }

    @Before
    public void initInvIdxAndDataSrcList2() throws Exception {
        dSrcList2.add(new StringDataSource("Hello, world!"));
        dSrcList2.add(new StringDataSource("Hello,  World!"));
        dSrcList2.add(new StringDataSource("   HELLO  , WORLD."));
        indexStringDataSourceList(invIdx2, dSrcList2);
    }

    @Before
    public void initInvIdxAndDataSrcList3() throws Exception {
        dSrcList3.add(new StringDataSource(" we can define the distance between two vectors u and v as u−v. This turns " +
                "the seminormed space into a pseudometric space (notice this is weaker than a metric) and allows the" +
                " definition of notions such as "));
        dSrcList3.add(new StringDataSource("between the normed vector spaces V and W is called an isometric isomorphism, " +
                "and V and W are called isometrically isomorphic. Isometrically isomorphic" +
                " normed vector spaces are identical for "));
        dSrcList3.add(new StringDataSource(" normed vector space is a vector space on which a norm is defined. In a vector" +
                " space with 2- or 3-dimensional vectors with real-valued entries, the idea of the length of a vector" +
                " is intuitive. This intuition can easily be extended to any real vector"));
        dSrcList3.add(new StringDataSource(" distance between two points as the length of the straight line segment connecting" +
                " them. Other metric spaces occur for example in elliptic geometry and hyperbolic geometry, where distance on" +
                " a sphere measured by angle "));
        indexStringDataSourceList(invIdx3, dSrcList3);
    }

    @Test
    public void indexingInvIdx1Test() throws Exception {
        checkIsAllDataSourcesIndexed(invIdx1, dSrcList1);
    }

    @Test
    public void queryResultInvIdx1Test1() throws Exception {
        List<DataSource> list = new ArrayList<>();
        list.add(dSrcList1.get(2));
        list.add(dSrcList1.get(4));
        assertTrue(isEqualElementsInDataSrcLists(invIdx1.getQueryResult("Index the for or".trim().split("\\W+")), list));
    }

    @Test
    public void queryResultInvIdx1Test2() throws Exception {
        List<DataSource> list = new ArrayList<>();
        list.add(dSrcList1.get(0));
        list.add(dSrcList1.get(1));
        assertTrue(isEqualElementsInDataSrcLists(invIdx1.getQueryResult("Structure lists forward".trim().split("\\W+")), list));
    }

    @Test
    public void queryResultInvIdx1Test3() throws Exception {
        List<DataSource> list = new ArrayList<>();
        list.add(dSrcList1.get(1));
        assertTrue(isEqualElementsInDataSrcLists(invIdx1.getQueryResult("words document is listing".trim().split("\\W+")), list));
    }

    @Test
    public void indexingInvIdx2Test() throws Exception {
        checkIsAllDataSourcesIndexed(invIdx2, dSrcList2);
    }

    @Test
    public void queryResultInvIdx2Test() throws Exception {
        List<DataSource> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add(dSrcList2.get(i));
        }
        assertTrue(isEqualElementsInDataSrcLists(invIdx2.getQueryResult("Hello world.".trim().split("\\W+")), list));
    }

    @Test
    public void indexingInvIdx3Test() throws Exception {
        checkIsAllDataSourcesIndexed(invIdx3, dSrcList3);
    }

    @Test
    public void queryResultInvIdx13Test1() throws Exception {
        List<DataSource> list = new ArrayList<>();
        list.add(dSrcList3.get(0));
        list.add(dSrcList3.get(2));
        assertTrue(isEqualElementsInDataSrcLists(invIdx3.getQueryResult("Space this of a".trim().split("\\W+")), list));
    }

    @Test
    public void queryResultInvIdx3Test2() throws Exception {
        List<DataSource> list = new ArrayList<>();
        list.add(dSrcList3.get(1));
        list.add(dSrcList3.get(3));
        assertTrue(isEqualElementsInDataSrcLists(invIdx3.getQueryResult("The between for and".trim().split("\\W+")), list));
    }

    @Test
    public void queryResultInvIdx3Test3() throws Exception {
        List<DataSource> list = new ArrayList<>();
        list.add(dSrcList3.get(3));
        assertTrue(isEqualElementsInDataSrcLists(invIdx3.getQueryResult("distance between geometry".trim().split("\\W+")), list));
    }

    @Test
    public void queryResultInvIdx3Test4() throws Exception {
        List<DataSource> list = new ArrayList<>();
        assertTrue(isEqualElementsInDataSrcLists(invIdx3.getQueryResult("hello".trim().split("\\W+")), list));
    }
}