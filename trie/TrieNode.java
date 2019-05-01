package trie;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Node object for the Trie class
 * @version 1.0
 * @author Emil
 */

public class TrieNode implements Serializable {

    private static final long serialVersionUID = 2L;

    protected TrieNode[] children;
    protected boolean isWord;
    protected char character;
    //int outDegree; //Specifies character position in the word
    protected TrieNode parent; //Link to the parent node
    protected TrieNode failure; //Suffix(Failure) Link
    protected TrieNode output; //Output Link

    //Initialize your data structure here
    public TrieNode(int alphabetSize) {
        this.children = new TrieNode[alphabetSize];
        this.isWord = false;
        //outDegree = 0;
    }

    public TrieNode(TrieNode parent, int alphabetSize, char character) {
        this.parent = parent;
        this.children = new TrieNode[alphabetSize];
        this.isWord = false;
        this.character = character;
        //outDegree = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrieNode trieNode = (TrieNode) o;
        return isWord == trieNode.isWord &&
                character == trieNode.character &&
                Arrays.equals(children, trieNode.children) &&
                Objects.equals(parent, trieNode.parent) &&
                Objects.equals(failure, trieNode.failure) &&
                Objects.equals(output, trieNode.output);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(isWord, character, parent, failure, output);
        result = 31 * result + Arrays.hashCode(children);
        return result;
    }
}
