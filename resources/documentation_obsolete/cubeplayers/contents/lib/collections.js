/*
 * @(#)collections.js 1.1  2007-08-05
 *
 * Copyright (c) 2003 Werner Randelshofer, Switzerland. MIT License.
 */

/**
 * This file contains constructors for the following collections:
 * - Map
 * - TreeNode
 *
 * @version 1.1 2007-08-05 Added method get(key, defaultValue);
 */
var debug = false;

/**
 * Returns the index of the key or -1 if the map contains no
 * mapping for the specified key.
 * This operation performs a linear search and is O(n)
 * where n is the number of entries. 
 * This operation is for internal use by the map only!
 */
function Map_indexOf(key) {
  for (var i=0; i < this.entries.length; i++) {
	  if (this.entries[i].key == key) return i;
	}
	return -1;
}
/**
 * Returns true if this map contains a mapping for
 * the specified  key.
 * This operation performs a linear search and is O(n)
 * where n is the number of entries. 
 */
function Map_containsKey(key) {
  return this.indexOf(key) != -1;
}
/**
 * Returns the value to which this map maps the specified key. 
 * If default value is supplied, returns the default value, if
 * the map does not contain the key.
 */
function Map_get(key, defaultValue) {
  var i = this.indexOf(key);
	return (i == -1) ? defaultValue : this.entries[i].value;
}
/**
 * Associates the specified value with the specified key in this map.
 * If the map previously contained a mapping for this key, the old value
 * is replaced by the specified value. 
 */
function Map_put(key, value) {
  var i = this.indexOf(key); // linear search!
	if (i == -1) {
	  var newEntries = new Array(this.entries.length + 1);
		for (var i=0; i < this.entries.length; i++) {
		  newEntries[i] = this.entries[i];
		}
		newEntries[i] = new MapEntry(key, value);
		this.entries = newEntries;
	} else {
	  this.entries[i].value = value;
	}
}
/**
 * Removes the mapping for this key from this map if it is present.
 */
function Map_remove(key) {
  var ii = this.indexOf(key);
	if (ii != -1) {
	  var newEntries = new Array(this.entries.length - 1);
		var j = 0;
		for (var i=0; i < this.entries.length; i++) {
		  newEntries[j] = this.entries[i];
			if (j != ii) j++;
		}
		this.entries = newEntries;
	}
}

/**
 * Returns a descriptive string representation of this map.
 * The string can be used for debugging or for display of the contents
 * of the map, but it can not be imported using importFromString.
 *
 * The string has the following contents: "[key1=value1,key2=value2,...,keyn=valuen]"
 */
function Map_toString() {
  var str = "[";
	for (var i=0; i < this.entries.length; i++) {
		if (i != 0) str += ",";
	  str = str + this.entries[i].key + "=" + this.entries[i].value;
	}
	return str + "]";
}
/**
 * Exports the contents of this map to a string.
 *
 * The string has the following contents: "key1=value1 key2=value2 ... keyn=valuen"
 * The keys and values are escaped before they are added to the string.
 */
function Map_exportToString() {
  var str = "";
	for (var i=0; i < this.entries.length; i++) {
		if (i != 0) str += " ";
	  str = str + escape(this.entries[i].key) + "=" + escape(this.entries[i].value);
	}
	return str;
}
/**
 * Imports map entries from a string and adds them to this map.
 * This operation works only properly if none of the
 * keys or values in the string contain the characters '=' and ' '.
 *
 * The string must have the following contents: "key1=value1 key2=value2 ... keyn=valuen");
 * The keys and values must have been escaped before they were are added to the string.
 */
