
public class PowerGridUtils {

	// Return the IDs of all elements in the power grid pg in pre-order.
	public static Queue<Integer> collectPreorder(GT<PGElem> pg) {
		Queue<Integer> q = new LinkedQueue<Integer>();
		if (pg == null)
			return q;
		if (pg.empty())
			return q;
		pg.findRoot();
		q.enqueue(pg.retrieve().getId());
		return collectPreorderHelp(pg, q);
	}

	private static Queue<Integer> collectPreorderHelp(GT<PGElem> pg, Queue<Integer> q) {
		int n = pg.nbChildren();
		for (int i = 0; i < n; i++) {
			pg.findChild(i);
			q.enqueue(pg.retrieve().getId());
			collectPreorderHelp(pg, q);
		}
		pg.findParent();
		return q;
	}

	// Searches the power grid pg for the element with ID id. If found, it is made
	// current and true is returned, otherwise false is returned.
	public static boolean find(GT<PGElem> pg, int id) {
		if (pg == null)
			return false;
		if (pg.empty())
			return false;
		pg.findRoot();
		return findHelp(pg, id);

	}

	private static boolean findHelp(GT<PGElem> pg, int id) {
		if (pg.retrieve().getId() == id)
			return true;
		boolean flag = false;
		int n = pg.nbChildren();
		for (int i = 0; i < n ;i++) {
			pg.findChild(i);
			flag = findHelp(pg, id);
			if (flag == true)
				break;
		}
		if (!flag)
			pg.findParent();
		return flag;
	}

	// Add the generator element gen to the power grid pg. This can only be done if
	// the grid is empty. If successful, the method returns true. If there is
	// already a generator, or gen is not of type Generator, false is returned.
	public static boolean addGenerator(GT<PGElem> pg, PGElem gen) {
		if (pg == null || gen == null)
			return false;
		pg.findRoot();
		if (pg.empty() && gen.getType().equals(ElemType.Generator)) {
			pg.insert(gen);
			return true;
		}
		return false;
	}

	// Attaches pgn to the element id and returns true if successful.
	// Note that a consumer can only be attached to a transmitter,
	// and no element can be be attached to it.
	// The tree must not contain more than one generator located at the root. If id
	// does not exist, or there is already an element with the same ID as pgn, pgn
	// is not attached, and the method retrurns false.
	public static boolean attach(GT<PGElem> pg, PGElem pgn, int id) {
		if(pg == null || pgn == null)
			return false;
		if(pg.empty())
			return false;
		if (find(pg, pgn.getId()))
			return false;
		if (find(pg, id)) {
			if (pg.retrieve().getType().equals(ElemType.Consumer))
				return false;
			if (pgn.getType().equals(ElemType.Consumer)) {
				if (pg.retrieve().getType().equals(ElemType.Transmitter)) {
					pg.insert(pgn);
					return true;
				} else
					return false;
			} else {
				if(!(pgn.getType().equals(ElemType.Generator))) {
					pg.insert(pgn);
					return true;
				}
			}
		}
		return false;
	}

	// Removes element by ID, all corresponding subtree is removed. If removed, true
	// is returned, false is returned otherwise.
	public static boolean remove(GT<PGElem> pg, int id) {
		if (pg == null)
			return false;
		if(pg.empty())
			return false;
		if(find(pg,id)) {
			pg.remove();
			return true;
		}
		return false;
	}

	// Computes total power that consumed by an element (and all its subtree). If id
	// is incorrect, -1 is returned.
	public static double totalPower(GT<PGElem> pg, int id) {
		if (pg == null)
			return -1;
		if(pg.empty())
			return -1;
		if (find(pg, id))
			return totalPowerHelp(pg);
		return -1;
	}

	private static double totalPowerHelp(GT<PGElem> pg) {
		double total = 0.0;
		int n = pg.nbChildren();
		if (pg.retrieve().getType().equals(ElemType.Consumer)) {
			return pg.retrieve().getPower();
		}
		for (int i = 0; i < n; i++) {
			pg.findChild(i);
			if (pg.retrieve().getType().equals(ElemType.Consumer)) {
				total += pg.retrieve().getPower();
				pg.findParent();
			} else
				total += totalPowerHelp(pg);
		}
		pg.findParent();

		return total;
	}

	// Checks if the power grid contains an overload. The method returns the ID of
	// the first element preorder that has an overload and -1 if there is no
	// overload.
	public static int findOverload(GT<PGElem> pg) {
		if (pg == null)
			return -1;
		if(pg.empty())
			return -1;
		pg.findRoot();
		return findOverloadHelp(pg);
	}

	private static int findOverloadHelp(GT<PGElem> pg) {
		double total = 0.0;
		double totalC = 0.0;
		int n = pg.nbChildren();
		for (int i = 0; i < n; i++) {
			pg.findChild(i);
			if (pg.retrieve().getType().equals(ElemType.Transmitter) || pg.retrieve().getType().equals(ElemType.Generator)) {
				int id = pg.retrieve().getId();
				total = pg.retrieve().getPower();
				totalC = totalPower(pg, pg.retrieve().getId());
				find(pg, id);
			} else
				totalC = 0;

			if (totalC > total)
				return pg.retrieve().getId();
			else
				totalC += findOverloadHelp(pg);
		}
		pg.findParent();

		return -1;
	}

}
