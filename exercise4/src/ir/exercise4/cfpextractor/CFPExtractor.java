package ir.exercise4.cfpextractor;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.ProcessingResource;
import gate.Resource;
import gate.Utils;
import gate.creole.ANNIEConstants;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.gui.MainFrame;
import gate.learning.RunMode;
import gate.util.ExtensionFileFilter;
import gate.util.GateException;
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;


/**
 * This application extracts information from CFP files. the application is divided into two parts, 
 * the learning part and the application part. first the model must be automatically learned by the machine
 * and then the learned model can be applied to new files, which is the extraction step.
 *
 * TODO: how to get workshopname annotations..., now Persons annotations are extracted and learned
 */
public class CFPExtractor
{
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(CFPExtractor.class);

	/**
	 * The directory of the plugins and the corpora
	 */
	private final String GATE_PLUGINS_DIR = System.getenv("GATE_HOME") + "/plugins";
	private final String TRAINING_SET_DIR = System.getenv("CFP_HOME") + "/data/training-xml/";
	private final String TEST_SET_DIR = System.getenv("CFP_HOME") + "/data/test-xml/";
	
	private final String OUTPUT_DIR = System.getenv("CFP_HOME") + "/output/";
	
	/**
	 * The configuration file for the batch learning PR
	 */
	private final String ML_CONFIG_FILE_DIR = System.getenv("CFP_HOME") + "/config/ml-config.xml";
	
	/**
	 * Plugins directory
	 */
	private File pluginsDir = new File(GATE_PLUGINS_DIR);
	
	/**
	 * Annie controller.
	 * If we don't extract from plain text, we don't need annie
	 */
	private SerialAnalyserController annieController;
	
	/**
	 * The controller is pipeline of PRs
	 */
	private SerialAnalyserController machineLearningController;
	
	/**
	 * The language resources (LRs)
	 */
	private Corpus trainingCorpus;
	private Corpus testCorpus;
	
	/**
	 * The processing resource (PR) that implements various machine learning algorithms
	 * PRs are used to run over our LRs
	 */
	private ProcessingResource batchLearningTraining;
	
	/**
	 * The list of extracted annotation from our application/extraction step
	 */
	private List<GateAnalysisResult> extractedAnnotations = new ArrayList<GateAnalysisResult>(); ;
	
	/**
	 * Main method
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		CFPExtractor extractor = new CFPExtractor();
		
		Gate.init();
		
		// Disabled GUI loading because (I think) is not required and does not work when started from Ant
//		callGateGui();
		
//		extractor.initAnnieController();
		
		extractor.initMachineLearningController();
		extractor.loadCorpora();
		extractor.useMachineLearningTraining();
		extractor.useMachineLearningApplication();
		extractor.getMachineLearnedAnnotations();
		extractor.resultsToXML();
	}
	
	/**
	 * Shows the gate main window for mac users
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void callGateGui() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				MainFrame.getInstance().setVisible(true);
			}
		});
		logger.info("Gate-MainFrame started");
	}
	
	/**
	 * Annie is the default controller by gate. it is a ready made collection of 
	 * PRs (tokenizer, sentence splitter, POS tagger, gazetteers etc.) that performs 
	 * text extraction on plain/unstrctured text
	 * 
	 * We actually don't need annie, if we only apply the batch learning on preprocessed data
	 * 
	 * @throws GateException
	 * @throws MalformedURLException
	 */
	@SuppressWarnings("unchecked")
	public void initAnnieController() throws GateException, MalformedURLException {
		logger.info("initialize annieController...");
		
		//load the ANNIE Plugins (don't know if we really need it)
		File anniePluginDir = new File(pluginsDir, "ANNIE");
		Gate.getCreoleRegister().registerDirectories(anniePluginDir.toURI().toURL());
		
		//create annieController
		annieController = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController", 
				Factory.newFeatureMap(), Factory.newFeatureMap(), "ANNIE_Controller");
		
		FeatureMap params = Factory.newFeatureMap(); //with defaults
		Resource pr = Factory.createResource("gate.creole.ANNIETransducer", params);
		annieController.add((ProcessingResource) pr);
		
