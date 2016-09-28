
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;


public class indexComparison {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("---Using Keyword Analyzer---");
		generateIndex keyword_an= new generateIndex();
		keyword_an.createIndex(new KeywordAnalyzer());
		
		System.out.println("---Using Simple Analyzer---");
		generateIndex simple_an= new generateIndex();
		simple_an.createIndex(new SimpleAnalyzer());
	   
		System.out.println("---Using Stop Analyzer---");
		generateIndex stop_an= new generateIndex();
		stop_an.createIndex(new StopAnalyzer());
		
		System.out.println("---Using Standard Analyzer---");
		generateIndex standard_an= new generateIndex();
		standard_an.createIndex(new StandardAnalyzer());
	}

}
