bugfree-octo-happiness
======================

The code in this repository has been written as an experiment (1 week) in load-balancing. The pdf file in 'report' explains the idea. Consistency of data is not guaranteed, we focused the experiment on load-balancing to address latency differential among stores.

We compute the latency of each transaction on each store. The database supervisor samples the latencies on each store, the samples are filtered through an exponential filter.
When a store is has a significantly higher latency, some of its data is transferred on an under-balanced store.
