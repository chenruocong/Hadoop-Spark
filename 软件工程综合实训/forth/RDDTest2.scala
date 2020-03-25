package jxufe.edu.spark.rddtest

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object RDDTest2 {
  def main(args: Array[String]): Unit = {
    val conf: SparkConf = new SparkConf()
    conf.setAppName("RDDTEST2")
    conf.setMaster("local")
    val sc: SparkContext = new SparkContext(conf)

    val array1 = Array(1,2,3,4,5)
    val array2 = Array(5,6,7,8)
    val rdd1: RDD[Int] = sc.parallelize(array1)
    val rdd2: RDD[Int] = sc.parallelize(array2)
    println("-----------------cartesian--------------------")
    //cartesian 笛卡尔积
    val res1: RDD[(Int, Int)] = rdd1.cartesian(rdd2)
    res1.foreach(f = x=>println(x))

    println("-----------------union--------------------")
    //union 并集
    val res2: RDD[Int] = rdd1.union(rdd2)
    res2.foreach(f = x=>println(x))

    println("-----------------intersection--------------------")
    //intersection 交集
    val res3: RDD[Int] = rdd1.intersection(rdd2)
    res3.foreach(f = x=>println(x))

    println("-----------------subtract--------------------")
    //subtract 差集
    val res4: RDD[Int] = rdd2.subtract(rdd1)
    res4.foreach(f = x=>println(x))

    println("-----------------join--------------------")
    //join 连接
    val list1 = List((1,1),(2,2),(3,3))
    val list2 = List((1,"one"),(2,"two"),(3,"three"))
    val rdd3: RDD[(Int,Int)] = sc.parallelize(list1)
    val rdd4: RDD[(Int,String)] = sc.parallelize(list2)
    val res5: RDD[(Int, (Int, String))] = rdd3.join(rdd4)
    res5.foreach(f = x=>println(x))

    sc.stop()
  }
}
