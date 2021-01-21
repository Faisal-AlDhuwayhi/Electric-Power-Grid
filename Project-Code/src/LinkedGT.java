
class TNode<T> {
	public T data;
	public LinkedList<TNode<T>> childs;
	public TNode<T> parent;

	public TNode() {
		data = null;
		childs = new LinkedList<TNode<T>>();
	}

	public TNode(T val) {
		data = val;
		childs = new LinkedList<TNode<T>>();
	}

}

// Implementing the general tree (GT) interface
public class LinkedGT<T> implements GT<T> {
	private TNode<T> root;
	private TNode<T> current;

	// Return true if the tree is empty
	public boolean empty() {
		return root == null;
	}

	// Return true if the tree is full
	public boolean full() {
		return false;
	}

	// Return the data of the current node
	public T retrieve() {
		return current.data;
	}

	// Update the data of the current node
	public void update(T e) {
		current.data = e;
	}

	// If the tree is empty e is inserted as root. If the tree is not empty, e is
	// added as a child of the current node. The new node is made current and true
	// is returned.
	public boolean insert(T e) {
		TNode<T> t = new TNode<T>(e);
		if (empty()) {
			root = t;
			root.parent = null;
			current = root;
		} else {
			current.childs.insert(t);
			t.parent = current;
			current = t;
		}
		return true;
	}

	// Return the number of children of the current node.
	public int nbChildren() {
		return current.childs.size();
	}

	// Put current on the i-th child of the current node (starting from 0), if it
	// exists, and return true. If the child does not exist, current is not changed
	// and the method returns false.
	public boolean findChild(int i) {
		int n = current.childs.size();
		if (i >= n || i < 0)
			return false;
		current.childs.findfirst();
		TNode<T> t = current.childs.retrieve();
		for (int j = 0; j < n; j++) {
			if (j == i) {
				current = t;
				break;
			}
			current.childs.findnext();
			t = current.childs.retrieve();
		}
		return true;
	}

	// Put current on the parent of the current node. If the parent does not exist,
	// current does not change and false is returned.
	public boolean findParent() {
		if (current.parent == null)
			return false;

		current = current.parent;
		return true;
	}

	// Put current on the root. If the tree is empty nothing happens.
	public void findRoot() {
		if (empty())
			return;
		current = root;
	}

	// Remove the current subtree. The parent of current, if it exists, becomes the
	// new current.
	public void remove() {
		if (current.parent == null) {
			root = null;
			current = null;
			return;
		}
		TNode<T> t = current.parent;
		TNode<T> t2 = current;

		t.childs.findfirst();
		TNode<T> k = t.childs.retrieve();
		int n = t.childs.size();
		for (int i = 0; i < n; i++) {
			if (k.equals(t2)) {
				t.childs.remove();
				break;
			}
			t.childs.findnext();
			k = t.childs.retrieve();
		}
		current = t;
	}
}
