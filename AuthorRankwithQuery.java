import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;

	public class AuthorRankwithQuery {
	


		public static void main(String args[]) throws Exception
		{	
			
			HashMap<String, String> id_label =new HashMap<String,String>();
			
			BufferedReader br = new BufferedReader(new FileReader("/Users/rebeccasam/Documents/Search/assignment3/assignment3/author.net"));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			//Skipping the first line since it contains info of vertices
			line=br.readLine();
			int count=0;
			HashMap<String, String> label_id =new HashMap<String,String>();
			//Iterating through all 2000 vertex and storing the node ID and label in HashMap
			while (count <2000) 
			{
				sb.append(line);
				sb.append(System.lineSeparator());
				//System.out.println(line);
				String s[]=line.split(" ");
				int start=s[1].indexOf('"');
				int end=s[1].lastIndexOf('"');
				//System.out.println(start+" "+end);
				String id=s[1].substring(start+1, end);
				String label=s[0];
				id_label.put(id, label);
				label_id.put(label, id);
				line = br.readLine();
				count++;
			}

			br.close();
			String indexpath="/Users/rebeccasam/Documents/Search/assignment3/assignment3/author_index/";
			IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(indexpath)));
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.setSimilarity(new BM25Similarity());
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("content", analyzer);
			int temp=0;
			while(temp<2)
			{	
				String queryString=null;
				if(temp==0)
				{
					queryString="Data Mining";
					System.out.println("For Data Mining the Top Ten Authors are:");
				}
				if(temp==1)
				{
					queryString="Information Retrieval";
					System.out.println("For Information Retrieval the Top Ten Authors are:");
				}
				temp++;
				HashMap<String, Double> priors= new HashMap<String, Double>();
				Query query = parser.parse(QueryParser.escape(queryString));
				TopDocs collector = searcher.search(query, 300);
				ScoreDoc[] docs = collector.scoreDocs;
				Double total=0.0;
				Double old=0.0;
				for (int i1 = 0; i1 < docs.length; i1++)
				{
					Document doc = searcher.doc(docs[i1].doc); 
					String key=doc.get("authorid");
					Double value=(double) docs[i1].score;
					if(priors.containsKey(key))
					{
						old=(double)priors.get(key);
						value+=old;
					}
					priors.put(key, value);
					
					total+=(double)docs[i1].score;
					int docId = docs[i1].doc;
					Document d = searcher.doc(docId);

					//System.out.println("Rank "+(i1 + 1) + "> \n\t Doc ID:" +docId+ "\n\t Paper ID:" +d.get("paperid")+"\n\t Author ID:"+ d.get("authorid")+"\n\t Author Name:"+ d.get("authorName")+"\n\t Score:" +hits[i1].score);
					
					//if the author has already been encountered before, i.e multiple publications case
					if(priors.containsKey(d.get("authorid")))
					{
						old=priors.get(d.get("authorid"));
					}
					priors.put((String)d.get("authorid"),old+(double)docs[i1].score);
	
				}
//				System.out.println(total);
				HashMap<String, Double> normalized= new HashMap<String, Double>();
				for (Map.Entry<String, Double> val: priors.entrySet() ) {
					String vertexID= id_label.get(val.getKey());
					//System.out.println("Vertex ID:"+vertexID);
					normalized.put(vertexID,val.getValue()/total);
				   }
	//			System.out.println(total);
				for(Integer i=0;i<2000;i++)
				{
					if(!(normalized.containsKey(i.toString())))
						{normalized.put(i.toString(),(double)0);}
						
				}
	//			for (Map.Entry<String, Double> val: normalized.entrySet() ) 
	//			{
	//				 System.out.println("Author is: "+val.getKey()+" Score: "+ val.getValue());
	//			}
				 PajekNetReader pnr = new PajekNetReader(FactoryUtils.instantiateFactory(Object.class));
			        Graph<Integer, String> graph = new UndirectedSparseGraph<Integer,String>();
			        
			        pnr.load("/Users/rebeccasam/Documents/Search/assignment3/assignment3/author.net", graph);
				
		        Transformer<Integer, Double> p=new Transformer<Integer, Double>()
	    		{
	    			@Override
	    			public Double transform(Integer V)
	    			{
	//    				System.out.println((double) normalized.get(V.toString()));
	    				return (double) normalized.get(V.toString());
	    			}
	    			
	    		};
	    		HashMap<String, Double> topten= new HashMap<String, Double>();
//	    		PageRank PR1 = new PageRank(graph, 0.85);
//	    		PageRankWithPriors<Integer, String> PR = new PageRankWithPriors<Integer, String>(graph,PR1.getEdgeWeights(),p, 0.85);
		        PageRankWithPriors<Integer, String> PR = new PageRankWithPriors<Integer, String>(graph,p, 0.85);
	    		PR.setMaxIterations(30);
//	    		PR1.evaluate();
		        PR.evaluate();
		        for (Object v : graph.getVertices()) {
		        	topten.put(v.toString(), PR.getVertexScore(Integer.parseInt(v.toString())));
	//	        	System.out.println(v.toString() + " - " + PR.getVertexScore(Integer.parseInt(v.toString())));
		        }
		        int c=1;
				Comparator<String> comparator = new ValueComparator<String, Double>(topten);
				TreeMap<String, Double> result = new TreeMap<String, Double>(comparator);
				result.putAll(topten);
				for (Map.Entry<String, Double> val: result.entrySet() ) {
						 
					      System.out.println(c+" "+"Author ID:"+" "+ val.getKey()+"\n"+"      Score:"+" "+ val.getValue());
	
					      if(c==10)
					    	  break;
						  c++;
					   }
				System.out.println("------");
			}
		}
	}

