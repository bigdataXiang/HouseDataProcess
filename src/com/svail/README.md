# 项目说明
+ 该项目重在于完成房地产数据从空间化,格网化，插值，区划等一系列的操作流程。
+ 数据的获取主要集中于项目【Crawl】中，目前该项目实现了数据的实时动态抓取和入库
等步骤。
# 项目流程
+ 空间化
利用【geocoding】中的【BatchProcess】程序，可以对本地的txt形式的房地产文件进行批量化
的地理编码处理

在空间化处理的过程中，有一部分数据因为地址名字不完全或者比较生僻，地名库没有，故存在
【nonPostalCoor】文件夹中，有时间需要把这部分数据处理一下
【/media/bigdataxiang/data/houseprice/temp_woaiwojia/nonPostalCoor】


+ 属性字段的标准化
利用【field_standardization】中的【toMongo.java】程序，实现将空间化的房地产数据进
行属性字段的名称的标准化，综合信息的分割，如“两室一厅”的分割

检查有无燕郊的数据，有的话将数据删除，因为燕郊的数据基本匹配错误了

+ 数据入库
数据入库的程序和属性字段标准化的程序是同一个程序

将经过上述处理的数据进行入库；
（1）将数据导入到本地的“houseprice”数据库中的【BasicData_Resold_50】中
（2）成功导入了6-11月份的房天下和安居客的数据之后，再将【houseprice_backup】
中的2015年10月到2016年5月份的数据融合到本地的【houseprice】数据库中的
【BasicData_Resold_50】中。

在数据导入的过程中发现，有个别数据值极低的噪音点，这些数据将严重影响整个
格网的数据准确度，因此需要进行这种噪音的去除

（3）现在已经成功将2015年10月至2016年11月的数据全部融合到linux系统下
的【houseprice】数据库下的【BasicData_Resold_50】的文件夹中，总计
6409691条数据

+ 数据库数据去重
在数据进行去重之前，首先要复制一个备份的数据表，名为【BasicData_Resold_50_backup】
该表包含冗余数据,之后，再对【BasicData_Resold_50】数据进行去重处理

+ 2016年12月13日
【182.168.6.9】上的mongodb数据库中的【temp_houseprice】中的表【BasicData_Resold_50】
被删除，该表之前存储了2015年10月至2016年5月的二手房去重数据。现在需要将【temp_houseprice】
作为【linux/windows/182.168.6.9】三者的文件临时中转站。该数据库现在要临时存储linux传送到windows
上的数据，即linux中的mongodb的【houseprice】中的【BasicData_Resold_50】，该数据是
未去重的2015年10月到2016年10月的数据，共计6409691条。

+ 2016年12月14日
1.将【182.168.6.9】中的【BasicData_Resold_50】6409691条数据复制到windows的数据库
【paper】,该数据库存储了论文所需要的所有进程的数据。

2.在项目中设置package【grid50】,该包内包含了数据处理的所有流程；

3.新建【BatchProcess_1】类，该类将存储在本地的原始数据文件存储进行批量的地理编码，
  其中"1"表示是数据处理的第一步；

4.新建【ToMongo_2】类，该类将批量地理编码的数据导入到数据库中；

5.新建【RemovalDuplicate_3】类，该类的主要作用是去除【paper】中的【BasicData_Resold_50】
  6409691条数据中的重复数据。
  进行该一步的程序是用js写的，存储在"D:\ruanjian\MongoDB\bin"中
  执行命令"mongo 127.0.0.1/paper BasicData_50.js"
  
  去重后【BasicData_Resold_50】表中的数据量为3017291条。
  
6.新建【BasicData_Clean_4】，该类主要是清除【BasicData_Resold_50】数据库中的噪音数据，
  将所有的数据都统一成【String】类型，为了避免数据类型的不一致造成的数据处理麻烦。
  再次清洗后的数据存在【BasicData_Resold】
  此时每一条数据都会含有【50m*50m】的[row,col,code]的属性
  
7.新建【CompareSource_5】类，该类是比较同一格网内的不同房地产网站的房源数据情况
  通过对比，发现不同网站的数据报价一致，因此所有数据一视同仁
    
8.新建【GridData_Resold_6】类，该类用于生成50m*50m的"混合像元"数据。
  这一步非常重要，这一步不仅要算出格网的平均房价，还要统计出格网的所有房源信息，
  这需要将房产投资的那一部分内容加进来，融合成数据结构形式
