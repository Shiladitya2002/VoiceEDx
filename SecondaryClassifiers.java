import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Stacking;
import weka.core.Instances;
public class SecondaryClassifiers
{
    //static String voiceFile = "D:\\Composite Voice Dataset\\Secondary Datasets";
    //static String combinedFile = "D:\\Composite Voice Dataset\\Secondary Datasets\\CombinedDataset.arff\\";
    static String voiceFile = "/detector/program/Secondary Datasets";
    static String combinedFile = "/detector/program/Secondary Datasets/CombinedDataset.arff/";
    public SecondaryClassifiers()
    {
    	
    }
    public int ParkinsonsSecond(Instances unlabeled, Stacking ps1, Stacking ps2, Stacking ps3, Stacking ps4)throws Exception
    {
    	if(ps1.classifyInstance(unlabeled.instance(0)) == 1)
    	{
    		if(ps2.classifyInstance(unlabeled.instance(0)) == 1)
    		{
    			return 1;
    		}
    		else
    		{
    			if(ps4.classifyInstance(unlabeled.instance(0)) == 1)
        		{
        			return 2;
        		}
    			else
    			{
    				return 3;
    			}
    		}
    	}
    	else
    	{
    		if(ps3.classifyInstance(unlabeled.instance(0)) == 1)
    		{
    			return 4;
    		}
    		else
    		{
    			return 5;
    		}
    	}
    }
    public int AlzheimersSecond(Instances unlabeled, Stacking ps1, Stacking ps2)throws Exception
    {
    	if(ps1.classifyInstance(unlabeled.instance(0)) == 1)
    	{
    		if(ps2.classifyInstance(unlabeled.instance(0)) == 1)
    		{
    			return 1;
    		}
    		else
    		{
    			return 2;
    		}
    	}
    	else
    	{
    		return 3;
    	}
    }
    public int ALSSecond(Instances unlabeled, Stacking ps1, Stacking ps2)throws Exception
    {
    	if(ps1.classifyInstance(unlabeled.instance(0)) == 1)
    	{
    		return 1;
    	}
    	else
    	{
    		if(ps2.classifyInstance(unlabeled.instance(0)) == 1)
    		{
    			return 2;
    		}
    		else
    		{
    			return 3;
    		}
    	}
    }
    public int HypopharyngealSecond(Instances unlabeled, Stacking ps1)throws Exception
    {
    	if(ps1.classifyInstance(unlabeled.instance(0)) == 1)
    	{
    		return 1;
    	}
    	else
    	{
    		return 2;
    	}
    }
    public int LaryngealSecond(Instances unlabeled, Stacking ps1)throws Exception
    {
    	if(ps1.classifyInstance(unlabeled.instance(0)) == 1)
    	{
    		return 1;
    	}
    	else
    	{
    		return 2;
    	}
    }
    public static Stacking ps1()throws Exception
	{
		/*
		 * Compile Data
		 */
		//Detail file status
    	File[] whiteList = new File[3];
    	File[] blackList = new File[2];
    	//Positive
    	whiteList[0] = new File(voiceFile+"ParkinsonsDataSet1.txt");
    	whiteList[1] = new File(voiceFile+"ParkinsonsDataSet2.txt");
    	whiteList[2] = new File(voiceFile+"ParkinsonsDataSet3.txt");
    	//Negative
    	blackList[0] = new File(voiceFile+"ParkisonsDataSet4.txt");
    	blackList[1] = new File(voiceFile+"ParkinsonsDataSet5.txt");
		//Detail which file to send to
		File tnf2 = new File(combinedFile);
		String skip2 = tnf2.getName();
		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
		//Make header
    	printer.println("@RELATION    Initial");
		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
		printer.println("@ATTRIBUTE NHR                    NUMERIC");
		printer.println("@ATTRIBUTE HNR                    NUMERIC");
	    printer.println("@ATTRIBUTE class                    {0,1}");
	    printer.println("");
	    printer.println("@DATA");
	    //Access Base Files
	    File folder1 = new File(voiceFile);
    	File[] arr = folder1.listFiles();
	    ArrayList<String> finals = new ArrayList<String>();
    	for(int i = 0; i < arr.length; i++)
    	{
    		Scanner scanner = new Scanner(new FileReader(arr[i]));
    		System.out.println(arr[i]);
    		int count =0;
    		if(arr[i].getName().equals(skip2))continue;
    		while(scanner.hasNextLine())
    		{
    			String at = scanner.nextLine();
    			int ret = checkSkip(arr[i],whiteList,blackList);
    			if(ret == 1)
    			{
    				at+=",1";
    				finals.add(at);
    				count++;
    			}
    			else if(ret == -1)
    			{
    				int shouldUse = (int)(Math.random()*10);
    				if(shouldUse == 3)
    				{
    					at+=",0";
        				finals.add(at);
        				count++;
    				}
    			}
    		}
    	}
    	//Convert to training format
    	String[] nxt = new String[finals.size()];
    	for(int i = 0; i < finals.size(); i++)
    	{
    		nxt[i] = finals.get(i);
    	}
    	shuffleArray(nxt);
    	for(int i = 0; i < nxt.length; i++)
    	{
    		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
			if(tokens.countTokens() != 14)
			{
				continue;
			}
    		printer.println(nxt[i]);
    	}
    	printer.close();
    	//Set up Instance
    	BufferedReader br = null;
        br = new BufferedReader(new FileReader(combinedFile));
        Instances trainData = new Instances(br);
        trainData.setClassIndex(16);
        br.close();
        /*
         * Train Classifiers
         */
        //Set up SPegasos
        String[] options1 = new String[6];
        options1[0] = "-P";
        options1[1] = "100";
        options1[2] = "-S";
        options1[3] = "1";
        options1[4] = "-I";
        options1[5] = "10";
        options1[6] = "-W: weka.classifiers.functions.LibSVM -- -S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1";
        Classifier scl1 = AbstractClassifier.forName("weka.classifiers.meta.AdaBoostM1", options1);
        //Set Up LWL
   	 	String[] options2 = new String[10];
   	 	options2[0] = "-A";
   	 	options2[1] = "weka.core.neighboursearch.LinearNNSearch";
   	 	options2[2] = "-W";
   	 	options2[3] = "weka.classifiers.functions.SMO";
   	 	options2[4] = "--";
   	 	options2[5] = "-C"; 
   	 	options2[6] = "0.7958629079622792"; 
   	 	options2[7] = "-N";
     	options2[8] = "2";
     	options2[9] = "-K";
        options2[10] = "weka.classifiers.functions.supportVector.RBFKernel -G 0.005283673772958745";
        Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.LWL", options2);
        //Set Up MultiBoosted J48 tree
   	 	String[] options3 = new String[9];
   	 	options3[0] = "-C";
   	 	options3[1] = "3";
   	 	options3[2] = "-P";
   	 	options3[3] = "100";
   	 	options3[4] = "-S";
   	 	options3[5] = "1";
   	 	options3[6] = "-I";
   	 	options3[7] = "10";
   	 	options3[8] = "-W weka.classifiers.trees.J48 -- -C 0.25 -M 2";
   	 	Classifier scl3 = AbstractClassifier.forName("weka.classifiers.meta.RealAdaBoost", options3);
        //Set Up BayesNet
        String[] options4 = new String[5];
        options4[0] = "-D";
        options4[1] = "-Q";
        options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
        options4[3] = "-E";
        options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
        Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
        //Set Up JRip
   	 	String[] options5 = new String[8];
   	 	options5[0] = "-F"; 
   	 	options5[1] = "3";
   	 	options5[2] = "-N";
   	 	options5[3] = "2.0";
   	 	options5[4] = "-O";
   	 	options5[5] = "2";
   	 	options5[6] = "-S";
   	 	options5[7] = "1";
   	 	Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.JRip", options5);
        //Set Up FLR
   	 	String[] options6 = new String[4];
   	 	options6[0] = "-R";
   	 	options6[1] = "1.0";
   	 	options6[2] = "-Y";
   	 	options6[3] = "-B";
   	 	Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.FLR", options6);
        //Set up RBFClassifier
        String[] options7 = new String[14];
        options7[0] = "-N";
        options7[1] = "2";
        options7[2] = "-R";
        options7[3] = "0.01";
        options7[4] = "-L";
        options7[5] = "1.0E-6";
        options7[6] = "-C";
        options7[7] = "2";
        options7[8] = "-P";
        options7[9] = "1";
        options7[10] = "-E";
        options7[11] = "1";
        options7[12] = "-S";
        options7[13] = "1";
        Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
        //Set up SMO
        String[] options8 = new String[14];
        options8[0] = "-C";
        options8[1] = "1.0";
        options8[2] = "-L";
        options8[3] = "0.001";
        options8[4] = "-P";
        options8[5] = "1.0E-12";
        options8[6] = "-N";
        options8[7] = "0";
        options8[8] = "-V";
        options8[9] = "-1";
        options8[10] = "-W";
        options8[11] = "1";
        options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
        options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
        Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
        Classifier[] cl = new Classifier[7];
        cl[0] = scl1;
        cl[1] = scl2;
        cl[2] = scl3;
        cl[3] = scl4;
        cl[4] = scl5;
        cl[5] = scl6;
        cl[6] = scl7;
        //Set up Stacking
        Stacking finalClassifier = new Stacking();
        finalClassifier.setMetaClassifier(sclmeta);
        finalClassifier.setClassifiers(cl);
        finalClassifier.setDebug(false);
        finalClassifier.setNumDecimalPlaces(2);
        finalClassifier.setNumExecutionSlots(1);
        finalClassifier.buildClassifier(trainData);
        return finalClassifier;
	}
    public static Stacking ps2()throws Exception
	{
		/*
		 * Compile Data
		 */
		//Detail file status
    	File[] whiteList = new File[1];
    	File[] blackList = new File[2];
    	//Positive
    	whiteList[0] = new File(voiceFile+"ParkinsonsDataSet1.txt");
    	//Negative
    	blackList[0] = new File(voiceFile+"ParkinsonsDataSet2.txt");
    	blackList[1] = new File(voiceFile+"ParkinsonsDataSet3.txt");
		//Detail which file to send to
		File tnf2 = new File(combinedFile);
		String skip2 = tnf2.getName();
		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
		//Make header
    	printer.println("@RELATION    Initial");
		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
		printer.println("@ATTRIBUTE NHR                    NUMERIC");
		printer.println("@ATTRIBUTE HNR                    NUMERIC");
	    printer.println("@ATTRIBUTE class                    {0,1}");
	    printer.println("");
	    printer.println("@DATA");
	    //Access Base Files
	    File folder1 = new File(voiceFile);
    	File[] arr = folder1.listFiles();
	    ArrayList<String> finals = new ArrayList<String>();
    	for(int i = 0; i < arr.length; i++)
    	{
    		Scanner scanner = new Scanner(new FileReader(arr[i]));
    		System.out.println(arr[i]);
    		int count =0;
    		if(arr[i].getName().equals(skip2))continue;
    		while(scanner.hasNextLine())
    		{
    			String at = scanner.nextLine();
    			int ret = checkSkip(arr[i],whiteList,blackList);
    			if(ret == 1)
    			{
    				at+=",1";
    				finals.add(at);
    				count++;
    			}
    			else if(ret == -1)
    			{
    				int shouldUse = (int)(Math.random()*10);
    				if(shouldUse == 3)
    				{
    					at+=",0";
        				finals.add(at);
        				count++;
    				}
    			}
    		}
    	}
    	//Convert to training format
    	String[] nxt = new String[finals.size()];
    	for(int i = 0; i < finals.size(); i++)
    	{
    		nxt[i] = finals.get(i);
    	}
    	shuffleArray(nxt);
    	for(int i = 0; i < nxt.length; i++)
    	{
    		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
			if(tokens.countTokens() != 14)
			{
				continue;
			}
    		printer.println(nxt[i]);
    	}
    	printer.close();
    	//Set up Instance
    	BufferedReader br = null;
        br = new BufferedReader(new FileReader(combinedFile));
        Instances trainData = new Instances(br);
        trainData.setClassIndex(16);
        br.close();
        /*
         * Train Classifiers
         */
        //Set up SPegasos
   	 	String[] options1 = new String[6];
   	 	options1[0] = "-F";
   	 	options1[1] = "0";
   	 	options1[2] = "-L";
   	 	options1[3] = "1.0E-4";
   	 	options1[4] = "-E";
   	 	options1[5] = "500";
   	 	Classifier scl1 = AbstractClassifier.forName("weka.classifiers.functions.SPegasos", options1);
   	 	//Set up IBk/KNN
   	 	String[] options2 = new String[8];
 		options2[0] = "-U";
	 	options2[1] = "0";
	 	options2[2] = "-K";
 		options2[3] = "-1";
 		options2[4] = "-A"; 
 		options2[5] = "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"";
 		options2[6] = "-W";
 		options2[7] = "weka.classifiers.trees.DecisionStump";
 		Classifier scl3 = AbstractClassifier.forName("weka.classifiers.lazy.IBk", options2);
        //Set Up MultiBoosted J48 tree
   	 	String[] options3 = new String[9];
   	 	options3[0] = "-C";
   	 	options3[1] = "3";
   	 	options3[2] = "-P";
   	 	options3[3] = "100";
   	 	options3[4] = "-S";
   	 	options3[5] = "1";
   	 	options3[6] = "-I";
   	 	options3[7] = "10";
   	 	options3[8] = "-W weka.classifiers.trees.J48 -- -C 0.25 -M 2";
   	 	Classifier scl2 = AbstractClassifier.forName("weka.classifiers.meta.RealAdaBoost", options3);
        //Set Up BayesNet
        String[] options4 = new String[5];
        options4[0] = "-D";
        options4[1] = "-Q";
        options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
        options4[3] = "-E";
        options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
        Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
        //Set Up Multi-Objective Evolutionary Fuzzy Classifier
        String[] options5 = new String[11];
        options5[0] = "-g 10";
        options5[1] = "-ps 100";
        options5[2] = "-s 1";
        options5[3] = "-ms 0.1";
        options5[4] = "-minv 30.0";
        options5[5] = "-maxv 2.0";
        options5[6] = "-maxr -1";
        options5[7] = "-ev 0";
        options5[8] = "-a 0";
        options5[9] = "-report-frequency 10";
        options5[10] = "-log-file D:\\Weka-3-8";
        Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.MultiObjectiveEvolutionaryFuzzyClassifier", options5);
        //Set Up FLR
   	 	String[] options6 = new String[4];
   	 	options6[0] = "-R";
   	 	options6[1] = "1.0";
   	 	options6[2] = "-Y";
   	 	options6[3] = "-B";
   	 	Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.FLR", options6);
        //Set up RBFClassifier
        String[] options7 = new String[14];
        options7[0] = "-N";
        options7[1] = "2";
        options7[2] = "-R";
        options7[3] = "0.01";
        options7[4] = "-L";
        options7[5] = "1.0E-6";
        options7[6] = "-C";
        options7[7] = "2";
        options7[8] = "-P";
        options7[9] = "1";
        options7[10] = "-E";
        options7[11] = "1";
        options7[12] = "-S";
        options7[13] = "1";
        Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
        //Set up SMO
        String[] options8 = new String[14];
        options8[0] = "-C";
        options8[1] = "1.0";
        options8[2] = "-L";
        options8[3] = "0.001";
        options8[4] = "-P";
        options8[5] = "1.0E-12";
        options8[6] = "-N";
        options8[7] = "0";
        options8[8] = "-V";
        options8[9] = "-1";
        options8[10] = "-W";
        options8[11] = "1";
        options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
        options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
        Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
        Classifier[] cl = new Classifier[7];
        cl[0] = scl1;
        cl[1] = scl2;
        cl[2] = scl3;
        cl[3] = scl4;
        cl[4] = scl5;
        cl[5] = scl6;
        cl[6] = scl7;
        //Set up Stacking
        Stacking finalClassifier = new Stacking();
        finalClassifier.setMetaClassifier(sclmeta);
        finalClassifier.setClassifiers(cl);
        finalClassifier.setDebug(false);
        finalClassifier.setNumDecimalPlaces(2);
        finalClassifier.setNumExecutionSlots(1);
        finalClassifier.buildClassifier(trainData);
        return finalClassifier;
	}
    public static Stacking ps3()throws Exception
	{
		/*
		 * Compile Data
		 */
		//Detail file status
    	File[] whiteList = new File[1];
    	File[] blackList = new File[1];
    	//Positive
    	whiteList[0] = new File(voiceFile+"ParkinsonsDataSet4.txt");
    	//Negative
    	blackList[0] = new File(voiceFile+"ParkinsonsDataSet5.txt");
		//Detail which file to send to
		File tnf2 = new File(combinedFile);
		String skip2 = tnf2.getName();
		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
		//Make header
    	printer.println("@RELATION    Initial");
		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
		printer.println("@ATTRIBUTE NHR                    NUMERIC");
		printer.println("@ATTRIBUTE HNR                    NUMERIC");
	    printer.println("@ATTRIBUTE class                    {0,1}");
	    printer.println("");
	    printer.println("@DATA");
	    //Access Base Files
	    File folder1 = new File(voiceFile);
    	File[] arr = folder1.listFiles();
	    ArrayList<String> finals = new ArrayList<String>();
    	for(int i = 0; i < arr.length; i++)
    	{
    		Scanner scanner = new Scanner(new FileReader(arr[i]));
    		System.out.println(arr[i]);
    		int count =0;
    		if(arr[i].getName().equals(skip2))continue;
    		while(scanner.hasNextLine())
    		{
    			String at = scanner.nextLine();
    			int ret = checkSkip(arr[i],whiteList,blackList);
    			if(ret == 1)
    			{
    				at+=",1";
    				finals.add(at);
    				count++;
    			}
    			else if(ret == -1)
    			{
    				int shouldUse = (int)(Math.random()*10);
    				if(shouldUse == 3)
    				{
    					at+=",0";
        				finals.add(at);
        				count++;
    				}
    			}
    		}
    	}
    	//Convert to training format
    	String[] nxt = new String[finals.size()];
    	for(int i = 0; i < finals.size(); i++)
    	{
    		nxt[i] = finals.get(i);
    	}
    	shuffleArray(nxt);
    	for(int i = 0; i < nxt.length; i++)
    	{
    		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
			if(tokens.countTokens() != 14)
			{
				continue;
			}
    		printer.println(nxt[i]);
    	}
    	printer.close();
    	//Set up Instance
    	BufferedReader br = null;
        br = new BufferedReader(new FileReader(combinedFile));
        Instances trainData = new Instances(br);
        trainData.setClassIndex(16);
        br.close();
        /*
         * Train Classifiers
         */
        //Set up SPegasos
   	 	String[] options1 = new String[6];
   	 	options1[0] = "-F";
   	 	options1[1] = "0";
   	 	options1[2] = "-L";
   	 	options1[3] = "1.0E-4";
   	 	options1[4] = "-E";
   	 	options1[5] = "500";
   	 	Classifier scl1 = AbstractClassifier.forName("weka.classifiers.functions.SPegasos", options1);
   	 	//Set Up LWL
   	 	String[] options2 = new String[10];
   	 	options2[0] = "-A";
   	 	options2[1] = "weka.core.neighboursearch.LinearNNSearch";
   	 	options2[2] = "-W";
   	 	options2[3] = "weka.classifiers.functions.SMO";
   	 	options2[4] = "--";
   	 	options2[5] = "-C"; 
   	 	options2[6] = "0.7958629079622792"; 
   	 	options2[7] = "-N";
     	options2[8] = "2";
     	options2[9] = "-K";
        options2[10] = "weka.classifiers.functions.supportVector.RBFKernel -G 0.005283673772958745";
        Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.LWL", options2);
        //Set Up MultiBoosted J48 tree
   	 	String[] options3 = new String[9];
   	 	options3[0] = "-C";
   	 	options3[1] = "3";
   	 	options3[2] = "-P";
   	 	options3[3] = "100";
   	 	options3[4] = "-S";
   	 	options3[5] = "1";
   	 	options3[6] = "-I";
   	 	options3[7] = "10";
   	 	options3[8] = "-W weka.classifiers.trees.J48 -- -C 0.25 -M 2";
   	 	Classifier scl3 = AbstractClassifier.forName("weka.classifiers.meta.RealAdaBoost", options3);
        //Set Up BayesNet
        String[] options4 = new String[5];
        options4[0] = "-D";
        options4[1] = "-Q";
        options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
        options4[3] = "-E";
        options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
        Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
        //Set Up JRip
   	 	String[] options5 = new String[8];
   	 	options5[0] = "-F"; 
   	 	options5[1] = "3";
   	 	options5[2] = "-N";
   	 	options5[3] = "2.0";
   	 	options5[4] = "-O";
   	 	options5[5] = "2";
   	 	options5[6] = "-S";
   	 	options5[7] = "1";
   	 	Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.JRip", options5);
        //Set Up FLR
   	 	String[] options6 = new String[4];
   	 	options6[0] = "-R";
   	 	options6[1] = "1.0";
   	 	options6[2] = "-Y";
   	 	options6[3] = "-B";
   	 	Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.FLR", options6);
        //Set up RBFClassifier
        String[] options7 = new String[14];
        options7[0] = "-N";
        options7[1] = "2";
        options7[2] = "-R";
        options7[3] = "0.01";
        options7[4] = "-L";
        options7[5] = "1.0E-6";
        options7[6] = "-C";
        options7[7] = "2";
        options7[8] = "-P";
        options7[9] = "1";
        options7[10] = "-E";
        options7[11] = "1";
        options7[12] = "-S";
        options7[13] = "1";
        Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
        //Set up SMO
        String[] options8 = new String[14];
        options8[0] = "-C";
        options8[1] = "1.0";
        options8[2] = "-L";
        options8[3] = "0.001";
        options8[4] = "-P";
        options8[5] = "1.0E-12";
        options8[6] = "-N";
        options8[7] = "0";
        options8[8] = "-V";
        options8[9] = "-1";
        options8[10] = "-W";
        options8[11] = "1";
        options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
        options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
        Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
        Classifier[] cl = new Classifier[7];
        cl[0] = scl1;
        cl[1] = scl2;
        cl[2] = scl3;
        cl[3] = scl4;
        cl[4] = scl5;
        cl[5] = scl6;
        cl[6] = scl7;
        //Set up Stacking
        Stacking finalClassifier = new Stacking();
        finalClassifier.setMetaClassifier(sclmeta);
        finalClassifier.setClassifiers(cl);
        finalClassifier.setDebug(false);
        finalClassifier.setNumDecimalPlaces(2);
        finalClassifier.setNumExecutionSlots(1);
        finalClassifier.buildClassifier(trainData);
        return finalClassifier;
	}
    public static Stacking ps4()throws Exception
   	{
   		/*
   		 * Compile Data
   		 */
   		//Detail file status
       	File[] whiteList = new File[1];
       	File[] blackList = new File[1];
       	//Positive
       	whiteList[0] = new File(voiceFile+"ParkinsonsDataSet2.txt");
       	//Negative
       	blackList[0] = new File(voiceFile+"ParkinsonsDataSet3.txt");
   		//Detail which file to send to
   		File tnf2 = new File(combinedFile);
   		String skip2 = tnf2.getName();
   		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
   		//Make header
       	printer.println("@RELATION    Initial");
   		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
   		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
   		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
   		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
   		printer.println("@ATTRIBUTE NHR                    NUMERIC");
   		printer.println("@ATTRIBUTE HNR                    NUMERIC");
   	    printer.println("@ATTRIBUTE class                    {0,1}");
   	    printer.println("");
   	    printer.println("@DATA");
   	    //Access Base Files
   	    File folder1 = new File(voiceFile);
       	File[] arr = folder1.listFiles();
   	    ArrayList<String> finals = new ArrayList<String>();
       	for(int i = 0; i < arr.length; i++)
       	{
       		Scanner scanner = new Scanner(new FileReader(arr[i]));
       		System.out.println(arr[i]);
       		int count =0;
       		if(arr[i].getName().equals(skip2))continue;
       		while(scanner.hasNextLine())
       		{
       			String at = scanner.nextLine();
       			int ret = checkSkip(arr[i],whiteList,blackList);
       			if(ret == 1)
       			{
       				at+=",1";
       				finals.add(at);
       				count++;
       			}
       			else if(ret == -1)
       			{
       				int shouldUse = (int)(Math.random()*10);
       				if(shouldUse == 3)
       				{
       					at+=",0";
           				finals.add(at);
           				count++;
       				}
       			}
       		}
       	}
       	//Convert to training format
       	String[] nxt = new String[finals.size()];
       	for(int i = 0; i < finals.size(); i++)
       	{
       		nxt[i] = finals.get(i);
       	}
       	shuffleArray(nxt);
       	for(int i = 0; i < nxt.length; i++)
       	{
       		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
   			if(tokens.countTokens() != 14)
   			{
   				continue;
   			}
       		printer.println(nxt[i]);
       	}
       	printer.close();
       	//Set up Instance
       	BufferedReader br = null;
           br = new BufferedReader(new FileReader(combinedFile));
           Instances trainData = new Instances(br);
           trainData.setClassIndex(16);
           br.close();
           /*
            * Train Classifiers
            */
           //AdaBoost M1 SVM
           String[] options1 = new String[6];
           options1[0] = "-P";
           options1[1] = "100";
           options1[2] = "-S";
           options1[3] = "1";
           options1[4] = "-I";
           options1[5] = "10";
           options1[6] = "-W: weka.classifiers.functions.LibSVM -- -S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1";
            Classifier scl1 = AbstractClassifier.forName("weka.classifiers.meta.AdaBoostM1", options1);
      	 	//Set Up K*
      	 	String[] options2 = new String[8];
      	 	options2[0] = "-B";
      	 	options2[0] = "30";
      	 	options2[0] = "-E";
      	 	options2[0] = "-M";
      	 	options2[0] =  "n";
      	 	Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.KStar", options2);
           //Set Up MultiBoosted J48 tree
      	 	String[] options3 = new String[9];
      	 	options3[0] = "-C";
      	 	options3[1] = "3";
      	 	options3[2] = "-P";
      	 	options3[3] = "100";
      	 	options3[4] = "-S";
      	 	options3[5] = "1";
      	 	options3[6] = "-I";
      	 	options3[7] = "10";
      	 	options3[8] = "-W weka.classifiers.trees.J48 -- -C 0.25 -M 2";
      	 	Classifier scl3 = AbstractClassifier.forName("weka.classifiers.meta.RealAdaBoost", options3);
           //Set Up BayesNet
           String[] options4 = new String[5];
           options4[0] = "-D";
           options4[1] = "-Q";
           options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
           options4[3] = "-E";
           options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
           Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
           //Set Up JRip
      	 	String[] options5 = new String[8];
      	 	options5[0] = "-F"; 
      	 	options5[1] = "3";
      	 	options5[2] = "-N";
      	 	options5[3] = "2.0";
      	 	options5[4] = "-O";
      	 	options5[5] = "2";
      	 	options5[6] = "-S";
      	 	options5[7] = "1";
      	 	Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.JRip", options5);
      	 	//Set up CHIRP
            String[] options6 = new String[2];
            options6[0] = "-V 7";
            options6[1] = "-S 1";
            Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.CHIRP", options6);
           //Set up RBFClassifier
           String[] options7 = new String[14];
           options7[0] = "-N";
           options7[1] = "2";
           options7[2] = "-R";
           options7[3] = "0.01";
           options7[4] = "-L";
           options7[5] = "1.0E-6";
           options7[6] = "-C";
           options7[7] = "2";
           options7[8] = "-P";
           options7[9] = "1";
           options7[10] = "-E";
           options7[11] = "1";
           options7[12] = "-S";
           options7[13] = "1";
           Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
           //Set up SMO
           String[] options8 = new String[14];
           options8[0] = "-C";
           options8[1] = "1.0";
           options8[2] = "-L";
           options8[3] = "0.001";
           options8[4] = "-P";
           options8[5] = "1.0E-12";
           options8[6] = "-N";
           options8[7] = "0";
           options8[8] = "-V";
           options8[9] = "-1";
           options8[10] = "-W";
           options8[11] = "1";
           options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
           options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
           Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
           Classifier[] cl = new Classifier[7];
           cl[0] = scl1;
           cl[1] = scl2;
           cl[2] = scl3;
           cl[3] = scl4;
           cl[4] = scl5;
           cl[5] = scl6;
           cl[6] = scl7;
           //Set up Stacking
           Stacking finalClassifier = new Stacking();
           finalClassifier.setMetaClassifier(sclmeta);
           finalClassifier.setClassifiers(cl);
           finalClassifier.setDebug(false);
           finalClassifier.setNumDecimalPlaces(2);
           finalClassifier.setNumExecutionSlots(1);
           finalClassifier.buildClassifier(trainData);
           return finalClassifier;
   	}
    public static Stacking as1()throws Exception
   	{
   		/*
   		 * Compile Data
   		 */
   		//Detail file status
       	File[] whiteList = new File[2];
       	File[] blackList = new File[1];
       	//Positive
       	whiteList[0] = new File(voiceFile+"AlzheimersDataSet1.txt");
       	whiteList[1] = new File(voiceFile+"AlzheimersDataSet2.txt");
       	//Negative
       	blackList[0] = new File(voiceFile+"AlzheimersDataSet3.txt");
   		//Detail which file to send to
   		File tnf2 = new File(combinedFile);
   		String skip2 = tnf2.getName();
   		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
   		//Make header
       	printer.println("@RELATION    Initial");
   		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
   		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
   		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
   		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
   		printer.println("@ATTRIBUTE NHR                    NUMERIC");
   		printer.println("@ATTRIBUTE HNR                    NUMERIC");
   	    printer.println("@ATTRIBUTE class                    {0,1}");
   	    printer.println("");
   	    printer.println("@DATA");
   	    //Access Base Files
   	    File folder1 = new File(voiceFile);
       	File[] arr = folder1.listFiles();
   	    ArrayList<String> finals = new ArrayList<String>();
       	for(int i = 0; i < arr.length; i++)
       	{
       		Scanner scanner = new Scanner(new FileReader(arr[i]));
       		System.out.println(arr[i]);
       		int count =0;
       		if(arr[i].getName().equals(skip2))continue;
       		while(scanner.hasNextLine())
       		{
       			String at = scanner.nextLine();
       			int ret = checkSkip(arr[i],whiteList,blackList);
       			if(ret == 1)
       			{
       				at+=",1";
       				finals.add(at);
       				count++;
       			}
       			else if(ret == -1)
       			{
       				int shouldUse = (int)(Math.random()*10);
       				if(shouldUse == 3)
       				{
       					at+=",0";
           				finals.add(at);
           				count++;
       				}
       			}
       		}
       	}
       	//Convert to training format
       	String[] nxt = new String[finals.size()];
       	for(int i = 0; i < finals.size(); i++)
       	{
       		nxt[i] = finals.get(i);
       	}
       	shuffleArray(nxt);
       	for(int i = 0; i < nxt.length; i++)
       	{
       		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
   			if(tokens.countTokens() != 14)
   			{
   				continue;
   			}
       		printer.println(nxt[i]);
       	}
       	printer.close();
       	//Set up Instance
       	BufferedReader br = null;
           br = new BufferedReader(new FileReader(combinedFile));
           Instances trainData = new Instances(br);
           trainData.setClassIndex(16);
           br.close();
           /*
            * Train Classifiers
            */
           //Set up SPegasos
      	 	String[] options1 = new String[6];
      	 	options1[0] = "-F";
      	 	options1[1] = "0";
      	 	options1[2] = "-L";
      	 	options1[3] = "1.0E-4";
      	 	options1[4] = "-E";
      	 	options1[5] = "500";
      	 	Classifier scl1 = AbstractClassifier.forName("weka.classifiers.functions.SPegasos", options1);
      	   //Set Up K*
      	   String[] options2 = new String[8];
      	   options2[0] = "-B";
      	   options2[0] = "30";
      	   options2[0] = "-E";
      	   options2[0] = "-M";
      	   options2[0] =  "n";
      	   Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.KStar", options2);
      	   //Set Up RandomForest
      	   String[] options3 = new String[14];
           options3[0] = "-P";
           options3[1] = "100";
           options3[2] = "-I";
           options3[3] = "100";
           options3[4] = "-num-slots";
           options3[5] = "1";
           options3[6] = "-K";
           options3[7] = "0";
           options3[8] = "-M";
           options3[9] = "1.0";
           options3[10] = "-V";
           options3[11] = "0.001";
           options3[12] = "-S";
           options3[13] = "1";
           Classifier scl3 = AbstractClassifier.forName("weka.classifiers.trees.RandomForest", options3);
           //Set Up BayesNet
           String[] options4 = new String[5];
           options4[0] = "-D";
           options4[1] = "-Q";
           options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
           options4[3] = "-E";
           options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
           Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
           //Set Up FURIA
      	   String[] options5 = new String[12];
      	   options5[0] = "-F";
      	   options5[1] = "3";
      	   options5[2] = "-N";
      	   options5[3] = "2.0";
      	   options5[4] = "-O";
      	   options5[5] = "2";
      	   options5[6] = "-S";
      	   options5[7] = "1";
      	   options5[8] = "-p";
      	   options5[9] = "0";
      	   options5[10] = "-s";
      	   options5[11] = "0";
      	   Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.FURIA", options5);
           //Set Up FLR
      	 	String[] options6 = new String[4];
      	 	options6[0] = "-R";
      	 	options6[1] = "1.0";
      	 	options6[2] = "-Y";
      	 	options6[3] = "-B";
      	 	Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.FLR", options6);
           //Set up RBFClassifier
           String[] options7 = new String[14];
           options7[0] = "-N";
           options7[1] = "2";
           options7[2] = "-R";
           options7[3] = "0.01";
           options7[4] = "-L";
           options7[5] = "1.0E-6";
           options7[6] = "-C";
           options7[7] = "2";
           options7[8] = "-P";
           options7[9] = "1";
           options7[10] = "-E";
           options7[11] = "1";
           options7[12] = "-S";
           options7[13] = "1";
           Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
           //Set up SMO
           String[] options8 = new String[14];
           options8[0] = "-C";
           options8[1] = "1.0";
           options8[2] = "-L";
           options8[3] = "0.001";
           options8[4] = "-P";
           options8[5] = "1.0E-12";
           options8[6] = "-N";
           options8[7] = "0";
           options8[8] = "-V";
           options8[9] = "-1";
           options8[10] = "-W";
           options8[11] = "1";
           options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
           options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
           Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
           Classifier[] cl = new Classifier[7];
           cl[0] = scl1;
           cl[1] = scl2;
           cl[2] = scl3;
           cl[3] = scl4;
           cl[4] = scl5;
           cl[5] = scl6;
           cl[6] = scl7;
           //Set up Stacking
           Stacking finalClassifier = new Stacking();
           finalClassifier.setMetaClassifier(sclmeta);
           finalClassifier.setClassifiers(cl);
           finalClassifier.setDebug(false);
           finalClassifier.setNumDecimalPlaces(2);
           finalClassifier.setNumExecutionSlots(1);
           finalClassifier.buildClassifier(trainData);
           return finalClassifier;
   	}
    public static Stacking as2()throws Exception
   	{
   		/*
   		 * Compile Data
   		 */
   		//Detail file status
       	File[] whiteList = new File[1];
       	File[] blackList = new File[1];
       	//Positive
       	whiteList[0] = new File(voiceFile+"AlzheimersDataSet1.txt");
       	//Negative
       	blackList[0] = new File(voiceFile+"AlzheimersDataSet2.txt");
   		//Detail which file to send to
   		File tnf2 = new File(combinedFile);
   		String skip2 = tnf2.getName();
   		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
   		//Make header
       	printer.println("@RELATION    Initial");
   		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
   		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
   		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
   		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
   		printer.println("@ATTRIBUTE NHR                    NUMERIC");
   		printer.println("@ATTRIBUTE HNR                    NUMERIC");
   	    printer.println("@ATTRIBUTE class                    {0,1}");
   	    printer.println("");
   	    printer.println("@DATA");
   	    //Access Base Files
   	    File folder1 = new File(voiceFile);
       	File[] arr = folder1.listFiles();
   	    ArrayList<String> finals = new ArrayList<String>();
       	for(int i = 0; i < arr.length; i++)
       	{
       		Scanner scanner = new Scanner(new FileReader(arr[i]));
       		System.out.println(arr[i]);
       		int count =0;
       		if(arr[i].getName().equals(skip2))continue;
       		while(scanner.hasNextLine())
       		{
       			String at = scanner.nextLine();
       			int ret = checkSkip(arr[i],whiteList,blackList);
       			if(ret == 1)
       			{
       				at+=",1";
       				finals.add(at);
       				count++;
       			}
       			else if(ret == -1)
       			{
       				int shouldUse = (int)(Math.random()*10);
       				if(shouldUse == 3)
       				{
       					at+=",0";
           				finals.add(at);
           				count++;
       				}
       			}
       		}
       	}
       	//Convert to training format
       	String[] nxt = new String[finals.size()];
       	for(int i = 0; i < finals.size(); i++)
       	{
       		nxt[i] = finals.get(i);
       	}
       	shuffleArray(nxt);
       	for(int i = 0; i < nxt.length; i++)
       	{
       		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
   			if(tokens.countTokens() != 14)
   			{
   				continue;
   			}
       		printer.println(nxt[i]);
       	}
       	printer.close();
       	//Set up Instance
       	BufferedReader br = null;
           br = new BufferedReader(new FileReader(combinedFile));
           Instances trainData = new Instances(br);
           trainData.setClassIndex(16);
           br.close();
           /*
            * Train Classifiers
            */
           //Set up SPegasos
      	 	String[] options1 = new String[6];
      	 	options1[0] = "-F";
      	 	options1[1] = "0";
      	 	options1[2] = "-L";
      	 	options1[3] = "1.0E-4";
      	 	options1[4] = "-E";
      	 	options1[5] = "500";
      	 	Classifier scl1 = AbstractClassifier.forName("weka.classifiers.functions.SPegasos", options1);
      	 	//Set Up LWL
       	 	String[] options2 = new String[10];
       	 	options2[0] = "-A";
       	 	options2[1] = "weka.core.neighboursearch.LinearNNSearch";
       	 	options2[2] = "-W";
       	 	options2[3] = "weka.classifiers.functions.SMO";
       	 	options2[4] = "--";
       	 	options2[5] = "-C"; 
       	 	options2[6] = "0.7958629079622792"; 
       	 	options2[7] = "-N";
         	options2[8] = "2";
         	options2[9] = "-K";
            options2[10] = "weka.classifiers.functions.supportVector.RBFKernel -G 0.005283673772958745";
            Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.LWL", options2);
           //Set Up MultiBoosted J48 tree
      	 	String[] options3 = new String[9];
      	 	options3[0] = "-C";
      	 	options3[1] = "3";
      	 	options3[2] = "-P";
      	 	options3[3] = "100";
      	 	options3[4] = "-S";
      	 	options3[5] = "1";
      	 	options3[6] = "-I";
      	 	options3[7] = "10";
      	 	options3[8] = "-W weka.classifiers.trees.J48 -- -C 0.25 -M 2";
      	 	Classifier scl3 = AbstractClassifier.forName("weka.classifiers.meta.RealAdaBoost", options3);
           //Set Up BayesNet
           String[] options4 = new String[5];
           options4[0] = "-D";
           options4[1] = "-Q";
           options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
           options4[3] = "-E";
           options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
           Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
           //Set Up Multi-Objective Evolutionary Fuzzy Classifier
           String[] options5 = new String[11];
           options5[0] = "-g 10";
           options5[1] = "-ps 100";
           options5[2] = "-s 1";
           options5[3] = "-ms 0.1";
           options5[4] = "-minv 30.0";
           options5[5] = "-maxv 2.0";
           options5[6] = "-maxr -1";
           options5[7] = "-ev 0";
           options5[8] = "-a 0";
           options5[9] = "-report-frequency 10";
           options5[10] = "-log-file D:\\Weka-3-8";
           Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.MultiObjectiveEvolutionaryFuzzyClassifier", options5);
           //Set Up FLR
      	 	String[] options6 = new String[4];
      	 	options6[0] = "-R";
      	 	options6[1] = "1.0";
      	 	options6[2] = "-Y";
      	 	options6[3] = "-B";
      	 	Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.FLR", options6);
           //Set up RBFClassifier
           String[] options7 = new String[14];
           options7[0] = "-N";
           options7[1] = "2";
           options7[2] = "-R";
           options7[3] = "0.01";
           options7[4] = "-L";
           options7[5] = "1.0E-6";
           options7[6] = "-C";
           options7[7] = "2";
           options7[8] = "-P";
           options7[9] = "1";
           options7[10] = "-E";
           options7[11] = "1";
           options7[12] = "-S";
           options7[13] = "1";
           Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
           //Set up SMO
           String[] options8 = new String[14];
           options8[0] = "-C";
           options8[1] = "1.0";
           options8[2] = "-L";
           options8[3] = "0.001";
           options8[4] = "-P";
           options8[5] = "1.0E-12";
           options8[6] = "-N";
           options8[7] = "0";
           options8[8] = "-V";
           options8[9] = "-1";
           options8[10] = "-W";
           options8[11] = "1";
           options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
           options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
           Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
           Classifier[] cl = new Classifier[7];
           cl[0] = scl1;
           cl[1] = scl2;
           cl[2] = scl3;
           cl[3] = scl4;
           cl[4] = scl5;
           cl[5] = scl6;
           cl[6] = scl7;
           //Set up Stacking
           Stacking finalClassifier = new Stacking();
           finalClassifier.setMetaClassifier(sclmeta);
           finalClassifier.setClassifiers(cl);
           finalClassifier.setDebug(false);
           finalClassifier.setNumDecimalPlaces(2);
           finalClassifier.setNumExecutionSlots(1);
           finalClassifier.buildClassifier(trainData);
           return finalClassifier;
   	}
    public static Stacking alss1()throws Exception
   	{
   		/*
   		 * Compile Data
   		 */
   		//Detail file status
       	File[] whiteList = new File[1];
       	File[] blackList = new File[2];
       	//Positive
       	whiteList[0] = new File(voiceFile+"ALSDataSet1.txt");
       	//Negative
       	blackList[0] = new File(voiceFile+"ALSDataSet2.txt");
       	blackList[1] = new File(voiceFile+"ALSDataSet3.txt");
   		//Detail which file to send to
   		File tnf2 = new File(combinedFile);
   		String skip2 = tnf2.getName();
   		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
   		//Make header
       	printer.println("@RELATION    Initial");
   		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
   		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
   		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
   		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
   		printer.println("@ATTRIBUTE NHR                    NUMERIC");
   		printer.println("@ATTRIBUTE HNR                    NUMERIC");
   	    printer.println("@ATTRIBUTE class                    {0,1}");
   	    printer.println("");
   	    printer.println("@DATA");
   	    //Access Base Files
   	    File folder1 = new File(voiceFile);
       	File[] arr = folder1.listFiles();
   	    ArrayList<String> finals = new ArrayList<String>();
       	for(int i = 0; i < arr.length; i++)
       	{
       		Scanner scanner = new Scanner(new FileReader(arr[i]));
       		System.out.println(arr[i]);
       		int count =0;
       		if(arr[i].getName().equals(skip2))continue;
       		while(scanner.hasNextLine())
       		{
       			String at = scanner.nextLine();
       			int ret = checkSkip(arr[i],whiteList,blackList);
       			if(ret == 1)
       			{
       				at+=",1";
       				finals.add(at);
       				count++;
       			}
       			else if(ret == -1)
       			{
       				int shouldUse = (int)(Math.random()*10);
       				if(shouldUse == 3)
       				{
       					at+=",0";
           				finals.add(at);
           				count++;
       				}
       			}
       		}
       	}
       	//Convert to training format
       	String[] nxt = new String[finals.size()];
       	for(int i = 0; i < finals.size(); i++)
       	{
       		nxt[i] = finals.get(i);
       	}
       	shuffleArray(nxt);
       	for(int i = 0; i < nxt.length; i++)
       	{
       		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
   			if(tokens.countTokens() != 14)
   			{
   				continue;
   			}
       		printer.println(nxt[i]);
       	}
       	printer.close();
       	//Set up Instance
       	BufferedReader br = null;
           br = new BufferedReader(new FileReader(combinedFile));
           Instances trainData = new Instances(br);
           trainData.setClassIndex(16);
           br.close();
           /*
            * Train Classifiers
            */
           //AdaBoost M1 SVM
           String[] options1 = new String[6];
           options1[0] = "-P";
           options1[1] = "100";
           options1[2] = "-S";
           options1[3] = "1";
           options1[4] = "-I";
           options1[5] = "10";
           options1[6] = "-W: weka.classifiers.functions.LibSVM -- -S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1";
            Classifier scl1 = AbstractClassifier.forName("weka.classifiers.meta.AdaBoostM1", options1);
      	 	//Set Up K*
      	 	String[] options2 = new String[8];
      	 	options2[0] = "-B";
      	 	options2[0] = "30";
      	 	options2[0] = "-E";
      	 	options2[0] = "-M";
      	 	options2[0] =  "n";
      	 	Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.KStar", options2);
      	 	//Set Up CART
      	 	String[] options3 = new String[8];
      	 	options3[0] = "-M";
      	 	options3[1] = "2.0";
       		options3[2] = "-N";
       		options3[3] = "5";
       		options3[4] = "-C";
       		options3[5] = "1.0";
       		options3[6] = "-S";
       		options3[7] = "1";
       		Classifier scl3 = AbstractClassifier.forName("weka.classifiers.trees.SimpleCart", options3);
           //Set Up BayesNet
           String[] options4 = new String[5];
           options4[0] = "-D";
           options4[1] = "-Q";
           options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
           options4[3] = "-E";
           options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
           Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
           //Set Up MODLEM
      	 	String[] options5 = new String[8];
      	 	options5[0] = "-RT";
      	 	options5[1] = "1";
      	 	options5[2] = "-CM";
      	 	options5[3] = "1";
      	 	options5[4] = "-CS";
      	 	options5[5] = "8";
      	 	options5[6] = "-AS";
      	 	options5[7] = "0";
      	 	Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.MODLEM", options5);
      	 	//Set up CHIRP
            String[] options6 = new String[2];
            options6[0] = "-V 7";
            options6[1] = "-S 1";
            Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.CHIRP", options6);
           //Set up RBFClassifier
           String[] options7 = new String[14];
           options7[0] = "-N";
           options7[1] = "2";
           options7[2] = "-R";
           options7[3] = "0.01";
           options7[4] = "-L";
           options7[5] = "1.0E-6";
           options7[6] = "-C";
           options7[7] = "2";
           options7[8] = "-P";
           options7[9] = "1";
           options7[10] = "-E";
           options7[11] = "1";
           options7[12] = "-S";
           options7[13] = "1";
           Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
           //Set up SMO
           String[] options8 = new String[14];
           options8[0] = "-C";
           options8[1] = "1.0";
           options8[2] = "-L";
           options8[3] = "0.001";
           options8[4] = "-P";
           options8[5] = "1.0E-12";
           options8[6] = "-N";
           options8[7] = "0";
           options8[8] = "-V";
           options8[9] = "-1";
           options8[10] = "-W";
           options8[11] = "1";
           options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
           options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
           Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
           Classifier[] cl = new Classifier[7];
           cl[0] = scl1;
           cl[1] = scl2;
           cl[2] = scl3;
           cl[3] = scl4;
           cl[4] = scl5;
           cl[5] = scl6;
           cl[6] = scl7;
           //Set up Stacking
           Stacking finalClassifier = new Stacking();
           finalClassifier.setMetaClassifier(sclmeta);
           finalClassifier.setClassifiers(cl);
           finalClassifier.setDebug(false);
           finalClassifier.setNumDecimalPlaces(2);
           finalClassifier.setNumExecutionSlots(1);
           finalClassifier.buildClassifier(trainData);
           return finalClassifier;
   	}
    public static Stacking alss2()throws Exception
   	{
   		/*
   		 * Compile Data
   		 */
   		//Detail file status
       	File[] whiteList = new File[1];
       	File[] blackList = new File[1];
       	//Positive
       	whiteList[0] = new File(voiceFile+"ALSDataSet2.txt");
       	//Negative
       	blackList[0] = new File(voiceFile+"ALSDataSet3.txt");
   		//Detail which file to send to
   		File tnf2 = new File(combinedFile);
   		String skip2 = tnf2.getName();
   		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
   		//Make header
       	printer.println("@RELATION    Initial");
   		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
   		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
   		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
   		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
   		printer.println("@ATTRIBUTE NHR                    NUMERIC");
   		printer.println("@ATTRIBUTE HNR                    NUMERIC");
   	    printer.println("@ATTRIBUTE class                    {0,1}");
   	    printer.println("");
   	    printer.println("@DATA");
   	    //Access Base Files
   	    File folder1 = new File(voiceFile);
       	File[] arr = folder1.listFiles();
   	    ArrayList<String> finals = new ArrayList<String>();
       	for(int i = 0; i < arr.length; i++)
       	{
       		Scanner scanner = new Scanner(new FileReader(arr[i]));
       		System.out.println(arr[i]);
       		int count =0;
       		if(arr[i].getName().equals(skip2))continue;
       		while(scanner.hasNextLine())
       		{
       			String at = scanner.nextLine();
       			int ret = checkSkip(arr[i],whiteList,blackList);
       			if(ret == 1)
       			{
       				at+=",1";
       				finals.add(at);
       				count++;
       			}
       			else if(ret == -1)
       			{
       				int shouldUse = (int)(Math.random()*10);
       				if(shouldUse == 3)
       				{
       					at+=",0";
           				finals.add(at);
           				count++;
       				}
       			}
       		}
       	}
       	//Convert to training format
       	String[] nxt = new String[finals.size()];
       	for(int i = 0; i < finals.size(); i++)
       	{
       		nxt[i] = finals.get(i);
       	}
       	shuffleArray(nxt);
       	for(int i = 0; i < nxt.length; i++)
       	{
       		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
   			if(tokens.countTokens() != 14)
   			{
   				continue;
   			}
       		printer.println(nxt[i]);
       	}
       	printer.close();
       	//Set up Instance
       	BufferedReader br = null;
           br = new BufferedReader(new FileReader(combinedFile));
           Instances trainData = new Instances(br);
           trainData.setClassIndex(16);
           br.close();
           /*
            * Train Classifiers
            */
           //Set up SPegasos
      	 	String[] options1 = new String[6];
      	 	options1[0] = "-F";
      	 	options1[1] = "0";
      	 	options1[2] = "-L";
      	 	options1[3] = "1.0E-4";
      	 	options1[4] = "-E";
      	 	options1[5] = "500";
      	 	Classifier scl1 = AbstractClassifier.forName("weka.classifiers.functions.SPegasos", options1);
      	 	//Set Up LWL
       	 	String[] options2 = new String[10];
       	 	options2[0] = "-A";
       	 	options2[1] = "weka.core.neighboursearch.LinearNNSearch";
       	 	options2[2] = "-W";
       	 	options2[3] = "weka.classifiers.functions.SMO";
       	 	options2[4] = "--";
       	 	options2[5] = "-C"; 
       	 	options2[6] = "0.7958629079622792"; 
       	 	options2[7] = "-N";
         	options2[8] = "2";
         	options2[9] = "-K";
            options2[10] = "weka.classifiers.functions.supportVector.RBFKernel -G 0.005283673772958745";
            Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.LWL", options2);
            //Set Up RandomForest
            String[] options3 = new String[14];
            options3[0] = "-P";
            options3[1] = "100";
            options3[2] = "-I";
            options3[3] = "100";
            options3[4] = "-num-slots";
            options3[5] = "1";
            options3[6] = "-K";
            options3[7] = "0";
            options3[8] = "-M";
            options3[9] = "1.0";
            options3[10] = "-V";
            options3[11] = "0.001";
            options3[12] = "-S";
            options3[13] = "1";
            Classifier scl3 = AbstractClassifier.forName("weka.classifiers.trees.RandomForest", options3);

           //Set Up BayesNet
           String[] options4 = new String[5];
           options4[0] = "-D";
           options4[1] = "-Q";
           options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
           options4[3] = "-E";
           options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
           Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
           //Set Up JRip
      	 	String[] options5 = new String[8];
      	 	options5[0] = "-F"; 
      	 	options5[1] = "3";
      	 	options5[2] = "-N";
      	 	options5[3] = "2.0";
      	 	options5[4] = "-O";
      	 	options5[5] = "2";
      	 	options5[6] = "-S";
      	 	options5[7] = "1";
      	 	Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.JRip", options5);
           //Set Up FLR
      	 	String[] options6 = new String[4];
      	 	options6[0] = "-R";
      	 	options6[1] = "1.0";
      	 	options6[2] = "-Y";
      	 	options6[3] = "-B";
      	 	Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.FLR", options6);
           //Set up RBFClassifier
           String[] options7 = new String[14];
           options7[0] = "-N";
           options7[1] = "2";
           options7[2] = "-R";
           options7[3] = "0.01";
           options7[4] = "-L";
           options7[5] = "1.0E-6";
           options7[6] = "-C";
           options7[7] = "2";
           options7[8] = "-P";
           options7[9] = "1";
           options7[10] = "-E";
           options7[11] = "1";
           options7[12] = "-S";
           options7[13] = "1";
           Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
           //Set up SMO
           String[] options8 = new String[14];
           options8[0] = "-C";
           options8[1] = "1.0";
           options8[2] = "-L";
           options8[3] = "0.001";
           options8[4] = "-P";
           options8[5] = "1.0E-12";
           options8[6] = "-N";
           options8[7] = "0";
           options8[8] = "-V";
           options8[9] = "-1";
           options8[10] = "-W";
           options8[11] = "1";
           options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
           options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
           Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
           Classifier[] cl = new Classifier[7];
           cl[0] = scl1;
           cl[1] = scl2;
           cl[2] = scl3;
           cl[3] = scl4;
           cl[4] = scl5;
           cl[5] = scl6;
           cl[6] = scl7;
           //Set up Stacking
           Stacking finalClassifier = new Stacking();
           finalClassifier.setMetaClassifier(sclmeta);
           finalClassifier.setClassifiers(cl);
           finalClassifier.setDebug(false);
           finalClassifier.setNumDecimalPlaces(2);
           finalClassifier.setNumExecutionSlots(1);
           finalClassifier.buildClassifier(trainData);
           return finalClassifier;
   	}
    public static Stacking hs1()throws Exception
   	{
   		/*
   		 * Compile Data
   		 */
   		//Detail file status
       	File[] whiteList = new File[1];
       	File[] blackList = new File[1];
       	//Positive
       	whiteList[0] = new File(voiceFile+"HpopharyngealDataSet1.txt");
       	//Negative
       	blackList[0] = new File(voiceFile+"HypopharyngealDataSet2.txt");
   		//Detail which file to send to
   		File tnf2 = new File(combinedFile);
   		String skip2 = tnf2.getName();
   		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
   		//Make header
       	printer.println("@RELATION    Initial");
   		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
   		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
   		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
   		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
   		printer.println("@ATTRIBUTE NHR                    NUMERIC");
   		printer.println("@ATTRIBUTE HNR                    NUMERIC");
   	    printer.println("@ATTRIBUTE class                    {0,1}");
   	    printer.println("");
   	    printer.println("@DATA");
   	    //Access Base Files
   	    File folder1 = new File(voiceFile);
       	File[] arr = folder1.listFiles();
   	    ArrayList<String> finals = new ArrayList<String>();
       	for(int i = 0; i < arr.length; i++)
       	{
       		Scanner scanner = new Scanner(new FileReader(arr[i]));
       		System.out.println(arr[i]);
       		int count =0;
       		if(arr[i].getName().equals(skip2))continue;
       		while(scanner.hasNextLine())
       		{
       			String at = scanner.nextLine();
       			int ret = checkSkip(arr[i],whiteList,blackList);
       			if(ret == 1)
       			{
       				at+=",1";
       				finals.add(at);
       				count++;
       			}
       			else if(ret == -1)
       			{
       				int shouldUse = (int)(Math.random()*10);
       				if(shouldUse == 3)
       				{
       					at+=",0";
           				finals.add(at);
           				count++;
       				}
       			}
       		}
       	}
       	//Convert to training format
       	String[] nxt = new String[finals.size()];
       	for(int i = 0; i < finals.size(); i++)
       	{
       		nxt[i] = finals.get(i);
       	}
       	shuffleArray(nxt);
       	for(int i = 0; i < nxt.length; i++)
       	{
       		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
   			if(tokens.countTokens() != 14)
   			{
   				continue;
   			}
       		printer.println(nxt[i]);
       	}
       	printer.close();
       	//Set up Instance
       	BufferedReader br = null;
           br = new BufferedReader(new FileReader(combinedFile));
           Instances trainData = new Instances(br);
           trainData.setClassIndex(16);
           br.close();
           /*
            * Train Classifiers
            */
         //AdaBoost M1 SVM
           String[] options1 = new String[6];
           options1[0] = "-P";
           options1[1] = "100";
           options1[2] = "-S";
           options1[3] = "1";
           options1[4] = "-I";
           options1[5] = "10";
           options1[6] = "-W: weka.classifiers.functions.LibSVM -- -S 0 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.5 -M 40.0 -C 1.0 -E 0.001 -P 0.1";
            Classifier scl1 = AbstractClassifier.forName("weka.classifiers.meta.AdaBoostM1", options1);
           // Set up IBk/KNN
        	String[] options2 = new String[8];
        	options2[0] = "-U";
        	options2[1] = "0";
        	options2[2] = "-K";
        	options2[3] = "-1";
        	options2[4] = "-A"; 
        	options2[5] = "weka.core.neighboursearch.LinearNNSearch -A \"weka.core.EuclideanDistance -R first-last\"";
        	options2[6] = "-W";
        	options2[7] = "weka.classifiers.trees.DecisionStump";
        	Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.IBk", options2);
        	//Set Up CART
      	 	String[] options3 = new String[8];
      	 	options3[0] = "-M";
      	 	options3[1] = "2.0";
       		options3[2] = "-N";
       		options3[3] = "5";
       		options3[4] = "-C";
       		options3[5] = "1.0";
       		options3[6] = "-S";
       		options3[7] = "1";
       		Classifier scl3 = AbstractClassifier.forName("weka.classifiers.trees.SimpleCart", options3);
           //Set Up BayesNet
           String[] options4 = new String[5];
           options4[0] = "-D";
           options4[1] = "-Q";
           options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
           options4[3] = "-E";
           options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
           Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
           //Set Up FURIA
      	  String[] options5 = new String[12];
      	  options5[0] = "-F";
      	  options5[1] = "3";
      	  options5[2] = "-N";
      	  options5[3] = "2.0";
      	  options5[4] = "-O";
      	  options5[5] = "2";
      	  options5[6] = "-S";
      	  options5[7] = "1";
      	  options5[8] = "-p";
      	  options5[9] = "0";
      	  options5[10] = "-s";
      	  options5[11] = "0";
      	  Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.FURIA", options5);
           //Set Up FLR
      	 	String[] options6 = new String[4];
      	 	options6[0] = "-R";
      	 	options6[1] = "1.0";
      	 	options6[2] = "-Y";
      	 	options6[3] = "-B";
      	 	Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.FLR", options6);
           //Set up RBFClassifier
           String[] options7 = new String[14];
           options7[0] = "-N";
           options7[1] = "2";
           options7[2] = "-R";
           options7[3] = "0.01";
           options7[4] = "-L";
           options7[5] = "1.0E-6";
           options7[6] = "-C";
           options7[7] = "2";
           options7[8] = "-P";
           options7[9] = "1";
           options7[10] = "-E";
           options7[11] = "1";
           options7[12] = "-S";
           options7[13] = "1";
           Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
           //Set up SMO
           String[] options8 = new String[14];
           options8[0] = "-C";
           options8[1] = "1.0";
           options8[2] = "-L";
           options8[3] = "0.001";
           options8[4] = "-P";
           options8[5] = "1.0E-12";
           options8[6] = "-N";
           options8[7] = "0";
           options8[8] = "-V";
           options8[9] = "-1";
           options8[10] = "-W";
           options8[11] = "1";
           options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
           options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
           Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
           Classifier[] cl = new Classifier[7];
           cl[0] = scl1;
           cl[1] = scl2;
           cl[2] = scl3;
           cl[3] = scl4;
           cl[4] = scl5;
           cl[5] = scl6;
           cl[6] = scl7;
           //Set up Stacking
           Stacking finalClassifier = new Stacking();
           finalClassifier.setMetaClassifier(sclmeta);
           finalClassifier.setClassifiers(cl);
           finalClassifier.setDebug(false);
           finalClassifier.setNumDecimalPlaces(2);
           finalClassifier.setNumExecutionSlots(1);
           finalClassifier.buildClassifier(trainData);
           return finalClassifier;
   	}
    public static Stacking ls1()throws Exception
   	{
   		/*
   		 * Compile Data
   		 */
   		//Detail file status
       	File[] whiteList = new File[1];
       	File[] blackList = new File[1];
       	//Positive
       	whiteList[0] = new File(voiceFile+"PharyngealDataSet1.txt");
       	//Negative
       	blackList[0] = new File(voiceFile+"PharyngeamDataSet2.txt");
   		//Detail which file to send to
   		File tnf2 = new File(combinedFile);
   		String skip2 = tnf2.getName();
   		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(combinedFile)));
   		//Make header
       	printer.println("@RELATION    Initial");
   		printer.println("@ATTRIBUTE JitterPer              NUMERIC");
   		printer.println("@ATTRIBUTE JitterAbs              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPRAP                NUMERIC");
   		printer.println("@ATTRIBUTE MDVPPPQ                NUMERIC");
   		printer.println("@ATTRIBUTE JitterDDP              NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmer            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPShimmerDB          NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ3            NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerAPQ5            NUMERIC");
   		printer.println("@ATTRIBUTE MDVPAPQ                NUMERIC");
   		printer.println("@ATTRIBUTE ShimmerDDA             NUMERIC");
   		printer.println("@ATTRIBUTE NHR                    NUMERIC");
   		printer.println("@ATTRIBUTE HNR                    NUMERIC");
   	    printer.println("@ATTRIBUTE class                    {0,1}");
   	    printer.println("");
   	    printer.println("@DATA");
   	    //Access Base Files
   	    File folder1 = new File(voiceFile);
       	File[] arr = folder1.listFiles();
   	    ArrayList<String> finals = new ArrayList<String>();
       	for(int i = 0; i < arr.length; i++)
       	{
       		Scanner scanner = new Scanner(new FileReader(arr[i]));
       		System.out.println(arr[i]);
       		int count =0;
       		if(arr[i].getName().equals(skip2))continue;
       		while(scanner.hasNextLine())
       		{
       			String at = scanner.nextLine();
       			int ret = checkSkip(arr[i],whiteList,blackList);
       			if(ret == 1)
       			{
       				at+=",1";
       				finals.add(at);
       				count++;
       			}
       			else if(ret == -1)
       			{
       				int shouldUse = (int)(Math.random()*10);
       				if(shouldUse == 3)
       				{
       					at+=",0";
           				finals.add(at);
           				count++;
       				}
       			}
       		}
       	}
       	//Convert to training format
       	String[] nxt = new String[finals.size()];
       	for(int i = 0; i < finals.size(); i++)
       	{
       		nxt[i] = finals.get(i);
       	}
       	shuffleArray(nxt);
       	for(int i = 0; i < nxt.length; i++)
       	{
       		StringTokenizer tokens = new StringTokenizer(nxt[i],",");
   			if(tokens.countTokens() != 14)
   			{
   				continue;
   			}
       		printer.println(nxt[i]);
       	}
       	printer.close();
       	//Set up Instance
       	BufferedReader br = null;
           br = new BufferedReader(new FileReader(combinedFile));
           Instances trainData = new Instances(br);
           trainData.setClassIndex(16);
           br.close();
           /*
            * Train Classifiers
            */
           //Set up SPegasos
      	 	String[] options1 = new String[6];
      	 	options1[0] = "-F";
      	 	options1[1] = "0";
      	 	options1[2] = "-L";
      	 	options1[3] = "1.0E-4";
      	 	options1[4] = "-E";
      	 	options1[5] = "500";
      	 	Classifier scl1 = AbstractClassifier.forName("weka.classifiers.functions.SPegasos", options1);
      	 	//Set Up K*
      	 	String[] options2 = new String[8];
      	 	options2[0] = "-B";
      	 	options2[0] = "30";
      	 	options2[0] = "-E";
      	 	options2[0] = "-M";
      	 	options2[0] =  "n";
      	 	Classifier scl2 = AbstractClassifier.forName("weka.classifiers.lazy.KStar", options2);
           //Set Up MultiBoosted J48 tree
      	 	String[] options3 = new String[9];
      	 	options3[0] = "-C";
      	 	options3[1] = "3";
      	 	options3[2] = "-P";
      	 	options3[3] = "100";
      	 	options3[4] = "-S";
      	 	options3[5] = "1";
      	 	options3[6] = "-I";
      	 	options3[7] = "10";
      	 	options3[8] = "-W weka.classifiers.trees.J48 -- -C 0.25 -M 2";
      	 	Classifier scl3 = AbstractClassifier.forName("weka.classifiers.meta.RealAdaBoost", options3);
           //Set Up BayesNet
           String[] options4 = new String[5];
           options4[0] = "-D";
           options4[1] = "-Q";
           options4[2] = "weka.classifiers.bayes.net.search.local.K2 -- -P 1 -S BAYES";
           options4[3] = "-E";
           options4[4] = "weka.classifiers.bayes.net.estimate.SimpleEstimator -- -A 0.5";
           Classifier scl4 = AbstractClassifier.forName("weka.classifiers.bayes.BayesNet", options4);
           //Set Up JRip
      	 	String[] options5 = new String[8];
      	 	options5[0] = "-F"; 
      	 	options5[1] = "3";
      	 	options5[2] = "-N";
      	 	options5[3] = "2.0";
      	 	options5[4] = "-O";
      	 	options5[5] = "2";
      	 	options5[6] = "-S";
      	 	options5[7] = "1";
      	 	Classifier scl5 = AbstractClassifier.forName("weka.classifiers.rules.JRip", options5);
           //Set Up FLR
      	 	String[] options6 = new String[4];
      	 	options6[0] = "-R";
      	 	options6[1] = "1.0";
      	 	options6[2] = "-Y";
      	 	options6[3] = "-B";
      	 	Classifier scl6 = AbstractClassifier.forName("weka.classifiers.misc.FLR", options6);
           //Set up RBFClassifier
           String[] options7 = new String[14];
           options7[0] = "-N";
           options7[1] = "2";
           options7[2] = "-R";
           options7[3] = "0.01";
           options7[4] = "-L";
           options7[5] = "1.0E-6";
           options7[6] = "-C";
           options7[7] = "2";
           options7[8] = "-P";
           options7[9] = "1";
           options7[10] = "-E";
           options7[11] = "1";
           options7[12] = "-S";
           options7[13] = "1";
           Classifier scl7 = AbstractClassifier.forName("weka.classifiers.functions.RBFClassifier", options7);
           //Set up SMO
           String[] options8 = new String[14];
           options8[0] = "-C";
           options8[1] = "1.0";
           options8[2] = "-L";
           options8[3] = "0.001";
           options8[4] = "-P";
           options8[5] = "1.0E-12";
           options8[6] = "-N";
           options8[7] = "0";
           options8[8] = "-V";
           options8[9] = "-1";
           options8[10] = "-W";
           options8[11] = "1";
           options8[12] = "-K weka.classifiers.functions.supportVector.PolyKernel -E 1.0 -C 250007";
           options8[13] = "-calibrator weka.classifiers.functions.Logistic -R 1.0E-8 -M -1 -num-decimal-places 4";
           Classifier sclmeta = AbstractClassifier.forName("weka.classifiers.functions.SMO", options8);
           Classifier[] cl = new Classifier[7];
           cl[0] = scl1;
           cl[1] = scl2;
           cl[2] = scl3;
           cl[3] = scl4;
           cl[4] = scl5;
           cl[5] = scl6;
           cl[6] = scl7;
           //Set up Stacking
           Stacking finalClassifier = new Stacking();
           finalClassifier.setMetaClassifier(sclmeta);
           finalClassifier.setClassifiers(cl);
           finalClassifier.setDebug(false);
           finalClassifier.setNumDecimalPlaces(2);
           finalClassifier.setNumExecutionSlots(1);
           finalClassifier.buildClassifier(trainData);
           return finalClassifier;
   	}
    static int checkSkip(File name, File[] whiteList, File[] blackList)
	{
		for(int i = 0; i < whiteList.length; i++)
		{
			if(name.getName().equals(whiteList[i].getName()))
			{
				return 1;
			}
		}
		return -1;
	}
	static void shuffleArray(String[] ar)
	{
	    Random rnd = ThreadLocalRandom.current();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      String a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	}
    
}