function Map_importFromString(str) {
  if (debug) alert("Map.importFromString("+str+")");
  var p1 = 0;
  var p2 = str.indexOf("=");
	while (p2 != -1) {
	  var p3 = str.indexOf(" ",p2+1);
		if (p3 == -1) p3 = str.length;
		this.put(unescape(str.substring(p1,p2)),unescape(str.substring(p2 + 1,p3)));
		p1 = p3 + 1;
	  p2 = str.indexOf("=",p1);
	}
}
/**
 * Exports the contents of this map to a string.
 * This operation works only properly if none of the
 * keys or values in the string contain the delimiter character specified 
 * by 'delim'.
 *
 * The string has the following contents: "key1 delim value1 delim key2 delim value2 ... keyn delim valuen");
 * Please note: there is no whitespace between keys, delimiters and values.
 * The keys and values are escaped before they are added to the string.
 */
function Map_exportToStringDelim(delim) {
  var str = "";
	for (var i=0; i < this.entries.length; i++) {
		if (i != 0) str += delim;
	  str = str + escape(this.entries[i].key) + delim + escape(this.entries[i].value);
	}
	return str;
}
/**
 * Imports map entries from a string and adds them to this map.
 * This operation works only properly if none of the
 * keys or values in the string contain the delimiter character
 *  specified by 'delim'.
 *
 * The string must have the following contents: "key1 delim value1 delim key2 delim value2 ... keyn delim valuen");
 * Please note: there is no whitespace between keys, delimiters and values.
 * The keys and values must have been escaped before they were are added to the string.
 */
function Map_importFromStringDelim(str, delim) {
  if (debug) alert("Map.importFromString("+str+","+delim+")");
  var p1 = 0;
  var p2 = str.indexOf(delim);
	while (p2 != -1) {
	  var p3 = str.indexOf(delim,p2+1);
		if (p3 == -1) p3 = str.length;
		this.put(unescape(str.substring(p1,p2)),unescape(str.substring(p2 + 1,p3)));
		p1 = p3 + 1;
	  p2 = str.indexOf(delim,p1);
	}
}
/**
 * Imports map entries from an array and adds them to this map.
 *
 * The array must have two dimensions. The first dimension represents the
 * entries. The second dimension represents the key and value paris.
 * Example: var myArray = [[key1,value1],[key2,value2],...[keyn,valuen]]
 */
function Map_importFromArray(a) {
  for (var i = 0; i < a.length; i++) {
	  this.put(a[i][0], a[i][1]);
	}
}
function Map_size() {
  return this.entries.length;
}
function Map_getEntry(i) {
  return this.entries[i];
}
/**
 * Removes all mappings from this map.
 */
function Map_clear() {
  this.entries = [];
}
/**
 * An entry of a map.
 */
function MapEntry(key, value) {
  this.key = key;
	this.value = value;
}

/**
 * Gets a string with the specified key and replaces
 * all occurences of "{i}" to the value in anArray[i]. 
 * Note: This function only works for map values that
 * are of type String.
 */
function Map_format(key, anArray) {
  var text = this.get(key);
	var pos1, pos2, index;
	while ((pos1 = text.indexOf("{")) != -1) {
	  	pos2 = text.indexOf("}", pos1);
		index = text.substring(pos1 + 1, pos2);
		text = text.substring(0, pos1)+anArray[index]+text.substring(pos2 + 1);
	}
	return text;
}

/**
 * A map with key/value entries.
 * The access operations of this map
 * perform linear searches and are O(n)
 * where n is the number of entries. 
 */
function Map() {
  this.entries = []
	
	this.clear = Map_clear;
	this.put = Map_put;
	this.get = Map_get;
	this.format = Map_format;
	this.remove = Map_remove;
	this.indexOf = Map_indexOf; // for private use only!
	this.containsKey = Map_containsKey;
	this.toString = Map_toString;
	this.exportToString = Map_exportToString;
	this.importFromString = Map_importFromString;
	this.exportToStringDelim = Map_exportToStringDelim;
	this.importFromStringDelim = Map_importFromStringDelim;
	this.size = Map_size;
	this.getEntry = Map_getEntry;
	this.importFromArray = Map_importFromArray;
	this.importFromArray = Map_importFromArray;
}



