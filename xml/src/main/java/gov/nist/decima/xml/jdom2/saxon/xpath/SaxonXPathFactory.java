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
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR
 * IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */
package gov.nist.decima.xml.jdom2.saxon.xpath;

import net.sf.saxon.xpath.XPathFactoryImpl;

import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;

import java.util.Map;
import java.util.Objects;

public class SaxonXPathFactory extends org.jdom2.xpath.XPathFactory {
  private static final XPathFactoryImpl DEFAULT_XPATH_FACTORY = new XPathFactoryImpl();

  private final XPathFactoryImpl xpathFactory;

  public SaxonXPathFactory() {
    this(DEFAULT_XPATH_FACTORY);
  }

  protected SaxonXPathFactory(XPathFactoryImpl factory) {
    Objects.requireNonNull(factory, "factory");
    this.xpathFactory = factory;
  }

  private XPathFactoryImpl getXPathFactory() {
    return xpathFactory;
  }

  @Override
  public <X> XPathExpression<X> compile(String expression, Filter<X> filter, Map<String, Object> variables,
      Namespace... namespaces) {
    return new SaxonCompiledXPath<X>(this.getXPathFactory(), expression, filter, variables, namespaces);
  }
}