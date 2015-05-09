package com.pandora.bus.convert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.anotherbigidea.flash.movie.ImageUtil;
import com.anotherbigidea.flash.movie.Movie;
import com.anotherbigidea.flash.movie.Shape;
import com.pandora.AttachmentTO;
import com.pandora.exception.BusinessException;


public abstract class Converter {

	public static final  int OUTPUT_MIND_MAP_VIEWER = 1;
	
	public static final  int OUTPUT_FLASH_VIEWER    = 2;
	
	public static final  int OUTPUT_IFRAME_VIEWER   = 3;
	
	
	public abstract int getOutputFormat();
		
	public abstract String getContentType();
	
	public abstract String[] getMimeTypeList();
	
	public abstract String[] getExtensionList();

	public abstract byte[] convert(AttachmentTO file) throws BusinessException ;
	
	
	protected void convertJpgToSwf(ByteArrayOutputStream baos, String folder, String key) throws Exception {
		ArrayList tempFiles = new ArrayList();
		
		try {
			File f = new File(folder);
			if (f!=null) {
			    String[] list = f.list();
			    if (list!=null) {
					Movie localMovie = null;
					
			        for (int k=0; k<list.length; k++) {
			        	if (list[k].startsWith(key)){
			    	        tempFiles.add(folder + list[k]);
			        		FileInputStream jpgFile = null;			        		
			        		try {
				    	        jpgFile = new FileInputStream(folder + list[k]);
				    	        int[] arrayOfInt = new int[2];			        		
				    	        Shape localShape = ImageUtil.shapeForImage(jpgFile, arrayOfInt);
				    	        int i = arrayOfInt[0];
				    	        int j = arrayOfInt[1];
				    	        
				    	        if (localMovie==null) {
					    	        localMovie = new Movie(i + 10, j + 10, 12, 5, null);
					    	    	localMovie.setFrameRate(1);				    	        	
				    	        }
				    	        localMovie.appendFrame().placeSymbol(localShape, 5, 5);
				    	        				    	        
			        		} catch(Exception e){
			        			jpgFile = null;
			        			localMovie = null;
			        			break;
			        		} finally {
			        			if (jpgFile!=null) {
			        				jpgFile.close();	
			        			}
			        		}
			        	}
			        }
			        
			        localMovie.write(baos);
			        //localMovie.write(folder + "dump.swf");
			    }	        	
			}			

		} catch(IOException e){
			throw new Exception(e);
			
		}finally {
			this.removeTempFiles(tempFiles);			
		}
	}


	private void removeTempFiles(ArrayList tempFiles) {
		try {
			
			//delete the temporary files
			Iterator i = tempFiles.iterator();
			while(i.hasNext()) {
				String tempfilePath = (String)i.next();
				File f = new File(tempfilePath);
				f.delete();
			}
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
}
