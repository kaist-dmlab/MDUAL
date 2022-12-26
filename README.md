# MDUAL
# Multiple Dynamic Outlier-Detection from a Data Stream by Exploiting Duality of Data and Queries

This is the implementation of the paper published in SIGMOD 2021 [[Paper](https://drive.google.com/file/d/13lANlork5a42Uro4QIISrk-mQMymGgfV/view?usp=sharing)] [[Slide](XX)] [[Poster](XX)] [[Video](XX)]

## 1. Overview
Real-time outlier detection from a data stream has become increasingly important in the current hyperconnected world. This paper focuses on an important yet unaddressed challenge in continuous outlier detection: the multiplicity and dynamicity of queries. This challenge arises from various contexts of outliers evolving over time, but the state-of-the-art algorithms cannot handle the challenge effectively, as they can only process a fixed set of outlier detection queries for each data point separately. In this paper, we propose a novel algorithm, abbreviated as MDUAL, based on a new idea called duality-based unified processing. The underlying rationale is to exploit the duality of data and queries so that a group of similar data points are processed together by a group of similar queries incrementally. Two main techniques embodying the idea, data-query grouping and prioritized group processing, are employed. Comprehensive experiments showed that MDUAL runs 216 to 221 times faster while consuming 11 to 13 times less memory than the state-of-the-art algorithms through its efficient and effective handling of the multiplicity–dynamicity challenge.

## 2. Data Sets
| Name    | # data points  | # Dim    | Size    | Link           |
| :-----: | :------------: | :------: |:-------:|:--------------:|
| STK     | 1.05M          | 1        |  7.57MB |[link](https://infolab.usc.edu/Luan/Outlier/Datasets/stock.txt) |
| TAO     | 0.58M          | 3        |  10.7MB |[link](https://infolab.usc.edu/Luan/Outlier/Datasets/tao.txt) |
| HPC     | 1M             | 7        |  28.4MB  |[link](https://infolab.usc.edu/Luan/Outlier/Datasets/household2.txt) |
| GAS     | 0.93M          | 10       |  70.7MB  |[link](http://archive.ics.uci.edu/ml/machine-learning-databases/00362/HT_Sensor_UCIsubmission.zip) |
| EM      | 1M             | 16       |  119MB  |[link](https://infolab.usc.edu/Luan/Outlier/Datasets/ethylene.txt) |
| FC      | 1M             | 55       |  72.2MB  |[link](https://infolab.usc.edu/Luan/Outlier/Datasets/fc.data) |

## 3. Configuration
MDUAL algorithm was implemented in JAVA and run on **JDK 1.8.0_252.**
- Edit test/testLoad.java to set experiment parameters (dataset, num of queries, change rate, repeat num, etc.)
- Compile and run
```
cd ~/MDUAL/src
javac test/testLoad.java
java test.testLoad
```
- Example output
```
Dataset Queryset ChgQRatio Time AvgMem PeakMem #Out #OutQ   
STK STK_Q10 0.2 2.42 3.3 13.5 5 10  
```

## 4. Citation
```
@inproceedings{Yoon2021MDUAL,
  title={Multiple Dynamic Outlier-Detection from a Data Stream by Exploiting Duality of Data and Queries},
  author={Yoon, Susik and Shin, Yooju and Lee, Jae-Gil and Lee, Byung Suk},
  booktitle={Proceedings of the 2021 ACM SIGMOD International Conference on Management of Data},
  year={2021}
}
````

