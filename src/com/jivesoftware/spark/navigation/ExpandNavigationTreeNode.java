package com.jivesoftware.spark.navigation;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;


public class ExpandNavigationTreeNode extends DefaultMutableTreeNode {
	private String guid;
	private String url;
	private String name;
	
	  public ExpandNavigationTreeNode(String nodename,String guid,String url,String name)
	  {
	   super(nodename);
	   this.guid=guid;
	   this.url=url;
	   this.name=name;
	  }  
	 
	  public String getGuid() {
			return guid;
		}

		public void setGuid(String guid) {
			this.guid = guid;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
     
}

