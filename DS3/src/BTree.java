import java.util.ArrayList;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Stack;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Map;

public class BTree
{
	
	static void addNode(RandomAccessFile raf, Node n)
	{
		try
		{
			raf.writeInt(n.getLeaf());
			raf.writeInt(n.getLeftChild());
			raf.writeInt(n.getFirstKey());
			raf.writeInt(n.getFirstOffset());
			raf.writeInt(n.getMiddleChild());
			raf.writeInt(n.getSecondKey());
			raf.writeInt(n.getSecondOffset());
			raf.writeInt(n.getRightChild());
		}
		catch(Exception e)
		{
			System.out.println("Cannot Add Node");
		}
	}
	static Node readNode(RandomAccessFile raf) throws IOException
	{
		Node node = new Node();
		node.setIsLeaf(raf.readInt());
		node.setLeftChild(raf.readInt());
		node.setFirstKey(raf.readInt());
		node.setFirstOffset(raf.readInt());
		node.setMiddleChild(raf.readInt());
		node.setSecondKey(raf.readInt());
		node.setSecondOffset(raf.readInt());
		node.setRightChild(raf.readInt());
		return node;
		
		
	}
	static void incrementCounter(String fileName)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(fileName,"rw");
			int len=(int)raf.length();
			int recs=len/32;
			raf.seek(8); //1st key
			int counter=raf.readInt();
			counter++;
			raf.seek(8);
			if(recs==counter)
			{
				counter=-1;
			}
			raf.writeInt(counter);
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("NO");
		}
	}
	static void CreateIndexFile(String fileName,int NumberOfRecords)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(fileName,"rw");
			for(int i=0;i<NumberOfRecords-1;i++)
			{
				raf.writeInt(0);
				raf.writeInt(0);
				raf.writeInt(i+1);
				raf.writeInt(0);
				raf.writeInt(0);
				raf.writeInt(0);
				raf.writeInt(0);	
				raf.writeInt(0);	
			}
			for(int i=0;i<8;i++)
			{
			raf.writeInt(0);
			}
			raf.close();
		}
		catch(Exception e)
		{
			
		}
	}
	static void DisplayIndexFileContent(String filename)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"r");
			long len=raf.length();
			raf.seek(len);
			long eof=raf.getFilePointer();
			raf.seek(0);
			System.out.println("F  | P  | K  | O  | P  | K  | O  | P");
			while(raf.getFilePointer()!=eof)
			{
				System.out.print(raf.readInt()+ "    ");
				System.out.print(raf.readInt()+ "    ");
				System.out.print(raf.readInt()+ "    ");
				System.out.print(raf.readInt()+ "    ");
				System.out.print(raf.readInt()+ "    ");
				System.out.print(raf.readInt()+ "    ");
				System.out.print(raf.readInt()+ "   ");
				System.out.println(raf.readInt()+ "    ");
			}
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception Display");
		}
	}
	static boolean isEmpty(String f)
	{
		try
		{
		RandomAccessFile raf=new RandomAccessFile(f,"rw");
		if(raf.length()==0)
		{
			raf.close();
			return true;
		}
		else
		{
			raf.close();
			return false;
		}
		}
		catch(Exception e)
		{
			System.out.println("Error Checking file if empty.");
			return false;
		}
	}
	static void emptyFile(String f)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(f, "rw");
			raf.setLength(0);
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Empty File");
		}
	}
	
	static void InsertNewRecordAtIndex(String filename,int key,int byteOffset) throws IOException
	{
		RandomAccessFile raf=new RandomAccessFile(filename,"rw");
		Stack<Integer> visitedIndices = new Stack<>();
		raf.seek(8);
		Node input;
		int nodenum=raf.readInt();
		
			if(nodenum==1) //addRoot
			{
				raf.seek(32);
				input = new Node(0,0,key,byteOffset,0,0,0,0);
				addNode(raf,input);
				incrementCounter(filename);
			}
			else
			{
				raf.seek(32);
				Node current = readNode(raf);
				//Node parent = current;
				visitedIndices.push(32);
				int currentPosition = 32;
				while(true)
				{
					
					if(current.getSecondKey() == 0 && current.IsLeaf())
					{
						if(key>current.getFirstKey()) //add to its right right
						{
							input = new Node(current.getLeaf(),0,current.getFirstKey(),current.getFirstOffset(),0,key,byteOffset,0);
							raf.seek(currentPosition);
							addNode(raf,input);
							//incrementCounter(filename);
							break;
						}
						else //swap smaller to the left bigger to the right
						{
							input = new Node(current.getLeaf(),0,key,byteOffset,0,current.getFirstKey(),current.getFirstOffset(),0);
							raf.seek(currentPosition);
							addNode(raf,input);
							//incrementCounter(filename);
							break;
						}
					}else if(current.getSecondKey() == 0 && !current.IsLeaf())
					{
						if(key < current.getFirstKey())
						{   
							
							currentPosition = current.getLeftChild()*32;
							raf.seek(current.getLeftChild()*32);
							current = readNode(raf);
							visitedIndices.push(currentPosition);
						}
						else
						{
							/*parent = current;
							parentPosition = (int) raf.getFilePointer();*/
							currentPosition= current.getMiddleChild()*32;
							raf.seek(current.getMiddleChild()*32);
							current = readNode(raf);
							visitedIndices.push(currentPosition);
						}
						
					}else if(!(current.getSecondKey() == 0) && !current.IsLeaf())
					{
						if(key < current.getFirstKey())
						{   /*parent = current;
							parentPosition = (int) raf.getFilePointer();*/
							currentPosition = current.getLeftChild()*32;
							raf.seek(current.getLeftChild()*32);
							current = readNode(raf);
							visitedIndices.push(currentPosition);
						}
						else if (key > current.getSecondKey())
						{
							/*parent = current;
							parentPosition = (int) raf.getFilePointer();*/
							currentPosition = current.getRightChild()*32;
							raf.seek(current.getRightChild()*32);
							current = readNode(raf);
							visitedIndices.push(currentPosition);
						}else 
						{
							/*parent = current;
							parentPosition = (int) raf.getFilePointer();*/
							currentPosition = current.getMiddleChild()*32;
							raf.seek(current.getMiddleChild()*32);
							current = readNode(raf);
							visitedIndices.push(currentPosition);
						}
						
					}
					else
					{
						if(nodenum==-1)
						{
							System.out.println("File is Full, Cannot Insert");
							break;
						}
						System.out.println("Splitting...");
						int x = splitNode(raf,nodenum,key,byteOffset,visitedIndices,0,0,0);
						//incrementCounter(filename);
						if(x == 1)
						{
							break;
						}
					}					
				}
			}
			raf.close();
		}
		
	public static int splitNode(RandomAccessFile raf,int nodenum,int key,int byteOffset,Stack<Integer> st,int left,int middle,int right) throws IOException
	{
		System.out.println(st);	
		int nodePosition = st.peek();
		raf.seek(nodePosition); 
		Node node = readNode(raf);
		ArrayList<Integer> keys = new ArrayList<>();
		ArrayList<Integer> offsets = new ArrayList<>();
		HashMap<Integer,Integer> map = new HashMap<>();
		map.put(node.getFirstKey(), node.getFirstOffset());
		map.put(key, byteOffset);
		map.put(node.getSecondKey(), node.getSecondOffset());
		
		Map<Integer, Integer> sortedMap = new TreeMap<>(map);
		System.out.println(sortedMap);
		for (Map.Entry<Integer, Integer> entry : sortedMap.entrySet()) {
		    System.out.println(entry.getKey() + "/" + entry.getValue());
		    keys.add(entry.getKey());  
		    offsets.add(entry.getValue());
		}
		if(nodePosition == 32 && node.getSecondKey() != 0 && !node.IsLeaf())
		{
			System.out.println("Splitting root...");
			if(key > node.getSecondKey())
			{
				
				int leftPosition=32*(nodenum+1);				
				Node leftChild = new Node(1,node.getLeftChild(),keys.get(0),offsets.get(0),node.getMiddleChild(),0,0,0);
				raf.seek(leftPosition);
				addNode(raf,leftChild);
				incrementCounter("Index.bin");
				
				int rightPosition=32*(nodenum+2);
				Node rightChild = new Node(1,left,keys.get(2),offsets.get(2),middle,0,0,0);
				raf.seek(rightPosition);
				addNode(raf,rightChild);
				incrementCounter("Index.bin");
					
				raf.seek(nodePosition);
				Node newRoot = new Node(1,leftPosition/32,keys.get(1),offsets.get(1),rightPosition/32,0,0,0);
				addNode(raf,newRoot);
				return 1;
			}else if(key < node.getFirstKey())
			{
				//to be added
				return 1;
				
			}else
			{
				//to be added
				return 1;
			}
			
			 
		}else if(nodePosition == 32 && node.getSecondKey() != 0 && node.IsLeaf())
		{
			
			int leftPosition=32*(nodenum);
			Node leftChild = new Node(0,0,keys.get(0),offsets.get(0),0,0,0,0);
			raf.seek(leftPosition);
			addNode(raf,leftChild);
			incrementCounter("Index.bin");
			
			int rightPosition=32*(nodenum+1);
			Node rightChild = new Node(0,0,keys.get(2),offsets.get(2),0,0,0,0);
			raf.seek(rightPosition);
			addNode(raf,rightChild);
			incrementCounter("Index.bin");
			
			Node newParent = new Node(1,leftPosition/32,keys.get(1),offsets.get(1),rightPosition/32,0,0,0);
			raf.seek(32);
			addNode(raf,newParent);
			raf.close();
				
			
			return 1;
		}
		st.pop();
		int parentPosition = st.peek();
		raf.seek(parentPosition);
		Node parent = readNode(raf);
		if(parent.getSecondKey() == 0)
		{
			int leaf = 1;
			if(key > parent.getFirstKey())
			{
				if(middle == 0 && left == 0)
				{
					leaf = 0;
				}
				int leftPosition=nodePosition;
				Node leftChild = new Node(leaf,left,keys.get(0),offsets.get(0),middle,0,0,0);
				raf.seek(leftPosition);
				addNode(raf,leftChild);
				
				int rightPosition=32*(nodenum);
				
				Node rightChild = new Node(leaf,left,keys.get(2),offsets.get(2),middle,0,0,0);
				raf.seek(rightPosition);
				addNode(raf,rightChild);
				incrementCounter("Index.bin");
				
				Node newParent = new Node(1,parent.getLeftChild(),parent.getFirstKey(),parent.getFirstOffset(),leftPosition/32,keys.get(1),offsets.get(1),rightPosition/32);
				raf.seek(parentPosition);
				addNode(raf,newParent);
				raf.close();
				return 1;
			}else if(key < parent.getFirstKey())
			{
				if(middle == 0 && left == 0)
				{
					leaf = 0;
				}
				int leftPosition=nodePosition;
				Node leftChild = new Node(leaf,left,keys.get(0),offsets.get(0),middle,0,0,0);
				raf.seek(leftPosition);
				addNode(raf,leftChild);
				
				int rightPosition=32*(nodenum);
				Node rightChild = new Node(leaf,left,keys.get(2),offsets.get(2),middle,0,0,0);
				raf.seek(rightPosition);
				addNode(raf,rightChild);
				incrementCounter("Index.bin");
				
				Node newParent = new Node(1,leftPosition/32,keys.get(1),offsets.get(1),rightPosition/32,parent.getFirstKey(),parent.getFirstOffset(),parent.getRightChild());
				raf.seek(parentPosition);
				addNode(raf,newParent);
				raf.close();
				return 1;
			}
			
		}
		else if(parent.getSecondKey() != 0)
		{
			int leaf = 1;
			if(key > parent.getSecondKey())
			{
				if(middle == 0 && left == 0)
				{
					leaf = 0;
				}
				int leftPosition=nodePosition;
				Node leftChild = new Node(leaf,left,keys.get(0),offsets.get(0),middle,0,0,0);
				raf.seek(leftPosition);
				addNode(raf,leftChild);
				
				
				int rightPosition=32*(nodenum);
				Node rightChild = new Node(leaf,left,keys.get(2),offsets.get(2),middle,0,0,0);
				raf.seek(rightPosition);
				addNode(raf,rightChild);
				incrementCounter("Index.bin");
				
				
				return splitNode(raf,nodenum,keys.get(1),offsets.get(1),st,leftPosition/32,rightPosition/32,0);
				
			}else if(key < parent.getFirstKey())
			{
				//to be added
				return 1;
			}
			else
			{
				//to be added
				return 1;
			}
		
			
		}
		return -1;
		
	}
	}	
