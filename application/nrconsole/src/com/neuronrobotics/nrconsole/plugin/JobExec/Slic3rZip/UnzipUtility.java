package com.neuronrobotics.nrconsole.plugin.JobExec.Slic3rZip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.neuronrobotics.nrconsole.util.FileSelectionFactory;
import com.neuronrobotics.nrconsole.util.Slic3rFilter;
import com.neuronrobotics.sdk.common.Log;

 
/**
 * This utility extracts files and directories of a standard zip file to
 * a destination directory.
 * @author www.codejava.net
 *
 */
public class UnzipUtility {
	   /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    
    public static String extractSlic3r() throws IOException{
    	String file;
    	String name = "Slic3r"+OSUtil.getExtension();
    	File zipFile=null;

    	zipFile =inJarLoad();
	
    	if(OSUtil.isOSX()) {
			file="osx/" + name;
			return FileSelectionFactory.GetFile(zipFile,"Location of slic3r after installing dmg dmg", "Select" , new Slic3rFilter()).getPath();
			
		}else{
			String destDirectory=System.getProperty("java.io.tmpdir");
			unzip(zipFile.getAbsolutePath(), destDirectory);
			if(OSUtil.isWindows()) {
				return destDirectory+"\\Slic3r\\slic3r.exe";
			}else if(OSUtil.isLinux()) {
				return destDirectory+"/Slic3r/bin/slic3r";
			}
			return null;
		}
    	
    }
    
	private  static  InputStream locateResource() {
    	String file;
    	String name = "Slic3r"+OSUtil.getExtension();
    	if(OSUtil.isOSX()) {
			file="osx/" + name;
		}else if(OSUtil.isWindows()) {
			if(OSUtil.is64Bit()){
				file="/windows/x86_64/" + name;
			}else {
				file="/windows/x86/" + name;
			}
		}else if(OSUtil.isLinux()) {
			if(OSUtil.is64Bit()) {
				file="/linux/x86_64/" + name;
			}else {
				file="/linux/x86_32/" + name;
			}
		}else{
			//System.err.println("Can't load native file: "+name+" for os arch: "+OSUtil.getOsArch());
			return null;
		}
    	
    	InputStream is = UnzipUtility.class.getResourceAsStream(file);
    	if(is == null){
    		Log.error("This resource is not present:" +file+" ");
    	}
		return is;
	}
    
	private static  File inJarLoad() {
		//start by assuming the library can be loaded from the jar
		InputStream resourceSource = locateResource();
		File resourceLocation = prepResourceLocation();

		try {
			copyResource(resourceSource, resourceLocation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resourceLocation;
	}
	
	private static void copyResource(InputStream io, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		
		
		byte[] buf = new byte[256];
		int read = 0;
		while ((read = io.read(buf)) > 0) {
			fos.write(buf, 0, read);
		}
		fos.close();
		io.close();
	}

	private  static File prepResourceLocation(){	
		String fileName = "Slic3r"+OSUtil.getExtension();
		String tmpDir = System.getProperty("java.io.tmpdir");
		//String tmpDir = "M:\\";
		if ((tmpDir == null) || (tmpDir.length() == 0)) {
			tmpDir = "tmp";
		}
		
		String displayName = new File(fileName).getName().split("\\.")[0];
		
		String user = System.getProperty("user.name");
		
		File fd = null;
		File dir = null;
		
		for(int i = 0; i < 10; i++) {
			dir = new File(tmpDir, displayName + "_" + user + "_" + (i));
			if (dir.exists()) {
				if (!dir.isDirectory()) {
					continue;
				}
				
				try {
					File[] files = dir.listFiles();
					for (int j = 0; j < files.length; j++) {
						if (!files[j].delete()) {
							continue;
						}
					}
				} catch (Throwable e) {
					
				}
			}
			
			if ((!dir.exists()) && (!dir.mkdirs())) {
				continue;
			}
			
			try {
				dir.deleteOnExit();
			} catch (Throwable e) {
				// Java 1.1 or J9
			}
			
			fd = new File(dir, fileName + OSUtil.getExtension());
			if ((fd.exists()) && (!fd.delete())) {
				continue;
			}
			
			try {
				if (!fd.createNewFile()) {
					continue;
				}
			} catch (IOException e) {
				continue;
			} catch (Throwable e) {
				// Java 1.1 or J9
			}
			
			break;
		}
		
		if(fd == null || !fd.canRead()) {
			throw new RuntimeException("Unable to deploy native resource");
		}
		//System.out.println("Local file: "+fd.getAbsolutePath());
		return fd;
	}
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public static void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private  static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
    
    private static class OSUtil {
		public static boolean is64Bit() {
			////System.out.println("Arch: "+getOsArch());
			return getOsArch().startsWith("x86_64") || getOsArch().startsWith("amd64");
		}
		public static boolean isARM() {
			return getOsArch().startsWith("arm");
		}
		public static boolean isPPC() {
			return getOsArch().toLowerCase().contains("ppc");
		}
		public static boolean isCortexA8(){
			if(isARM()){
				//TODO check for cortex a8 vs arm9 generic
				return true;
			}
			return false;
		}
		public static boolean isWindows() {
			////System.out.println("OS name: "+getOsName());
			return getOsName().toLowerCase().startsWith("windows") ||getOsName().toLowerCase().startsWith("microsoft") || getOsName().toLowerCase().startsWith("ms");
		}
		
		public static boolean isLinux() {
			return getOsName().toLowerCase().startsWith("linux");
		}
		
		public static boolean isOSX() {
			return getOsName().toLowerCase().startsWith("mac");
		}
		
		public static String getExtension() {
			if(isWindows()) {
				return ".zip";
			}
			
			if(isLinux()) {
				return ".zip";
			}
			
			if(isOSX()) {
				return ".dmg";
			}
			
			return "";
		}
		
		public static String getOsName() {	
			return System.getProperty("os.name");
		}
		
		public static String getOsArch() {
			return System.getProperty("os.arch");
		}
		
		@SuppressWarnings("unused")
		public static String getIdentifier() {
			return getOsName() + " : " + getOsArch();
		}
	}
}
