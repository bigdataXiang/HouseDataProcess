# 项目说明
+ 该项目重在于完成房地产数据从空间化,格网化，插值，区划等一系列的操作流程。
+ 数据的获取主要集中于项目【Crawl】中，目前该项目实现了数据的实时动态抓取和入库
  等步骤。
# 项目流程
+ 空间化
利用【geocoding】中的【BatchProcess】程序，可以对本地的txt形式的房地产文件进
行批量化的地理编码处理

在空间化处理的过程中，有一部分数据因为地址名字不完全或者比较生僻，地名库没有，
故存在【nonPostalCoor】文件夹中，有时间需要把这部分数据处理一下
【/media/bigdataxiang/data/houseprice/temp_woaiwojia/nonPostalCoor】


+ 属性字段的标准化
利用【field_standardization】中的【toMongo.java】程序，实现将空间化的房地产
数据进行属性字段的名称的标准化，综合信息的分割，如“两室一厅”的分割
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
 在数据进行去重之前，首先要复制一个备份的数据表，名为
【BasicData_Resold_50_backup】该表包含冗余数据,之后，
 再对【BasicData_Resold_50】数据进行去重处理

+ 2016年12月13日
【182.168.6.9】上的mongodb数据库中的【temp_houseprice】中的表
【BasicData_Resold_50】被删除，该表之前存储了2015年10月至2016年5月的二手房去
 重数据。现在需要将【temp_houseprice】作为【linux/windows/182.168.6.9】三者
 的文件临时中转站。该数据库现在要临时存储linux传送到windows上的数据，即linux中
 的mongodb的【houseprice】中的【BasicData_Resold_50】，该数据是未去重的2015
 年10月到2016年10月的数据，共计6409691条。

+ 2016年12月14日
1.将【182.168.6.9】中的【BasicData_Resold_50】6409691条数据复制到windows的
  数据库【paper】,该数据库存储了论文所需要的所有进程的数据。

2.在项目中设置package【grid50】,该包内包含了数据处理的所有流程；

3.新建【BatchProcess_1】类，该类将存储在本地的原始数据文件存储进行批量的地理
  编码，其中"1"表示是数据处理的第一步；

4.新建【ToMongo_2】类，该类将批量地理编码的数据导入到数据库中；

5.新建【RemovalDuplicate_3】类，该类的主要作用是去除【paper】中的
 【BasicData_Resold_50】6409691条数据中的重复数据。
  进行该一步的程序是用js写的，存储在"D:\ruanjian\MongoDB\bin"中
  执行命令【mongo 127.0.0.1/paper BasicData_50.js】
  【mongo 127.0.0.1/pois poi.js】
  
  去重后【BasicData_Resold_50】表中的数据量为3017291条。
  
6.新建【BasicData_Clean_4】，该类主要是清除【BasicData_Resold_50】数据库中的
  噪音数据，将所有的数据都统一成【String】类型，为了避免数据类型的不一致造成的
  数据处理麻烦。再次清洗后的数据存在【BasicData_Resold】此时每一条数据都会含
  有【50m*50m】的[row,col,code]的属性；
  
7.新建【CompareSource_5】类，该类是比较同一格网内的不同房地产网站的房源数据情况
  通过对比，发现不同网站的数据报价一致，因此所有数据一视同仁；
    
