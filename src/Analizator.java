import org.apache.mina.filter.codec.ProtocolCodecException;
import quickfix.*;
import quickfix.mina.message.FIXMessageDecoder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by bogdan on 17.07.18.
 */
public class Analizator {
    private String fileInput;
    private String fileOutput;
    public Analizator() throws ProtocolCodecException, InvalidMessage, ConfigError {
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
                    } catch (ConfigError configError) {
                        configError.printStackTrace();
                    } catch (InvalidMessage invalidMessage) {
                        invalidMessage.printStackTrace();
                    } catch (FieldNotFound fieldNotFound) {
                        fieldNotFound.printStackTrace();
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

    private void readFile(File file) throws InvalidMessage, ConfigError, IOException {
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
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
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

    private void listAnalizer(ArrayList<String> list) throws FieldNotFound {
        try {
                for (String s:list) {
                    getMessage(s);
                }

        }catch (NullPointerException e){
            e.printStackTrace();
        } catch (ConfigError configError) {
            configError.printStackTrace();
        } catch (InvalidMessage invalidMessage) {
            invalidMessage.printStackTrace();
        }

    }

    private void getMessage(String s) throws ConfigError, InvalidMessage, FieldNotFound {
        System.out.println(s);
        Message message = new Message(s);
        System.out.println("LineAnalizer: "+message);
        messageAnalizer(message);

    }

    private void messageAnalizer(Message message) throws FieldNotFound {
        try {
            double d = message.getDouble(279);
            System.out.println("messageAnalizer: find some modify: "+d);
        }catch (Exception e){
            System.out.println("empty message");
        }
    }
}
