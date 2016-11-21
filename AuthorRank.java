
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.collections15.FactoryUtils;
import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.io.PajekNetReader;

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
//class Format extends Object
//{
//	@Override
//	public String toString() {
//	    return getClass().getName() + "@" + Integer.toHexString(hashCode());
//	}
//}
public class AuthorRank {

	
	public static void main(String[] args) throws IOException {

        PajekNetReader pnr = new PajekNetReader(FactoryUtils.instantiateFactory(Object.class));
        Graph graph = new UndirectedSparseGraph();
        pnr.load("/Users/rebeccasam/Documents/Search/assignment3/assignment3/author.net", graph);
        HashMap<String, Double> score= new HashMap<String, Double>();
        PageRank<Integer, String> PR = new PageRank<Integer, String>(graph, 0.85);
        PR.setMaxIterations(30);
        PR.evaluate();
        int count=0;
//      HashMap<String, List<String>> rest=new HashMap<String,List<String>>();
      
        for (Object v : graph.getVertices()) {
        
//        		String vertex=v.toString();
//        		List<String> val = new ArrayList<String>();
//        		System.out.println(v+" "+"........");
//        		Collection edges= graph.getIncidentEdges(v);
//        		System.out.println(edges.size());
//        		for (Object obj : edges) 
//        		{	
//        			Pair p=graph.getEndpoints(obj);
////        		System.out.println(p);
//        			String pstring=p.toString();
//        			String uv=pstring.substring(pstring.indexOf(" "),pstring.indexOf(">"));
////        		System.out.println(uv);
//        	        val.add(uv);
////        		String s=obj.toString();
////        		String snew[]=s.split("@");
////        		System.out.println(Integer.parseInt(snew[1], 16));
//        			System.out.println(ReflectionToStringBuilder.toString(obj));
//        		}
//        		rest.put(vertex, val);
//        		System.out.println(v+" "+"........");
//        		System.out.println(v.toString() + " - " + PR.getVertexScore(Integer.parseInt(v.toString())));
        		String key=v.toString();
        		Double value= PR.getVertexScore(Integer.parseInt(v.toString()));
        		score.put(key, value);
        }
//        for (Object v : graph.getVertices()) {
//    		String vertex=v.toString();
//    		List<String> val = new ArrayList<String>();
//    		System.out.println(v+" "+"........");
//    		Collection vertices= graph.getNeighbors(v);
//    		System.out.println(vertices.size());
//    		for (Object obj : vertices) 
//    		{	
//    			System.out.println(ReflectionToStringBuilder.toString(obj));
//    		}
//    		rest.put(vertex, val);
//    		System.out.println(v+" "+"........");
//        }
//        for (Map.Entry<String, List<String>> val: rest.entrySet() ) 
//        {
////        	System.out.println(val.getKey()+" "+ val.getValue());
//        }
		Comparator<String> comparator = new ValueComparator<String, Double>(score);
		TreeMap<String, Double> result = new TreeMap<String, Double>(comparator);
		result.putAll(score);
		for (Map.Entry<String, Double> val: result.entrySet() ) {

			      System.out.println(val.getKey()+" "+ val.getValue());

			      if(count==9)
			    	  break;
				  count++;
			   }

    }
}
