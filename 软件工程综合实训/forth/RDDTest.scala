package jxufe.edu.spark.rddtest

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDDTest {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf()
    conf.setAppName("RDDTEST")
    conf.setMaster("local")
    val sc: SparkContext = new SparkContext(conf)

    println("-------------------map-----------------------")
    val array1 = Array(x=1,xs=2,3,4,5,6,7,8,9,10)
    val rdd1 = sc.parallelize(array1)
    rdd1.map(f=x=>x*2).foreach(println)

    val rdd2: RDD[(Int, Int)] = rdd1.map(f= x=>(x,1))
    rdd2.foreach(f=x=>println(x))

    println("-------------------filter-----------------------")
    val rdd3: RDD[Int] = rdd1.filter(f = x => if (x % 2 == 0) true else false)
    rdd3.foreach(f = x=>println(x))

    println("-----------------flatMap--------------------")
    val array2 = Array(xs="hello spark","hello hadoop","hello hive")

    val rdd4 = sc.parallelize(array2)

    val rdd5 = rdd4.flatMap(f = line=>line.split(" "))

    rdd5.foreach(println)

    println("-----------------mapPartitions--------------------")
    val rdd6 = sc.parallelize(seq = array1,3)
    val rdd7: RDD[Int] = rdd6.mapPartitions(f = x => {
      println("执行了一次分区处理")
      val newArray = x.toArray.map(f = y => y * y)
      newArray.toIterator
    })
    rdd7.foreach(println)

    println("-----------------mapPartitionsWithIndex--------------------")
    //x为分区编号，data为数据
    val rdd8: RDD[Int] = rdd6.mapPartitionsWithIndex(f = (x,data) => {
      println("执行了一次分区处理，分区号为："+x)
      val newArray = data.toArray.map(f = y => y * y)
      newArray.toIterator
    })
    rdd8.foreach(println)

    println("-----------------sample--------------------")
    /**
     * sample与action算子takesample的区别：
     * sample返回rdd；takesample返回array
     * 执行逻辑一样
     */
    val array3 = (1 to 100).toArray
    val rdd9: RDD[Int] = sc.parallelize(array3)
    //withReplacement: Boolean,样本是否放回
    //fraction: Double, 采样的比例
    //seed: Long = Utils.random.nextLong  随机数生成器的种子
    val res9: RDD[Int] = rdd9.sample(withReplacement = true, fraction = 0.1, 0)
    res9.foreach(println)

    println("-----------------aggregate--------------------")
    //求和
    //zeroValue : U 初始值，每个分区的初始值
    //seqOp : (U,Int) => U  迭代操作，拿RDD中的每个元素跟初始值进行聚合，在每个分区中执行
    //combOp : (U,U) => U  聚合每个分区的结果
    val sum: Int = rdd1.aggregate(zeroValue = 0)(seqOp = (a, b)=>a+b,
      combOp = (x, y)=>x+y)
    println("array1中所有数的和="+sum)

    //求平均值
    val result = rdd1.aggregate(zeroValue = (0,0))(seqOp = (u:(Int,Int),x:Int)=>(u._1+x,u._2 + 1),
      combOp = (a:(Int,Int),b:(Int,Int))=>(a._1+b._1,a._2+b._2))
    val avg = result._1.toDouble/result._2
    println("平均值="+avg)


    sc.stop()
  }

}
