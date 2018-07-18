//import org.apache.mina.filter.codec.ProtocolCodecException;
import org.apache.mina.filter.codec.ProtocolCodecException;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix43.MarketDataSnapshotFullRefresh;
import quickfix.mina.message.FIXMessageDecoder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by bogdan on 17.07.18.
 */
public class Analizator {
    private String fileInput;
    private String fileOutput;
    public Analizator() throws ProtocolCodecException {
        fileInput=getFilePath("Choose log File");
        System.out.println(fileInput);
       // readFile(new File(fileInput));
        messageListDecoder(new File(fileInput));
        //listAnalizer(messList);


    }

    private File unzipMethod(String fileInput){
        final String dstDirectory = destinationDirectory(fileInput);
        final File dstDir = new File(dstDirectory);
        if (!dstDir.exists()) {
            dstDir.mkdir();
        }
        File nextFile = null;
        try{
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileInput));
            ZipEntry ze = zis.getNextEntry();
            String nextFileName;
            while (ze != null) {
                nextFileName = ze.getName();
                nextFile = new File(dstDirectory + File.separator
                        + nextFileName);
                System.out.println("Распаковываем: "
                        + nextFile.getAbsolutePath());
            }
            zis.close();
        }catch (FileNotFoundException e){
            e.getMessage();
        }catch (IOException e1){
            e1.getMessage();
        }
        return nextFile;
    }

    private String destinationDirectory(final String srcZip) {
        return srcZip.substring(0, srcZip.lastIndexOf("."));
    }

    private void messageListDecoder(File file) throws ProtocolCodecException {
        FIXMessageDecoder fmd;
        try{
           fmd = new FIXMessageDecoder();
            FIXMessageDecoder.MessageListener listener = new FIXMessageDecoder.MessageListener() {
                @Override
                public void onMessage(String message) {
                    try {
                        getMessage(message);
                    } catch (InvalidMessage invalidMessage) {
                        invalidMessage.printStackTrace();
                    }
                }
            };
                fmd.extractMessages(file,listener);
        }catch (UnsupportedEncodingException e){
            e.getMessage();
        }catch (IOException e){
            e.getMessage();
        }
    }

    private void readFile(File file) throws InvalidMessage{
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(file));
            String s;
            while ((s=br.readLine())!=null){
                System.out.println(s);
                getMessage(s);
                //messageParse(s);
            }
        }catch (IOException e){
            e.getMessage();
        }
    }

    private  String getFilePath(String dialogTitle){
        String filePath="";
        String[] filters = {"summary"};
        JFileChooser dialog = new JFileChooser();
        dialog.setDialogTitle(dialogTitle);
        dialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("summary", filters);
        dialog.setFileFilter(filter);
        try{
            dialog.showOpenDialog(null);
            filePath =dialog.getSelectedFile().getAbsolutePath();}
        catch(NullPointerException e){
            System.out.println("Canceled");
            System.exit(0);
        }
        return filePath;

    }

    private void listAnalizer(ArrayList<String> list) {
        try {
                for (String s:list) {
                    getMessage(s);
                }

        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (InvalidMessage invalidMessage) {
            invalidMessage.printStackTrace();
        }

    }

    private void getMessage(String s) throws InvalidMessage {
        System.out.println(s);
        Message message = new Message(s);
        System.out.println("LineAnalizer: "+message);
        messageAnalizer(message);

    }

    private void messageAnalizer(Message message) {
        try {
            String typeMsg = message.getHeader().getString(35);
            int d = message.getInt(279);
            System.out.println("messageAnalizer: find some modify: "+d);
            String pair = message.getString(55);
            System.out.println("Pair: "+pair);
            double id = message.getDouble(278);
            System.out.println("Id: "+id);
            double price = message.getDouble(270);
            System.out.println("Price: "+price);
            long size = message.getInt(271);
            System.out.println("size: "+size);
            int type = message.getInt(269);
            int groupCnt = message.getInt(268);
            System.out.println("bid offer trade: "+ type);
           // System.out.println("Group: "+message.hasGroup(270));
           // ArrayList<Group> gl = (ArrayList<Group>) message.getGroups(279);
           // System.out.println(Arrays.asList(gl));
            int[] order = {269,278,55,270,271};
            quickfix.fix43.MessageFactory msg = new quickfix.fix43.MessageFactory();
            Group group11 =msg.create("FIX.4.4",typeMsg,NoMDEntries.FIELD);
           // quickfix.Group group = new quickfix.Group(279,269,order);
            Group gl = message.getGroup(268,group11);

           // System.out.println("279 groups: "+message.getGroup(268,group).toString());
            //System.out.println("279 groups: "+message.getGroup(groupCnt,group.getFieldTag()));
          //  NoMDEntries noMDEntries = new NoMDEntries();
           // message.get(noMDEntries);
          // ArrayList<Group> gl = (ArrayList<Group>) message.getGroups(279);
//            quickfix.fix42.MarketDataSnapshotFullRefresh.NoMDEntries group =
//                    new quickfix.fix42.MarketDataSnapshotFullRefresh.NoMDEntries();
//            MDEntryType MDEntryType = new MDEntryType();
//            MDEntryPx MDEntryPx = new MDEntryPx();
//            MDEntrySize MDEntrySize = new MDEntrySize();
//            OrderID orderID = new OrderID();
//
//            message.getGroup(1, group);
//            group.get(MDEntryType);
//            group.get(MDEntryPx);
//            group.get(MDEntrySize);
//            group.get(orderID);
//
//            message.getGroup(2, group);
//            group.get(MDEntryType);
//            group.get(MDEntryPx);
//            group.get(MDEntrySize);
            //DataDictionary dd = new DataDictionary("asdfasf:"+String.valueOf(message));
           // System.out.println("agsdfgdsfg: "+dd.getGroup(String.valueOf(message),279));



        }catch (Exception e){
            e.printStackTrace();
            System.out.println("empty message");
        }
    }
}
