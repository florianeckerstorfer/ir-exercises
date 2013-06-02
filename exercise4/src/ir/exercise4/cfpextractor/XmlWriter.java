package ir.exercise4.cfpextractor;

import gate.Annotation;
import gate.FeatureMap;
import gate.Utils;
import ir.exercise4.cfpextractor.CFPExtractor.GateAnalysisResult;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlWriter
{
	private static Logger logger = Logger.getLogger(XmlWriter.class);
	
	public StreamResult writeXml(List<GateAnalysisResult> annotations, PrintWriter printWriter)
	{
		StreamResult result = null;
		
		try {
			 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("result");
			doc.appendChild(rootElement);
			
			for(GateAnalysisResult res : annotations) {
				Element docElement = doc.createElement("document");
				rootElement.appendChild(docElement);
				Element docNameElement = doc.createElement("name");
				docNameElement.appendChild(doc.createTextNode(res.getDocument().getName()));
				docElement.appendChild(docNameElement);
				Element annotsElement = doc.createElement("annotations");
				docElement.appendChild(annotsElement);
				logger.info("\n\n" + res.getDocument().getName());
				logger.info("Number of annotations: " + res.getSortedAnnotationList().size());
				Iterator<Annotation> it = res.getSortedAnnotationList().iterator();
				while (it.hasNext()) {
					Annotation annotation = it.next();
					String value = Utils.stringFor(res.getDocument(), annotation);
					FeatureMap features = annotation.getFeatures();
					Element annotElement = doc.createElement(features.get("type").toString());
					annotElement.appendChild(doc.createTextNode(value));
					annotsElement.appendChild(annotElement);
					logger.info("annotation: " + annotation);
					logger.info("value: " + value);
				}
			}
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
	 
			// Output to console for testing
			result = new StreamResult(printWriter);
			transformer.transform(source, result);
	 
			System.out.println("File saved!");
	 
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		
		return result;
	}
}