/**
 * Returns true if anotherNode is a descendant of this node.
 * If it is this node, one of this node's children, or a descendant
 * of one of this node's children. 
 *
 * Note that a node is considered a descendant of itself. If anotherNode
 * is null, returns false.
 *
 * This operation is at worst O(h) where h is the distance from the root
 * to anotherNode .
 *
 * @param anotherNode node to test as descendant of this node. 
 * @return true if this node is an ancestor of anotherNode
 */
function TreeNode_isNodeDescendant(anotherNode) {
	while (true) {
	  if (anotherNode == null) return false;
		if (anotherNode == this) return true;
		//if (! anotherNode.isTreeNode) alert("collections.js TreeNode.isNodeDescendant() is not a tree node:"+anotherNode);
		anotherNode = anotherNode.getParent();
	}
}
/**
 * Removes newChild from its parent and makes it a child of this node
 * by adding it to the end of this node's child array. 
 *
 * @param newChild A TreeNode to add as a child of this node 
 *
 * @param node The TreeNode to be added.
 */
function TreeNode_add(newChild) {
  if (debug) alert("TreeNode.add("+newChild+")");
  if (newChild.parent != null) newChild.parent.removeFromParent(newChild);
	
  var a = new Array(this.children.length + 1);
	for (var i=0; i < this.children.length; i++) {
	  a[i] = this.children[i];
	}
	a[this.children.length] = newChild;
	this.children = a;
	newChild.parent = this;
}
/**
 * Returns the index of the specified child in this
 * node's children array. If the specified node is
 * not a child of this node, returns -1 . This method
 * performs a linear search and is O(n) where n is
 * the number of children. 
 */ 
function TreeNode_getIndex(aChild) {
	for (var i=0; i < this.children.length; i++) {
	  if (this.children[i] == aChild) return i;
	}
	return -1;
}
/**
 * Removes the subtree rooted at this node from the
 * tree, giving this node a null parent.
 * Does nothing if this node is the root of its tree. 
 */
function TreeNode_removeFromParent() {
  if (this.parent != null) {
	  this.parent.remove(this.parent.getIndex(this));
	}
}
/**
 * Removes the parent of this node or null if this node has no parent.
 */
function TreeNode_getParent() {
  return this.parent;
}

/**
 * Removes the child at the specified index from this
 * node's children  and sets that node's parent to
 * null.
 */
function TreeNode_remove(index) {
	var a = new Array(this.children.length - 1);
	var j=0;
	for (var i=0; i < this.children.length; i++) {
		a[j] = this.children[i];
		if (i == index) {
			this.children[i].parent = null;
		} else {
			j++;
		}
	}
	this.children = a;
}

/**
 * Returns the root of the tree that contains this node.
 * The root is  the ancestor with a null parent. 
 *
 * @return the root of the tree that contains this node.
 */
function TreeNode_getRoot() {
  if (this.parent == null) return this;
	else return this.parent.getRoot();
}
/**
 * Returns the child at the specified index in this node's child array.
 *
 * @param	index	an index into this node's child array
 * @exception	ArrayIndexOutOfBoundsException	if <code>index</code>
 *						is out of bounds
 * @return	the TreeNode in this node's child array at  the specified index
 */
function TreeNode_getChildAt(index) {
  return this.children[index];
}
/**
 * Returns the number of children of this node.
 *
 * @return	an int giving the number of children of this node
 */
function TreeNode_getChildCount() {
	return this.children.length;
}
/**
 * Returns true if this node has no children.
 *
 * @return	true if this node has no children
 */
function TreeNode_isLeaf() {
  if (debug) alert("TreeNode@"+this.identifier+".isLeaf()");
	return (this.getChildCount() == 0);
}
/**
 * Returns the child in this node's child array that immediately
 * follows <code>aChild</code>, which must be a child of this node.  If
 * <code>aChild</code> is the last child, returns null.  This method
 * performs a linear search of this node's children for
 * <code>aChild</code> and is O(n) where n is the number of children.
 *
 * @return	the child of this node that immediately follows
 *		<code>aChild</code>
 */
