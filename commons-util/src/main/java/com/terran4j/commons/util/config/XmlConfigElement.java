package com.terran4j.commons.util.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;
import com.terran4j.commons.util.error.ErrorCodes;

public class XmlConfigElement implements ConfigElement {

	private static final String T = "    ";

	private final ClassLoader classLoader;

	private Element element;
	
	public XmlConfigElement(String xmlContent) throws BusinessException {
	    classLoader = Thread.currentThread().getContextClassLoader();
	    InputStream in = Strings.toInputStream(xmlContent);
	    try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            element = doc.getDocumentElement();
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.XML_ERROR, e).put("xmlContent", xmlContent)
                    .setMessage("parse xml file error, xmlContent:\n{xmlContent}");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	}

	public XmlConfigElement(Class<?> clazz, String fileName) throws BusinessException {
		super();
		classLoader = clazz.getClassLoader();
		InputStream in = classLoader.getResourceAsStream(fileName);
		if (in == null) {
			throw new BusinessException(ErrorCodes.RESOURCE_NOT_FOUND)
					.put("fileName", fileName).put("classpath", clazz.getPackage().getName())
					.setMessage("xml file not found in classpath, fileName = ${fileName}, in classpath: ${classpath}");
		}
		
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
			element = doc.getDocumentElement();
		} catch (Exception e) {
			throw new BusinessException(CommonErrorCode.XML_ERROR, e)
					.put("fileName", fileName).put("classpath", clazz.getPackage().getName())
					.setMessage("parse xml file error, fileName = ${fileName}, in classpath: ${classpath}");
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public XmlConfigElement(Element element) {
		super();
		this.element = element;
		classLoader = getClass().getClassLoader();
	}

    @Override
    public int size() {
        return 0; // XML 没有数组结构，因此个数一律返回 0 .
    }

    public String attr(String attriName) {
		String value = element.getAttribute(attriName);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return value;
	}

	public ConfigElement[] getChildren() {
		NodeList nodeList = element.getChildNodes();
		if (nodeList == null) {
			return new ConfigElement[0];
		}

		List<ConfigElement> list = new ArrayList<ConfigElement>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element) {
				Element childElement = (Element) node;
				ConfigElement c = new XmlConfigElement(childElement);
				list.add(c);
			}
		}

		return list.toArray(new ConfigElement[list.size()]);
	}

	public ConfigElement[] getChildren(String eleName) {
		NodeList nodeList = element.getChildNodes();
		if (nodeList == null) {
			return new ConfigElement[0];
		}

		List<ConfigElement> list = new ArrayList<ConfigElement>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Element) {
				Element childElement = (Element) node;
				if (eleName == null || eleName.equals(childElement.getTagName())) {
					ConfigElement c = new XmlConfigElement(childElement);
					list.add(c);
				}
			}
		}

		return list.toArray(new ConfigElement[list.size()]);
	}

	public String getValue() {
		NodeList nodeList = element.getChildNodes();
		if (nodeList == null || nodeList.getLength() == 0) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node instanceof Text) {
				Text text = (Text) node;
				String str = text.getNodeValue();
				if (StringUtils.isEmpty(str)) {
					continue;
				}
				sb.append(str.trim()).append("\n");
			}
		}

		String value = sb.toString().trim();
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return value;
	}

	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

    @Override
    public <T> T asObject(Class<T> clazz) throws BusinessException {
        throw new UnsupportedOperationException("XmlConfigElement can't as Object!");
    }

    public ConfigElement getChild(String eleName) throws BusinessException {
		ConfigElement[] childElements = getChildren(eleName);
		if (childElements != null && childElements.length > 0) {
			if (childElements.length > 1) {
				StringBuffer sb = new StringBuffer();
				for (ConfigElement e : childElements) {
					sb.append("\n").append(e.getValue());
				}
				throw new BusinessException(ErrorCodes.CONFIG_ERROR)
						.put("childrenElementName", eleName)
						.put("element", asText())
						.setMessage("Children Elment must NOT more than one");
			}
			return childElements[0];
		}
		return null;
	}



	public String getName() {
		return element.getTagName();
	}

	public String asText() {
		return toString(0);
	}

	public final Element getElement() {
		return element;
	}

	public final void setElement(Element element) {
		this.element = element;
	}

	public String toString() {
		return toString(0);
	}

	public Set<String> attrSet() {
		if (element == null) {
			return null;
		}
		Set<String> keys = new HashSet<String>();
		NamedNodeMap map = element.getAttributes();
		for (int i = 0; i < map.getLength(); i++) {
			Node node = map.item(i);
			String key = node.getNodeName();
			keys.add(key);
		}
		return keys;
	}

	private final String toString(int indent) {
		StringBuffer isb = new StringBuffer("");
		for (int i = 0; i < indent; i++) {
			isb.append(T);
		}
		String is = isb.toString();

		StringBuffer sb = new StringBuffer();
		sb.append(is).append("<").append(getName());

		// 属性输出
		Iterator<String> it = attrSet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = attr(key);
			sb.append(" ").append(key).append("=\"").append(value).append("\"");
		}

		String value = getValue();
		ConfigElement[] children = getChildren();

		if ((value == null || value.length() == 0) && (children == null || children.length == 0)) {
			sb.append("/>");
			return sb.toString();
		}
		sb.append(">\n");

		if (value != null && value.trim().length() > 0) {
			sb.append(is).append(T).append(value.trim()).append("\n");
		}

		if (children != null && children.length > 0) {
			for (ConfigElement child : children) {
				if (child instanceof XmlConfigElement) {
					XmlConfigElement xmlElement = (XmlConfigElement) child;
					String str = xmlElement.toString(indent + 1);
					sb.append(str).append("\n");
				}
			}
		}

		sb.append(is).append("</").append(getName()).append(">");

		return sb.toString();
	}
}
