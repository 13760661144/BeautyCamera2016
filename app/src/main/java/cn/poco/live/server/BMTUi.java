package cn.poco.live.server;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import cn.poco.live.dui.DUICtrl;


/**
 * Created by Administrator on 2018/1/10.
 */

public class BMTUi {
    private static BMTUi sInstance;
    private DUICtrl mRoot;
    private ArrayList<Object> mResList = new ArrayList<Object>();
    private WeakReference<Resources> mResources;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static BMTUi getInstance()
    {
        if(sInstance == null)
        {
            sInstance = new BMTUi();
        }
        return sInstance;
    }

    public BMTUi()
    {
        BMTServer.getInstance().addOnStreamChangeListener(mOnStreamChangeListener);
        BMTServer.getInstance().getStream().addPacketParser(mBMTPacketParser);
    }

    public void release()
    {
        BMTServer.getInstance().removeOnStreamChangeListener(mOnStreamChangeListener);
        BMTServer.getInstance().getStream().removePacketParser(mBMTPacketParser);
    }

    public void setRoot(DUICtrl root)
    {
        mRoot = root;
    }

    public void setResources(Resources res)
    {
        mResources = new WeakReference<Resources>(res);
    }

    public void addResource(Object res)
    {
        synchronized(mResList) {
            boolean existed = false;
            for(Object obj : mResList)
            {
                if(obj instanceof String && res instanceof String)
                {
                    if(((String)obj).equals((String)res))
                    {
                        existed = true;
                    }
                }
                else if(obj instanceof Integer && res instanceof Integer)
                {
                    if(((Integer)obj).equals((Integer)res))
                    {
                        existed = true;
                    }
                }
            }
            if(existed == false) {
                mResList.add(res);
                if(BMTServer.getInstance().getStream().isWorking())
                {
                    BMTResPacket p = new BMTResPacket();
                    p.setResource(mResources.get(), res);
                    BMTServer.getInstance().getStream().addPacket(p);
                }
            }
        }
    }

    public void postInsertCommand(int parentId, List<DUICtrl> ctrls, int index)
    {
        if(BMTServer.getInstance().getStream().isWorking())
        {
            StringBuffer xml = new StringBuffer();
            xml.append("<command name=\"insert\" parent_id=\""+parentId+"\" pos_index=\""+index+"\">");
            for(int i = 0; i < ctrls.size(); i++)
            {
                DUICtrl ctrl = ctrls.get(i);
                if(ctrl != null)
                {
                    xml.append(ctrl.toXml());
                }
            }
            xml.append("</command>");

            BMTServer.getInstance().getStream().addPacket(BMTPacket.PACKET_CMD, xml.toString().getBytes());
        }
    }

    public void postInsertCommand(DUICtrl ctrl, int index)
    {
        if(BMTServer.getInstance().getStream().isWorking())
        {
            StringBuffer xml = new StringBuffer();
            xml.append("<command name=\"insert\" parent_id=\""+ctrl.getParentId()+"\" pos_index=\""+index+"\">");
            if(ctrl != null)
            {
                xml.append(ctrl.toXml());
            }
            xml.append("</command>");

            BMTServer.getInstance().getStream().addPacket(BMTPacket.PACKET_CMD, xml.toString().getBytes());
        }
    }

    public void postDeleteCommand(int parentId, List<DUICtrl> ctrls)
    {
        if(BMTServer.getInstance().getStream().isWorking())
        {
            StringBuffer xml = new StringBuffer();
            xml.append("<command name=\"delete\" parent_id=\""+parentId+"\">");
            for(int i = 0; i < ctrls.size(); i++)
            {
                DUICtrl ctrl = ctrls.get(i);
                if(ctrl != null)
                {
                    xml.append("<ctrl id=\""+ctrl.getId()+"\"></ctrl>");
                }
            }
            xml.append("</command>");
            BMTServer.getInstance().getStream().addPacket(BMTPacket.PACKET_CMD, xml.toString().getBytes());
        }
    }

    public void postDeleteCommand(DUICtrl ctrl)
    {
        if(BMTServer.getInstance().getStream().isWorking())
        {
            StringBuffer xml = new StringBuffer();
            xml.append("<command name=\"delete\" parent_id=\""+ctrl.getParentId()+"\">");
            if(ctrl != null)
            {
                xml.append("<ctrl id=\""+ctrl.getId()+"\"></ctrl>");
            }
            xml.append("</command>");
            BMTServer.getInstance().getStream().addPacket(BMTPacket.PACKET_CMD, xml.toString().getBytes());
        }
    }

    public void postModifyCommand(int parentId, List<DUICtrl> ctrls)
    {
        if(BMTServer.getInstance().getStream().isWorking())
        {
            StringBuffer xml = new StringBuffer();
            xml.append("<command name=\"modify\" parent_id=\""+parentId+"\">");
            for(int i = 0; i < ctrls.size(); i++)
            {
                DUICtrl ctrl = ctrls.get(i);
                if(ctrl != null)
                {
                    xml.append("<ctrl");
                    ArrayList<Pair<String,String>> attrs = ctrl.getAttributes();
                    for(int j = 0; j < attrs.size(); j++)
                    {
                        Pair<String,String> pair = attrs.get(j);
                        xml.append(" " + pair.first + "=\"" + pair.second + "\"");
                    }
                    xml.append("></ctrl>");
                }
            }
            xml.append("</command>");

            BMTServer.getInstance().getStream().addPacket(BMTPacket.PACKET_CMD, xml.toString().getBytes());
        }
    }

