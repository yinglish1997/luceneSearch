package search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;

public class HanlpCutContent {

	/**
	 * @param args
	 */
	public static ArrayList<String> cutSentences(String originalPath){
		File file = new File(originalPath);
		ArrayList<String> list = new ArrayList<String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp = "";
			int count = 0;
			
			while((temp = br.readLine()) != null){
				String cut = count + "\001";
				temp = temp.trim().replaceAll("\r", "").replaceAll("\n", "");
				String[] arr = temp.split("\\t");
				String title = arr[0];
				String author = arr[1];
				String local = arr[2];
				String content = arr[3];
				
				//cut title	
				byte[] bt = title.getBytes();// str
				InputStream ip = new ByteArrayInputStream(bt);
				Reader read = new InputStreamReader(ip);
				IKSegmenter iks = new IKSegmenter(read, true);
				Lexeme t;
				while ((t = iks.next()) != null) {
					cut += t.getLexemeText() + " ";
					}
//				List<Term> titleList = HanLP.segment(title);
//				for(Term t: titleList)
//					cut += t.word + " ";
				cut += "\001";
				
				//split authors
				String[] authors = author.split(";");
				for(String s: authors)
					cut += s + " ";
				cut += "\001";
				//local
				cut += local + "\001";
				//cut content
//				List<Term> termList = HanLP.segment(content);
//				for(Term t: termList)
//					cut += t.word + " ";
				bt = content.getBytes();// str
			   ip = new ByteArrayInputStream(bt);
				read = new InputStreamReader(ip);
				iks = new IKSegmenter(read, true);
				while ((t = iks.next()) != null) {
					cut += t.getLexemeText() + " ";
					}
				
				cut += "\001";
				//split keyWords
				for(int i = 4; i < arr.length; i ++)
					cut += arr[i] + " ";
				System.out.println("cut: " + cut);
				list.add(cut);
				count ++;
				System.out.println(cut);
			}
			br.close();
			System.out.println("finish read");
		}catch(IOException e){
			e.printStackTrace();
		}
		return list;
	}
	public static void write(String destPath, ArrayList<String> cutSentences){
		File file = new File(destPath);
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			int i = 0;
			for( ; i < cutSentences.size() - 1; i ++)
				bw.write(cutSentences.get(i) + "\n");
			bw.write(cutSentences.get(i));
			bw.close();
			System.out.println("finish wirte");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		ArrayList<String> cut = cutSentences("/home/yingying/下载/IR-groupTask/result.txt");
		write("/home/yingying/下载/IR-groupTask/cutIdData.txt", cut);

	}

}
