package ir.exercise4.cfpextractor;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
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
import gate.util.persistence.PersistenceManager;

import java.io.File;
import java.io.IOException;
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

import org.apache.log4j.Logger;


//TODO how to get workshopname annotations..., now Persons annotations are extracted and learned
/*
* this application extracts information from CFP files. the application is divided into two parts, 
* the learning part and the application part. first the model must be automatically learned by the machine
* and then the learned model can be applied to new files, which is the extraction step.
*/
public class CFPExtractor {
	
	private static Logger logger = Logger.getLogger(CFPExtractor.class);

	//the directory of the plugins and the corpora
	private final String GATE_PLUGINS_DIR = "exercise4/vendor/GATE_Developer_7.1/plugins";
	private final String TRAINING_SET_DIR = "exercise4/data/training/";
	private final String TEST_SET_DIR = "exercise4/data/test/";
	
	//the configuration file for the batch learning PR
	private final String ML_CONFIG_FILE_DIR = "exercise4/config/ml-config.xml";
	
	private File pluginsDir = new File(GATE_PLUGINS_DIR);;
	
	//if we don't extract from plain text, we don't need annie
	private SerialAnalyserController annieController;
	
	//the controller is pipeline of PRs
	private SerialAnalyserController machineLearningController;
	
	//the language resources (LRs)
	private Corpus trainingCorpus;
	private Corpus testCorpus;
	
	//the processing resource (PR) that implements various machine learning algorithms
	//PRs are used to run over our LRs
	private ProcessingResource batchLearningTraining;
	
	//the list of extracted annotation from our application/extraction step
	private List<GateAnalysisResult> extractedAnnotations = new ArrayList<GateAnalysisResult>(); ;
	
	public static void main(String[] args) throws Exception {
		
		CFPExtractor extractor = new CFPExtractor();
		
		Gate.init();
		
		callGateGui();
		
		//extractor.initAnnieController();
		
		extractor.initMachineLearningController();
		
		extractor.loadCorpora();
		
		extractor.useMachineLearningTraining();
		
		extractor.useMachineLearningApplication();
		
		extractor.getMachineLearnedAnnotations();
		
		extractor.resultsToXML();
	}
	
