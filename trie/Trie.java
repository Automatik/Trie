/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Trie implementation with some more features.
 * @link https://github.com/Automatik/Trie
 * @version 1.0
 * @author Emil
 */

public class Trie implements Serializable {

    /**
     * The default alphabet size correspond to the english alphabet
     */
    public static final int ENGLISH_ALPHABET_SIZE = 26;

    /**
     * The symbol used as wildcard in the following method
     * @see #query(String expression)
     */
    public static final char WILDCARD = '?';

    private static final long serialVersionUID = 1L;

    private TrieNode root;

    private int alphabetSize;

    private boolean suffixLinksBuild = false;

    /**
     * Create new Trie with only the root and the nodes' children length set to english alphabet's size
     * @see #ENGLISH_ALPHABET_SIZE
     */
    public Trie() {
        this(new TrieNode(ENGLISH_ALPHABET_SIZE));
    }

    /**
     * Create new Trie with all the children from the given root and
     * set the alphabet size to the root's children length
     * @param root the root of the Trie
     */
    public Trie(TrieNode root) {
        this.root = root;
        setAlphabetSize(root.children.length);
    }

    /**
     * Create new Trie with only the root and set the alphabet size according to the given parameter
     * @param alphabetSize the length of the alphabet will be the nodes' children length
     */
    public Trie(int alphabetSize) {
        root = new TrieNode(alphabetSize);
        setAlphabetSize(alphabetSize);
    }

    /**
     * Inserts a word into the Trie
     * @param word the word to insert
     * @throws IllegalArgumentException if the word is null
     */
    public void insert(String word){
        if(word == null)
            throw new IllegalArgumentException("String word argument is null");
        TrieNode current = root;
        //int depth = 0;
        for(char c : word.toCharArray()){
            int index = c - 'a';
            if(current.children[index] == null)//Create new node
                current.children[index] = new TrieNode(current, alphabetSize, c);
            current = current.children[index];
            //current.outDegree = ++depth;
        }
        current.isWord = true; 
    }

    /**
     * @return the current alphabet size for this Trie
     */
    public int getAlphabetSize() {
        return alphabetSize;
    }

    /**
     * Sets the alphabet size for this Trie
     * @param alphabetSize the length of the alphabet corresponding to nodes' children length
     */
    private void setAlphabetSize(int alphabetSize) {
        if(alphabetSize < 1)
            throw new IllegalArgumentException("Alphabet size must be positive");
        this.alphabetSize = alphabetSize;
    }

    /**
     * Returns true if the word is in this Trie
     * @param word the word to search in this Trie
     * @return true if the word is in this Trie
     */
    public boolean contains(String word){
        TrieNode current = getTrieNode(word);
        return current != null && current.isWord;
    }

    /**
     * Find all words which starts with the given prefix
     * @param prefix the characters with which start the words
     * @return ArrayList with all the words that starts with the given prefix
     */
    public ArrayList<String> startsWith(String prefix){
        TrieNode current = getTrieNode(prefix);
        return DFS(current, prefix);
    }

    /**
     * Gets the TrieNode whose children have all the given prefix
     * @param prefix the prefix with which the words start
     * @return TrieNode
     */
    public TrieNode getTrieNode(String prefix) {
        TrieNode current = root;
        for(char c : prefix.toCharArray()) {
            int index = c - 'a';
            if(current.children[index] == null)
                return null;
            current = current.children[index];
        }
        return (current == root) ? null : current;
    }
    
    //Recursive (elegance)
    private void DFS(TrieNode root, String prefix, List<String> words){
        if(root == null)
            throw new IllegalArgumentException("TrieNode root is null");
        if(root.isWord)
            words.add(prefix);
        for(int i = 0; i < alphabetSize; i++)
            if(root.children[i] != null)
                DFS(root.children[i], prefix + (char) (i + 'a'), words);
    }
  
    //Iterative (performance)
    private ArrayList<String> DFS(TrieNode root, String prefix) {
        ArrayList<String> words = new ArrayList<>();
        ArrayDeque<TrieNode> nodesQueue = new ArrayDeque<>();
        ArrayDeque<String> prefixQueue = new ArrayDeque<>();
        if(root.isWord)
            words.add(prefix);
        nodesQueue.offer(root);
        prefixQueue.offer(prefix);
        while(!nodesQueue.isEmpty()){
            TrieNode current = nodesQueue.poll();
            String prefixTemp = prefixQueue.poll();
            for(int i = 0; i < alphabetSize; i++){
                TrieNode temp = current.children[i];
                if(temp != null){
                    nodesQueue.offer(temp);
                    prefixQueue.offer(prefixTemp + (char)(i + 'a'));
                    if(temp.isWord)
                        words.add(prefixTemp + (char)(i + 'a'));
                }
            }
        }
        return words;
    }
    
    //Finds all occurrences of all array words in pattern
    //Suffix Links should already be built

