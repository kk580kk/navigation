package com.jivesoftware.spark.navigation;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.jivesoftware.resource.SparkRes;

import com.jivesoftware.spark.navigation.ExpandNavigationTreeNode;
public class IconNavigationNodeRenderer extends DefaultTreeCellRenderer//继承该类 
{   
	 /** 
     * ID 
     */  
    private static final long   serialVersionUID    = 1L;  
  
    /** 
     * 重写父类DefaultTreeCellRenderer的方法 
     */  
    @Override  
    public Component getTreeCellRendererComponent(JTree tree, Object value,  
            boolean sel, boolean expanded, boolean leaf, int row,  
            boolean hasFocus)  
    {  
  
        //执行父类原型操作  
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,  
                row, hasFocus);  
  
        setText(value.toString());  
          
        if (sel)  
        {  
            setForeground(getTextSelectionColor());  
        }  
        else  
        {  
            setForeground(getTextNonSelectionColor());  
        }  
          
        //得到每个节点的TreeNode  
        ExpandNavigationTreeNode node = (ExpandNavigationTreeNode) value;  
          
        //得到每个节点的text  
           
     
        String url ="src/plugins/navigation/resources/images";
       
       // this.setIcon(new ImageIcon(url+"/leaf.png")); 
      
        this.setOpenIcon(SparkRes.getImageIcon("DOWN"));
        this.setClosedIcon(SparkRes.getImageIcon("RIGHT"));
        this.setLeafIcon(SparkRes.getImageIcon("FILE"));
//        this.setOpenIcon(new ImageIcon(url+"/down.png"));
//        this.setClosedIcon(new ImageIcon(url+"/right.png"));
//        this.setLeafIcon(new ImageIcon(url+"/file.png"));
        this.setFont(new Font("宋体",Font.PLAIN,13));
        this.setBackground(new Color(240,243,253));
        this.setBackgroundNonSelectionColor(new Color(240,243,253));
        this.setBackgroundSelectionColor(Color.BLACK);

     
        
        return this;  
    }  
}


