package ir.exercise4.cfpextractor;

import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.gui.MainFrame;
import gate.learning.RunMode;
import gate.util.ExtensionFileFilter;
import gate.util.GateException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

//TODO how to get workshopname annotations..., now Persons annotations are extracted and learned
public class CFPExtractor {
	
	private static Logger logger = Logger.getLogger(CFPExtractor.class);
	
	private final String GATE_PLUGINS_DIR = "exercise4/vendor/GATE_Developer_7.1/plugins";
	private final String TRAINING_SET_DIR = "exercise4/data/training/";
	private final String TEST_SET_DIR = "exercise4/data/test/";
	private final String ML_CONFIG_FILE_DIR = "exercise4/config/ml-config.xml";
	
	private File pluginsDir = new File(GATE_PLUGINS_DIR);;
	
	private SerialAnalyserController annieController;
	private SerialAnalyserController machineLearningController;
	private Corpus trainingCorpus;
	private Corpus testCorpus;
	private ProcessingResource batchLearningTraining;
	public static void main(String[] args) throws Exception {
		
		CFPExtractor extractor = new CFPExtractor();
		
		Gate.init();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				MainFrame.getInstance().setVisible(true);
			}
		});
		
		logger.info("Gate-MainFrame started");
		
		//extractor.initAnnieController();
		
		extractor.initMachineLearningController();
		
		extractor.loadCorpora();
		
		extractor.useMachineLearningTraining();
		
		extractor.useMachineLearningApplication();
		
	}
	
	
	public void initAnnieController() throws GateException, MalformedURLException {
		logger.info("initialize annieController...");
		
		//load the ANNIE Plugins (don't know if we really need it)
		File anniePluginDir = new File(pluginsDir, "ANNIE");
		Gate.getCreoleRegister().registerDirectories(anniePluginDir.toURI().toURL());
		
		//create annieController
		annieController = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController", 
				Factory.newFeatureMap(), Factory.newFeatureMap(), "ANNIE_Controller");

		//load ANNIE controller with PRs as defined in ANNIEConstants
		for(int i = 0; i < ANNIEConstants.PR_NAMES.length; i++) {
			FeatureMap params = Factory.newFeatureMap(); //with defaults
			
			ProcessingResource pr = (ProcessingResource) Factory.createResource(ANNIEConstants.PR_NAMES[i], params);
			
			annieController.add(pr);
		}
		
		logger.info("annieController loaded");
	}
	

	public void initMachineLearningController() throws GateException, MalformedURLException {
		logger.info("initialize MachineLearningController...");
		
		//load the Learning Plugin
		File learningPluginDir = new File(pluginsDir, "Learning");
		Gate.getCreoleRegister().registerDirectories(learningPluginDir.toURI().toURL());
		
		//create machineLearningController
		machineLearningController = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController", 
				Factory.newFeatureMap(), Factory.newFeatureMap(), "MachineLearning_Controller");
		
		// load machineLearningController with PRs
		FeatureMap params_batch_learning = Factory.newFeatureMap();
		params_batch_learning.put("configFileURL", new File(ML_CONFIG_FILE_DIR).toURL());	
		params_batch_learning.put("learningMode", RunMode.TRAINING);
		params_batch_learning.put("inputASName", "Original markups");
		
		batchLearningTraining = (ProcessingResource) Factory.createResource("gate.learning.LearningAPIMain", params_batch_learning);
		
		/*
		FeatureMap params_document_reset = Factory.newFeatureMap();
		List<String> setsToRemove = new ArrayList<String>();
		setsToRemove.add("ML");
		params_document_reset.put("setsToRemove", setsToRemove);
		params_document_reset.put("setsToKeep", new ArrayList<String>());
		ProcessingResource document_reset = (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR", params_document_reset);
		
		machineLearningController.add(document_reset);
		*/
		machineLearningController.add(batchLearningTraining);	
		
		logger.info("MachineLearningController loaded");
	}
	
	public void useMachineLearningTraining() throws ExecutionException {
		logger.info("executing machine learning controller -> TRAINING Mode");
		machineLearningController.setCorpus(trainingCorpus);
		
		machineLearningController.execute();
		logger.info("machine learning controller TRAINING Mode executed");
	}
	
	public void useMachineLearningApplication() throws ExecutionException, MalformedURLException, ResourceInstantiationException {
		logger.info("executing machine learning controller -> APPLICATION Mode...");
		machineLearningController.setCorpus(testCorpus);
		
		//code from here is very clumsy, but it works... 
		//the same PR should be used for Application mode to save resources
		//TODO
		machineLearningController.remove(batchLearningTraining);
		FeatureMap params_batch_learning = Factory.newFeatureMap();
		params_batch_learning.put("configFileURL", new File(ML_CONFIG_FILE_DIR).toURL());	
		params_batch_learning.put("learningMode", RunMode.APPLICATION);
		params_batch_learning.put("inputASName", "Original markups");
		params_batch_learning.put("outputASName", "machine_learned");
		ProcessingResource batchLearningApplication = (ProcessingResource) Factory.createResource("gate.learning.LearningAPIMain", params_batch_learning);
		machineLearningController.add(batchLearningApplication);	
		
		machineLearningController.execute();
		logger.info("machine learning controller APPLICATION Mode executed");
	}
	
	private void loadCorpora() throws ResourceInstantiationException, MalformedURLException, IOException {
		//create training corpus
		trainingCorpus = Factory.newCorpus("training");
		//create test corpus
		testCorpus = Factory.newCorpus("test");
		
		//only these file extensions should be loaded into the corpus
		ExtensionFileFilter exf_xml = new ExtensionFileFilter();
		exf_xml.addExtension("xml");
		
		logger.info("load training Corpus...");
		trainingCorpus.populate(new File(TRAINING_SET_DIR).toURL(), exf_xml, "", false);
		
		ExtensionFileFilter exf_key = new ExtensionFileFilter();
		exf_key.addExtension("key");		
		logger.info("load test Corpus...");
		testCorpus.populate(new File(TEST_SET_DIR).toURL(), exf_key, "", false);
	}
	
}