    /**
     * Finds all words occurring in a given pattern from left to right.
     * Suffix Links must be build first from the buildSuffixLinks method.
     * e.g: given the pattern "wverticall" the words found will be
     * "vertical", "call" and "all".
     * @see #buildSuffixLinks()
     * @param pattern the letters in which to search words
     * @throws IllegalStateException if suffix links are not build yet
     * @return ArrayList containing all the words found
     */
    public ArrayList<String> match(String pattern) {
        if(!suffixLinksBuild)
            throw new IllegalStateException("Suffix Links not build yet. Call first buildSuffixLinks method");
        ArrayList<String> words = new ArrayList<>();
        TrieNode current = root;
        char[] input = pattern.toCharArray();
        for(int i = 0; i < input.length; i++) {
            current = findNextNode(current, input[i]);
            boolean noMatch = false;
            //if match not found move to next node
            if(current.output == null)
                noMatch = true;
            if(noMatch && current.isWord)
                words.add(getStringWord(current));
            else if(!noMatch){
                TrieNode temp = current;
                while(temp != null){ // e != root ?
                    if(temp.isWord)
                        words.add(getStringWord(temp));
                    temp = temp.output;
                }
            }
        }
        return words;
    }
    
    //all valid words that are possible using Characters of Array

    /**
     * Finds all the valid words from the permutation of the given letters.
     * e.g: given the letters "a e r d" the words found will be
     * "dare","dear","are","rad","red","read","ear" and "era"
     * @param letters the characters to permute to find words
     * @return ArrayList with all the words found
     */
    public ArrayList<String> permute(char[] letters) {
        ArrayList<String> words = new ArrayList<>();
        Queue<Character> queue = new ArrayDeque<>(letters.length);
        for(char c : letters)
            queue.offer(c);
        permute(queue, "", words);
        return words;
    }
    
    private void permute(Queue<Character> letters, String current, List<String> words) {
        if(!current.equals("")){
            TrieNode node = getTrieNode(current);
            if(node != null && node.isWord)
                words.add(current);
        }
        if(!letters.isEmpty()){
            Character[] array = new Character[letters.size()];
            array = letters.toArray(array);
            for(Character ch : array){
                Queue<Character> temp = new ArrayDeque<>(array.length);
                temp.addAll(letters);
                temp.remove(ch);
                permute(temp, current + ch, words);
            }
        }
    }

    //String expression's length should be limited with max word of current dictionary

    /**
     * Finds all the words that fit in the given string expression according to the wildcards' position.
     * e.g: given the expression "s??ce" the words found will be "slice", "space", "since", ecc ...
     * @see #WILDCARD for the character to use as wildcard
     * @param expression the string containing the letters and wildcards
     * @return ArrayList containing all the words found
     */
    public ArrayList<String> query(String expression) {
        ArrayList<String> words = new ArrayList<>();       
        query(expression.toCharArray(),0, root, "", words);
        return words;
    }

    private void query(char[] expression, int index, TrieNode root, String current, List<String> words){
        if(root.isWord && current.length() == expression.length)
            words.add(current);
        if(index < expression.length) {
            char next = expression[index];
            if(next == WILDCARD) {
                int next_index = index + 1;
                for(int i = 0; i < alphabetSize; i++)
                    if(root.children[i] != null)
                        query(expression, next_index, root.children[i], current + (char)(i + 'a'), words);
            } else {
                int idx = next - 'a';
                if(root.children[idx] == null)
                    return;
                query(expression, index + 1, root.children[idx], current + next, words);
            }
        }
    }

    /**
     * Returns the next node the machine will transition to using goto and failure functions
     */
    private TrieNode findNextNode(TrieNode current, char nextInput) {
        int index = nextInput - 'a';
        //if goto is not defined(there is no word), use failure function
        while(current.children[index] == null && current != root)
            current = current.failure;
        return current.children[index];
    }

    /**
     * Returns the word starting from the leaf node till root
     */
    private String getStringWord(TrieNode end) {
        //end.isWord must be true
        StringBuilder sb = new StringBuilder();
        while(end != root){
            sb.append(end.character);
            end = end.parent;
        }
        return sb.reverse().toString();
    }

    /**
     * Builds the Suffix Links
     */
    public void buildSuffixLinks() {
        //Failure funcion is computed in BFS using a queue
        ArrayDeque<TrieNode> nodesQueue = new ArrayDeque<>();
        root.failure = root;
        //All nodes of depth 1 has root as suffix link
        for(int i = 0; i < alphabetSize; i++){
            TrieNode temp = root.children[i];
            if(temp != null){
                temp.failure = root;
                nodesQueue.offer(temp);
            }
        }
        while(!nodesQueue.isEmpty()) {
            TrieNode current = nodesQueue.poll();
            for(int i = 0; i < alphabetSize; i++) {
                if(current.children[i] != null) {
                    
                    //Find failure node of the current node
                    TrieNode fail = current.failure;
                    
                    //Find the deepest node labeled by proper
                    //suffix of string from root to current node
                    while(fail.children[i] == null && fail != root)
                        fail = fail.failure;
                    
                    fail = (fail.children[i] == null) ? root : fail.children[i];
                    current.children[i].failure = fail;
                    
                    //Set output link
                    current.children[i].output = (fail.isWord) ? fail : fail.output;
                    
                    nodesQueue.offer(current.children[i]);
                }
            }
        }
        suffixLinksBuild = true;
    }
}
