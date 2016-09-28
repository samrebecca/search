
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.commons.lang3.StringUtils;
public class Indexer {
   private IndexWriter writer;

   public Indexer(String indexDirectoryPath, Analyzer analy) throws IOException
   {
      //this directory will contain the indexes
	  Directory dir = FSDirectory.open(Paths.get(indexDirectoryPath));
      Analyzer analyzer = analy;
      IndexWriterConfig iwc = new IndexWriterConfig(analyzer); 
      iwc.setOpenMode(OpenMode.CREATE);
      writer = new IndexWriter(dir, iwc);
      
   }

   public void close() throws CorruptIndexException, IOException
   {
      writer.close();
   }

   private void indexFile(File file, String dataDirPath) throws IOException
   {
	   String content = new Scanner(new File(dataDirPath+"/"+file.getName()+"")).useDelimiter("\\Z").next();
	   //System.out.println(content);
	   String subDocs[] = content.split("<DOC>");
	   //System.out.println(subDocs[1]);
	   for(int i=1;i<subDocs.length;i++)
		{
			   String currentDoc=subDocs[i];
			   //System.out.println(currentDoc);
			   Document luceneDoc = new Document();
			   // Get DOCNO
			   String docNo= currentDoc.substring(currentDoc.indexOf("<DOCNO>"),currentDoc.indexOf("</DOCNO>")).replace("<DOCNO>","") ;
			   //System.out.println(docNo);
			   luceneDoc.add(new StringField("DOCNO", docNo, Field.Store.YES));
			   
			   //Get HEAD
			   if(currentDoc.contains("</HEAD>"))
			   {
				   int countHead=StringUtils.countMatches(currentDoc, "<HEAD>");
				   //System.out.println(countHead);
				   String head="";
				   if(countHead>1)
				   {   
					   
					   for (int i1 = -1; (i1 = currentDoc.indexOf("<HEAD>", i1 + 1)) != -1; ) 
					   {
						   //System.out.println(currentDoc.indexOf("</HEAD>"));
						   head+= currentDoc.substring(i1+"<HEAD>".length() ,currentDoc.indexOf("</HEAD>", i1)) ;
						  
					   }
				   }
				   else
				   {
					   head=currentDoc.substring(currentDoc.indexOf("<HEAD>"),currentDoc.indexOf("</HEAD>")).replace("<HEAD>","") ;
				   }
				   //System.out.println(head);
				   luceneDoc.add(new StringField("HEAD", head, Field.Store.YES));
				   
		   }
			   //Get BYLINE
			   if(currentDoc.contains("<BYLINE>"))
			   {
				   int countByline=StringUtils.countMatches(currentDoc, "<BYLINE>");
				   String byline="";
				   if(countByline>1)
				   {   
					   
					   for (int i2 = -1; (i2 = currentDoc.indexOf("<BYLINE>", i2 + 1)) != -1; ) 
					   {
						  // System.out.println(currentDoc.indexOf("</BYLINE>"));
						   byline+= currentDoc.substring(i2+"<BYLINE>".length() ,currentDoc.indexOf("</BYLINE>",i2)) ;
					   }
				   }  
				   else
				   {
					   byline=currentDoc.substring(currentDoc.indexOf("<BYLINE>"),currentDoc.indexOf("</BYLINE>")).replace("<BYLINE>","") ;
				   }
				   //System.out.println(byline);
				   luceneDoc.add(new StringField("BYLINE",byline, Field.Store.YES));
			   }
			   //Get DATELINE
			   if(currentDoc.contains("<DATELINE>"))
			   {
				   int countDateline=StringUtils.countMatches(currentDoc, "<DATELINE>");
				   String dateline="";
				   if(countDateline>1)
				   {   
					   
					   for (int i3 = -1; (i3 = currentDoc.indexOf("<DATELINE>", i3 + 1)) != -1; ) 
					   {
						   //System.out.println(currentDoc.indexOf("</DATELINE>"));
						   dateline+= currentDoc.substring(i3 +"<DATELINE>".length(),currentDoc.indexOf("</DATELINE>",i3)) ;
					   }
				   }
				   else
				   {
					   dateline=currentDoc.substring(currentDoc.indexOf("<DATELINE>"),currentDoc.indexOf("</DATELINE>")).replace("<DATELINE>","") ;
				   }
				   //System.out.println(dateline);
				   luceneDoc.add(new StringField("DATELINE",dateline, Field.Store.YES));
			   }   
			   //Get TEXT
			   if(currentDoc.contains("<TEXT>"))
			   {   
				   String text= currentDoc.substring(currentDoc.indexOf("<TEXT>"),currentDoc.indexOf("</TEXT>")).replace("<TEXT>","") ;
				   luceneDoc.add(new TextField("TEXT", text, Field.Store.YES));
			   }   
			   writer.addDocument(luceneDoc); 
		   }
	   }
  

   
   public int createIndex(String dataDirPath) throws IOException
   {
      //get all files in the data directory

      File[] files = new File(dataDirPath).listFiles();
      for (File file : files) 
      {
         if(!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead())
         {
        	 //String content = new Scanner(new File(dataDirPath+file.getName()+"");
 			 
        	 indexFile(file, dataDirPath);

               
         }
      }
      return writer.numDocs();
   }
}