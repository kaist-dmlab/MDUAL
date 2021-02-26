# MDUAL
# Multiple Dynamic Outlier-Detection from a Data Stream by Exploiting Duality of Data and Queries

This is the implementation of the paper published in SIGMOD 2021 [[Paper](XX)] [[Slide](XX)] [[Poster](XX)] [[Video](XX)]

## 1. Overview
TBD

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
Dataset &nbsp; Queryset &nbsp; ChgQRatio &nbsp; Time &nbsp; AvgMem &nbsp; PeakMem &nbsp; #Out &nbsp; #OutQ   
STK &nbsp; STK_Q10 &nbsp; 0.2 &nbsp; 2.42 &nbsp; 3.3 &nbsp; 13.5 &nbsp; 5 &nbsp; 10  

## 4. Citation
```
@inproceedings{Yoon2021MDUAL,
  title={Multiple Dynamic Outlier-Detection from a Data Stream by Exploiting Duality of Data and Queries},
  author={Yoon, Susik and Shin, Yooju and Lee, Jae-Gil and Lee, Byung Suk},
  booktitle={Proceedings of the 2021 ACM SIGMOD International Conference on Management of Data},
  year={2021}
}
````
