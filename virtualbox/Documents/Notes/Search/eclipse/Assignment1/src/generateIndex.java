

import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.store.FSDirectory;




public class generateIndex {
	String indexDir = "/Users/Rebecca/virtualbox/Documents/Notes/Search/eclipse/Assignment1/indexDirectory";
    String dataDir = "/Users/Rebecca/virtualbox/Documents/Notes/Search/assignment1/corpus";
	Indexer indexer;
	
    void createIndex(Analyzer an) throws Exception
    {
    	
    	
    	indexer = new Indexer(indexDir, an);
        int numIndexed;
        long startTime = System.currentTimeMillis();	
        numIndexed = indexer.createIndex(dataDir);
        long endTime = System.currentTimeMillis();
        indexer.close();
        System.out.println(numIndexed+" files indexed, time taken: "+(endTime-startTime)+" ms");
        
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get( (indexDir))));

		//Print the total number of documents in the corpus
		System.out.println("Total number of documents in the corpus:"+reader.maxDoc());
		//Print the number of documents containing the term "new" in <field>TEXT</field>.
		System.out.println("Number of documents containing the term \"new\" for field \"TEXT\": "+reader.docFreq(new Term("TEXT", "new")));
		//Print the total number of occurrences of the term "new" across all documents for <field>TEXT</field>.
		System.out.println("Number of occurences of \"new\" in the field \"TEXT\": "+reader.totalTermFreq(new Term("TEXT","new")));
		Terms vocabulary = MultiFields.getTerms(reader, "TEXT");
		//Print the size of the vocabulary for <field>content</field>, only available per-segment.
		System.out.println("Size of the vocabulary for this field: "+vocabulary.size());
		//Print the total number of documents that have at least one term for <field>TEXT</field>
		System.out.println("Number of documents that have at least one term for this field: "+vocabulary.getDocCount());
		//Print the total number of tokens for <field>TEXT</field>
		System.out.println("Number of tokens for this field: "+vocabulary.getSumTotalTermFreq());
		//Print the total number of postings for <field>TEXT</field>
		System.out.println("Number of postings for this field: "+vocabulary.getSumDocFreq());
		//Print the vocabulary for <field>TEXT</field>
		/*TermsEnum iterator = vocabulary.iterator(null);
		BytesRef byteRef = null;
		System.out.println("\n*******Vocabulary-Start**********");
		while((byteRef = iterator.next()) != null) {
		String term = byteRef.utf8ToString();
		System.out.print(term+"\t");
		}*/
		System.out.println("\n*******Vocabulary-End**********");
		reader.close();
    }

}
