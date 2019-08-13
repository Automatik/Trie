# Trie
A trie implementation in Java with the following features:
* finds words that start with the given prefix;
* finds all words occurring in a given pattern from left to right;

  e.g: given the pattern _"wverticall"_ the words found will be _"vertical"_, _"call"_ and _"all"_;
* finds all the valid words from the permutation of the given letters;

  e.g: given the letters _"a e r d"_ the words found will be _"dare"_,_"dear"_,_"are"_,_"rad"_,_"red"_,_"read"_,_"ear"_ and _"era"_;
* finds all the words that fit in the given string expression according to the wildcards' position;

  e.g: given the expression _"s??ce"_, where the symbol _?_ is the wildcard, the words found will be _"slice"_, _"space"_, _"since"_, ecc ...
  
Useful for finding anagrams and words.
A faster and lighter implementation is given by this [DoubleArrayTrie](https://github.com/Automatik/DoubleArrayTrie)
## Usage
Create and populate the Trie
```
Trie trie = new Trie();
for(String word : myDictionary) {
  trie.insert(word);
}
```
Only for the `match` method you need to call first the `buildSuffixLinks` method
```
trie.buildSuffixLinks();
ArrayList<String> words = trie.match("wverticall");
```
## ToDo
- [ ] Improve performance of `permute` and `query` methods by making them from recursive to iterative 
- [ ] Write own writeObject and readObject methods to speed up serialization and reduce the risk of `StackOverflow`
