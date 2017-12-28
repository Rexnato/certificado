package mx.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipConverter {
	
	 List<String> fileList;
//	    private static final String OUTPUT_ZIP_FILE = "C:\\MyFile.zip";
//	    private static final String SOURCE_FOLDER = "C:\\testzip";

	    public ZipConverter(){
		fileList = new ArrayList<String>();
	    }
	    
	    
	    public void zipIt(String zipFile, String sourceFolder){

	        byte[] buffer = new byte[1024];

	        try{

	       	FileOutputStream fos = new FileOutputStream(zipFile);
	       	ZipOutputStream zos = new ZipOutputStream(fos);

	       	System.out.println("Output to Zip : " + zipFile);

	       	for(String file : this.fileList){

	       		System.out.println("File Added : " + file);
	       		ZipEntry ze= new ZipEntry(file);
	           	zos.putNextEntry(ze);

	           	FileInputStream in =
	                          new FileInputStream(sourceFolder + File.separator + file);

	           	int len;
	           	while ((len = in.read(buffer)) > 0) {
	           		zos.write(buffer, 0, len);
	           	}

	           	in.close();
	       	}

	       	zos.closeEntry();
	       	//remember close it
	       	zos.close();

	       	System.out.println("Done");
	       }catch(IOException ex){
	          ex.printStackTrace();
	       }
	      }

	    
	    
	
	    
	    
	    public void generateFileList(File node, String sourceFolder){

	    	//add file only
		if(node.isFile()){
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString(), sourceFolder));
		}

		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				generateFileList(new File(node, filename), sourceFolder);
			}
		}

	    }
	    
	    
	    
	    private String generateZipEntry(String file, String sourceFolder){
	    	return file.substring(sourceFolder.length()+1, file.length());
	    }
	    
	public  void comprimir(String path_zip, String path_file, String nameZip) {

			byte[] buffer = new byte[1024];

	    	try{

	    		FileOutputStream fos = new FileOutputStream(path_zip);
	    		ZipOutputStream zos = new ZipOutputStream(fos);
	    		ZipEntry ze= new ZipEntry(nameZip);
	    		zos.putNextEntry(ze);
	    		FileInputStream in = new FileInputStream(path_file);

	    		int len;
	    		while ((len = in.read(buffer)) > 0) {
	    			zos.write(buffer, 0, len);
	    		}

	    		in.close();
	    		zos.closeEntry();

	    		//remember close it
	    		zos.close();

	    		System.out.println("Done");

	    	}catch(IOException ex){
	    	   ex.printStackTrace();
	    	}
		 
	 }
	
	
	public void comprimirRecursivo() {
		
		
		
		
	}

}
