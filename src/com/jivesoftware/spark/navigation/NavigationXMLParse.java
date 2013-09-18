package com.jivesoftware.spark.navigation;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
import com.jivesoftware.spark.navigation.ExpandNavigationTreeNode;

public class NavigationXMLParse {
	  Element userLabelElement ;
	  String userLabel ;
	  Element userNameElement;
	  String userName ;
	  
	  ExpandNavigationTreeNode treeRoot;
	  String name;
	  String url;
	  String guid;
	  
	public  ExpandNavigationTreeNode xmlNavigation(String xmlDoc) {
        StringReader read = new StringReader(xmlDoc);
        InputSource source = new InputSource(read);
        SAXBuilder sb = new SAXBuilder();
  	    treeRoot = new ExpandNavigationTreeNode("门户菜单","00000000","1","2");
       
        try {
            Document doc = sb.build(source);
            Element root = doc.getRootElement();
            System.out.println(root.getName());
            //得到根元素所有子元素的集合
            makeNavigationTree(root,treeRoot);
          

        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
      
        return treeRoot;
    }
	
	
	public ExpandNavigationTreeNode makeNavigationTree(Element eleNode,ExpandNavigationTreeNode currentNode)
	{
      List eleNodeList = eleNode.getChildren();
      if( eleNodeList!=null)
      { 
        for(int i=0;i < eleNodeList.size();i++)
        {
        	Element e = (Element) eleNodeList.get(i);//循环依次得到子元素
        	name = e.getAttributeValue("name");
        	url = e.getAttributeValue("url");
        	guid = e.getAttributeValue("guid");
        	ExpandNavigationTreeNode node = new ExpandNavigationTreeNode(name,guid,url,name);
        	currentNode.add(node);
        	makeNavigationTree(e,node);
        }
        
      }
      return currentNode;
      
	}
	


}

