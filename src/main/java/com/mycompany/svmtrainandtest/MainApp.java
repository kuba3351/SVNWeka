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
import weka.core.Instances;
import weka.core.Option;
import weka.experiment.InstanceQuery;

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
                        case "trainData.class":
                            params.setTrainingDataClass(value);
                            break;
                        case "testData.file":
                            params.setTestDataFileName(value);
                            break;
                        case "testData.percent":
                            params.setTestDataPercent(Integer.parseInt(value));
                            break;
                        case "testData.seed":
                            params.setTestDataSeed(Integer.parseInt(value));
                            break;
                        case "output.file":
                            params.setResultFile(value);
                            break;
                        default:
                            logger.error("Nieobsługiwany paranetr: "+name);
                            System.exit(1);
                    }
                }
            }
            try {
                params.validateParams();
            } catch (Exception e) {
                logger.error(e.getMessage());
                System.exit(1);
            }
            logger.info("Parametry prawidłowe. Rozpoczynam wczytywanie danych treningowych");
            File trainingDataFile = new File(params.getTrainingDataFileName());
            String extension;
            int dot = trainingDataFile.getName().lastIndexOf(".");
            extension = trainingDataFile.getName().substring(dot + 1, trainingDataFile.getName().length());
            LoadInstances loadInstances = new LoadInstances(extension).invoke(trainingDataFile);
            if (loadInstances.is()) return;
            Instances trainingInstances = loadInstances.getData();
            System.out.println("Wczytano "+trainingInstances.numInstances()+" instancji.");
            Instances testInstances;
            if(params.getTestDataFileName() != null) {
                logger.info("Rozpoczynam wczytywanie danych testowych...");
                File testDataFile = new File(params.getTestDataFileName());
                dot = testDataFile.getName().lastIndexOf(".");
                extension = testDataFile.getName().substring(dot + 1, testDataFile.getName().length());
                loadInstances = new LoadInstances(extension).invoke(testDataFile);
                if (loadInstances.is()) return;
                testInstances = loadInstances.getData();
                logger.info("Wczytano "+testInstances.numInstances()+"instancji");
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
            logger.info("Rozpoczynam uczenie klasyfikatora...");
            trainingInstances.setClassIndex(trainingInstances.numAttributes() - 1);
            testInstances.setClassIndex(testInstances.numAttributes() - 1);
            SMO smo = new SMO();
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
        System.exit(0);
    }

}