function TreeNode_getChildAfter(aChild) {
  if (debug) alert("TreeNode@"+this.identifier+".getChildAfter("+aChild+"@"+aChild.identifier+")");
	var index = this.getIndex(aChild);		// linear search
	/*
	if (index == -1) {
		throw new IllegalArgumentException("node is not a child");
	}
	*/
	if (index < this.getChildCount() - 1) {
		return this.getChildAt(index + 1);
	} else {
		return null;
	}
}
/**
 * Returns the child in this node's child array that immediately
 * precedes <code>aChild</code>, which must be a child of this node.  If
 * <code>aChild</code> is the first child, returns null.  This method
 * performs a linear search of this node's children for <code>aChild</code>
 * and is O(n) where n is the number of children.
 *
 * @return	the child of this node that immediately precedes
 *		<code>aChild</code>
 */
function TreeNode_getChildBefore(aChild) {
  if (debug) alert("TreeNode@"+this.identifier+".getChildBefore("+aChild+"@"+aChild.identifier+")");
	var index = this.getIndex(aChild);		// linear search
	/*
	if (index == -1) {
		throw new IllegalArgumentException("argument is not a child");
	}
	*/
	if (index > 0) {
		return this.getChildAt(index - 1);
	} else {
		return null;
	}
}
/**
 * Returns this node's first child.  If this node has no children,
 * returns null.
 *
 * @return	the first child of this node
 */
function TreeNode_getFirstChild() {
  if (debug) alert("TreeNode@"+this.identifier+".getFirstChild()");
	return (this.getChildCount() == 0) ? null : this.getChildAt(0);
}
/**
 * Returns this node's last child.  If this node has no children,
 * returns null.
 *
 * @return	the last child of this node
 */
function TreeNode_getLastChild() {
  if (debug) alert("TreeNode@"+this.identifier+".getLastChild()");
	return (this.getChildCount() == 0) ? null : this.getChildAt(this.getChildCount()-1);
}

/**
 * Finds and returns the first leaf that is a descendant of this node --
 * either this node or its first child's first leaf.
 * Returns this node if it is a leaf.
 *
 * @return	the first leaf in the subtree rooted at this node
 */
function TreeNode_getFirstLeaf() {
  if (debug) alert("TreeNode@"+this.identifier+".getFirstLeaf()");
	var node = this;
	
	while (! node.isLeaf()) {
		node = node.getFirstChild();
	}
	
	return node;
}

/**
 * Finds and returns the last leaf that is a descendant of this node --
 * either this node or its last child's last leaf. 
 * Returns this node if it is a leaf.
 *
 * @return	the last leaf in the subtree rooted at this node
 */
function TreeNode_getLastLeaf() {
  if (debug) alert("TreeNode@"+this.identifier+".getLastLeaf()");
	var node = this;

	while (! node.isLeaf()) {
		node = node.getLastChild();
	}

	return node;
}

/**
 * Returns the next sibling of this node in the parent's children array.
 * Returns null if this node has no parent or is the parent's last child.
 * This method performs a linear search that is O(n) where n is the number
 * of children.
 *
 * @return	the sibling of this node that immediately follows this node
 */
function TreeNode_getNextSibling() {
  if (debug) alert("TreeNode@"+this.identifier+".getNextSibling()");
	var retval;
	var myParent = this.getParent();

	if (myParent == null) {
		retval = null;
	} else {
		retval = myParent.getChildAfter(this);	// linear search
	}
	/*
	if (retval != null && !isNodeSibling(retval)) {
		throw new Error("child of parent is not a sibling");
	}
  */
	return retval;
}
/**
 * Returns the previous sibling of this node in the parent's children
 * array.  Returns null if this node has no parent or is the parent's
 * first child.  This method performs a linear search that is O(n) where n
 * is the number of children.
 *
 * @return	the sibling of this node that immediately precedes this node
 */
