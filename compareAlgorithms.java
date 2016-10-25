import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.store.FSDirectory;

public class compareAlgorithms {

	public static void main(String args[]) throws Exception
	{	String outputFilePath="/Users/rebeccasam/Documents/Search/Assignment2/VSM-ShortQuery.txt";
//		PrintWriter f0 = new PrintWriter(new FileWriter("/Users/rebeccasam/Documents/Search/Assignment2/VSMshortQuery.txt"));
		File outputFile = new File(outputFilePath);
		//outputFile.delete();
		outputFile.getParentFile().mkdirs();
		if(outputFile.exists() == false)
		{
			outputFile.createNewFile();
		}
		FileWriter fileWriter = new FileWriter(outputFile, true);
		String indexpath="/Users/rebeccasam/Documents/Search/Assignment2/index";
		String datapath="/Users/rebeccasam/Documents/Search/Assignment2/topics.51-100";
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexpath)));
		IndexSearcher searcher = new IndexSearcher(reader);
//		searcher.setSimilarity(new BM25Similarity());
		searcher.setSimilarity(new ClassicSimilarity());
//		searcher.setSimilarity(new LMDirichletSimilarity());
//		searcher.setSimilarity(new LMJelinekMercerSimilarity((float) 0.7));
		Analyzer analyzer = new StandardAnalyzer();
		QueryParser parser = new QueryParser("TEXT", analyzer);
	
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
//					   System.out.println("Title is: " +title);
					   
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
//					   String queryString=desc.replace("\n", "");
		

	    
		Query query = parser.parse(QueryParser.escape(queryString));
		TopScoreDocCollector collector = TopScoreDocCollector.create(1000); 
		searcher.search(query, collector);
		ScoreDoc[] docs = collector.topDocs().scoreDocs;
		int rank=1;
		for (int i1 = 0; i1 < docs.length; i1++)
		{
			Document doc = searcher.doc(docs[i1].doc); 
			System.out.println(doc.get("DOCNO")+" "+docs[i1].score);
			fileWriter.append(counter+" "+"0"+" "+doc.get("DOCNO")+" "+rank+" "+ docs[i1].score+" "+"VSM-ShortQuery\n");

			rank++;
		}
		counter++;
		
		}fileWriter.flush();
		fileWriter.close();
		reader.close();
		
		
	}
}
