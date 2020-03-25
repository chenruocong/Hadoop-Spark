package jxufe.edu.spark.rddtest

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object PairRDDTest {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf();
    sparkConf.setAppName("PairRDD_Test")
    sparkConf.setMaster("local")
    val sparkContext = new SparkContext(sparkConf);

    val data = List(("spark",55),("hadoop",68),("hive",80),("hbase",84),("flink",90),("spark",82))
    val rdd1: RDD[(String, Int)] = sparkContext.makeRDD(data)

    println("-----------------groupByKey--------------------")
    val result: RDD[(String, Iterable[Int])] = rdd1.groupByKey()
    //result.foreach(f = x=>println(x._1 + "\t" +x._2))
    result.foreach(f = x=>{println(x._1 + "\t" +x._2.toList.mkString(","))})

    println("-----------------aggregateByKey--------------------")
    //aggregateByKey = groupByKey + aggregate
    //求平均值  aggregateByKey //zeroValue : U 初始值 U:(Int,Int)(sum,num)
    //    //seqOp : (U,Int) => U  迭代操作，拿RDD中的每个元素跟初始值进行聚合，在每个分区中执行
    //    //combOp : (U,U) => U  聚合每个分区的结果= groupByKey + aggregate
    val rdd2: RDD[(String, (Int, Int))] = rdd1.aggregateByKey(zeroValue = (0, 0))(seqOp = (u: (Int, Int), x: Int) => (u._1 + x, u._2 + 1),
      combOp = (a: (Int, Int), b: (Int, Int)) => (a._1 + b._1, a._2 + b._2))

    rdd2.foreach(f = x=>{
      println(x._1,"avg="+x._2._1.toDouble/x._2._2)
    })
    println("-----------------combineByKey--------------------")
    //求平均值
    //createCombiner : scala.Function1[V, C]  V=>C 这个函数把当前的值作为参数，此时我们可以对其做些附加操作(类型转换)并把它返回 (这一步类似于初始化操作)
    // mergeValue : scala.Function2[C, V, C]  (C,V)=>C 该函数把元素V合并到之前的元素C(createCombiner)上 (这个操作在每个分区内进行)
    // mergeCombiners : scala.Function2[C, C, C]  (C,C)=>C 该函数把2个元素C合并 (这个操作在不同分区间进行)
    val rdd33: RDD[(String, (Int, Int))] = rdd1.combineByKey(createCombiner = x => (x, 1), mergeValue = (x: (Int, Int), y: Int) => (x._1 + y, x._2 + 1),
      mergeCombiners = (a: (Int, Int), b: (Int, Int)) => (a._1 + b._1, a._2 + b._2))
    val rdd44: RDD[(String, Double)] = rdd33.map(f = (x: (String, (Int, Int))) => {
      (x._1, x._2._1.toDouble / x._2._2)
    })
    rdd44.collect().foreach(println)

    sparkContext.stop()
  }
}
