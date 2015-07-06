import java.io.Serializable;

public class CelebTreeMap<String> implements Serializable{
	private String item;
	private CelebTreeMap<String> leftChild;
	private CelebTreeMap<String> rightChild;
	private String question;

	public CelebTreeMap(String newItem) {
		// Initializes tree node with item and no children.
		item = newItem;
		leftChild = null;
		rightChild = null;
	} // end constructor

	public CelebTreeMap(String newItem, CelebTreeMap<String> left, CelebTreeMap<String> right) {
		// Initializes tree node with item and the left and right children references.
		item = newItem;
		leftChild = left;
		rightChild = right;
	} // end constructor

	public CelebTreeMap(String newItem, CelebTreeMap<String> left) {
		// Initializes tree node with item and the left and right children references.
		item = newItem;
		leftChild = left;
		rightChild = null;
	} // end constructor
	
	public boolean isLeaf(){
		if( this.hasLeft() == false && this.hasRight() == false)
			return true;
		else
			return false;
	}
	
	
	public boolean hasQuestion(){
		if(question == null)
			return false;
		else
			return true;
	}
	
	public boolean hasLeft(){
		if(leftChild == null)
			return false;
		else
			return true;
	}
	public boolean hasRight(){
		if(rightChild == null)
			return false;
		else 
			return true;
	}
	
	public void setQuestion(String question){
		
		this.question = question;
	}
	
	public String getQuestion(){
		return question;
	}

	public String getItem() {
		// Returns the item field.
		return item;
	} // end getItem

	public void setItem(String newItem) {
		// Sets the item field to the new value newItem.
		item = newItem;
	} // end setItem

	public CelebTreeMap<String> getLeft() {
		// Returns the reference to the left child.
		return leftChild;
	} // end getLeft

	public void setLeft(CelebTreeMap<String> left) {
		// Sets the left child reference to left.
		leftChild = left;
	} // end setLeft

	public CelebTreeMap<String> getRight() {
		// Returns the reference to the right child.
		return rightChild;
	} // end getRight

	public void setRight(CelebTreeMap<String> right) {
		// Sets the right child reference to right.
		rightChild = right;
	} // end setRight
} // end TreeNode