function TreeNode_getPreviousSibling() {
  if (debug) alert("TreeNode@"+this.identifier+".getPreviousSibling()");
	var retval;
	var myParent = this.getParent();

	if (myParent == null) {
		retval = null;
	} else {
		retval = myParent.getChildBefore(this);	// linear search
	}
	/*
	if (retval != null && !isNodeSibling(retval)) {
		throw new Error("child of parent is not a sibling");
	}
	*/
	if (debugResults) alert("TreeNode@"+this.identifier+".getPreviousSibling():"+retval);
	return retval;
}
/**
 * Returns the node that follows this node in a preorder traversal of this
 * node's tree.  Returns null if this node is the last node of the
 * traversal.  This is an inefficient way to traverse the entire tree.
 *
 * @return	the node that follows this node in a preorder traversal, or
 *		null if this node is last
 */
function TreeNode_getNextNode() {
  if (debug) alert("TreeNode@"+this.identifier+".getNextNode()");
	if (this.getChildCount() == 0) {
		// No children, so look for nextSibling
		var nextSibling = this.getNextSibling();
		
		if (nextSibling == null) {
			var aNode = this.getParent();
		
			do {
				if (aNode == null) {
					return null;
				}

				nextSibling = aNode.getNextSibling();
				if (nextSibling != null) {
				  if (debugResults) alert("TreeNode@"+this.identifier+".getNextNode():"+this.nextSibling+"@"+this.nextSibling.identifier);
					return nextSibling;
				}

				aNode = aNode.getParent();
			} while(true);
		} else {
 		  if (debugResults) alert("TreeNode@"+this.identifier+".getNextNode():"+this.nextSibling+"@"+this.nextSibling.identifier);
			return nextSibling;
		}
	} else {
	  if (debugResults) alert("TreeNode@"+this.identifier+".getNextNode():"+this.getChildAt(0)+"@"+this.getChildAt(0).identifier);
		return this.getChildAt(0);
	}
}
/**
 * Returns the node that precedes this node in a preorder traversal of
 * this node's tree.  Returns <code>null</code> if this node is the
 * first node of the traversal -- the root of the tree. 
 * This is an inefficient way to
 * traverse the entire tree.
 *
 * @return	the node that precedes this node in a preorder traversal, or
 *		null if this node is the first
 */
function TreeNode_getPreviousNode() {
  if (debug) alert("TreeNode@"+this.identifier+".getPreviousNode()");
	var previousSibling;
	var myParent = this.getParent();
	
	if (myParent == null) {
		return null;
	}

	previousSibling = this.getPreviousSibling();

	if (previousSibling != null) {
		if (previousSibling.getChildCount() == 0)
			return previousSibling;
		else {
			return previousSibling.getLastLeaf();
			}
	} else {
		return myParent;
	}
}

/**
 * This is the constructor for a tree node.
 */
function TreeNode() {
  this.parent = null;
  this.children = [];
	
	this.add = TreeNode_add;
	this.getIndex = TreeNode_getIndex;
	this.remove = TreeNode_remove;
	this.removeFromParent = TreeNode_removeFromParent;
	this.getRoot = TreeNode_getRoot;

	this.getParent = TreeNode_getParent;
	this.getChildAt = TreeNode_getChildAt;
	this.getChildCount = TreeNode_getChildCount;
	this.isLeaf = TreeNode_isLeaf;
  this.getChildAfter = TreeNode_getChildAfter;
  this.getChildBefore = TreeNode_getChildBefore;
  this.getFirstChild = TreeNode_getFirstChild;
  this.getLastChild = TreeNode_getLastChild;
  this.getFirstLeaf = TreeNode_getFirstLeaf;
  this.getLastLeaf = TreeNode_getLastLeaf;
  this.getNextSibling = TreeNode_getNextSibling;
  this.getPreviousSibling = TreeNode_getPreviousSibling;
  this.getNextNode = TreeNode_getNextNode;
  this.getPreviousNode = TreeNode_getPreviousNode;
	this.isNodeDescendant = TreeNode_isNodeDescendant;
}

