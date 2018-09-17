package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class ZWPaperInex {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void index(String paperPath, String destIndexPath) throws IOException{		
		//读取文件并建立索引
		//title  authors   local   content   keyWords
//		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44); //分词器
		Analyzer analyzer = new IKAnalyzer();
		File destIndex = new File(destIndexPath);//索引所在文件
		if(!destIndex.exists())
			destIndex.mkdirs();
		Directory directory = FSDirectory.open(destIndex);//读取本地文件	
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_44, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, config) ;//indexWriter可以对索引进行操作
		
		File file = new File(paperPath);//原数据集文件
			try{
				BufferedReader br = new BufferedReader(new FileReader(file));	
				String temp = "";
				int count = 0;
				while((temp = br.readLine()) != null){
					//提取各字段
					temp = temp.trim().replaceAll("\n", "");
					String[] arr = temp.split("\001");
					String id = arr[0];
					String title = arr[1];
					String authors = arr[2];
					String local = arr[3];
					String content = arr[4];
					String keyWords = arr[5];
					System.out.println(title);
					System.out.println(keyWords);
					/**/
					//建立索引
					//每一个文档是一个document
					Document document = new Document();
					//对document添加field
					document.add(new TextField("id", id, Store.YES));
					Field titleField = new TextField("title", title, Store.YES);
					titleField.setBoost(1.5F);
					document.add(titleField);
//					document.add(new TextField("author", authors, Store.YES));
//					document.add(new TextField("local", local, Store.YES));
					document.add(new TextField("content", content, Store.YES));
//					document.add(new TextField("keyWords", keyWords, Store.YES));				
					indexWriter.addDocument(document);
					indexWriter.commit();
					count ++;
					System.out.println(count + " finish !");
				}
				indexWriter.close();
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		
	}
	public static void searchIndex(String destIndexPath, String searchText){
		//输入字符串进行检索
		if(searchText.length() >= 4 && searchText.contains("电影"))
			searchText = searchText.replace("电影", "");
		System.out.println(searchText);
		System.out.println("search......");
		try{
			Directory directory = FSDirectory.open(new File(destIndexPath));
//			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
			Analyzer analyzer = new IKAnalyzer();
			DirectoryReader ireader = DirectoryReader.open(directory);
			IndexSearcher isearcher = new IndexSearcher(ireader);
			//title  authors   local   content   keyWords
			//可以对多个field进行搜索
//			String[] fields = {"title", "author", "local", "content", "keyWords"};
//			// 字段之间的与或非关系，MUST表示and，MUST_NOT表示not，SHOULD表示or，有几个fields就必须有几个clauses
//			BooleanClause.Occur[] clauses = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD, 
//					BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD,};
			String[] fields = {"title",  "content"};
			BooleanClause.Occur[] clauses = {BooleanClause.Occur.SHOULD, BooleanClause.Occur.SHOULD};
			Query multiFieldQuery = MultiFieldQueryParser.parse(Version.LUCENE_44, searchText, fields, clauses, analyzer);

			TopDocs topDocs = isearcher.search(multiFieldQuery,100);
//			MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_44, fields, analyzer);
//			Query q = parser.parse(searchText);
//			TopDocs topDocs = isearcher.search(q,100);
			System.out.println("return " + topDocs.totalHits + " results");
			ScoreDoc[] scoreDocs = topDocs.scoreDocs;
			for(ScoreDoc sd: scoreDocs){
				Document document = isearcher.doc(sd.doc);
				System.out.println("id: " + document.get("id") + ", score: " + sd.score);
				System.out.println("title: " + document.get("title"));
				System.out.println("content: " + document.get("content"));
				System.out.println("----------------------------------------------------------");
			}
			ireader.close();
			directory.close();
			
//			QueryParser parser = new QueryParser(Version.LUCENE_44, searchText, analyzer);
//			BooleanQuery booleanQuery = new BooleanQuery();
//			Query query = parser.parse()
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		String contentIndexPath = "/home/yingying/下载/IR-groupTask/zeroFiveToOneIndex";
		String titleContentindexPath = "/home/yingying/下载/IR-groupTask/titleContentIndex";
		//  infer: index(String paperPath, String destIndexPath) 
			index("/home/yingying/下载/IR-groupTask/cutIdData.txt", contentIndexPath);

		// infer: searchIndex(String destIndexPath, String searchText)
//		searchIndex(contentIndexPath, "分镜");
//		searchIndex(titleContentindexPath, "电影");
	}

}
