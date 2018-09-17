//package search;
//
///*
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.index.AtomicReaderContext;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryparser.classic.QueryParser;
////import org.apache.lucene.queries.CustomScoreProvider;
////import org.apache.lucene.queries.CustomScoreQuery;
//import org.apache.lucene.search.FieldCache;
//import org.apache.lucene.search.FieldCache.Longs;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.Sort;
//import org.apache.lucene.search.TermQuery;
//import org.apache.lucene.search.TopDocs;
//
////import com.johnny.lucene04.advance_search.FileIndexUtils;
//
///**
// * 自定义评分第一种写法
// * （1）创建类并继承CustomScoreQuery
//    （2）覆盖重写类中的getCusomScoreProvider方法
//    （3）创建类并继承CustomScoreProvider
//    （4）覆盖重写类中的customScore确定新的评分规则
// * @author Johnny
// *
// */
//public class MySelfScore {
//    public void searchBySelfScore(){
//        try{
//            IndexSearcher search = new IndexSearcher(DirectoryReader.open(FileIndexUtils.getDirectory()));
//            Query q = new TermQuery(new Term("content","java"));
//            MyCustomScoreQuery myQuery = new MyCustomScoreQuery(q);
//            TopDocs tds = search.search(myQuery, 200);
//            
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            
//            for(ScoreDoc sd:tds.scoreDocs){
//                Document d = search.doc(sd.doc);
//                System.out.println(sd.doc+":("+sd.score+")" +
//                        "["+d.get("filename")+"【"+d.get("path")+"】--->"+
//                        d.get("size")+"-----"+sdf.format(new Date(Long.valueOf(d.get("date"))))+"]");
//
//            }
//            System.out.println("-----------Total result:"+tds.scoreDocs.length);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//    /**
//     *重写评分的实现方式
//     * **/
//    private class MyScoreProvider extends CustomScoreProvider{
//        private AtomicReaderContext context;
//        public MyScoreProvider(AtomicReaderContext context) {
//            super(context);
//            this.context = context;
//        }
//        /**重写评分方法，假定需求为文档size大于1000的评分/1000**/
//        @Override
//        public float customScore(int doc, float subQueryScore, float valSrcScore)
//                throws IOException {
//            // 从域缓存中加载索引字段信息
//            Longs longs= FieldCache.DEFAULT.getLongs(context.reader(), "size", false);
//            //doc实际上就是Lucene中得docId
//            long size = longs.get(doc);
//            float ff = 1f;//判断加权
//            if(size>1000){
//                ff = 1f/1000;
//            }
//            /*
//             * 通过得分相乘放大分数
//             * 此处可以控制与原有得分结合的方式，加减乘除都可以
//             * **/
//            return subQueryScore*valSrcScore*ff;
//        }
//    }
//    /**
//     * 重写CustomScoreQuery 的getCustomScoreProvider方法
//     * 引用自定义的Provider
//     */
//    private class MyCustomScoreQuery extends CustomScoreQuery{
//
//        public MyCustomScoreQuery(Query subQuery) {
//            super(subQuery);
//        }
//        @Override
//        protected CustomScoreProvider getCustomScoreProvider(
//                AtomicReaderContext context) throws IOException {
//            /**注册使用自定义的评分实现方式**/
//            return new MyScoreProvider(context);
//        }
//    }
//}
//
////按照评分进行排序：
//
//public class ScoreSearch {
//    /**
//     * 按照评分进行排序
//     */
//    public void searchByScore(String queryStr,Sort sort){
//        try{
//            IndexSearcher search = new IndexSearcher(DirectoryReader.open(FileIndexUtils.getDirectory()));
//            QueryParser qp = new QueryParser(null, "content", new StandardAnalyzer(null));
//            Query q = qp.parse(queryStr);
//            TopDocs tds = null;
//            if(sort!=null){
//                tds = search.search(q, 200,sort);
//            }else{
//                tds = search.search(q, 200);
//            }
//            for(ScoreDoc sd :tds.scoreDocs){
//                Document d = search.doc(sd.doc);
//                System.out.println(sd.doc+":("+sd.score+")" +
//                        "["+d.get("filename")+"【"+d.get("path")+"】---"+d.get("score")+"--->"+
//                        d.get("size")+"]");
//            }
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//
////测试方法如下：
//
//@Test
//    public void testSearchByScore(){
//        ScoreSearch ss = new ScoreSearch();
//        /**sort没有set sort时，默认按照关联性进行排序**/
//        Sort sort = new Sort();
//        //sort.setSort(new SortField("score", Type.INT,true));//true表示倒叙，默认和false表示正序
//        sort.setSort(new SortField("size", Type.LONG),new SortField("score",Type.INT));
//        ss.searchByScore("java",sort);
//    }
//    
//    @Test
//    public void testSelfScore(){
//        MySelfScore mss = new MySelfScore();
//        /**
//         * 如果使用标准分词器，那么java类中得java.io.IOException会被认为是一个词，不会进行拆分
//         */
//        mss.searchBySelfScore();
//    }
//    */
//    **/