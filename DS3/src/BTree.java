import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
	static int SearchRecordInIndex(String filename,int RecordID,int byteoffset)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"r");
			raf.seek(byteoffset);
			Node n=readNode(raf);
			int key1=n.getFirstKey();
			int key2=n.getSecondKey();
			System.out.println("key1 " + key1);
			System.out.println("key2 " + key2);
			System.out.println("Record id " + RecordID);
			TimeUnit.SECONDS.sleep(1);
			if(key1==RecordID || key2==RecordID)
			{
				raf.close();
				if(key1==RecordID)
				{
					return key1;
				}
				if(key2==RecordID)
				{
						return key2;
				}
			}
			if(key1<RecordID && n.getMiddleChild()!=-1)
			{
				System.out.println("First" + byteoffset);
				byteoffset=32*n.getMiddleChild();
				SearchRecordInIndex(filename,RecordID,byteoffset);
			}
			if(key1>RecordID && n.getLeftChild()!=-1)
			{
				byteoffset=32*n.getLeftChild();
				SearchRecordInIndex(filename,RecordID,byteoffset);
			}
			if(key2<RecordID && n.getRightChild()!=-1)
			{
				System.out.println("Third" + byteoffset);
				byteoffset=32*n.getRightChild();
				SearchRecordInIndex(filename,RecordID,byteoffset);
			}
			raf.close();
			return -1;	
		}
		catch(Exception e)
		{
			System.out.println("Cannot Search");
			return -1;
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
			/*Node n1=new Node(1,6,4,2,7,-1,-1,-1);
			Node n2=new Node(0,-1,1,1,-1,-1,-1,-1);
			Node n3=new Node(0,-1,3,3,-1,-1,-1,-1);
			Node n4=new Node(0,-1,5,5,-1,-1,-1,-1);
			Node n5=new Node(0,-1,7,7,-1,-1,-1,-1);
			Node n6=new Node(1,2,2,2,3,-1,-1,-1);
			Node n7=new Node(1,4,6,6,5,8,8,8);
			Node n8=new Node(0,-1,9,9,-1,10,10,-1);
			raf.seek(32);
			addNode(raf,n1);
			addNode(raf,n2);
			addNode(raf,n3);
			addNode(raf,n4);
			addNode(raf,n5);
			addNode(raf,n6);
			addNode(raf,n7);
			addNode(raf,n8);
			raf.close();*/
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
