package com.tests.imp;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import com.pandora.exception.BusinessException;
import com.pandora.imp.MsProjectXmlImportBUS;

/**
 * 
 */
public class MsProjectXmlImportBUSTest extends TestCase {
    
    private InputStream fis1, fis2;
        
    private MsProjectXmlImportBUS ms = new MsProjectXmlImportBUS();
    
    
    protected void setUp() throws Exception {
        File fakeFile = new File("D:\\projects\\Projeto_Pandora\\Fake_1.xml");
        this.fis1 = new FileInputStream(fakeFile);
        
        File xmlFile = new File("D:\\projects\\Projeto_Pandora\\proje_1.xml");
        this.fis2 = new FileInputStream(xmlFile);      
    }


    public void testValidate() {
        boolean status1 = false;
        try {
            this.ms.validate(fis1, null, null);
        } catch (BusinessException e) {
            if(e.getMessage().indexOf("is invalid")>-1){
                status1 = true;    
            }
        }
        assertTrue("test 1 - check if fake xml file failed", status1);

        boolean status2 = true;
        try {
            this.ms.validate(fis2, null, null);
        } catch (BusinessException e) {
            status1 = false;
        }
        assertTrue("test 2 - check if well-formed xml file works", status2);
    }


    public void testImportFile(){
        boolean status1 = true;
        try {
            this.ms.importFile(this.fis2, null, null);
        } catch (BusinessException e) {
            status1 = false;
        }
        assertTrue("", status1);
    }

}
