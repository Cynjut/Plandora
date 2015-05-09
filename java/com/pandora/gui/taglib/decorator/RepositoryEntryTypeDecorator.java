package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RepositoryFileTO;

public class RepositoryEntryTypeDecorator extends ColumnDecorator {

	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		Boolean isDirectory = (Boolean)columnValue;
		if (isDirectory!=null) {
			if (isDirectory.booleanValue()) {	
			    image = "<img border=\"0\" src=\"../images/folder.gif\" >";		
			} else {
				RepositoryFileTO item = (RepositoryFileTO)getObject();
				String name = item.getName();
				if (name==null) {
					name = item.getPath();
				}
				
				if (name.endsWith(".xls")) {
					image = "<img border=\"0\" src=\"../images/application-ms-excel.png\" >";
				} else if (name.endsWith(".ppt")) {
					image = "<img border=\"0\" src=\"../images/application-mspowerpoint.png\" >";
				} else if (name.endsWith(".doc")) {
					image = "<img border=\"0\" src=\"../images/application-msword.png\" >";
				} else if (name.endsWith(".pdf")) {
					image = "<img border=\"0\" src=\"../images/application-pdf.png\" >";
				} else if (name.endsWith(".xls")) {
					image = "<img border=\"0\" src=\"../images/application-x-latex.png\" >";
				} else if (name.endsWith(".zip") || name.endsWith(".jar") || name.endsWith(".arj") || name.endsWith(".tar")) {
					image = "<img border=\"0\" src=\"../images/application-zip.png\" >";
				} else if (name.endsWith(".gif")) {
					image = "<img border=\"0\" src=\"../images/image-gif.png\" >";				
				} else if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
					image = "<img border=\"0\" src=\"../images/image-jpeg.png\" >";				
				} else if (name.endsWith(".bmp")) {
					image = "<img border=\"0\" src=\"../images/image-x-ms-bmp.png\" >";
				} else if (name.endsWith(".htm") || name.endsWith(".html") || name.endsWith(".xml")) {
					image = "<img border=\"0\" src=\"../images/text-html.png\" >";				
				} else if (name.endsWith(".mm")) {
					image = "<img border=\"0\" src=\"../images/x-xmind.png\" >";
				} else {
					image = "<img border=\"0\" src=\"../images/text-plain.png\" >";
				}
			}
		}	
		return image;
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}
	
}
