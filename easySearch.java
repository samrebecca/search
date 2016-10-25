import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class easySearch {
	public static void main(String[] args) throws ParseException, IOException {
		String path="/Users/rebeccasam/Documents/Search/Assignment2/index";
		Analyzer analyzer = new StandardAnalyzer();
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(path)));
		IndexSearcher searcher = new IndexSearcher(reader);
		String queryString = "New York";
		QueryParser parser = new QueryParser("TEXT", analyzer);
		Query query = parser.parse(queryString);
		Set<Term> queryTerms = new LinkedHashSet<Term>();
		searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
		int N=reader.maxDoc();
		HashMap<String, Float> score= new HashMap<String, Float>();
		float TFIDF=0;
		for(Term t: queryTerms)
		{
//			System.out.println(t.text());
			int df = reader.docFreq(new Term("TEXT", t.text()));
//			System.out.println(df);
			float idf= (float) Math.log(1+(N/df));
			ClassicSimilarity dSimi = new ClassicSimilarity();
			List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
			float sumTf=0;
			for (int i = 0; i < leafContexts.size(); i++) {
				LeafReaderContext leafContext = leafContexts.get(i);
				int startDocNo = leafContext.docBase;
				int numberOfDoc = leafContext.reader().maxDoc();
//				System.out.println(numberOfDoc);
				PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(), "TEXT", new BytesRef(t.text()));
					if (de != null) {
						while ((de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
//							float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
//							float docLeng = 1 / (normDocLeng * normDocLeng);
							float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(de.docID()));
							float docLeng = 1 / (normDocLeng * normDocLeng);
							float tf= de.freq()/docLeng;
							float termTfIdf= tf*idf;
//							System.out.println(termTfIdf);
							String key=searcher.doc(de.docID() + startDocNo).get("DOCNO");
//							String key=Integer.toString(de.docID()+ startDocNo);
							if(score.containsKey(key))
							{
//								float value= score.get(key);
//								value=value+termTfIdf;
								score.put(key,score.get(key)+termTfIdf);
							}
							else
							{
							score.put(key,termTfIdf);
							}
//							System.out.println(t.text()+" occurs " + de.freq() + " time(s) in doc(" + (de.docID() + startDocNo) + ")");
						}
					}
				}
			}
		
		for (Map.Entry<String, Float> val: score.entrySet() ) {
		      System.out.println("Doc ID is: "+val.getKey()+"  Relevance Score: "+ val.getValue());
		   }
	}
}