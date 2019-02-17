//java imports
import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


//WEKA imports
import weka.core.Instances;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.BayesNet;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Vote;
import weka.classifiers.trees.RandomForest;
public class MultiVoiceAnalysisWindows
{
        /*static String voiceFile = "D:\\Composite Voice Dataset";
        static String combinedFile = "D:\\Composite Voice Dataset\\CombinedDataset.arff";*/
        static String voiceFile = "/detector/program/Secondary Datasets";
    static String combinedFile = "/detector/program/Secondary Datasets/CombinedDataset.arff/";
    public static void main(String[] args) throws Exception 
    {
            //Initialize classifiers.
            
            HighLevelClassifiers high = new HighLevelClassifiers();
            Stacking pathologyvscontrol = high.PathologyvsControl();
            Stacking othervsbasalganglia = high.OthervsBasalGanglia();
            MidLevelClassifiers mid = new MidLevelClassifiers();
            Stacking concussionvscoronaryartery = mid.ConcussionvsCoronaryArtery();
            Stacking coronaryarteryvscancer = mid.CoronaryArteryvsCancer();
            Stacking parkinsonsvshuntingtons = mid.ParkinsonsvsHuntingtons();
            Stacking parkinsonsvsdementia = mid.ParkinsonsvsDementia();
            Stacking huntingtonsvsmultiple = mid.HuntingtonsvsMultiple();
            LowLevelClassifiers low = new LowLevelClassifiers();
            Stacking concussionvstbi = low.ConcussionvsTBI();
            Stacking laryngealvshypopharyngeal = low.LaryngealvsHypopharyngeal();
            Stacking parkinsonsvsals = low.ParkinsonsvsALS();
            Stacking dementiavscerebralpalsy = low.DementiavsCerebralPalsy();
            Stacking huntingtonsvsalzheimers = low.HuntingtonsvsAlzheimers();
            Stacking multiplesclerosisvsmultiplesystematrophy = low.MultipleSclerosisvsMultipleSystemAtrophy();
            SecondaryClassifiers second = new SecondaryClassifiers();
            Stacking ps1 = second.ps1();
            Stacking ps2 = second.ps2();
            Stacking ps3 = second.ps3();
            Stacking ps4 = second.ps4();
            Stacking as1 = second.as1();
            Stacking as2 = second.as2();
            Stacking alss1 = second.alss1();
            Stacking alss2 = second.alss2();
            Stacking hs1 = second.hs1();
            Stacking ls1 = second.ls1();
            ConfirmationClassifiers confirm = new ConfirmationClassifiers();
        System.out.println("activated");
        while(1 > 0)
        {
                //Get Data
                Scanner dirscanner = new Scanner(System.in);
                String overbear = dirscanner.nextLine();
                //Check to Stop
                if(overbear.equals("exit_program"))
                {
                        System.exit(0);
                }
                //Run PRAAT
                PrintWriter prepare = new PrintWriter(new FileWriter("/detector/program/" + overbear + "_out.txt"));
                prepare.close();
                    Runtime rt = Runtime.getRuntime();
                    Process pr = rt.exec("./praat --run featureextractionscript.praat " + overbear);
                    Thread.sleep(3000);
                    Scanner scanner2 = new Scanner(new FileReader("/detector/program/" + overbear + "_out.txt"));
                    //Build Classification File Header
            Scanner scanner = new Scanner(new FileReader("/detector/program/sample.txt"));
                    PrintWriter print = new PrintWriter(new FileWriter("/detector/program/" + overbear + "_predict.txt"));
                    while(scanner.hasNextLine())
                    {
                            print.println(scanner.nextLine());
                    }
                    scanner.close();
                    print.println();
                    //Transfer Data to File
                    double[] arr = new double[13];
                    for(int i = 0; i < 13; i++)
                    {
                            arr[i] = scanner2.nextDouble();
                            print.print(arr[i] + ",");
                    }
                    print.print("?");
                    print.close();
                    scanner2.close();
                    //Classify
            Instances unlabeled = new Instances(new BufferedReader(new FileReader("/detector/program/" + overbear + "_predict.txt")));
            unlabeled.setClassIndex(13);
            double percentmatch = 1.0;
            percentmatch *= pathologyvscontrol.distributionForInstance(unlabeled.instance(0))[0];
            if(pathologyvscontrol.classifyInstance(unlabeled.instance(0)) == 1)
            {
                    percentmatch *= othervsbasalganglia.distributionForInstance(unlabeled.instance(0))[0];
                    if(othervsbasalganglia.classifyInstance(unlabeled.instance(0))==1)
                    {
                            percentmatch *= concussionvscoronaryartery.distributionForInstance(unlabeled.instance(0))[0];
                            if(concussionvscoronaryartery.classifyInstance(unlabeled.instance(0))==1)
                            {
                                    percentmatch *= concussionvstbi.distributionForInstance(unlabeled.instance(0))[0];
                                    if(concussionvstbi.classifyInstance(unlabeled.instance(0))==1)
                                    {
                                            if(percentmatch < 0.8)
                                            {
                                                    Stacking con = confirm.ConcussionConfirm();
                                                    if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                    {
                                                            if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.63)
                                                            {
                                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                    print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                            }
                                                            else
                                                            {
                                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                        print2.println("Concussion");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                            }
                                                    }
                                                    else
                                                    {
                                                            PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                            print2.println("Control");
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                    }
                                            }
                                            else
                                            {
                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                print2.println("Concussion");
                                for(int i = 0; i < 13; i++)
                                    {
                                            print2.println(arr[i]);
                                    }
                                print2.close();
                                System.out.println("finished");
                                            }
                                    }
                                    else
                                    {
                                            if(percentmatch < 0.8)
                                            {
                                                    Stacking con = confirm.TraumaticBrainInjuryConfirm();
                                                    if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                    {
                                                            if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.61)
                                                            {
                                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                    print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                            }
                                                            else
                                                            {
                                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                        print2.println("Traumatic Brain Injury");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                            }
                                                    }
                                                    else
                                                    {
                                                            PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                            print2.println("Control");
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                    }
                                            }
                                            else
                                            {
                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                print2.println("Traumatic Brain Injury");
                                for(int i = 0; i < 13; i++)
                                    {
                                            print2.println(arr[i]);
                                    }
                                print2.close();
                                System.out.println("finished");
                                            }
                                    }
                            }
                            else
                            {
                                    percentmatch *= coronaryarteryvscancer.distributionForInstance(unlabeled.instance(0))[0];
                                    if(coronaryarteryvscancer.classifyInstance(unlabeled.instance(0))==1)
                                    {
                                            if(percentmatch < 0.73)
                                            {
                                                    Stacking con = confirm.CoronaryArteryConfirm();
                                                    if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                    {
                                                            if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.63)
                                                            {
                                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                    print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                            }
                                                            else
                                                            {
                                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                        print2.println("Coronary Artery");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                            }
                                                    }
                                                    else
                                                    {
                                                            PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                            print2.println("Control");
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                    }
                                            }
                                            else
                                            {
                                                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                print2.println("Coronary Artery");
                                for(int i = 0; i < 13; i++)
                                    {
                                            print2.println(arr[i]);
                                    }
                                print2.close();
                                System.out.println("finished");
                                            }
                                    }
                                    else
                                    {
                                            percentmatch *= laryngealvshypopharyngeal.distributionForInstance(unlabeled.instance(0))[0];
                                            if(laryngealvshypopharyngeal.classifyInstance(unlabeled.instance(0))==1)
                                        {
                                                    if(percentmatch < 0.68)
                                                {
                                                        Stacking con = confirm.LaryngealCancerConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.55)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Laryngeal Cancer");
                                            print2.println(second.LaryngealSecond(unlabeled, ls1));
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Laryngeal Cancer");
                                    print2.println(second.LaryngealSecond(unlabeled, ls1));
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                        }
                                        else
                                        {
                                                if(percentmatch < 0.65)
                                                {
                                                        Stacking con = confirm.HypopharyngealCancerConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.53)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Hypopharyngeal Cancer");
                                            print2.println(second.HypopharyngealSecond(unlabeled, hs1));
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Hypopharyngeal Cancer");
                                    print2.println(second.HypopharyngealSecond(unlabeled, hs1));
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                        }
                                    }
                            }
                    }
                    else
                    {
                            percentmatch *= parkinsonsvshuntingtons.distributionForInstance(unlabeled.instance(0))[0];
                            if(parkinsonsvshuntingtons.classifyInstance(unlabeled.instance(0)) == 1)
                            {
                                    percentmatch *= parkinsonsvsdementia.distributionForInstance(unlabeled.instance(0))[0];
                                    if(parkinsonsvsdementia.classifyInstance(unlabeled.instance(0)) == 1)
                                {
                                            percentmatch *= parkinsonsvsals.distributionForInstance(unlabeled.instance(0))[0];
                                            if(parkinsonsvsals.classifyInstance(unlabeled.instance(0)) == 1)
                                    {
                                                    if(percentmatch < 0.76)
                                                {
                                                        Stacking con = confirm.ParkinsonsConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.57)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Parkinsons");
                                            print2.println(second.ParkinsonsSecond(unlabeled,ps1,ps2,ps3,ps4));
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Parkinsons");
                                    print2.println(second.ParkinsonsSecond(unlabeled,ps1,ps2,ps3,ps4));
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                    }
                                    else
                                    {
                                            if(percentmatch < 0.84)
                                                {
                                                        Stacking con = confirm.ALSConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.59)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("ALS");
                                            print2.println(second.ALSSecond(unlabeled,alss1,alss2));
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("ALS");
                                    print2.println(second.ALSSecond(unlabeled,alss1,alss2));
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                    }
                                }
                                else
                                {
                                        percentmatch *= dementiavscerebralpalsy.distributionForInstance(unlabeled.instance(0))[0];
                                        if(dementiavscerebralpalsy.classifyInstance(unlabeled.instance(0)) == 1)
                                    {
                                                if(percentmatch < 0.76)
                                                {
                                                        Stacking con = confirm.DementiaConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.57)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Dementia");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Dementia");
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                    }
                                    else
                                    {
                                            if(percentmatch < 0.83)
                                                {
                                                        Stacking con = confirm.CerebralPalsyConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.57)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Cerebral Palsy");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Cerebral Palsy");
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                    }
                                }
                            }
                            else
                            {
                                    percentmatch *= huntingtonsvsmultiple.distributionForInstance(unlabeled.instance(0))[0];
                                    if(huntingtonsvsmultiple.classifyInstance(unlabeled.instance(0)) == 1)
                                    {
                                            percentmatch *= huntingtonsvsalzheimers.distributionForInstance(unlabeled.instance(0))[0];
                                            if(huntingtonsvsalzheimers.classifyInstance(unlabeled.instance(0)) == 1)
                                        {
                                                    if(percentmatch < 0.65)
                                                {
                                                        Stacking con = confirm.HuntingtonsConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.78)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Huntingtons");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Huntingtons");
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                        }
                                        else
                                        {
                                                if(percentmatch < 0.84)
                                                {
                                                        Stacking con = confirm.AlzheimersConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.59)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Alzheimers");
                                            print2.println(second.AlzheimersSecond(unlabeled,as1,as2));
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Alzheimers");
                                    print2.println(second.AlzheimersSecond(unlabeled,as1,as2));
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                        }
                                    }
                                    else
                                    {
                                            percentmatch *= multiplesclerosisvsmultiplesystematrophy.distributionForInstance(unlabeled.instance(0))[0];
                                            if(multiplesclerosisvsmultiplesystematrophy.classifyInstance(unlabeled.instance(0)) == 1)
                                        {
                                                    if(percentmatch < 0.81)
                                                {
                                                        Stacking con = confirm.MultipleSclerosisConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.57)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Multiple Sclerosis");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Multiple Sclerosis");
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                        }
                                        else
                                        {
                                                if(percentmatch < 0.81)
                                                {
                                                        Stacking con = confirm.MultipleSystemAtrophyConfirm();
                                                        if(con.classifyInstance(unlabeled.instance(0)) == 1)
                                                        {
                                                                if(con.distributionForInstance(unlabeled.instance(0))[0] < 0.32)
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                        print2.println("Control");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                                else
                                                                {
                                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                            print2.println("Multiple System Atrophy");
                                            for(int i = 0; i < 13; i++)
                                                {
                                                        print2.println(arr[i]);
                                                }
                                            print2.close();
                                            System.out.println("finished");
                                                                }
                                                        }
                                                        else
                                                        {
                                                                PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                                                print2.println("Control");
                                        for(int i = 0; i < 13; i++)
                                            {
                                                    print2.println(arr[i]);
                                            }
                                        print2.close();
                                        System.out.println("finished");
                                                        }
                                                }
                                                else
                                                {
                                                        PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                                    print2.println("Multiple System Atrophy");
                                    for(int i = 0; i < 13; i++)
                                        {
                                                print2.println(arr[i]);
                                        }
                                    print2.close();
                                    System.out.println("finished");
                                                }
                                        }
                                    }
                            }
                    }
            }
            else
            {
                    PrintWriter print2 = new PrintWriter(new FileWriter("/detector/outputs/" + args[0] + ".txt"));
                print2.println("Normal");
                for(int i = 0; i < 13; i++)
                    {
                                print2.println(arr[i]);
                        }
                print2.close();
                System.out.println("finished");
            }
        }
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
