package mnsk;


import java.util.List;

public class DataTree {

    String word = null;
    DataTree parent = null;
    DataTree leftChild = null;
    DataTree rightChild = null;
    int size = 0;

    public DataTree getParent() {
        return parent;
    }

    public String getWord() {
        return word;
    }

    public void setParent(DataTree parent) {
        this.parent = parent;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public DataTree getLeftChild() {
        return leftChild;
    }

    public DataTree getRightChild() {
        return rightChild;
    }

    public void setLeftChild(DataTree leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(DataTree rightChild) {
        this.rightChild = rightChild;
    }

    public  void buildTree(List<String> words) {
        if(words.size() == 0)
            return;
        this.word = words.get(0);
        size++;

        DataTree cur;

        for(int i = 1; i < words.size(); i++) {
            cur = this;

            while (true) {
                if (words.get(i).equals(cur.word))
                    break;
                if(words.get(i).compareTo(cur.word) > 0) {
                    if(cur.rightChild == null) {
                        DataTree newNode = new DataTree();
                        newNode.setWord(words.get(i));
                        newNode.setParent(cur);
                        cur.setRightChild(newNode);
                        size++;
                        break;
                    }
                    else
                        cur = cur.rightChild;
                }
                else {
                    if(cur.leftChild == null) {
                        DataTree newNode = new DataTree();
                        newNode.setWord(words.get(i));
                        newNode.setParent(cur);
                        cur.setLeftChild(newNode);
                        size++;
                        break;
                    }
                    else
                        cur = cur.leftChild;
                }
            }
        }
    }


    public boolean contains(String str) {
        DataTree cur = this;
        while(cur != null) {
            if(cur.word.equals(str))
                return true;
            if(str.compareTo(cur.word) > 0)
                cur = cur.rightChild;
            else
                cur = cur.leftChild;
        }
        return false;
    }

    public void toList(List<String> list) {
        list.add(word);
        if(leftChild == null && rightChild == null)
            return;
        if(leftChild != null)
            leftChild.toList(list);
        if(rightChild != null)
            rightChild.toList(list);
    }

    public int getSize() {
        return size;
    }
}
