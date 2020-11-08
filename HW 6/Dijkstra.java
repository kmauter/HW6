import org.jheaps.array.BinaryArrayHeap;
import org.jheaps.tree.FibonacciHeap;
import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;

public class Dijkstra 
{
	static class Edge
	{
		int source;
		int dest;
		int weight;
		
		public Edge(int s, int d, int w)
		{
			source = s;
			dest = d;
			weight = w;
		}
	}
	
	static class Vertex implements Comparable<Vertex>
	{
		int key;
		int dist;
		
		@Override
		public int compareTo(Vertex v1)
		{
			return Comparators.DIST.compare(this, v1);
		}
	}
	
	public static class Comparators
	{
		public static Comparator<Vertex> KEY = new Comparator<Vertex>()
		{
			@Override
			public int compare(Vertex v1, Vertex v2)
			{
				return v1.key - v2.key;
			}
		};
		
		public static Comparator<Vertex> DIST = new Comparator<Vertex>()
		{
			@Override
			public int compare(Vertex v1, Vertex v2)
			{
				return v1.dist - v2.dist;
			}
		};
	}
	
	static class Graph
	{
		int vertices;
		LinkedList<Edge>[] adjacencylist;
		
		Graph(int v)
		{
			vertices = v;
			adjacencylist = new LinkedList[v];
			
			for (int i = 0; i < v; i++)
			{
				adjacencylist[i] = new LinkedList<>();
			}
		}
		
		public void addEdge(int s, int d, int w)
		{
			Edge edge = new Edge(s, d, w);
			adjacencylist[s].addFirst(edge);
		}
		
		public void extractMinBinaryHeap(int svx)
		{
			int INFINITY = Integer.MAX_VALUE;
			boolean[] SPT = new boolean[vertices];
			
			Vertex[] allVertices = new Vertex[vertices];
			for (int i = 0; i < vertices; i++)
			{
				allVertices[i] = new Vertex();
				allVertices[i].key = i;
				allVertices[i].dist = INFINITY;						
			}
			
			allVertices[svx].dist = 0;
			
			BinaryArrayHeap<Vertex> binHeap = new BinaryArrayHeap<Vertex>(Comparators.DIST, vertices);
			binHeap = BinaryArrayHeap.heapify(allVertices, Comparators.DIST);
			
			while (!binHeap.isEmpty())
			{
				//System.out.println("binHeap is not empty\n");
				//printDijkstra(allVertices, svx);
				
				Vertex vx = binHeap.deleteMin();
				int vkey = vx.key;
				SPT[vkey] = true;
				
				LinkedList<Edge> list = adjacencylist[vkey];
				for(int i = 0; i < list.size(); i++)
				{
					Edge edge = list.get(i);
					int dest = edge.dest;
					
					if (SPT[dest] == false)
					{
						int newKey = allVertices[vkey].dist + edge.weight;
						int curKey = allVertices[dest].dist;
						
						//System.out.println("newKey: " + newKey + " curKey: " + curKey + " vkey: " + vkey + " dest: " + dest);
						
						if(curKey > newKey)
						{

							allVertices[dest].dist = newKey;
							
							binHeap.clear();
							for(int j = 0; j < vertices; j++)
							{
								if(!SPT[j])
								{
									binHeap.insert(allVertices[j]);
								}
							}
						}
					}
				}
			}
			System.out.println("Adjacency List + Binary Heap");
			//printDijkstra(allVertices, svx);
		}
		