8.新建【GridData_Resold_6】类，该类用于生成50m*50m的"混合像元"数据。
  这一步非常重要，这一步不仅要算出格网的平均房价，还要统计出格网的所有房源信息，
  这需要将房产投资的那一部分内容加进来，融合成数据统计结构形式,
  最后的结果存储于【GridData_Resold】表；
  
  运行情况：
  2015年07月-2015年09月数据运行正常
  2015年10月程序溢出
  2015年11月程序溢出
  2015年12月程序溢出
  2016年01月至09月数据运行完毕，但是其中06、07、08、09这几个月有格网的数据有问题
  问题如下：
  { "_id" : { "$oid" : "58510c1011a746dd9c899268"} , "url" : "http://bj.5i5j.com/exchange/126002179" , "source" : "woaiwojia" , "region" : "北京市朝阳区常营乡" , "location" : "朝阳-常营" , "community" : "中弘·北京像素北区(在售144套)" , "price" : "210" , "area" : "0" , "unit_price" : "Infinity" , "year" : "2016" , "month" : "06" , "day" : "04" , "time" : "2016/06/0412:56:31" , "lng" : "116.59897" , "lat" : "39.92365" , "code" : "4314006" , "row" : "1079" , "col" : "2006" , "house_type" : "2室1厅1卫" , "rooms" : "2" , "halls" : "1" , "bathrooms" : "1" , "floor" : "下部/17" , "flooron" : "下部" , "floors" : "17" , "title" : "您正在查看王文君发布的北京中弘·北京像素北区房源" , "property_company" : "" , "green_rate" : "" , "direction" : "东" , "heat_supply" : "自供暖" , "property" : "公寓普通住宅" , "property_fee" : "" , "down_payment" : "63万" , "volume_rate" : "" , "households" : "" , "developer" : "" , "totalarea" : "" , "month_payment" : "8485元" , "built_year" : "2008-01-01"}

  2016年10月程序正常运行
  2016年11月程序于【2016年12月15日】下午新增，为了检查出今年930新政后的房价情况
  
+ 2016年12月15日
  2015年10月程序跑了一晚上了仍然在跑~~
  同时把2015年11月和2015年12月的程序也在linux上跑起来吧！
  将window上的【paper】数据库复制一份到【服务器】和【linux】上
  
  在【linux】上出现了一个问题，即不能用【double】形式的字符串作为BSON数据格式
  的key，在入库的时候会抛出异常，而在windows上可以是因为【.】号被转义了。
  
 【.】在mongodb中有特殊的含义。
 
  目前已经把2015年10月、11月、12月的数据放在linux上面跑。并且先存储与本地，之后
  再统一导入【paper】的【GridData】中
  
  用程序【SolveDotProblem】类解决了【.】的问题，并且把linux上的10.、11、12月
  的数据一起融合到本地的【temp】数据库的【temp】集合中
  此外，在【D】盘中备份了一份，其中：
  gridNew.txt：表示去除了【.】异常的数据；
  gridOld.txt：表示没有去除【.】异常的数据；
  nullException.txt：在运行中有异常的数据； 

9.新建【PBSHADE_Spatial_7】类，利用【P-BSHADE】插值方法进行空间插值，
  
  
10.新建【PBSHADE_Time_8】类，利用【P-BSHADE】插值方法进行时间插值，
   该部分暂时不用

11.新建【Neighbor_Interpolation_9】类，利用【PBSHADE_Spatial_7】中插值成功
   的数据进行邻近插值的源数据
   
12.新建【ContourLine_10】类，由插值结果生成等值线

+ 2016年12月16日

8. 在使用【SolveDotProblem】进行数据收拢的时候，又犯了一次错误，就是关于【db】
   的问题。一旦用db链接上了一个数据库，由于链接db在类【db】中属于全局变量，所以
   同一程序中只能开一个数据库。
   
   之后2015-07月到2016年-10月的数据还是存储到了【paper】的【temp】集合中。

   【D:\小论文\PBSHADE-邻近插值\1.插值的源数据】中文件说明：
     gridNew.txt：表示去除了【.】异常的数据；
     gridOld.txt：表示没有去除【.】异常的数据；
     nullException.txt：在运行中有异常的数据；
     
   使用【CopyCollections】类，将【paper】中【temp】的数据复制到【192.168.6.9】
   中【paper】的【GridData_Resold】中，共计：140794
   
   将本地windows的【paper】的【GridData_Resold】删除，然后将本地【paper】的【temp】
   赋值成新的【GridData_Resold】
   
   将本地windows的【paper】的【BasicData_Resold_50】复制到【192.168.6.9】中【paper】
   的【BasicData_Resold_50】，然后将本地windows的【BasicData_Resold_50】删除
   注意：【BasicData_Resold_50】数据是没有经历过类【BasicData_Clean_4】处理的原始
   数据，共计：3017291
 