	/*
	 * shows the gate main window. 
	 * for mac users
	 */
	public static void callGateGui() throws InterruptedException, InvocationTargetException {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				MainFrame.getInstance().setVisible(true);
			}
		});
		logger.info("Gate-MainFrame started");
	}
	
	//we actually don't need annie, if we only apply the batch learning on preprocessed data
	/*
	 * annie is the default controller by gate. it is a ready made collection of 
	 * PRs (tokenizer, sentence splitter, POS tagger, gazetteers etc.) that performs 
	 * text extraction on plain/unstrctured text
	 */
	public void initAnnieController() throws GateException, MalformedURLException {
		logger.info("initialize annieController...");
		
		//load the ANNIE Plugins (don't know if we really need it)
		File anniePluginDir = new File(pluginsDir, "ANNIE");
		Gate.getCreoleRegister().registerDirectories(anniePluginDir.toURI().toURL());
		
		//create annieController
		annieController = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController", 
				Factory.newFeatureMap(), Factory.newFeatureMap(), "ANNIE_Controller");
		
		Collection<String> prs = null;
		try {
			SerialAnalyserController annie = (SerialAnalyserController)
					PersistenceManager.loadObjectFromFile(
							new File(new File(Gate.getPluginsHome(), GATE_PLUGINS_DIR), ANNIEConstants.DEFAULT_FILE)
					);
			prs = (Collection<String>)annie.getPRs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//load ANNIE controller with PRs as defined in ANNIEConstants
		Iterator<String> prsIterator = prs.iterator();
		while (prsIterator.hasNext()) {
			FeatureMap params = Factory.newFeatureMap(); //with defaults
			ProcessingResource pr = (ProcessingResource) Factory.createResource(prsIterator.next(), params);
			annieController.add(pr);
		}
		
		logger.info("annieController loaded");
	}
	
	/*
	 * we need two corpora. 
	 * one for the training mode to train our classes
	 * one to apply our learned model (application mode)
	 */
	private void loadCorpora() throws ResourceInstantiationException, MalformedURLException, IOException {
		//create training corpus
		trainingCorpus = Factory.newCorpus("training");
		//create test corpus
		testCorpus = Factory.newCorpus("test");
		
		//only these file extensions should be loaded into the corpus
		ExtensionFileFilter exf_xml = new ExtensionFileFilter();
		exf_xml.addExtension("xml");
		
		logger.info("load training Corpus...");
		trainingCorpus.populate(new File(TRAINING_SET_DIR).toURI().toURL(), exf_xml, "", false);
		
		ExtensionFileFilter exf_key = new ExtensionFileFilter();
		exf_key.addExtension("key");		
		logger.info("load test Corpus...");
		testCorpus.populate(new File(TEST_SET_DIR).toURI().toURL(), exf_key, "", false);
	}
	
	/*
	 * initializes a controller ("corpus pipeline") with an batch learning PR
	 */
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
		File configFile =  new File(ML_CONFIG_FILE_DIR);
		params_batch_learning.put("configFileURL", configFile.toURI().toURL());	
		params_batch_learning.put("learningMode", RunMode.TRAINING);
		params_batch_learning.put("inputASName", "Original markups");
		
		batchLearningTraining = (ProcessingResource) Factory.createResource("gate.learning.LearningAPIMain", params_batch_learning);
		
		//delete the machine_learned AS
		//only needed if we want to apply APPLICATION multiple times
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
	
	/*
	 * executes the controller on the training corpus
	 * the result is a learned model, specified in the ml_config.xml
	 */
	public void useMachineLearningTraining() throws ExecutionException {
		logger.info("executing machine learning controller -> TRAINING Mode");
		machineLearningController.setCorpus(trainingCorpus);
		
		machineLearningController.execute();
		logger.info("machine learning controller TRAINING Mode executed");
	}
	
	/*
	 * applys the learned model on the test corpus
	 * the result is saved in a new Annotation Set called "machine_learned"
	 */
	public void useMachineLearningApplication() throws ExecutionException, MalformedURLException, ResourceInstantiationException {
		logger.info("executing machine learning controller -> APPLICATION Mode...");
		machineLearningController.setCorpus(testCorpus);
		
		//code from here is very clumsy, but it works... 
		//the same PR should be used for Application mode to save resources
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
	/*
	 * fetches the annotations from the machine_learned Annotation Set
	 * which contains the automatically annotated data
	 */
	//Code partially based on StandAloneAnnie.java tutorial
	public void getMachineLearnedAnnotations() {
		logger.info("fetch the required annotations from test corpus...");
		
		Iterator<Document> iter = testCorpus.iterator();
		
		while(iter.hasNext()) {
			Document doc = iter.next();
			
			 
			
			Set<String> requiredAnnotations = new HashSet<String>();
			requiredAnnotations.add("workshopname");
			requiredAnnotations.add("workshopacronym");
			requiredAnnotations.add("workshopdate");
			requiredAnnotations.add("workshoplocation");
			requiredAnnotations.add("conferencename");
			requiredAnnotations.add("conferenceacronym");
			requiredAnnotations.add("workshoppapersubmissiondate");
			
			
			//TODO the resulting machine_learned AS is empty since the batch learning PR is not configured yet
			//see ml-config.xml 
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
	
	/*
	 * the required Annotations are inserted into an list of Annotations
	 */
	//Code partially based on StandAloneAnnie.java tutorial
	public void resultsToXML() {
		logger.info("saving the result into XML");
		for(GateAnalysisResult res : extractedAnnotations) {
			logger.info(res.document.getName());
			Iterator<Annotation> it = res.sortedAnnotationList.iterator();
			while (it.hasNext()) {
				logger.info("annotation: " + it.next());
			}
		}
		logger.info("XML file saved!");
	}
	
	/*
	 * the datastructure for the required annotations
	 */
	//Code based on StandAloneAnnie.java tutorial
	public static class SortedAnnotationList extends Vector<Annotation> {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public SortedAnnotationList() {
			super();
		}
		
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
	
	//Code based on StandAloneAnnie.java tutorial
	public static class GateAnalysisResult {
        private Document document;
        private SortedAnnotationList sortedAnnotationList;

        public GateAnalysisResult(Document document, SortedAnnotationList sortedAnnotationList) {
            this.document = document;
            this.sortedAnnotationList = sortedAnnotationList;
        }

        public SortedAnnotationList getSortedAnnotationList() {
            return sortedAnnotationList;
        }

        public Document getDocument() {
            return document;
        }

        public void setSortedAnnotationList(SortedAnnotationList sortedAnnotationList) {
            this.sortedAnnotationList = sortedAnnotationList;
        }

        public void setDocument(Document document) {
            this.document = document;
        }

    }
	
}
