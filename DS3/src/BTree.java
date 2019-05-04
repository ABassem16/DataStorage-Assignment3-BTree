import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;

public class BTree
{
	static String filename="Index.bin";
	static void addNode(RandomAccessFile raf,int f,int p,int k,int o,int p1,int k1,int o1,int p2,int byteoffset)
	{
		try
		{
			raf.seek(byteoffset);
			raf.writeInt(f);
			raf.writeInt(p);
			raf.writeInt(k);
			raf.writeInt(o);
			raf.writeInt(p1);
			raf.writeInt(k1);
			raf.writeInt(o1);
			raf.writeInt(p2);
		}
		catch(Exception e)
		{
			System.out.println("Cannot Add Node" + "    " + k);
		}
	}
	static void incrementnode(String f)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(f,"rw");
			int len=(int)raf.length();
			int recs=len/32;
			raf.seek(8);
			int node=raf.readInt();
			node++;
			raf.seek(8);
			if(recs==node)
			{
				node=-1;
			}
			raf.writeInt(node);
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("NO");
		}
	}
	static void CreateIndexFile(String filename,int NumberofRecords)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			for(int i=0;i<NumberofRecords-1;i++)
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
	static void InsertNewRecordAtIndex(String filename,int key,int byteoffset)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			raf.seek(8);
			int nodenum=raf.readInt();
			if(nodenum==-1)
			{
				System.out.println("File is Full, Cannot Insert");
			}
			else
			{
				if(nodenum==1)
				{
					raf.seek(32);
					raf.writeInt(0);
					raf.writeInt(-1);
					raf.writeInt(key);
					raf.writeInt(key);
					incrementnode(filename);
				}
				else
				{
					raf.seek(((nodenum-1)*32)+20);
					int k=raf.readInt();
					if(k==0)
					{
						raf.seek(((nodenum-1)*32)+8);
						int keycmp=raf.readInt();
						if(key>keycmp)
						{
						raf.seek(((nodenum-1)*32)+20);
						raf.writeInt(key);
						raf.writeInt(key);
						raf.writeInt(0);
						}
						else
						{
							raf.seek(((nodenum-1)*32)+4);
							int p=raf.readInt();
							raf.readInt();
							raf.readInt();
							int p1=raf.readInt();
							raf.seek((nodenum-1)*32);
							addNode(raf,1,p,key,key,p1,keycmp,keycmp,0,(nodenum-1)*32);
						}
					}
					else
					{
						splitNode(filename,nodenum,key);
						incrementnode(filename);
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
	public static void splitNode(String filename,int nodenum,int key)
	{
		try
		{
			RandomAccessFile raf=new RandomAccessFile(filename,"rw");
			raf.seek((32*(nodenum-1))+8);
			int key1=raf.readInt();
			System.out.println("  key1  " + key1);
			raf.seek((32*(nodenum-1))+20);
			int key2=raf.readInt();
			System.out.println("  key2  " + key2);
			int arr[]= {key,key1,key2};
			Arrays.sort(arr);
			int byteoffset=32*(nodenum-1);
			addNode(raf,1,nodenum,arr[1],arr[1],nodenum+1,0,0,0,byteoffset);
			byteoffset=32*nodenum;
			addNode(raf,0,-1,arr[0],arr[0],-1,0,0,0,byteoffset);
			raf.seek(32*(nodenum+1));
			byteoffset=32*(nodenum+1);
			addNode(raf,0,-1,arr[2],arr[2],-1,0,0,0,byteoffset);
			raf.close();
		}
		catch(Exception e)
		{
			System.out.println("Cannot Split");
		}
	}
	public static void main(String[] args)
	{
		Scanner scan=new Scanner(System.in);
		while(true)
		{
			System.out.println("1- Create Record File"
					+ "\n2- Insert Record"
					+ "\n3- Search Record"
					+ "\n4- Display Whole File"
					+ "\n5- Empty File"
					+ "\n6- Exit");
			int x=scan.nextInt();
			if(x==1)
			{
				if(isEmpty(filename))
				{
					System.out.println("Enter Number of Records");
					int recs=scan.nextInt();
					CreateIndexFile(filename, recs);
				}
				else
				{
					System.out.println("File Already Exists\nCannot Create New File\n");
				}
			}
			else if(x==2)
			{
				System.out.println("Enter Key");
				int key=scan.nextInt();
				InsertNewRecordAtIndex(filename,key,32);
			}
			/*else if(x==3)
			{
				System.out.println("Enter Value to be Searched on.");
				int key=scan.nextInt();
				int search=SearchRecordIndex(filename,key);
				if(search==-1)
				{
					System.out.println("Cannot Find Key");
				}
			}*/
			else if(x==4)
				DisplayIndexFileContent(filename);
			else if(x==5)
				emptyFile(filename);
			else if(x==6)
				break;
		}
		scan.close();
	}
}
