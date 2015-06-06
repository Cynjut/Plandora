package com.pandora.exception;

import com.pandora.AdditionalFieldTO;

public class MandatoryMetaFieldDataException extends DataAccessException {
	private AdditionalFieldTO afto;
	
	private static final long serialVersionUID = 1L;
    /**
     * Constructor
     * @param e
     */
    public MandatoryMetaFieldDataException(Exception e, AdditionalFieldTO afto) {
        super(e);
        this.afto = afto;
    }

    /**
     * Constructor
     * @param msg
     */
    public MandatoryMetaFieldDataException(String msg, AdditionalFieldTO afto) {
        super(msg);
        this.afto = afto;
    }
    
    public MandatoryMetaFieldDataException(AdditionalFieldTO afto) {
        super("Invalid Meta Field Value [" + afto.getMetaField().getName() + "].");
        this.afto = afto;
    }

	public AdditionalFieldTO getAfto() {
		return afto;
	}
    
    
}
