import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

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
					raf.seek(((nodenum-1)*32));
					Node node = readNode(raf);
					node.print();
					int k = node.getSecondKey();
					if(k==0)
					{
						int keycmp=node.getFirstKey();
						if(key>keycmp) //add to its right right
						{
						input = new Node(node.getLeaf(),node.getLeftChild(),node.getFirstKey(),node.getFirstOffset(),node.getMiddleChild(),key,byteOffset,0);
						raf.seek(((nodenum-1)*32));
						addNode(raf,input);
						}
						else //swap smaller to the left bigger to the right
						{
							input = new Node(node.getLeaf(),node.getLeftChild(),key,byteOffset,node.getMiddleChild(),node.getFirstKey(),node.getFirstOffset(),0);
							raf.seek((nodenum-1)*32);
							addNode(raf,input);
						}
					}
					else //recode already has 2 keys
					{
						splitNode(filename,nodenum,key,byteOffset);
						incrementCounter(filename);
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
	public static void splitNode(String filename,int nodenum,int key,int byteOffset)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			raf.seek(((nodenum-1)*32)); 
			Node node = readNode(raf);
			int arr[]= {key,node.getFirstKey(),node.getSecondKey()};
			Arrays.sort(arr);
			
			int pos=32*(nodenum-1);
			Node parent = new Node(1,nodenum,arr[1],node.getSecondOffset(),nodenum+1,0,0,0);
			raf.seek(pos);
			addNode(raf,parent);
			
			pos=32*(nodenum);
			Node leftChild = new Node(0,-1,arr[0],node.getFirstOffset(),-1,0,0,0);
			raf.seek(pos);
			addNode(raf,leftChild);
			
			
			pos=32*(nodenum+1);
			Node rightChild = new Node(0,-1,arr[2],byteOffset,-1,0,0,0);
			raf.seek(pos);
			addNode(raf,rightChild);
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Split");
		}
	}
	
	}
