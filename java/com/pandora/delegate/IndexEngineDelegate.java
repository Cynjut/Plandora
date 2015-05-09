package com.pandora.delegate;

import com.pandora.bus.kb.IndexEngineBUS;
import com.pandora.bus.kb.KbIndex;

/**
 */
public class IndexEngineDelegate extends GeneralDelegate {

	public KbIndex getKbClass(String className) throws Exception{
	    return IndexEngineBUS.getKbClass(className);
	}
}