		public void extractMinFibonacciHeap(int svx)
		{
			int INFINITY = Integer.MAX_VALUE;
			boolean[] SPT = new boolean[vertices];
			
			Vertex[] allVertices = new Vertex[vertices];
			for (int i = 0; i < vertices; i++)
			{
				allVertices[i] = new Vertex();
				allVertices[i].key = i;
				allVertices[i].dist = INFINITY;						
			}
			
			allVertices[svx].dist = 0;
			
			FibonacciHeap<Vertex, Integer> fibHeap = new FibonacciHeap<Vertex, Integer>(Comparators.DIST);
			
			for (int i = 0; i < vertices; i++)
			{
				fibHeap.insert(allVertices[i], i);
			}
			
			//boolean[] remainingVertices = new boolean[vertices];
			
			
			while (!fibHeap.isEmpty())
			{
				//System.out.println("binHeap is not empty\n");
				//printDijkstra(allVertices, svx);
				
				Vertex vx = fibHeap.deleteMin().getKey();
				int vkey = vx.key;
				SPT[vkey] = true;
				
				LinkedList<Edge> list = adjacencylist[vkey];
				for(int i = 0; i < list.size(); i++)
				{
					Edge edge = list.get(i);
					int dest = edge.dest;
					
					if (SPT[dest] == false)
					{
						int newKey = allVertices[vkey].dist + edge.weight;
						int curKey = allVertices[dest].dist;
						
						//System.out.println("newKey: " + newKey + " curKey: " + curKey + " vkey: " + vkey + " dest: " + dest);
						
						if(curKey > newKey)
						{
							
							allVertices[dest].dist = newKey;
							
							fibHeap.clear();
							for(int j = 0; j < vertices; j++)
							{
								if(!SPT[j])
								{
									fibHeap.insert(allVertices[j], j);
								}
							}
						}
					}
				}
			}
			System.out.println("Adjacency List + Fibonacci Heap");
			//printDijkstra(allVertices, svx);
		}
		
		public void printDijkstra(Vertex[] results, int svx)
		{
			for(int i = 0; i < vertices; i++)
			{
				System.out.print("Source Vertex: " + svx + " to vertex " + i + " distance: "); // + results[i].dist);
				if(results[i].dist == Integer.MAX_VALUE || results[i].dist < 0)
				{
					System.out.println("No Route");
				}
				else
				{
					System.out.println(results[i].dist);
				}
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		System.out.println("Pass a file in this format:");
		System.out.println("v         - where v is an integer representing the number of vertices");
		System.out.println("s         - where s is an integer representing the key of the source vertex");
		System.out.println("x y z     - where x y and z are integers representing the source, destination, and weight of an edge");
		System.out.println("...       - where any number of following lines are in the x y z format");
		System.out.println("-1        - represent the end of a graph, all graphs in the file should follow this format");
		System.out.println();
		
		BufferedReader fin = new BufferedReader(new FileReader("input.txt"));
		
		int graphs = 6;
		while(graphs --> 0)
		{
			int vertices = Integer.parseInt(fin.readLine());
			Graph graph = new Graph(vertices);
			int sourceVertex = Integer.parseInt(fin.readLine());
			
			String line = fin.readLine();
			while(line != null)
			{
				String[] nums = line.split(" ");
				int s = Integer.parseInt(nums[0]);
				
				if(s == -1)
					break;
				
				int d = Integer.parseInt(nums[1]);
				int w = Integer.parseInt(nums[2]);
				graph.addEdge(s, d, w);
				line = fin.readLine();
			}
			
			Instant start, finish;
			
			start = Instant.now();
			graph.extractMinBinaryHeap(sourceVertex);
			finish = Instant.now();
			
			long binHeapTime = Duration.between(start, finish).toNanos();
			
			start = Instant.now();
			graph.extractMinFibonacciHeap(sourceVertex);
			finish = Instant.now();
			
			long fibHeapTime = Duration.between(start, finish).toNanos();
			
			System.out.println("Binary Heap Time: " + binHeapTime);
			System.out.println("Fibonacci Heap Time: " + fibHeapTime);
			
			if(binHeapTime > fibHeapTime)
			{
				System.out.println("Fibonacci Heap was faster");
			}
			else if(binHeapTime < fibHeapTime)
			{
				System.out.println("Binary Heap was faster");
			}
			else
			{
				System.out.println("Both implementations took equal time");
			}
			System.out.println("\n");
		}
		fin.close();
	}
	
	
}