    public void postModifyCommand(DUICtrl ctrl)
    {
        if(BMTServer.getInstance().getStream().isWorking())
        {
            StringBuffer xml = new StringBuffer();
            xml.append("<command name=\"modify\" parent_id=\""+ctrl.getParentId()+"\">");
            if(ctrl != null)
            {
                xml.append("<ctrl");
                ArrayList<Pair<String,String>> attrs = ctrl.getAttributes();
                for(int j = 0; j < attrs.size(); j++)
                {
                    Pair<String,String> pair = attrs.get(j);
                    xml.append(" " + pair.first + "=\"" + pair.second + "\"");
                }
                xml.append("></ctrl>");
            }
            xml.append("</command>");

            BMTServer.getInstance().getStream().addPacket(BMTPacket.PACKET_CMD, xml.toString().getBytes());
        }
    }

    public void updateUIHeader()
    {
        if(mResources == null || mResources.get() == null || mRoot == null){
            return;
        }
        StringBuffer xml = new StringBuffer();
        xml.append("<command name=\"config\">");
        xml.append(mRoot.toXml());
        xml.append("</command>");
        ArrayList<BMTPacket> packets = new ArrayList<BMTPacket>();
        synchronized(mResList) {
            for(Object res : mResList)
            {
                BMTResPacket p = new BMTResPacket();
                p.setResource(mResources.get(), res);
                packets.add(p);
            }
        }
        packets.add(new BMTPacket(BMTPacket.PACKET_CMD, xml.toString().getBytes()));
        BMTServer.getInstance().getStream().setDUIHeaderPacket(packets);
    }

    private BMTServer.OnStreamChangeListener mOnStreamChangeListener = new BMTServer.OnStreamChangeListener()
    {
        @Override
        public void onStreamCreated(BMTStream stream) {
            stream.addPacketParser(mBMTPacketParser);
        }

        @Override
        public void onStreamRelease(BMTStream stream) {
            stream.removePacketParser(mBMTPacketParser);
        }
    };

    private BMTStream.BMTPacketParser mBMTPacketParser = new BMTStream.BMTPacketParser()
    {
        @Override
        public void parse(BMTPacket p) {
            if(p.getType() == BMTPacket.PACKET_CMD)
            {
                parseCommand(p.getData());
            }
        }
    };

    private void parseCommand(byte[] data)
    {
        //Log.d("hwq", new String(data));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(bis);
            Element root = dom.getDocumentElement();
            if("notify".equals(root.getAttribute("name")))
            {
                NodeList items = root.getElementsByTagName("event");//查找所有person节点
                for (int i = 0; i < items.getLength(); i++) {
                    Element element = (Element) items.item(i);
                    String name = element.getAttribute("name");
                    String id = element.getAttribute("id");
                    String parent = element.getAttribute("parent_id");
                    if(parent != null && parent.length() > 0)
                    {
                        DUICtrl p = findDUICtrl(mRoot, Integer.parseInt(parent));
                        if(p != null)
                        {
                            p = findDUIChild(p, Integer.parseInt(id));
                            if(p != null)
                            {
                                Bundle bundle = new Bundle();
                                NamedNodeMap map = element.getAttributes();
                                int count = map.getLength();
                                for(int j = 0; j < count; j++)
                                {
                                    Node n = map.item(j);
                                    bundle.putString(n.getNodeName(), n.getNodeValue());
                                }
                                p.fireEvent(name, bundle);
                            }
                        }
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DUICtrl findDUICtrl(DUICtrl ctrl, int id)
    {
        DUICtrl c = findDUIChild(ctrl, id);
        if(c != null)
        {
            return c;
        }
        int count = ctrl.getChildCount();
        for(int i = 0; i < count; i++)
        {
            c = ctrl.getChildAt(i);
            if(c != null)
            {
                c = findDUIChild(c, id);
                if(c != null)
                {
                    return c;
                }
            }
        }
        for(int i = 0; i < count; i++)
        {
            c = ctrl.getChildAt(i);
            if(c != null)
            {
                c = findDUICtrl(c, id);
                if(c != null)
                {
                    return c;
                }
            }
        }
        return null;
    }

    private DUICtrl findDUIChild(DUICtrl ctrl, int id)
    {
        if(id == ctrl.getId()) {
            return ctrl;
        }
        DUICtrl p = null;
        DUICtrl c = null;
        int count = ctrl.getChildCount();
        for(int i = 0; i < count; i++)
        {
            c = ctrl.getChildAt(i);
            if(c != null && c.getId() == id)
            {
                return c;
            }
        }
        return null;
    }
}
