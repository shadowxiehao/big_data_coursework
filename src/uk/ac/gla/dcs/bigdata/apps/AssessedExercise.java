package uk.ac.gla.dcs.bigdata.apps;

import java.io.File;
import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import uk.ac.gla.dcs.bigdata.providedfunctions.NewsFormaterMap;
import uk.ac.gla.dcs.bigdata.providedfunctions.QueryFormaterMap;
import uk.ac.gla.dcs.bigdata.providedstructures.DocumentRanking;
import uk.ac.gla.dcs.bigdata.providedstructures.NewsArticle;
import uk.ac.gla.dcs.bigdata.providedstructures.Query;
import uk.ac.gla.dcs.bigdata.studentfunctions.DPHCalculator;
import uk.ac.gla.dcs.bigdata.studentfunctions.NewsProcessMap;
import uk.ac.gla.dcs.bigdata.studentfunctions.TextualDistanceReducer;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleList;
import uk.ac.gla.dcs.bigdata.studentstructures.NewsArticleInNeed;

/**
 * This is the main class where your Spark topology should be specified.
 * 
 * By default, running this class will execute the topology defined in the
 * rankDocuments() method in local mode, although this may be overriden by
 * the spark.master environment variable.
 * @author Richard
 *
 */
public class AssessedExercise {

	
	public static void main(String[] args) {
		
		File hadoopDIR = new File("resources/hadoop/"); // represent the hadoop directory as a Java file so we can get an absolute path for it
		System.setProperty("hadoop.home.dir", hadoopDIR.getAbsolutePath()); // set the JVM system property so that Spark finds it
		
		// The code submitted for the assessed exerise may be run in either local or remote modes
		// Configuration of this will be performed based on an environment variable
//		String sparkMasterDef = System.getenv("spark.master");
		String sparkMasterDef = System.getenv("spark.local");
		if (sparkMasterDef==null) sparkMasterDef = "local[2]"; // default is local mode with two executors
		
		String sparkSessionName = "BigDataAE"; // give the session a name
		
		// Create the Spark Configuration 
		SparkConf conf = new SparkConf()
				.setMaster(sparkMasterDef)
				.setAppName(sparkSessionName);
		
		// Create the spark session
		SparkSession spark = SparkSession
				  .builder()
				  .config(conf)
				  .getOrCreate();
	
		
		// Get the location of the input queries
		String queryFile = System.getenv("bigdata.queries");
		if (queryFile==null) queryFile = "data/queries.list"; // default is a sample with 3 queries
		
		// Get the location of the input news articles
		String newsFile = System.getenv("bigdata.news");
		if (newsFile==null) newsFile = "data/TREC_Washington_Post_collection.v3.example.json"; // default is a sample of 5000 news articles
		
		// Call the student's code
		List<DocumentRanking> results = rankDocuments(spark, queryFile, newsFile);
		
		// Close the spark session
		spark.close();
		
		// Check if the code returned any results
		if (results==null) System.err.println("Topology return no rankings, student code may not be implemented, skiping final write.");
		else {
			
			// We have set of output rankings, lets write to disk
			
			// Create a new folder 
			File outDirectory = new File("results/"+System.currentTimeMillis());
			if (!outDirectory.exists()) outDirectory.mkdir();
			
			// Write the ranking for each query as a new file
			for (DocumentRanking rankingForQuery : results) {
				rankingForQuery.write(outDirectory.getAbsolutePath());
			}
		}
		
		
	}
	
	
	
	public static List<DocumentRanking> rankDocuments(SparkSession spark, String queryFile, String newsFile) {
		
		// Load queries and news articles
		Dataset<Row> queriesjson = spark.read().text(queryFile);
		Dataset<Row> newsjson = spark.read().text(newsFile); // read in files as string rows, one row per article
		
		// Perform an initial conversion from Dataset<Row> to Query and NewsArticle Java objects
		Dataset<Query> queries = queriesjson.map(new QueryFormaterMap(), Encoders.bean(Query.class)); // this converts each row into a Query
		Dataset<NewsArticle> news = newsjson.map(new NewsFormaterMap(), Encoders.bean(NewsArticle.class)); // this converts each row into a NewsArticle
		List<NewsArticle> newsArticlel = news.collectAsList();

		//----------------------------------------------------------------
		// Your Spark Topology should be defined here
		//----------------------------------------------------------------
		

		// KeyValueGroupedDataset<String, NewsArticle> newsAfterProcessor = news.groupByKey(new PreProcessNews(), Encoders.STRING());
		
		// In Spark, data tranformations are specified by calling transformation functions on Datasets
		// The most basic transformation is 'map', this converts each item in the dataset to a new item (that may be of a different type)
		// The map function takes as input two parameters
		//   - A class that implements MapFunction<InputType,OutputType>
		//   - An encoder for the output type (which we just created in the previous step)
		Encoder<NewsArticleInNeed> NewsArticleProcessedEncoder = Encoders.bean(NewsArticleInNeed.class);
		Dataset<NewsArticleInNeed> newsArticleInNeed =  news.flatMap(new NewsProcessMap(), NewsArticleProcessedEncoder);
		List<NewsArticleInNeed> listinneed = newsArticleInNeed.collectAsList();

		for (Query query:queries.collectAsList()) {
			Dataset<NewsArticleList> newsAsLists = DPHCalculator.calculateDPHScore(query.getQueryTerms(),newsArticleInNeed);
			TextualDistanceReducer similarityFilter = new TextualDistanceReducer();
			NewsArticleList filterdNewsArticles = newsAsLists.reduce(similarityFilter);
			List<NewsArticleInNeed> result = filterdNewsArticles.getNewsList().subList(0,10);

			System.out.println("size is:"+filterdNewsArticles.getNewsList().size());
			System.out.println("size of result is:"+result.size());
		}


		return null; // replace this with the the list of DocumentRanking output by your topology
	}
	
	
}
