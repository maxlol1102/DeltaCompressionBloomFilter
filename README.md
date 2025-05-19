# Delta-Compressed Bloom Filter (DECBF)

This project implements and benchmarks **Delta-Compressed Bloom Filters (DECBF)** for efficient data synchronization in distributed systems, inspired by the concepts outlined in *Delta Bloom Filters for Synchronization in Distributed Systems* and extended with support for Counting Bloom Filters (CBFs).

---

## Overview

Bloom filters are probabilistic data structures for fast set membership queries. This project enhances them to support **efficient synchronization** between nodes by:

- **Computing XOR deltas** between filter versions
- **Compressing deltas** using Run-Length Encoding (RLE)
- **Transmitting only the compressed delta**, not the full filter
- **Supporting multiple hash strategies** (e.g., MurmurHash, FNV, Universal)
- **Comparing delta-sync vs. full-sync strategies**
- **Integrating Counting Bloom Filters (CBF)** for deletions and advanced use cases

---

## Features

- ✅ Standard Bloom filter implementation with compression
- ✅ Delta encoding of Bloom filter differences
- ✅ Run-Length Encoding (RLE) for delta compression
- ✅ Counting Bloom Filter support for dynamic datasets
- ✅ Benchmark framework with configurable datasets
- ✅ Real-world password dataset for experimentation
- ✅ Supports multiple hash functions:
  - SimpleHash
  - BadHash
  - UniversalHash
  - MurmurHash
  - FNVHash
  - XXHash
  - HashMix

---

## 📁 Project Structure

DeltaCompressedBloomFilter/
├── bloom/ # BloomFilter and CountingBloomFilter classes
├── hash/ # Multiple hash function implementations
├── sync/ # DeltaEncoder and CountingDeltaEncoder
├── compress/ # RLECompressor implementation
├── benchmark/ # BenchmarkRunner and ReportGenerator
├── data/ # Input data (e.g., 100k-most-used-passwords-NCSC.txt)
├── main/ # Main class to execute experiments
└── benchmark_results.csv # Output CSV with experimental results



