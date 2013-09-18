package com.jivesoftware.spark.navigation;

import org.jivesoftware.MainWindow;
import org.jivesoftware.Spark;
import org.jivesoftware.resource.SparkRes;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.spark.ChatManager;
import org.jivesoftware.spark.SessionManager;
import org.jivesoftware.spark.SparkManager;
import org.jivesoftware.spark.UserManager;
import org.jivesoftware.spark.Workspace;
import org.jivesoftware.spark.component.VerticalFlowLayout;
import org.jivesoftware.spark.component.tabbedPane.SparkTabbedPane;
import org.jivesoftware.spark.filetransfer.FileTransferListener;
import org.jivesoftware.spark.filetransfer.SparkTransferManager;
import org.jivesoftware.spark.plugin.ContextMenuListener;
import org.jivesoftware.spark.plugin.Plugin;
import org.jivesoftware.spark.search.SearchManager;
import org.jivesoftware.spark.ui.ChatContainer;
import org.jivesoftware.spark.ui.ChatRoom;
import org.jivesoftware.spark.ui.ChatRoomButton;
import org.jivesoftware.spark.ui.ChatRoomListenerAdapter;
import org.jivesoftware.spark.ui.ContactGroupListener;
import org.jivesoftware.spark.ui.ContactItem;
import org.jivesoftware.spark.ui.ContactItemHandler;
import org.jivesoftware.spark.ui.ContactList;
import org.jivesoftware.spark.ui.MessageFilter;
import org.jivesoftware.spark.ui.PresenceListener;
import org.jivesoftware.spark.ui.TranscriptWindow;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.plaf.TreeUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.*;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.jivesoftware.PasswordMD5;
import com.jivesoftware.spark.navigation.ExpandNavigationTreeNode;
import com.jivesoftware.spark.navigation.NavigationXMLParse;
import com.jivesoftware.spark.navigation.IconNavigationNodeRenderer;
import com.jivesoftware.spark.navigation.MyTreeUI;

public class NavigationTreePanel extends JPanel {
	    
	 private JPanel mainTreePanel = new JPanel();
	 private JScrollPane treeScrollPane;
	 private JScrollPane treeScroller;
	 private JPanel panel;
	 private ExpandNavigationTreeNode selectedNode;
	 private ExpandNavigationTreeNode newNode;
	 private TreeNode[] nodes;
	 private TreePath path;
     JTree tree;
     
     private HttpURLConnection connect;
     private URL url;
     private InputStream is;
     private BufferedReader br;
     private String strLine;
     private String result;
 	// 上面JTree对象对应的model
 	DefaultTreeModel model;

	String xmlNodes;
 	ExpandNavigationTreeNode root;

	ExpandNavigationTreeNode oTreeNode;
	
	Runtime rt;
	
