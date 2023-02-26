# Big Data Group28

## Name

Big-data Assessed Coursework Group 2

## Description

Goal:
The core goal of this pipeline is to take in a large set of text documents and a set of user defined queries, then for each query, rank the text documents by relevance for that query, as well as filter out any overly similar documents in the final ranking. The top 10 documents for each query should be returned as output.

For each document and query

1. remove stopwords and apply stemming for document (processor provided->textPreProcessor.java)
2. get DPH score(calculate relevance)(provided->DPHScorer.java) , connect with document and query
3. textual distance(reduce duplication)(provided->TextDistanceCalculator.java)
4. ranking and return top 10 documents for current query(provided->RankedResult.java)
