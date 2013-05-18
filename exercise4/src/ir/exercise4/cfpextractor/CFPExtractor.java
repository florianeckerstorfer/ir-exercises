package ir.exercise4.cfpextractor;

import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.creole.ANNIEConstants;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.gui.MainFrame;
import gate.util.ExtensionFileFilter;
import gate.util.GateException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

public class CFPExtractor {
	
	private static Logger logger = Logger.getLogger(CFPExtractor.class);
	private final String GATE_PLUGINS_DIR = "/Users/atmanB/git/ir-exercises/exercise4/vendor/GATE_Developer_7.1/plugins";
	private final String TRAINING_SET_URL = "file:/Users/atmanB/git/ir-exercises/exercise4/data/training/";
	private final String TEST_SET_URL = "file:/Users/atmanB/git/ir-exercises/exercise4/data/test/";
	
	private File pluginsDir = new File(GATE_PLUGINS_DIR);;
	
	private SerialAnalyserController annieController;
	private SerialAnalyserController machineLearningController;
	private Corpus trainingCorpus;
	private Corpus testCorpus;
	
	public static void main(String[] args) throws Exception {
		
		CFPExtractor extractor = new CFPExtractor();
		
		Gate.init();
		
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				MainFrame.getInstance().setVisible(true);
			}
		});
		
		logger.info("Gate-MainFrame started");
		
		extractor.initAnnieController();
		
		//extractor.initMachineLearningController();
		
		extractor.loadCorpora();
		
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
		
		FeatureMap params = Factory.newFeatureMap();
		
		// load machineLearningController with PRs
		ProcessingResource machinelearner = (ProcessingResource) Factory.createResource("gate.learning.LearningAPIMain", params);
		
		machineLearningController.add(machinelearner);		
		
		logger.info("MachineLearningController loaded");
	}
	
	
	private void loadCorpora() throws ResourceInstantiationException, MalformedURLException, IOException {

		//create training corpus
		trainingCorpus = Factory.newCorpus("training");
		
		//create test corpus
		testCorpus = Factory.newCorpus("test");
		
		//only these file extensions should be loaded into the corpus
		ExtensionFileFilter exf = new ExtensionFileFilter();
		exf.addExtension("key");
		exf.addExtension("txt");
		
		trainingCorpus.populate(new URL(TRAINING_SET_URL), exf, "", false);
		testCorpus.populate(new URL(TEST_SET_URL), exf, "", false);
	}
	
}
