import java.util.Scanner;

public class Main {

	public static void main(String[] args)
	{
		String filename="Index.bin";
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
				if(BTree.isEmpty(filename))
				{
					System.out.println("Enter Number of Records");
					int recs=scan.nextInt();
					BTree.CreateIndexFile(filename, recs);
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
				System.out.println("Enter Offset");
				int offset=scan.nextInt();
				BTree.InsertNewRecordAtIndex(filename,key,offset);
			}
			else if(x==3)
			{
				System.out.println("Enter Value to be Searched on.");
				int key=scan.nextInt();
				BTree.SearchRecordInIndex(filename,key,32);
			}
			else if(x==4)
				BTree.DisplayIndexFileContent(filename);
			else if(x==5)
				BTree.emptyFile(filename);
			else if(x==6)
				break;
		}
		scan.close();
	}


}
