package com.jivesoftware.spark.navigation;

import org.jivesoftware.PasswordMD5;
import org.jivesoftware.Spark;
import org.jivesoftware.spark.SparkManager;

import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    String webUrl;
    String webPath;
    ConfigurationUtil config;

    public NavigationTreePanel() {
        SimpleDateFormat CREDENTIAL_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
        Calendar now = Calendar.getInstance();
        String minuteStr = CREDENTIAL_FORMAT.format(now.getTime());

        File file = new File(Spark.getSparkUserHome());
        if (!file.exists()) {
            file.mkdirs();
        }
        new File(file, "spark.properties");
        try {
            file = new File(Spark.getSparkUserHome(), "/spark.properties");
            config = new ConfigurationUtil(file.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        webUrl = config.getValue("weburl");
//        webPath = SparkRes.getString("NAVI");
//        use built in function
        webPath = "?type=navi";

        String path = webUrl + webPath;
        //组织机构树根节点
        // 	 String path = "http://localhost:8080/efmpx/EFMPX/IM/IM01.jsp";

        String authenType = "CodedPwd";
        PasswordMD5 passwordMD5 = PasswordMD5.getInstance();
        name = passwordMD5.getAdminName();
        password = passwordMD5.passwordMD5;


        cre = passwordMD5.md5((minuteStr + password));

        urlStr = path + "&p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;

        System.out.println("URL:" + urlStr);
        xmlNodes = getTreeChildren(urlStr);
        xmlNodes = xmlNodes.replaceAll("&", "&amp;");


        NavigationXMLParse navigation = new NavigationXMLParse();
        root = navigation.xmlNavigation(xmlNodes);


        tree = new JTree(root);
        model = (DefaultTreeModel) tree.getModel();
        tree.setRootVisible(false);
        tree.setCellRenderer(new IconNavigationNodeRenderer());
        tree.setToggleClickCount(1);
        tree.putClientProperty("JTree.lineStyle", "None");
        tree.setBackground(new Color(240, 243, 253));
        UIManager.getDefaults().put("Tree.lineTypeDashed", true);
        tree.setUI(new MyTreeUI());
        tree.setRowHeight(20);

        MouseListener ml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {

                if (e.getClickCount() == 1) return;
                //获取选中节点
                selectedNode = (ExpandNavigationTreeNode) tree.getLastSelectedPathComponent();
                //如果节点为空，直接返回
                if (selectedNode == null || selectedNode.getUrl() == null || selectedNode.getUrl().trim().length() == 0 || selectedNode.getUrl().equals("null"))
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
                if (!selectedNode.getUrl().contains("?"))
                    link = "?";

                File file = new File(Spark.getSparkUserHome());
                if (!file.exists()) {
                    file.mkdirs();
                }
                new File(file, "spark.properties");
                try {
                    file = new File(Spark.getSparkUserHome(), "/spark.properties");
                    config = new ConfigurationUtil(file.getAbsolutePath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                //get service url
                webUrl = config.getValue("weburl");
                //   urlStr = "http://localhost:8080/efmpx/"+selectedNode.getUrl()+link+"p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;
                urlStr = webUrl + selectedNode.getUrl() + link + "p_username=" + name + "&p_password=" + cre + "&p_authen=" + authenType;

                System.out.println("selectedNode的Url:" + urlStr);
                rt = Runtime.getRuntime();
                try {
//                    rt.exec("C:\\Program Files\\Internet Explorer\\iexplore.exe " + urlStr);
                	//TODO
                	Desktop.getDesktop().browse(new URI(selectedNode.getUrl()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        tree.addMouseListener(ml);


        setLayout(new BorderLayout());
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(240, 243, 253));
        treeScroller = new JScrollPane(tree);// 滚动条
        treeScroller.setBorder(BorderFactory.createEmptyBorder());
        panel.add(treeScroller, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,
                5, 5, 5), 0, 0));// 设置边界
        add(panel, BorderLayout.CENTER);
    }


    public String getTreeChildren(String path) {
        String detail = "";
        try {
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

            while ((strLine = br.readLine()) != null) {
                detail += strLine;
            }
            br.close();
            is.close();
            connect.disconnect();
        } catch (MalformedURLException e) {
            //:这里需要做一些错误处理工作。这里是服务器连接不能错误内容。
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(SparkManager.getWorkspace(),"导航菜单的数据源无法连接","Error:导航菜单树",JOptionPane.ERROR_MESSAGE);
                }
            });
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println(path + "   " + detail);
        return detail;

    }


}

