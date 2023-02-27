# Big Data Group28

## Name

Big-data Assessed Coursework Group 28

## Description

Goal:
The core goal of this pipeline is to take in a large set of text documents and a set of user defined queries, then for
each query, rank the text documents by relevance for that query, as well as filter out any overly similar documents in
the final ranking. The top 10 documents for each query should be returned as output.

For each query

1. remove stopwords and apply stemming for articles' title and paragraph content parts (processor provided->
   textPreProcessor.java)
2. get DPH score(calculate relevance)(provided->DPHScorer.java) , connect with document and query
3. textual distance(reduce duplication)(provided->TextDistanceCalculator.java)
4. ranking and return top 10 documents for current query(provided->RankedResult.java)

## Note

We have some differences in the actual process compared to the description above.
For details, please refer to our report or the comments in the source code.

We also add one running result of TREC_Washington_Post_collection.v3.example.json for reference.