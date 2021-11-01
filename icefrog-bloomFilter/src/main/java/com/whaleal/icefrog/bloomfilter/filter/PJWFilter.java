package com.whaleal.icefrog.bloomfilter.filter;

import com.whaleal.icefrog.core.util.HashUtil;

public class PJWFilter extends AbstractFilter {
    private static final long serialVersionUID = 1L;

    public PJWFilter( long maxValue, int machineNum ) {
        super(maxValue, machineNum);
    }

    public PJWFilter( long maxValue ) {
        super(maxValue);
    }

    @Override
    public long hash( String str ) {
        return HashUtil.pjwHash(str) % size;
    }

}
