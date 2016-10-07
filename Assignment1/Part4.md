#Part4
##1
BFS is not necessarily a better strategy. Because we want to find a sentence with the highest probability, we would need to traverse the whole tree/generate all possible result and find the best one. If we have a completed sentence, we could skip nodes (incomplete sentence) that have lower probability than the completed one. However, for BFS we can't reach a complete sentence until we have visited all N-1 levels of the tree.
##2
Our heuristic function evaluates a node in the following way:- Find the total probability of a [word=A, type=B] pair- Divide this by the total probability of all (word=*, type=B) pairs that have the same type. The idea behind the function is explained below.Since heuristic is basically a "relaxed" version of the original search problem, we would like to try some naive yet intuitive way for constructing a sentence with highest probability. One such way of constructing such a sentence is to find words given a sentence spec, that have a naturally high probability of appearing in the given text corpus.For example, let us assume that we have the following sentence spec: Sentence start with 'I', and ends with a noun(NN). I would scan the corpus and discover that 'water' is the most frequently used noun. So I would like my sentence to end with water, for the initial attempt. To choose the highest used next word, we would like to use the number of appearance of a word divided by the number of appearance of all words of that sentence spec type (Here we only consider the second word and type pairs, ignoring the type of starting word from our corpus). This collapse down to our heuristic function, guarantees us a value that is at most the probability given in the corpus (the second [word, type] pair appearing after the first). In all other cases, we guarantee that the heuristic value is less than the given probability.
##3
No, since our heuristic value only concern about the second word and its type, ignoring the restriction that the first word is already given, there would be scenarios that heuristic value is much higher than the actual value, and therefore the sentence we generated is not the highest probability sentence.

##4
Yes, heuristic search can have worse performance than other search.Since our heuristic function only evaluate on the next word, and does not consider any possible sentence following that word. It is possible that no sentence is valid given that word following the given sentence spec, or all possible sentences have really low probability compare to other words the fulfill the same sentence spec as the chosen word.



 
