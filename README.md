# tinysearch

<b>Overview</b>
Tiny search is a mini search engine that builds a disk positional index from a directory of files and then can query them returning results either based on boolean or a ranked retrieval system. It utilizes the Porter stemming method to process documents and calculates document weights for the ranked retrieval system.

<b>Process</b>
The first step of this project was to build an application to index a directory of files using a positional inverted index and then implement boolean retrieval for queries. I used the porter stemmer as a basis to process tokens and I implemented NOT query parsing in addition to the required AND and OR.
In the second milestone we implemented a ranked retrieval system in addition to the boolean retrieval system in milestone 1. We also scaled the application up to be a disk-based inverted index. To implement the ranked retrieval we calculated the document weights using Euclidian normalization. For my additional features at this step I chose to implement precomputed logarithms to speed up the calculation of the document weights.

<b>Challenges</b>
This was a project I stubbornly chose to work alone on, and therefore ran out of time for a lot of the features that I would have chosen to implement or expand on for a variety of reasons. In hindsight I would have rather worked with a team and gotten to explore some of the optional features my professor had suggested, like implementing a GUI and exploring spelling correction and incorporating wildcard querying.
We also were challenged to only use arrays when returning lists of postings that matched the query, meaning we had to use stepping algorithms rather than methods such as retainAll when merging two postings lists. This ended up taking more dedicated attention than using Collections would have.
Whats next? I'd like to go back and expand on some of the optional features that I didn't get to. Also I never managed to get the third milestone working, which was an experiment using Rocchio and Bayesian classification to identify the mystery authors of the 11 Federalist Papers with unknown/unconfirmed authorship, and I'd like to get that working without error.
