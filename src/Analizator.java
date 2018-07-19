import com.sun.deploy.panel.JreTableModel;
import org.apache.mina.filter.codec.ProtocolCodecException;
import quickfix.*;
import quickfix.mina.message.FIXMessageDecoder;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private ArrayList<MessageActionNew> messageActionNewsList = new ArrayList<>();
    private ReportTable rt;
    public Analizator() throws ProtocolCodecException {
        fileInput=getFilePath("Choose log File");
        System.out.println(fileInput);
        //messageActionNewsList.add(new MessageActionNew("","",false,"",""));
       // messageActionNewsList.add(new MessageActionNew("asdgf","",true,"",""));
        rt = new ReportTable(messageActionNewsList);
       // rt.addDynamicRow(new MessageActionNew("asdgf","",true,"",""));

        //messageListDecoder(new File(fileInput));


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

    private void getMessage(String s) throws InvalidMessage {
        System.out.println(s);
        String[] mar = s.split("\001");
        arrayAnalizer(mar);
    }

    private void arrayAnalizer(String[] array){
        System.out.println("Start analize message");
        ArrayList<String> list = new ArrayList<>(Arrays.asList(array));
        String typeOfMeth="";
        boolean bool=false;
        String id="";
        String pair="";
        String price="";
        String size="";
        ArrayList<MessageActionNew> newActions=new ArrayList<>();
        ArrayList<MessageActionDelete> dellActions = new ArrayList<>();
        for (String s : list) {
            if(s.contains("279")){
                typeOfMeth=s.substring(s.length()-1,s.length());
            }

            if(s.contains("269")){
                String type = s.substring(s.length()-1,s.length());
                if(type.equals("0")){
                    bool=true;
                }

                if(type.equals("1")){
                    bool=false;
                }
            }

            if(s.contains("278")){
                id=s.substring(4);
            }

            if(s.contains("55")){
                pair=s.substring(3);
            }

            if(s.contains("270")){
                price=s.substring(4);
            }

            if(s.contains("271")){
                size=s.substring(4);
            }

            if(typeOfMeth.equals("0") && !id.equals("") && !pair.equals("") && !price.equals("") && !size.equals("")){
                newActions.add(new MessageActionNew(id,pair,bool,price,size));
                id=pair=price=size="";
            }

            if(typeOfMeth.equals("2") && !id.equals("") && !pair.equals("")){
                dellActions.add(new MessageActionDelete(bool,id,pair));
                id="";
                pair="";
            }
        }

        for (MessageActionNew m:newActions) {
            rt.addDynamicRow(m);
        }


        System.out.println("Finish message!");
    }

}

class MessageActionNew{
    private String id;
    private String pair;
    private String bid;
    private String price;
    private String size;
    private boolean bool=false;

    MessageActionNew(String id, String pair, boolean bool, String price, String size){
        this.id=id;
        this.pair=pair;
        if(bool){
            bid="Bid";
            this.bool=true;
        }else{
            bid="Offer";
        }
        this.price=price;
        this.size=size;
    }

    public void writeToTable(){
        if(bool){
            writeToBids();
        } else {
            writeToOffers();
        }
    }

    private void writeToBids(){
        System.out.println("Bid+1 : "+id);
    }

    private  void writeToOffers(){
        System.out.println("Offer+1: "+id);
    }

    public String getId(){return id;}
    public String getPrice(){return price;}
    public String getSize(){return size;}
    public String getBid(){return  bid;}
    public String getPair(){return pair;}
}

class MessageActionDelete{
    private String id;
    private String pair;
    private boolean bid=false;
    private boolean offer=false;

    MessageActionDelete(boolean bool,String id, String pair){
        if(bool){
            bid=true;
        }else{
            offer=true;
        }
        this.id=id;
        this.pair=pair;
        deleteFromTable();
    }

    private void deleteFromTable(){
        if(bid){
            deleteBid();
        }

        if(offer){
            deleteOffer();
        }
    }

    private void deleteBid(){
        System.out.println("Delete bid : "+id);
    }

    private  void deleteOffer(){
        System.out.println("delete offer: "+id);
    }
}


class ReportTable{
    private JTable jTable;
    private JFrame jfrm;
    private ReportTableModel rt;
    private ArrayList<MessageActionNew> messageActionNewArrayList = new ArrayList<>();
    ReportTable(ArrayList<MessageActionNew> messageActionNewArrayList){
        this.messageActionNewArrayList=messageActionNewArrayList;
        jfrm = new JFrame("Report");
        jfrm.getContentPane().setLayout(new FlowLayout());
        jfrm.setSize(300, 170);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rt= new ReportTableModel(messageActionNewArrayList);
        this.jTable = new JTable(rt);
        //this.jTable = new JTable(new DefaultTableModel());
//        JScrollPane jscrlp = new JScrollPane(jTable);
//        jTable.setPreferredScrollableViewportSize(new Dimension(250, 100));
//        jfrm.getContentPane().add(jscrlp);
//        jfrm.setVisible(true);
    }

//    public void addMesage(MessageActionNew messageActionNew){
//        //jTable.add(messageActionNew.getId(),messageActionNew.getBid(),messageActionNew.getPair(),messageActionNew.getPrice(),messageActionNew.getSize());
//
//    }
    public void addDynamicRow(MessageActionNew messageActionNew){
        messageActionNewArrayList.add(messageActionNew);
        rt.fireTableDataChanged();
    }

    public void deleteRow(){

    }

    public void showTable(){
        JScrollPane jscrlp = new JScrollPane(jTable);
        jTable.setPreferredScrollableViewportSize(new Dimension(250, 100));
        jfrm.getContentPane().add(jscrlp);
        jfrm.setVisible(true);
    }
}

class ReportTableModel extends AbstractTableModel {
    private ArrayList<MessageActionNew> messageActionNewArrayList;
    ReportTableModel(ArrayList<MessageActionNew> messageActionNewArrayList){
        this.messageActionNewArrayList=messageActionNewArrayList;
    }

    public void deleteRow(){
        messageActionNewArrayList.remove(1);
    }

    @Override
    public int getRowCount() {
        return messageActionNewArrayList.size();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 0:
                return messageActionNewArrayList.get(rowIndex).getId();
            case 1:
                return messageActionNewArrayList.get(rowIndex).getBid();
            case 2:
                return messageActionNewArrayList.get(rowIndex).getPair();
            case 3:
                return messageActionNewArrayList.get(rowIndex).getPrice();
            case 4:
                return messageActionNewArrayList.get(rowIndex).getSize();
            default:
                return "";
        }
    }

    @Override
    public String getColumnName(int c){
        switch (c){
            case 0:
                return "ID";
            case 1:
                return "Side";
            case 2:
                return "Pair";
            case 3:
                return "Price";
            case 4:
                return "Size";
            default:
                return "Other";
        }
    }

//    @Override
//    public Object getValueAt(int rowIndex, int columnIndex) {
//        return null;
//    }
}