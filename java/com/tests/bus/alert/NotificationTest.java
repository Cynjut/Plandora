package com.tests.bus.alert;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

import junit.framework.TestCase;

import com.pandora.bus.alert.Notification;
import com.pandora.helper.DateUtil;

public class NotificationTest extends TestCase {
    
    private Notification n;
    
    public void testGetToken(){
        
        ArrayList list0 = n.getToken(null);
        assertTrue(list0.size()==0);
        
        String arg1 = "http://mydest:8080/name=#COL_1#&age=#COL_2#";
        ArrayList list1 = n.getToken(arg1);
        assertTrue(list1.size()==2);
        assertTrue(((Integer)list1.get(0)).intValue()==1);
        assertTrue(((Integer)list1.get(1)).intValue()==2);
        
        String arg2 = "#COL_1COL_1#&age=#COL_234##COL_1#";
        ArrayList list2 = n.getToken(arg2);
        assertTrue(list2.size()==2);
        assertTrue(((Integer)list2.get(0)).intValue()==234);
        assertTrue(((Integer)list2.get(1)).intValue()==1);

        String arg3 = "#&age=#COL_2a4##COL_1#COL_2#";
        ArrayList list3 = n.getToken(arg3);
        assertTrue(list3.size()==1);
        assertTrue(((Integer)list3.get(0)).intValue()==2);

        String arg4 = "#COL_1#";
        ArrayList list4 = n.getToken(arg4);
        assertTrue(list4.size()==1);
        assertTrue(((Integer)list4.get(0)).intValue()==1);
        
    }
    
    public void testGetColumn(){
        Vector v1 = getData("1|ABC|test", 1);        
        Object a1 = n.replaceByToken(v1, "http://mydest:8080/name=#COL_1#&age=#COL_2#");
        assertTrue(a1.equals("http://mydest:8080/name=1&age=ABC"));
                
        Object a3 = n.replaceByToken(null, "http://mydest:8080/name=#COL_1#&age=#COL_2#");
        assertTrue(a3.equals("http://mydest:8080/name=ERR!&age=ERR!"));
        
        Object a4 = n.replaceByToken(v1, null);
        assertTrue(a4==null);
        
        Vector v2 = getData("1|929292.00|1212.22", 2);
        Object a5 = n.replaceByToken(v2, "o terceiro valor eh #COL_3# o segundo eh #COL_2# e #COL_1#");
        assertTrue(a5.equals("o terceiro valor eh 1212.22 o segundo eh 929292.0 e 1.0"));
        
        Vector v3 = getData("23/05/2007|01/01/2001", 3);
        Object a6 = n.replaceByToken(v3, "as datas sao #COL_2# e #COL_1#");
        assertTrue(a6.equals("as datas sao 2001-01-01 00:00:00.0 e 2007-05-23 00:00:00.0"));

        Vector v4 = getData("AAA|BBB", 1);
        Object a7 = n.replaceByToken(v4, "o valor eh o #COL_5# q nao existe");
        assertTrue(a7.equals("o valor eh o ERR! q nao existe"));
        
        Vector v5 = getData("Fulano|beltrano@mydomain.com|blablabla", 1);
        Object a8 = n.replaceByToken(v5, "#COL_2#");
        assertTrue(a8.equals("beltrano@mydomain.com"));        
        
    }

    
    private Vector getData(String list, int type){
        Vector response = new Vector();
        String[] line = list.split("\\|");
        for(int j=0; j<line.length ; j++) {
            String col = line[j];
            if (type==1) {
                response.add(col);    
            } else if (type==2) {
                response.add(new Float(col));
            } else if (type==3) {
                response.add(DateUtil.getDateTime(col, "dd/MM/yyyy", new Locale("pt", "BR")));                
            }
        }
        return response;
    }
    
    
    protected void setUp() throws Exception {
        n = new Notification();
    }
}
