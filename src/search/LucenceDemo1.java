package search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LucenceDemo1 {

	/**
	 * @param args
	 */
	
	private static String fileDir = "/home/yingying/下载/materials";//TXT文件
	private static String indexDir = "index";//索引所在目录
	
	public static void index(){		
		//读取文件并建立索引
		File[] files = new File(fileDir).listFiles();
		for(File txtFile: files){
			//读取一个文件中内容
			String[] arr = txtFile.getAbsolutePath().split("/");
			String id = arr[arr.length - 1];
//			String newsGroup = "";
//			String id = "";
//			String source = "";
//			String subject = "";
			String content = "";
			try{
				BufferedReader br = new BufferedReader(new FileReader(txtFile));	
//				newsGroup = br.readLine();
//				System.out.println(newsGroup);
//				id = br.readLine();
//				System.out.println(id);
//				source = br.readLine();
//				System.out.println(source);
//				subject = br.readLine();
//				System.out.println(subject);
				
				String temp = "";
				while((temp = br.readLine()) != null){
					content += temp;
				}
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			//建立索引
			try{
				Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
				File storeIndex = new File(indexDir);
				Directory directory = FSDirectory.open(storeIndex);
				
				if(!storeIndex.exists())
					storeIndex.mkdirs();
				
				IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
				IndexWriter indexWriter = new IndexWriter(directory, config);
				
				Document document = new Document();
				document.add(new TextField("id", id, Store.YES));
//				document.add(new TextField("newsGroup", newsGroup, Store.YES));
//				document.add(new TextField("source", source, Store.YES));
//				document.add(new TextField("subject", subject, Store.YES));
				document.add(new TextField("content", content, Store.YES));
				indexWriter.addDocument(document);
				indexWriter.commit();
				indexWriter.close();
				System.out.println(id + " finish !");
			}catch(Exception e){
				e.printStackTrace();		
			}
		}
		System.out.println("finish all !");
	}

	public static void searchIndex(String text){
		//输入字符串进行检索
		try{
			Directory directory = FSDirectory.open(new File(indexDir));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT);
			DirectoryReader ireader = DirectoryReader.open(directory);
			IndexSearcher isearcher = new IndexSearcher(ireader);
			
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "content", analyzer);
			Query query = parser.parse(text);
			
			ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
			System.out.println("searching....");
			for(int i = 0; i < hits.length; i ++){
				Document hitDoc = isearcher.doc(hits[i].doc);
//				System.out.println("*****************************");
				System.out.println(hitDoc.get("id"));
//				System.out.println(hitDoc.get("newsGroup"));
//				System.out.println(hitDoc.get("subject"));
//				System.out.println(hitDoc.get("content"));
//				System.out.println("*****************************");
			}
			ireader.close();
			directory.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		searchIndex("PRESENTATIONS");
	}

}
