import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
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
	
	static void InsertNewRecordAtIndex(String filename,int key,int byteOffset)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			Stack<Integer> visitedIndices = new Stack<>();
			raf.seek(8);
			Node input;
			int nodenum=raf.readInt();
			if(nodenum==-1)
			{
				System.out.println("File is Full, Cannot Insert");
			}
			else
			{
				if(nodenum==1) //addRoot
				{
					raf.seek(32);
					input = new Node(0,-1,key,byteOffset,0,0,0,0);
					addNode(raf,input);
					incrementCounter(filename);
				}
				else
				{
					raf.seek(32);
					Node current = readNode(raf);
					//Node parent = current;
					visitedIndices.push(32);
					//int parentPosition = 32;
					while(true)
					{
						if(current.getSecondKey() == 0 && current.IsLeaf())
						{
							if(key>current.getFirstKey()) //add to its right right
							{
								input = new Node(current.getLeaf(),0,current.getFirstKey(),current.getFirstOffset(),0,key,byteOffset,0);
								raf.seek(((nodenum-1)*32));
								addNode(raf,input);
								break;
							}
							else //swap smaller to the left bigger to the right
							{
								input = new Node(current.getLeaf(),0,key,byteOffset,0,current.getFirstKey(),current.getFirstOffset(),0);
								raf.seek((nodenum-1)*32);
								addNode(raf,input);
								break;
							}
						}else if(current.getSecondKey() == 0 && !current.IsLeaf())
						{
							if(key < current.getFirstKey())
							{   
								
								int position = current.getLeftChild()*32;
								raf.seek(current.getLeftChild()*32);
								current = readNode(raf);
								visitedIndices.push(position);
							}
							else
							{
								/*parent = current;
								parentPosition = (int) raf.getFilePointer();*/
								int position = current.getMiddleChild();
								raf.seek(current.getMiddleChild()*32);
								current = readNode(raf);
								visitedIndices.push(position);
							}
							
						}else if(!(current.getSecondKey() == 0) && !current.IsLeaf())
						{
							if(key < current.getFirstKey())
							{   /*parent = current;
								parentPosition = (int) raf.getFilePointer();*/
								int position = current.getLeftChild()*32;
								raf.seek(current.getLeftChild()*32);
								current = readNode(raf);
								visitedIndices.push(position);
							}
							else if (key > current.getSecondKey())
							{
								/*parent = current;
								parentPosition = (int) raf.getFilePointer();*/
								int position = current.getRightChild()*32;
								raf.seek(current.getRightChild()*32);
								current = readNode(raf);
								visitedIndices.push(position);
							}else 
							{
								/*parent = current;
								parentPosition = (int) raf.getFilePointer();*/
								int position = current.getMiddleChild()*32;
								raf.seek(current.getMiddleChild()*32);
								current = readNode(raf);
								visitedIndices.push(position);
							}
							
						}
						else
						{
							splitNode(raf,nodenum,key,byteOffset,visitedIndices);
							incrementCounter(filename);
						}
						
					}
				}
			}
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Insert");
		}
	}
	public static int splitNode(RandomAccessFile raf,int nodenum,int key,int byteOffset,Stack<Integer> st)
	{
		try
		{
			
			int nodePosition = st.peek();
			raf.seek(nodePosition); 
			Node node = readNode(raf);
			int keys[] = {};
			int offsets[] = {};
			HashMap<Integer,Integer> map = new HashMap<>();
			
			map.put(node.getFirstKey(), node.getFirstOffset());
			map.put(key, byteOffset);
			map.put(node.getSecondKey(), node.getSecondOffset());
			
			Map<Integer, Integer> sortedMap = new TreeMap<>(map);
			int i = 0;
			for (Map.Entry<Integer, Integer> entry : sortedMap.entrySet()) {
			    System.out.println(entry.getKey() + "/" + entry.getValue());
			    keys[i] = entry.getKey();
			    offsets[i] = entry.getValue();
			    i++;
			}
			if(nodePosition == 32 && node.getSecondKey() != 0)
			{
				splitRoot(raf,nodenum,key,byteOffset,keys,offsets);
				return 0; 
			}else if(nodePosition == 32 && node.getSecondKey() == 0)
			{
				
			}
			st.pop();
			int parentPosition = st.peek();
			raf.seek(parentPosition);
			Node parent = readNode(raf);
			if(parent.getSecondKey() == 0 && key > parent.getFirstKey())
			{
				int leftPosition=32*(nodenum);
				Node leftChild = new Node(0,0,keys[0],offsets[0],0,0,0,0);
				raf.seek(leftPosition);
				addNode(raf,leftChild);
				
				int rightPosition=32*(nodenum+1);
				Node rightChild = new Node(0,0,keys[2],offsets[2],0,0,0,0);
				raf.seek(rightPosition);
				addNode(raf,rightChild);
				
				Node newParent = new Node(1,parent.getLeftChild(),parent.getFirstKey(),parent.getFirstOffset(),leftPosition/32,keys[1],offsets[1],rightPosition/32);
				raf.seek(parentPosition);
				addNode(raf,newParent);
				raf.close();
				return 0;
				
			}else if(parent.getSecondKey() == 0 && key < parent.getFirstKey())
			{
				int leftPosition=32*(nodenum);
				Node leftChild = new Node(0,0,keys[0],offsets[0],0,0,0,0);
				raf.seek(leftPosition);
				addNode(raf,leftChild);
				
				int rightPosition=32*(nodenum+1);
				Node rightChild = new Node(0,0,keys[2],offsets[2],0,0,0,0);
				raf.seek(rightPosition);
				addNode(raf,rightChild);
				
				Node newParent = new Node(1,leftPosition/32,keys[1],offsets[1],rightPosition/32,parent.getFirstKey(),parent.getFirstOffset(),parent.getRightChild());
				raf.seek(parentPosition);
				addNode(raf,newParent);
				raf.close();
				return 0;
			}
			else
			{
				int leftPosition=32*(nodenum);
				Node leftChild = new Node(0,0,keys[0],offsets[0],0,0,0,0);
				raf.seek(leftPosition);
				addNode(raf,leftChild);
				
				int rightPosition=32*(nodenum+1);
				Node rightChild = new Node(0,0,keys[2],offsets[2],0,0,0,0);
				raf.seek(rightPosition);
				addNode(raf,rightChild);
				
				splitNode(raf,nodenum,keys[1],offsets[1],st);
				raf.close();
				return 0;
			}
			
			
		}
		catch(Exception e)
		{
			System.out.println("Cannot Split");
			return 0;
		}
	}
	static void splitRoot(RandomAccessFile raf,int nodenum, int key, int byteOffset, int[] keys, int offsets[])
	{
		raf.seek(32);
		Node root = readNode(raf);
		if(key > root.getFirstKey())
		{
			int leftPosition=32*(nodenum);
			Node newLeftChild = new Node(1,root.getLeftChild(),keys[0],offsets[0],root.getMiddleChild(),0,0,0);
			raf.seek(leftPosition);
			addNode(raf,newLeftChild);
			
			int rightPosition=32*(nodenum+1);
			Node newRightChild = new Node(1,root.g,keys[2],offsets[2],0,0,0,0);
			raf.seek(rightPosition);
			addNode(raf,newRightChild);
			
			Node newRoot = new Node(1,parent.getLeftChild(),parent.getFirstKey(),parent.getFirstOffset(),leftPosition/32,keys[1],offsets[1],rightPosition/32);
			raf.seek(parentPosition);
			addNode(raf,newParent);
			raf.close();
			return 0;
			
		}else if(parent.getSecondKey() == 0 && key < parent.getFirstKey())
		{
			int leftPosition=32*(nodenum);
			Node leftChild = new Node(0,0,keys[0],offsets[0],0,0,0,0);
			raf.seek(leftPosition);
			addNode(raf,leftChild);
			
			int rightPosition=32*(nodenum+1);
			Node rightChild = new Node(0,0,keys[2],offsets[2],0,0,0,0);
			raf.seek(rightPosition);
			addNode(raf,rightChild);
			
			Node newParent = new Node(1,leftPosition/32,keys[1],offsets[1],rightPosition/32,parent.getFirstKey(),parent.getFirstOffset(),parent.getRightChild());
			raf.seek(parentPosition);
			addNode(raf,newParent);
			raf.close();
			return 0;
		}
	}
	
	}
