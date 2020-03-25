package jxufe.edu.spark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object WordCount {
  def main(args:Array[String]): Unit = {
    if(args.length < 2){
      System.err.println("Usage: WordCount <input> <output>")
      System.exit(1)
    }
    val inputpath:String = args(0)
    val outputpath:String = args(1)
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

    /**
     * 第1步：获取编程入口sc
     */
    var conf: SparkConf = new SparkConf()
    conf.setAppName("WordCont")
    //conf.setMaster("local")
    //conf.setMaster("spark://192.168.64.130:7077")
    var sc: SparkContext = new SparkContext(conf)
    //sc.setLogLevel("WARN")
    //var textFile: RDD[String] = sc.textFile("E:\\wordcount.txt")
    //var textFile: RDD[String] = sc.textFile("file:///home/hadoop/wordcount.txt")
    /**
     * 第2步：通过编程入口获取数据（得到初始RDD）
     */
    var textFile: RDD[String] = sc.textFile(inputpath)

    /**
     * 第3步：通过RDD操作对数据进行处理
     */
    var linerdd: RDD[String] = textFile.flatMap(line => line.split("\\s+"))
    var wordrdd: RDD[(String, Int)] = linerdd.map(word => (word, 1))
    var wordcountrdd: RDD[(String, Int)] = wordrdd.reduceByKey((a, b) => a + b)
    //wordcountrdd.foreach(println)

    /**
     * 第4步：对结果数据进行处理
     */
    wordcountrdd.saveAsTextFile(outputpath)

    /**
     * 第5步：关闭编程入口
     */
    sc.stop()
  }
}
