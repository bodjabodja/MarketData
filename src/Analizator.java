import org.apache.mina.filter.codec.ProtocolCodecException;
import quickfix.*;
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
                new MessageActionDelete(bool,id,pair);
                id="";
                pair="";
            }
        }

        for (MessageActionNew m:newActions) {
            m.writeToTable();
        }
        System.out.println("Finish message!");
    }

}

class MessageActionNew{
    private String id;
    private String pair;
    private boolean bid=false;
    private boolean offer=false;
    private String price;
    private String size;

    MessageActionNew(String id, String pair, boolean bool, String price, String size){
        this.id=id;
        this.pair=pair;
        if(bool){
            bid=true;
        }else{
            offer=true;
        }
        this.price=price;
        this.size=size;
    }

    public void writeToTable(){
        if(bid){
            writeToBids();
        }

        if(offer){
            writeToOffers();
        }
    }

    private void writeToBids(){
        System.out.println("Bid+1 : "+id);
    }

    private  void writeToOffers(){
        System.out.println("Offer+1: "+id);
    }
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
            writeToBids();
        }

        if(offer){
            writeToOffers();
        }
    }

    private void writeToBids(){
        System.out.println("Delete bid : "+id);
    }

    private  void writeToOffers(){
        System.out.println("delete offer: "+id);
    }
}

class ReportTable{
    ReportTable(){

    }
}