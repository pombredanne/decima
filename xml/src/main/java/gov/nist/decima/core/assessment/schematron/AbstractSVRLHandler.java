/**
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 17 United States Code Section 105, works of NIST employees are
 * not subject to copyright protection in the United States and are considered to
 * be in the public domain. Permission to freely use, copy, modify, and distribute
 * this software and its documentation without fee is hereby granted, provided that
 * this notice and disclaimer of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE. IN NO EVENT
 * SHALL NASA BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR
 * IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.decima.core.assessment.schematron;

import gov.nist.decima.core.assessment.AssessmentException;
import gov.nist.decima.core.assessment.result.AssessmentResultBuilder;
import gov.nist.decima.core.assessment.result.AssessmentResults;
import gov.nist.decima.core.assessment.result.BasicTestResult;
import gov.nist.decima.core.assessment.result.TestStatus;
import gov.nist.decima.core.document.SimpleXPathContext;
import gov.nist.decima.core.document.XMLDocument;
import gov.nist.decima.core.document.XPathContext;
import gov.nist.decima.core.document.XPathEvaluator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom2.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;

public abstract class AbstractSVRLHandler implements SVRLHandler {
  private static final Logger log = LogManager.getLogger(AbstractSVRLHandler.class);

  private final AssessmentResultBuilder assessmentResultBuilder;
  private final XMLDocument assessedDocument;
  private final XPathEvaluator xpathEvaluator;
  private final Map<String, String> prefixToNamespaceMap = new HashMap<>();

  /**
   * Constructs a handler that is capable of processing a SVRL result to produce an intermediate
   * form of {@link AssessmentResults}.
   * 
   * @param assessmentResultBuilder
   *          the builder that will be used to produce the {@link AssessmentResults}
   * @param sourceDocument
   *          the document being analyzed by the Schematron assessment
   * @throws AssessmentException
   *           if an error occurred while parsing the SVRL information
   */
  public AbstractSVRLHandler(AssessmentResultBuilder assessmentResultBuilder, XMLDocument sourceDocument)
      throws AssessmentException {
    this.assessmentResultBuilder = assessmentResultBuilder;
    this.assessedDocument = sourceDocument;
    try {
      this.xpathEvaluator = assessedDocument.newXPathEvaluator();
    } catch (XPathFactoryConfigurationException e) {
      throw new AssessmentException("Unable to create new XPathEvaluator", e);
    }
  }

  public XMLDocument getAssessedDocument() {
    return assessedDocument;
  }

  public XPathEvaluator getXPathEvaluator() {
    return xpathEvaluator;

  }

  public AssessmentResultBuilder getValidationResultBuilder() {
    return assessmentResultBuilder;
  }

  @Override
  public void handleNSPrefix(Element prefix) {
    prefixToNamespaceMap.put(prefix.getAttributeValue("prefix"), prefix.getAttributeValue("uri"));
  }

  @Override
  public void handleActivePattern(Element activePattern) {
    // do nothing by default
  }

  @Override
  public void handleFiredRule(Element xmlObject) {
    // do nothing by default
  }

  protected void handleAssertionResult(String derivedRequirementId, String assertionId, TestStatus testStatus,
      String xpath, List<String> values) {
    if (log.isTraceEnabled()) {
      log.trace("Adding '{}' assertion result for: {}", testStatus, derivedRequirementId);
    }
    XPathEvaluator evaluator = getXPathEvaluator();
    XPathContext context;
    try {
      context = evaluator.getContext(xpath);
    } catch (XPathExpressionException e) {
      context = new SimpleXPathContext(xpath, getAssessedDocument().getSystemId(), -1, -1);
      log.error("Unable to resolve XPath context", e);
    }
    BasicTestResult result = new BasicTestResult(assertionId, testStatus, context);
    result.addResultValues(values);
    getValidationResultBuilder().addTestResult(derivedRequirementId, result);
  }

}