9.新建【PBSHADE_Spatial_7】类，利用【P-BSHADE】插值方法进行空间插值， 
   插值的过程中没有处理好月份的问题，一共是17个月，其中个位数的月份前面是需要加“0”的
   此外，插值过程中的精度验证问题也不是百分之百靠谱，生成的15445条【interpolation_value_grids.txt】
   插值结果中，有84条数据是月份之间差绝对值超过3万或者价格为负的情况。
   上述验证结果在【Interpolation_Precision_Inspection】类中完成。
   这些数据需要找到真实数据验证后再作定夺

+ 2016年12月17日
11.【Neighbor_Interpolation_9】中每一步的执行过程都写在了main函数中，
    现在正在跑最漫长的邻近插值程序，插值完了之后，还需要对插值结果进行融合，
    生成最后的插值结果
    
    【Neighbor_Interpolation_9】第六步的执行时间为5h。生成两个文件：
    【以点代面_插值结果.txt】
    【以点代面_插值插值不成功结果.txt】
    
    之后再执行【step_3】生成融合数据
    
12.新建【GridInterpolation_11】类，将所有插值后的数据以【格网-月份】的形式存
   储到数据库【GridData_Resold_Interpolation】中，数据形式如下：
   【{code(int),row(int),col(int),year(int),month(int),price(double)}】
   
13.新建【GridAcceleration_12】程序，生成以房价加速度为值的栅格基元  
 
14.【ContourLine_10】类运行情况：
    linux有16g的运行内存，在linux上执行此程序能非常快，很快就能将这17个月的数据跑完。
    
    【pb】系列程序运行情况：
    linux上部署了2015-07至2015-10月份的数据区块分类
    服务器【192.168.6.168】部署了2015-11至2016-02月份的数据区块分类
    服务器【192.168.137.178】部署了2016-03至2016-07月份的数据区块分类

+ 2016年12月19日
14. 【ContourLine_10】类运行情况：
     【pb.py】运行除了大bug，每一个程序运行到2895行的时候就出现了bug，bug
     出现在
     【self.gArray[y][x] =self.gArray_ori[y][x]*100000+dic_label_p[str(self.gArray_ori[y][x])]  】
     原因是：python的range(a,b)函数的原因，只能从a取到b-1。而新的数据中出现了20的值，所以出现了
     越界异常。
     
     现在对数据进行重新运行：
     【linux】是2015-07到2015-09的数据
     【192.168.6.168】是2015-10到2015-12的数据
     【192.168.137.179】是2016-01到2016-04的数据
+ 2016年12月20日
     【linux】运行2016-05到2016-08的数据
     
  
+ 2016年12月24日
   这几天在为poi的数据整理弄得焦头烂额，但好歹有些许收获了。目前发现一个很重要的问题，
   小区的地址在数据库中都没有，不知什么原因。
+ 2016年12月25日
重新匹配地址所有小区的地址数据，并且进行插值计算
1、将所有小区的信息精准匹配
   匹配的数据源：shp_poi中的小区数据，高德地理编码，老师的地理编码（需要纠偏）
   匹配结果：存储于【D:\小论文\poi资料\小区\小区地理编码原始数据】
   
   【所有小区名称_去除冗余_匹配成功.txt】：根据【shp_poi中的小区数据】匹配到的小区数据，需要挑选出最精准的小区。
   其中包含两部分数据:
   【所有小区名称_去除冗余_匹配成功_部分匹配_shpPoi匹配结果.txt】：2020
   【所有小区名称_去除冗余_匹配成功_完全匹配_shpPoi匹配结果.txt】：2314
   
    利用老师地理编码得到的完全地址匹配的数据，已经高德纠偏：
   【所有小区名称_去除冗余_匹配不成功_原始_地址完全匹配_高德纠偏.txt】:6020
   【所有小区名称_去除冗余_匹配不成功_原始_地址部分匹配_高德纠偏.txt】2680
   
   
   
   
   
   
  