	 String urlStr;
	 String name;
	 String password;
	 String cre;
	 String webUrl ;
	 String webPath;
	 ConfigurationUtil config;
    public NavigationTreePanel(){
    	SimpleDateFormat CREDENTIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar now = Calendar.getInstance();
        String minuteStr = CREDENTIAL_FORMAT.format(now.getTime());
         
        File file = new File(Spark.getSparkUserHome());
        if (!file.exists()) {
            file.mkdirs();
        }
       new File(file, "spark.properties");
       try{
      file = new File(Spark.getSparkUserHome(), "/spark.properties");
      config = new ConfigurationUtil(file.getAbsolutePath());
      }catch(Exception ex){ex.printStackTrace();}
      webUrl = config.getValue("weburl");
      webPath = SparkRes.getString("NAVI");

      String path =webUrl+webPath ;
		//组织机构树根节点
   // 	 String path = "http://localhost:8080/efmpx/EFMPX/IM/IM01.jsp";
    
         String authenType = "CodedPwd";
         PasswordMD5 passwordMD5 = PasswordMD5.getInstance();
         name = passwordMD5.getAdminName();
         password = passwordMD5.passwordMD5;
         
         
         cre = passwordMD5.md5((minuteStr + password));
         
         urlStr = path + "?p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;

         System.out.println("URL:"+urlStr);
		 xmlNodes = getTreeChildren(urlStr);
		 xmlNodes = xmlNodes.replaceAll("&", "&amp;");
		 
		
			   
		      NavigationXMLParse navigation = new NavigationXMLParse();
		      root =navigation.xmlNavigation(xmlNodes);
		      
		     
		      tree = new JTree(root);
			  model = (DefaultTreeModel)tree.getModel();	
			  tree.setRootVisible(false);
			  tree.setCellRenderer(new IconNavigationNodeRenderer());  
			  tree.setToggleClickCount(1);
			  tree.putClientProperty("JTree.lineStyle" , "None");
	          tree.setBackground(new Color(240,243,253));
	          UIManager.getDefaults().put("Tree.lineTypeDashed", true);
			  tree.setUI(new MyTreeUI());
			  tree.setRowHeight(20);
			  
	     MouseListener ml = new MouseAdapter() 
		 {
			 	public void mouseClicked(MouseEvent e)
				{
			 		
			 		if(e.getClickCount()==1) return;
			 		//获取选中节点
					selectedNode = (ExpandNavigationTreeNode) tree.getLastSelectedPathComponent();
					//如果节点为空，直接返回
					if (selectedNode == null||selectedNode.getUrl()==null||selectedNode.getUrl().trim().length()==0||selectedNode.getUrl().equals("null")) 
						return;
				  
				     SimpleDateFormat CREDENTIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
			         Calendar now = Calendar.getInstance();
			         String minuteStr = CREDENTIAL_FORMAT.format(now.getTime());
			         String authenType = "CodedPwd";
			         PasswordMD5 passwordMD5 = PasswordMD5.getInstance();
			         name = passwordMD5.getAdminName();
			         password = passwordMD5.passwordMD5;
			         cre = passwordMD5.md5((minuteStr + password));
			         String link = "&";
			         if(!selectedNode.getUrl().contains("?"))
			        	 link="?";
			         
			         File file = new File(Spark.getSparkUserHome());
			         if (!file.exists()) {
			             file.mkdirs();
			         }
			        new File(file, "spark.properties");
			        try{
			       file = new File(Spark.getSparkUserHome(), "/spark.properties");
			       config = new ConfigurationUtil(file.getAbsolutePath());
			       }catch(Exception ex){ex.printStackTrace();}
			       webUrl = config.getValue("weburl");
			      //   urlStr = "http://localhost:8080/efmpx/"+selectedNode.getUrl()+link+"p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;
			           urlStr =webUrl +selectedNode.getUrl()+link+"p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;
			 		
			         System.out.println("selectedNode的Url:"+urlStr);
				    rt = Runtime.getRuntime();
				    try
				    {
				    rt.exec("C:\\Program Files\\Internet Explorer\\iexplore.exe "+urlStr);
				    }
				    catch(Exception ex){ex.printStackTrace();}
				}
			};
		    tree.addMouseListener(ml);
	   
	      
	        setLayout(new BorderLayout());
	        panel = new JPanel();
	        panel.setLayout(new GridBagLayout());
	        panel.setBackground(new Color(240,243,253));
	        treeScroller = new JScrollPane(tree);// 滚动条
	        treeScroller.setBorder(BorderFactory.createEmptyBorder());
	        panel.add(treeScroller, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
	                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
	                        5, 5, 5), 0, 0));// 设置边界
	        add(panel, BorderLayout.CENTER);
     }
     
    
     
     public String getTreeChildren(String path)
     {   	 
	     String detail = "";
    	 try
	 		{
    		 URL url = new URL(path);

				HttpURLConnection connect = (HttpURLConnection) url
						.openConnection();
				connect.setDoOutput(true);
				connect.setRequestMethod("GET");
				connect.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				connect.connect();

				InputStream is = connect.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, "utf-8"));
				String strLine = null;
				
				while ((strLine = br.readLine()) != null ) {
					detail += strLine;
				}
				br.close();
				is.close();
				connect.disconnect();
	 		}
	 		catch (Exception ex) {
				ex.printStackTrace();
			}
	 		System.out.println(path+"   "+detail);
            return detail;
	 		
     }
   
     
     
}

