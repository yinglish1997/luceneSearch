package search;

import java.io.BufferedReader;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;
public class Test {

	/**
	 * @param args
	 */
	public static void displayTokens(Analyzer analyzer, String text) throws Exception {
        System.out.println("当前使用的分词器：" + analyzer.getClass().getName());
        //分词流，即将对象分词后所得的Token在内存中以流的方式存在，也说是说如果在取得Token必须从TokenStream中获取，而分词对象可以是文档文本，也可以是查询文本。
        TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(text));
        //表示token的首字母和尾字母在原文本中的位置。比如I'm的位置信息就是(0,3)，需要注意的是startOffset与endOffset的差值并不一定就是termText.length()，
        //因为可能term已经用stemmer或者其他过滤器处理过；
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        //这个有点特殊，它表示tokenStream中的当前token与前一个token在实际的原文本中相隔的词语数量，用于短语查询。比如： 在tokenStream中[2:a]的前一个token是[1:I'm ]，
        //它们在原文本中相隔的词语数是1，则token="a"的PositionIncrementAttribute值为1；
        PositionIncrementAttribute positionIncrementAttribute = tokenStream.addAttribute(PositionIncrementAttribute.class);
        //问题说明：这里需要使用jdk1.7,如果使用jdk1.8或者jdk1.6则会出现报错信息
        //>>如果大家谁有相应的解决方案，请提交到git上我将会合并或者添加我的QQ我们互相讨论
        CharTermAttribute charTermAttribute= tokenStream.addAttribute(CharTermAttribute.class);

        //表示token词典类别信息，默认为“Word”，比如I'm就属于<APOSTROPHE>，有撇号的类型；
        TypeAttribute typeAttribute = tokenStream.addAttribute(TypeAttribute.class);
        tokenStream.reset();

        int position = 0;
        while (tokenStream.incrementToken()) {
          int increment = positionIncrementAttribute.getPositionIncrement();
          if(increment > 0) {
            position = position + increment;
          }
          int startOffset = offsetAttribute.startOffset();
          int endOffset = offsetAttribute.endOffset();
          String term ="输出结果为："+ charTermAttribute.toString();
          System.out.println("第"+position+"个分词，分词内容是:[" + term + "]" );
        }//+ "，分词内容的开始结束位置为：(" + startOffset + "-->" + endOffset + ")，类型是：" + typeAttribute.type()
        tokenStream.close();
    }

	public static void main(String[] args) throws Exception {
        String text="中国电影是一门很值得深究的电影艺术";
        Analyzer analyzer=new IKAnalyzer();
        displayTokens(analyzer,text);
		
	}

}
