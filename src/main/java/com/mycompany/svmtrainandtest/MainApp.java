package com.mycompany.svmtrainandtest;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.*;
import weka.core.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Random;


public class MainApp extends Application {

    final static Logger logger = Logger.getLogger(MainApp.class);

    @Override
    public void start(Stage stage) throws Exception {
        stage.setResizable(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
        MainWindowController controller = new MainWindowController(stage);
        loader.setController(controller);
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        
        stage.setTitle("SVN Train and Test");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        if(args.length == 0)
            launch(args);
        else {
            Params params = parseParams(args);
            try {
                params.validateParams();
            } catch (Exception e) {
                logger.error(e.getMessage());
                System.exit(1);
            }
            logger.info("Parametry prawidłowe. Rozpoczynam wczytywanie danych treningowych");
            Instances trainingInstances = loadTrainData(params);
            if (trainingInstances == null) return;
            TestDataLoader TestDataLoader = new TestDataLoader(params, trainingInstances).invoke();
            if (TestDataLoader.is()) return;
            trainingInstances = TestDataLoader.getTrainingInstances();
            Instances testInstances = TestDataLoader.getTestInstances();
            logger.info("Rozpoczynam uczenie klasyfikatora...");
            if(params.getTrainingDataClass() == null) {
                trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
                testInstances.setClassIndex(trainingInstances.numAttributes() - 1);
            }
            else {
                Boolean found = false;
                Enumeration<Attribute> attributes = trainingInstances.enumerateAttributes();
                while (attributes.hasMoreElements()) {
                    Attribute attribute = attributes.nextElement();
                    if(attribute.name().equals(params.getTrainingDataClass())) {
                        found = true;
                        trainingInstances.setClass(attribute);
                        testInstances.setClass(attribute);
                    }
                }
                if(!found) {
                    System.out.println("Nie znaleziono atrybutu decyzyjnego: "+params.getTrainingDataClass());
                    System.exit(10);
                }
            }
            Evaluation evaluation = trainAndTest(params, trainingInstances, testInstances);
            makeResult(params, evaluation);
        }
        System.exit(0);
    }

    private static void makeResult(Params params, Evaluation evaluation) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(evaluation.toClassDetailsString("Klasy decyzyjne:"));
            builder.append("\n\n");
            builder.append(evaluation.toCumulativeMarginDistributionString());
            builder.append("\n\n");
            builder.append(evaluation.toMatrixString("Macierz:"));
            builder.append("\n\n");
            builder.append(evaluation.toSummaryString("Podsumowanie:", false));
        } catch (Exception e) {
            logger.error("Błąd generowania wyników");
            System.exit(4);
        }
        if(params.getResultFile() == null) {
            System.out.println("wynik");
            System.out.println(builder);
        }
        else {
            logger.info("Zapisuję wynik do pliku... ");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(params.getResultFile()));
                writer.write(builder.toString());
                writer.close();
                logger.info("Wynik zapisany.");
            } catch (IOException e) {
                logger.error("Błąd zapisu danych do pliku!");
                System.exit(10);
            }
        }
    }

    private static Evaluation trainAndTest(Params params, Instances trainingInstances, Instances testInstances) {
        SMO smo = new SMO();
        if(params.getDebug() == null)
            smo.setDebug(false);
        else
            smo.setDebug(params.getDebug());
        if(params.getEpsilon() == null)
            smo.setEpsilon(Double.parseDouble("1.0E-12"));
        else {
            try {
                smo.setEpsilon(Double.parseDouble(params.getEpsilon()));
            } catch (Exception e) {
                System.out.println("Zły epsilon");
            }
        }
        SelectedTag tag = smo.getFilterType();
        Tag[] tags = tag.getTags();
        Tag normalize = null;
        Tag standarize = null;
        Tag disabled = null;
        for(Tag tag1 : tags) {
            if(tag1.getReadable().equals("Normalize training data"))
                normalize = tag1;
            else if(tag1.getReadable().equals("Standardize training data"))
                standarize = tag1;
            else if(tag1.getReadable().equals("No normalization/standardization"))
                disabled = tag1;
        }
        if(params.getFilteringMode() == null)
            smo.setFilterType(new SelectedTag(normalize.getID(), tags));
        else {
            switch (params.getFilteringMode()) {
                case DISABLED:
                    smo.setFilterType(new SelectedTag(disabled.getID(), tags));
                    break;
                case NORMALIZE:
                    smo.setFilterType(new SelectedTag(normalize.getID(), tags));
                    break;
                case STANDARIZE:
                    smo.setFilterType(new SelectedTag(standarize.getID(), tags));
                    break;
            }
        }
        if (params.getKernelType() == null)
            smo.setKernel(new PolyKernel());
        else {
            switch (params.getKernelType()) {
                case PUK:
                    smo.setKernel(new Puk());
                    break;
                case RBF:
                    smo.setKernel(new RBFKernel());
                    break;
                case POLY:
                    smo.setKernel(new PolyKernel());
                    break;
                case STRING:
                    smo.setKernel(new StringKernel());
                    break;
                case NORMALIZED_POLY:
                    smo.setKernel(new NormalizedPolyKernel());
                    break;
            }
        }
        if(params.getNumDecimal() == null)
            smo.setNumDecimalPlaces(2);
        else
            smo.setNumDecimalPlaces(params.getNumDecimal());
        Evaluation evaluation = null;
        try {
            smo.buildClassifier(trainingInstances);
            logger.info("Uczenie zakończone.");
            logger.info("Rozpoczynam testowanie klasyfikatora...");
            evaluation = new Evaluation(trainingInstances);
            evaluation.evaluateModel(smo, testInstances);
            logger.info("Test zakończony. Generuję wynik...");
        } catch (Exception e) {
            logger.info("Błąd klasyfikacji");
            System.exit(2);
        }
        return evaluation;
    }

    private static Instances loadTrainData(Params params) {
        Instances trainingInstances = null;
        try {
            File trainingDataFile = new File(params.getTrainingDataFileName());
            if(!trainingDataFile.exists()) {
                logger.error("Plik danych treningowych nie istnieje");
                System.exit(400);
            }
            String extension;
            int dot = trainingDataFile.getName().lastIndexOf(".");
            extension = trainingDataFile.getName().substring(dot + 1, trainingDataFile.getName().length());
            LoadInstances loadInstances = new LoadInstances(extension).invoke(trainingDataFile);
            if (loadInstances.is()) return null;
            trainingInstances = loadInstances.getData();
        } catch (Exception e) {
            logger.error("Zły format danych wejściowych");
            System.exit(101);
        }
        System.out.println("Wczytano "+trainingInstances.numInstances()+" instancji.");
        return trainingInstances;
    }

    private static Params parseParams(String[] args) {
        Params params = new Params();
        for(String arg : args) {
            String[] tab = arg.split("=", 2);
            if(tab.length < 2) {
                logger.error("Nieprawidłowy parametr: "+tab[0]);
                System.exit(1);
            }
            else {
                String name = tab[0];
                String value = tab[1];
                switch (name) {
                    case "trainData.file":
                        params.setTrainingDataFileName(value);
                        break;
                    case "decisionClass":
                        params.setTrainingDataClass(value);
                        break;
                    case "testData.file":
                        params.setTestDataFileName(value);
                        break;
                    case "testData.percent":
                        try {
                            int testDataPercent = Integer.parseInt(value);
                            if(testDataPercent <= 0 || testDataPercent >= 100) {
                                throw new Exception();
                            }
                            params.setTestDataPercent(testDataPercent);
                        } catch (Exception e) {
                            System.out.println("Zła wartość testData.percent");
                            System.exit(200);
                        }
                        break;
                    case "testData.seed":
                        try {
                            params.setTestDataSeed(Integer.parseInt(value));
                        } catch (Exception e) {
                            System.out.println("Nieprawidłowe ziarno losowe.");
                            System.exit(70);
                        }
                        break;
                    case "output.file":
                        params.setResultFile(value);
                        break;
                    case "debug":
                        if(value.equals("true"))
                            params.setDebug(true);
                        break;
                    case "epsilon":
                        params.setEpsilon(value);
                        break;
                    case "filtering":
                        if(value.equals("NORMALIZE"))
                            params.setFilteringMode(FilteringMode.NORMALIZE);
                        else if(value.equals("STANDARIZE"))
                            params.setFilteringMode(FilteringMode.STANDARIZE);
                        else if(value.equals("DISABLED"))
                            params.setFilteringMode(FilteringMode.DISABLED);
                        else {
                            System.out.println("Filtering mode: "+value+" not supported");
                            System.exit(20);
                        }
                        break;
                    case "kernel":
                        if(value.equals("NORMALIZED_POLY"))
                            params.setKernelType(KernelType.NORMALIZED_POLY);
                        else if(value.equals("POLY"))
                            params.setKernelType(KernelType.POLY);
                        else if (value.equals("PUK"))
                            params.setKernelType(KernelType.PUK);
                        else if(value.equals("RBF"))
                            params.setKernelType(KernelType.RBF);
                        else if (value.equals("STRING"))
                            params.setKernelType(KernelType.STRING);
                        else {
                            System.out.println("Nieobsługiwany typ jądra: "+value);
                            System.exit(50);
                        }
                        break;
                    case "numDecimal":
                        try {
                            params.setNumDecimal(Integer.parseInt(value));
                        } catch (Exception e) {
                            System.out.println("Nieprawidłowa wartość numDecimal");
                            System.exit(80);
                        }
                        break;
                    default:
                        logger.error("Nieobsługiwany paranetr: "+name);
                        System.exit(1);
                }
            }
        }
        return params;
    }

    private static class TestDataLoader {
        private boolean myResult;
        private Params params;
        private Instances trainingInstances;
        private Instances testInstances;

        public TestDataLoader(Params params, Instances trainingInstances) {
            this.params = params;
            this.trainingInstances = trainingInstances;
        }

        boolean is() {
            return myResult;
        }

        public Instances getTrainingInstances() {
            return trainingInstances;
        }

        public Instances getTestInstances() {
            return testInstances;
        }

        public TestDataLoader invoke() {
            testInstances = null;
            if(params.getTestDataFileName() != null) {
                try {
                    logger.info("Rozpoczynam wczytywanie danych testowych...");
                    File testDataFile = new File(params.getTestDataFileName());
                    if(!testDataFile.exists()) {
                        logger.error("Plik z danymi testowymi nie istnieje!");
                        System.exit(500);
                    }
                    int dot = testDataFile.getName().lastIndexOf(".");
                    String extension = testDataFile.getName().substring(dot + 1, testDataFile.getName().length());
                    LoadInstances loadInstances = new LoadInstances(extension).invoke(testDataFile);
                    if (loadInstances.is()) {
                        myResult = true;
                        return this;
                    }
                    testInstances = loadInstances.getData();
                    Enumeration<Attribute> trainAttributes = trainingInstances.enumerateAttributes();
                    Enumeration<Attribute> testAttributes = testInstances.enumerateAttributes();
                    if(trainingInstances.numAttributes() != testInstances.numAttributes()) {
                        logger.error("Atrybuty danych treningowych i testowych się nie zgadzają!");
                        System.exit(300);
                    }
                    while (trainAttributes.hasMoreElements() && testAttributes.hasMoreElements()) {
                        Attribute train = trainAttributes.nextElement();
                        Attribute test = testAttributes.nextElement();
                        if(!train.equals(test)) {
                            logger.error("Atrybuty danych treningowych i testowych się nie zgadzają!");
                            System.exit(300);
                        }
                    }
                    logger.info("Wczytano " + testInstances.numInstances() + "instancji");
                } catch (Exception e) {
                    logger.error("Zły format danych testowych");
                    System.exit(99);
                }
            }
            else {
                if(params.getTestDataSeed() == null) {
                    trainingInstances.randomize(new Random());
                }
                else {
                    trainingInstances.randomize(new Random(params.getTestDataSeed()));
                }
                logger.info("Rozpoczynam dzielenie tablic...");
                int trainSize = Math.round(trainingInstances.numInstances() * (100 - params.getTestDataPercent()) / 100);
                int testSize = trainingInstances.numInstances() - trainSize;
                Instances newTraining = new Instances(trainingInstances, 0, trainSize);
                Instances newTest = new Instances(trainingInstances, trainSize, testSize);
                trainingInstances = newTraining;
                testInstances = newTest;
                logger.info("Tablice podzielone. Wybrano "+testInstances.numInstances()+" instancji testowych.");
            }
            myResult = false;
            return this;
        }
    }
}
