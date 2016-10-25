import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.commons.lang3.StringUtils;


class ValueComparator<K, V extends Comparable<V>> implements Comparator<K>{
	 
	HashMap<K, V> map = new HashMap<K, V>();
 
	public ValueComparator(HashMap<K, V> map){
		this.map.putAll(map);
	}
 
	@Override
	public int compare(K s1, K s2) {
		return -map.get(s1).compareTo(map.get(s2));//descending order	
	}
}
public class searchTRECtopics {
		public static void main(String[] args) throws ParseException, IOException {
			PrintWriter f0 = new PrintWriter(new FileWriter("/Users/rebeccasam/Documents/Search/Assignment2/output.txt"));
			String indexpath="/Users/rebeccasam/Documents/Search/Assignment2/index";
			String datapath="/Users/rebeccasam/Documents/Search/Assignment2/topics.51-100";
			String content=new String(Files.readAllBytes(Paths.get(datapath)),StandardCharsets.UTF_8);
			String subDocs[] = content.split("<top>");
			int count=1;
			int counter=51;
			for(int i=1;i<subDocs.length;i++)
			{
					   String currentDoc=subDocs[i];
					   String title="";
					   String desc="";
					   if(currentDoc.contains("<title>"))
					   {
						   int countTitle=StringUtils.countMatches(currentDoc, "<title>");
						   
						   if(countTitle>1)
						   {   
							   
							   for (int i1 = -1; (i1 = currentDoc.indexOf("<title>", i1 + 1)) != -1; ) 
							   {
								   
								   title+= currentDoc.substring(i1+"<title>".length() ,currentDoc.indexOf("<desc>", i1)) ;
								  
							   }
						   }
						   else
						   {
							   title=currentDoc.substring(currentDoc.indexOf("<title>"),currentDoc.indexOf("<desc>")).replace("<title>","").replace("Topic:","") ;
						   }
//						   System.out.println("Title is: " +title);
						   
				   }
					   //Get DESC
					   if(currentDoc.contains("<desc>"))
					   {
						   int countdesc=StringUtils.countMatches(currentDoc, "<desc>");
						   
						   if(countdesc>1)
						   {   
							   
							   for (int i2 = -1; (i2 = currentDoc.indexOf("<desc>", i2 + 1)) != -1; ) 
							   {
								   desc+= currentDoc.substring(i2+"<desc>".length() ,currentDoc.indexOf("<smry>",i2)) ;
							   }
						   }  
						   else
						   {
							   desc=currentDoc.substring(currentDoc.indexOf("<desc>"),currentDoc.indexOf("<smry>")).replace("<desc>","").replace("Description:","") ;
						   }
					   }
						   String queryString=title.replace("\n", "");
//						   String queryString=desc.replace("\n", "");
//						   System.out.println(count +" "+queryString);
						   count++;
						   Analyzer analyzer = new StandardAnalyzer();
							IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexpath)));
							IndexSearcher searcher = new IndexSearcher(reader);
						    QueryParser parser = new QueryParser("TEXT", analyzer);			    
							Query query = parser.parse(QueryParser.escape(queryString));
							Set<Term> queryTerms = new LinkedHashSet<Term>();
							searcher.createNormalizedWeight(query, false).extractTerms(queryTerms);
							int N=reader.maxDoc();
							HashMap<String, Float> score= new HashMap<String, Float>();
							float TFIDF=0;
							for(Term t: queryTerms)
							{
//								System.out.println(t.text());
								int df = reader.docFreq(new Term("TEXT", t.text()));
//								System.out.println(df);
								if(df!=0)
								{
								float idf= (float) Math.log(1+(N/df));
								ClassicSimilarity dSimi = new ClassicSimilarity();
								List<LeafReaderContext> leafContexts = reader.getContext().reader().leaves();
								float sumTf=0;
								for (int i1 = 0; i1 < leafContexts.size(); i1++) {
									LeafReaderContext leafContext = leafContexts.get(i1);
									int startDocNo = leafContext.docBase;
									int numberOfDoc = leafContext.reader().maxDoc();
//									System.out.println(numberOfDoc);
									PostingsEnum de = MultiFields.getTermDocsEnum(leafContext.reader(), "TEXT", new BytesRef(t.text()));
										if (de != null) {
											while ((de.nextDoc()) != PostingsEnum.NO_MORE_DOCS) {
//												float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(docId));
//												float docLeng = 1 / (normDocLeng * normDocLeng);
												float normDocLeng = dSimi.decodeNormValue(leafContext.reader().getNormValues("TEXT").get(de.docID()));
												float docLeng = 1 / (normDocLeng * normDocLeng);
												float tf= de.freq()/docLeng;
												float termTfIdf= tf*idf;
//												System.out.println(termTfIdf);
												String key=searcher.doc(de.docID() + startDocNo).get("DOCNO");
//												String key=Integer.toString(de.docID()+ startDocNo);
												if(score.containsKey(key))
												{
//													float value= score.get(key);
//													value=value+termTfIdf;
													score.put(key,score.get(key)+termTfIdf);
												}
												else
												{
												score.put(key,termTfIdf);
												}
//												System.out.println(t.text()+" occurs " + de.freq() + " time(s) in doc(" + (de.docID() + startDocNo) + ")");
											}
										}
									}
								}
							
								
					   }
							Comparator<String> comparator = new ValueComparator<String, Float>(score);
							TreeMap<String, Float> result = new TreeMap<String, Float>(comparator);
							result.putAll(score);
							int count1=0;
						for (Map.Entry<String, Float> val: result.entrySet() ) {
							count1++;
//							      System.out.println(counter+" "+"0"+" "+val.getKey()+" "+count1+" "+ val.getValue()+" "+"TFIDF-ShortQuery");
							      f0.println(counter+" "+"0"+" "+val.getKey()+" "+count1+" "+ val.getValue()+" "+"TFIDF-ShortQuery");
							      if(count1==1000)
							    	  break;
							   }counter++;
					  
				}
			f0.close();
			}			   
					   
	}
	

