
public class Node {
	private int isLeaf;
	private int leftChild;
	private int firstKey;
	private int firstOffset;
	private int middleChild;
	private int secondKey;
	private int secondOffset;
	private int rightChild;
	
	Node(int isLeaf, int leftChild, int firstKey, int firstOffset, int middleChild,int secondKey, int secondOffset, int rightChild)
	{
		this.isLeaf = isLeaf;
		this.leftChild = leftChild;
		this.firstKey = firstKey;
		this.firstOffset = firstOffset;
		this.middleChild = middleChild;
		this.secondKey = secondKey;
		this.secondOffset = secondOffset;
		this.rightChild = rightChild;
	}
	Node(){
		
	}
	public void print()
	{
		System.out.println("isLeaf=> "+this.isLeaf);
		System.out.println("LeftChild=> "+this.leftChild);
		System.out.println("firstKey=> "+this.firstKey);
		System.out.println("firstOffset=> "+this.firstOffset);
		System.out.println("middleChild=> "+this.middleChild);
		System.out.println("secondKey=> "+this.secondKey);
		System.out.println("secondOffset=> "+this.secondOffset);
		System.out.println("rightChild=> "+this.rightChild);
	}
	public boolean IsLeaf() {
		if(this.isLeaf == 0)
		{
			return true;
		}
		return false;
	}
	public int getLeaf()
	{
		return this.isLeaf;
	}
	public void setIsLeaf(int isLeaf) {
		this.isLeaf = isLeaf;
	}

	public int getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(int leftChild) {
		this.leftChild = leftChild;
	}

	public int getFirstKey() {
		return firstKey;
	}

	public void setFirstKey(int firstKey) {
		this.firstKey = firstKey;
	}

	public int getFirstOffset() {
		return firstOffset;
	}

	public void setFirstOffset(int firstOffset) {
		this.firstOffset = firstOffset;
	}

	public int getMiddleChild() {
		return middleChild;
	}

	public void setMiddleChild(int middleChild) {
		this.middleChild = middleChild;
	}

	public int getSecondKey() {
		return secondKey;
	}

	public void setSecondKey(int secondKey) {
		this.secondKey = secondKey;
	}

	public int getSecondOffset() {
		return secondOffset;
	}

	public void setSecondOffset(int secondOffset) {
		this.secondOffset = secondOffset;
	}

	public int getRightChild() {
		return rightChild;
	}

	public void setRightChild(int rightChild) {
		this.rightChild = rightChild;
	}

	
}