		logger.info("annieController loaded");
	}

	/**
	 * We need two corpora: 
	 * - one for the training mode to train our classes
	 * - one to apply our learned model (application mode)
	 * 
	 * @throws ResourceInstantiationException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void loadCorpora() throws ResourceInstantiationException, MalformedURLException, IOException {
		//create training corpus
		trainingCorpus = Factory.newCorpus("training");
		//create test corpus
		testCorpus = Factory.newCorpus("test");
		
		//only these file extensions should be loaded into the corpus
		ExtensionFileFilter exf_xml = new ExtensionFileFilter();
		exf_xml.addExtension("xml");
		exf_xml.addExtension("key");
		
		logger.info("load training Corpus...");
		trainingCorpus.populate(new File(TRAINING_SET_DIR).toURI().toURL(), exf_xml, "", false);
		
		ExtensionFileFilter exf_key = new ExtensionFileFilter();
		exf_key.addExtension("key");
		exf_key.addExtension("xml");
		logger.info("load test Corpus...");
		testCorpus.populate(new File(TEST_SET_DIR).toURI().toURL(), exf_key, "", false);
	}
	
	/**
	 * Initializes a controller ("corpus pipeline") with an batch learning PR
	 * 
	 * @throws GateException
	 * @throws MalformedURLException
	 */
	public void initMachineLearningController() throws GateException, MalformedURLException {
		logger.info("initialize MachineLearningController...");
		
		// Load the Learning Plugin
		File learningPluginDir = new File(pluginsDir, "Learning");
		Gate.getCreoleRegister().registerDirectories(learningPluginDir.toURI().toURL());
		
		// Create machineLearningController
		machineLearningController = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController", 
				Factory.newFeatureMap(), Factory.newFeatureMap(), "MachineLearning_Controller");
		
		
		
		// Load machineLearningController with PRs
		FeatureMap params_batch_learning = Factory.newFeatureMap();
		File configFile =  new File(ML_CONFIG_FILE_DIR);
		params_batch_learning.put("configFileURL", configFile.toURI().toURL());	
		params_batch_learning.put("learningMode", RunMode.TRAINING);
		params_batch_learning.put("inputASName", "Original markups");
		
		batchLearningTraining = (ProcessingResource) Factory.createResource("gate.learning.LearningAPIMain", params_batch_learning);
		
		machineLearningController.add(batchLearningTraining);	
		
		logger.info("MachineLearningController loaded");
	}
	
	/**
	 * Executes the controller on the training corpus the result is a learned model, specified in the ml_config.xml
	 * 
	 * @throws ExecutionException
	 */
	public void useMachineLearningTraining() throws ExecutionException {
		logger.info("executing machine learning controller -> TRAINING Mode");
		machineLearningController.setCorpus(trainingCorpus);
		
		machineLearningController.execute();
		logger.info("machine learning controller TRAINING Mode executed");
	}
	
	/**
	 * Applies the learned model on the test corpus.
	 * 
	 * The result is saved in a new Annotation Set called "machine_learned"
	 * 
	 * @throws ExecutionException
	 * @throws MalformedURLException
	 * @throws ResourceInstantiationException
	 */
	public void useMachineLearningApplication() throws ExecutionException, MalformedURLException, ResourceInstantiationException {
		logger.info("executing machine learning controller -> APPLICATION Mode...");
		machineLearningController.setCorpus(testCorpus);
		
		// Code from here is very clumsy, but it works... 
		// The same PR should be used for Application mode to save resources
		//TODO maybe...
		machineLearningController.remove(batchLearningTraining);
		FeatureMap params_batch_learning = Factory.newFeatureMap();
		params_batch_learning.put("configFileURL", new File(ML_CONFIG_FILE_DIR).toURI().toURL());	
		params_batch_learning.put("learningMode", RunMode.APPLICATION);
		params_batch_learning.put("inputASName", "Original markups");
		params_batch_learning.put("outputASName", "machine_learned");
		ProcessingResource batchLearningApplication = (ProcessingResource) Factory.createResource("gate.learning.LearningAPIMain", params_batch_learning);
		machineLearningController.add(batchLearningApplication);	
		
		machineLearningController.execute();
		logger.info("machine learning controller APPLICATION Mode executed");
	}
	
	/**
	 * Fetches the annotations from the machine_learned Annotation Set
	 * which contains the automatically annotated data
	 *
	 * Code partially based on StandAloneAnnie.java tutorial
	 */
	public void getMachineLearnedAnnotations() {
		logger.info("fetch the required annotations from test corpus...");
		
		Iterator<Document> iter = testCorpus.iterator();
		
		while(iter.hasNext()) {
			Document doc = iter.next();
			
			Set<String> requiredAnnotations = new HashSet<String>();
			requiredAnnotations.add("IE");
//			requiredAnnotations.add("workshopacronym");
//			requiredAnnotations.add("workshopdate");
//			requiredAnnotations.add("workshoplocation");
//			requiredAnnotations.add("conferencename");
//			requiredAnnotations.add("conferenceacronym");
//			requiredAnnotations.add("workshoppapersubmissiondate");
//			requiredAnnotations.add("Person");
			
			// TODO:  the resulting machine_learned AS is empty since the batch learning PR is not configured yet
			// See ml-config.xml 
			AnnotationSet machineLearnedAS = doc.getAnnotations("machine_learned").get(requiredAnnotations);
			
			Iterator<Annotation> it = machineLearnedAS.iterator();
			Annotation currAnnot;
            SortedAnnotationList sortedAnnotationList = new SortedAnnotationList();

			while(it.hasNext()) {
				currAnnot = it.next();
				sortedAnnotationList.addSortedExclusive(currAnnot);
			}
			
			extractedAnnotations.add(new GateAnalysisResult(doc, sortedAnnotationList));
		}
		
		logger.info("annotations fetched");
	}
	
	/**
	 * The required Annotations are inserted into an list of Annotations
	 * 
	 * Code partially based on StandAloneAnnie.java tutorial
	 */
	public void resultsToXML() {
		logger.info("saving the result into XML");
		XmlWriter writer = new XmlWriter();
		PrintWriter out;
		try {
			out = new PrintWriter(OUTPUT_DIR + "/result.xml");
			writer.writeXml(extractedAnnotations, out);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("XML file saved!");
	}
	
	/**
	 * The datastructure for the required annotations
	 * 
	 * Code based on StandAloneAnnie.java tutorial
	 */
	public static class SortedAnnotationList extends Vector<Annotation> {
		private static final long serialVersionUID = 1L;

		public SortedAnnotationList() {
			super();
		}

		/**
		 * @param annot
		 * @return
		 */
		public boolean addSortedExclusive(Annotation annot) {
			 Annotation currAnot = null;

            // overlapping check
            for (int i = 0; i < size(); ++i) {
                currAnot = (Annotation) get(i);
                if (annot.overlaps(currAnot)) {
                    return false;
                }
            }
            
            long annotStart = annot.getStartNode().getOffset().longValue();
            long currStart;

            // insert
            for (int i = 0; i < size(); ++i) {
                currAnot = (Annotation) get(i);
                currStart = currAnot.getStartNode().getOffset().longValue();
                if (annotStart < currStart) {
                    insertElementAt(annot, i);
                    return true;
                }
            }

            int size = size();
            insertElementAt(annot, size);

            return true;
		}
	}
	
	/**
	 * Code based on StandAloneAnnie.java tutorial
	 *
	 */
	public static class GateAnalysisResult {
        private Document document;
        private SortedAnnotationList sortedAnnotationList;

        /**
         * @param document
         * @param sortedAnnotationList
         */
        public GateAnalysisResult(Document document, SortedAnnotationList sortedAnnotationList) {
            this.document = document;
            this.sortedAnnotationList = sortedAnnotationList;
        }

        /**
         * @return
         */
        public SortedAnnotationList getSortedAnnotationList() {
            return sortedAnnotationList;
        }

        /**
         * @return
         */
        public Document getDocument() {
            return document;
        }

        /**
         * @param sortedAnnotationList
         */
        public void setSortedAnnotationList(SortedAnnotationList sortedAnnotationList) {
            this.sortedAnnotationList = sortedAnnotationList;
        }

        /**
         * @param document
         */
        public void setDocument(Document document) {
            this.document = document;
        }
    }
}
