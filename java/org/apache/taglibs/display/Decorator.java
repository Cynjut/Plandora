package org.apache.taglibs.display;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpSession;

/**
 * This class provides some basic functionality for all objects which serve
 * as decorators for the objects in the List being displayed.
 **/
public abstract class Decorator extends Object {
   private HttpSession session = null;
   private Object collection = null;
    
   private Object obj = null;
   private int viewIndex = -1;
   private int listIndex = -1;


   public Decorator() {
   }

   public void init( HttpSession ses, Object list ) {
       this.session = ses;
       this.collection = list;
   }

   public String initRow( Object obj, int viewIndex, int listIndex ) {
       this.obj = obj;
       this.viewIndex = viewIndex;
       this.listIndex = listIndex;
       return "";
   }

   public String startRow() {
       return "";
   }

   public String finishRow() {
       return "";
   }

   public void finish() {
   }

   public HttpSession getSession() {
       return this.session;
   }

    @SuppressWarnings("unchecked")
	public List getList() {
        if (this.collection instanceof List){ 
            return (List)this.collection;
        } else { 
            throw new RuntimeException ("This function is only supported if the given collection is a java.util.List.");
        }
    }

    @SuppressWarnings("unchecked")
	public Collection getCollection () {
        if (this instanceof Collection) {
            return (Collection) this.collection;
        } else {
            throw new RuntimeException ("This function is only supported if the given collection is a java.util.Collection.");
        }
    }
    
    //////////////////////////////////////////
    public Object getObject() { 
        return this.obj; 
    }
    
    //////////////////////////////////////////    
    public int getViewIndex() { 
        return this.viewIndex; 
    }
    
    //////////////////////////////////////////    
    public int getListIndex() { 
        return this.listIndex; 
    }
}
