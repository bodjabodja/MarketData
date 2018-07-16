

import quickfix.mina.message.FIXMessageDecoder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    public Analizator(){
        fileInput=downloadView();
        System.out.println(fileInput);
       // unzipMethod(fileInput);
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

//    private void messageListDecoder(File file){
//        FIXMessageDecoder fmd;
//        try{
//           fmd = new FIXMessageDecoder();
//            ArrayList<String> messageList = (ArrayList<String>) fmd.extractMessages(file);
//        }catch (UnsupportedEncodingException e){
//            e.getMessage();
//        }catch (IOException e){
//            e.getMessage();
//        }
//    }

    private void readFile(File file){
        BufferedReader br;
        try{
            br = new BufferedReader(new FileReader(file));
            String s;
            while ((s=br.readLine())!=null){

            }
        }catch (IOException e){
            e.getMessage();
        }
    }

    private String downloadView(){
        JFrame frame = new JFrame("JFileChooser Popup");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();

        final JLabel directoryLabel = new JLabel(" ");
        directoryLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
        contentPane.add(directoryLabel, BorderLayout.NORTH);

        final JLabel filenameLabel = new JLabel(" ");
        filenameLabel.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 36));
        contentPane.add(filenameLabel, BorderLayout.SOUTH);

        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setControlButtonsAreShown(false);
        contentPane.add(fileChooser, BorderLayout.CENTER);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser theFileChooser = (JFileChooser) actionEvent
                        .getSource();
                String command = actionEvent.getActionCommand();
                if (command.equals(JFileChooser.APPROVE_SELECTION)) {
                    File selectedFile = theFileChooser.getSelectedFile();
                    fileInput = selectedFile.getAbsolutePath();
                    directoryLabel.setText(selectedFile.getParent());
                    filenameLabel.setText(selectedFile.getName());
                } else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
                    directoryLabel.setText(" ");
                    filenameLabel.setText(" ");
                }
            }
        };
        fileChooser.addActionListener(actionListener);

        frame.pack();
        frame.setVisible(true);
        return fileInput;
    }

    private void lineAnalizer(String line){

    }
}
