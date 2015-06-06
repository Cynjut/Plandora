package com.pandora.exception;

import com.pandora.AdditionalFieldTO;

public class MandatoryMetaFieldBusinessException extends BusinessException {

	private static final long serialVersionUID = 1L;
	private AdditionalFieldTO afto;
    /**
     * Constructor
     * @param e
     */
    public MandatoryMetaFieldBusinessException(Exception e, AdditionalFieldTO afto) {
        super(e);
        this.afto = afto;
    }

    /**
     * Constructor
     * @param msg
     */
    public MandatoryMetaFieldBusinessException(String msg, AdditionalFieldTO afto) {
        super(msg);
        this.afto = afto;
    }
    
    public MandatoryMetaFieldBusinessException(AdditionalFieldTO afto) {
        super("Invalid Meta Field Value [" + afto.getMetaField().getName() + "].");
        this.afto = afto;
    }

	public AdditionalFieldTO getAfto() {
		return afto;
	}

    
}
