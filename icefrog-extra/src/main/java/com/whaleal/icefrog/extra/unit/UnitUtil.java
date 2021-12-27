package com.whaleal.icefrog.extra.unit;


import com.whaleal.icefrog.core.util.StrUtil;


import java.util.HashMap;
import java.util.Map;

/**
 * 存储单位和 时间单位的转换
 * @author liheping
 */

public enum UnitUtil {
    BITS(Type.DATA, 1L, "bits", "bits"),
    KILOBITS(Type.DATA, BITS.times(1024L), "Kbits", "kilobits"),
    MEGABITS(Type.DATA, KILOBITS.times(1024L), "Mbits", "mbits"),
    GIGABITS(Type.DATA, MEGABITS.times(1024L), "Gbits", "gbits"),
    BYTES(Type.DATA, BITS.times(8L), "bytes", "bytes"),
    KILOBYTES(Type.DATA, BYTES.times(1024L), "KB", "kilobytes"),
    MEGABYTES(Type.DATA, KILOBYTES.times(1024L), "MB", "mbytes"),
    GIGABYTES(Type.DATA, MEGABYTES.times(1024L), "GB", "gbytes"),
    TERABYTES(Type.DATA, GIGABYTES.times(1024L), "TB", "tbytes"),
    PETABYTES(Type.DATA, TERABYTES.times(1024L), "PB", "pbytes"),

    NANOSECONDS(Type.TIME, 1L, "nsec", "nanoseconds"),
    MILLISECONDS(Type.TIME, NANOSECONDS.times(1000000L), "msec", "milliseconds"),
    SECONDS(Type.TIME, MILLISECONDS.times(1000L), "sec", "seconds"),
    MINUTES(Type.TIME, SECONDS.times(60L), "min", "minutes"),
    HOURS(Type.TIME, MINUTES.times(60L), "hours", "hours"),
    MILLION_MINUTES(Type.TIME, MINUTES.times(1000000L), "million minutes", "million minutes"),
    DAYS(Type.TIME, HOURS.times(24L), "days", "days"),
    REQUESTS(Type.REQUEST, 1L, "requests", "requests"),
    THOUSAND_REQUESTS(Type.REQUEST, REQUESTS.times(1000L), "1000 requests", "requests"),
    GIGABYTE_SECONDS(Type.GIGABYTE_TIME, 1L, "GB seconds", "GB seconds"),
    GIGABYTE_HOURS(Type.GIGABYTE_TIME, GIGABYTE_SECONDS.times(3600L), "GB hours", "GB hours"),
    GIGABYTE_DAYS(Type.GIGABYTE_TIME, GIGABYTE_HOURS.times(24L), "GB days", "GB days"),
    RAW(Type.RAW, 1L, "", "raw");

    private static final Map<String, UnitUtil> _displayNameMap = new HashMap();
    private static final Map<String, UnitUtil> _codeMap = new HashMap();

    static {
        UnitUtil[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            UnitUtil u = var0[var2];
            _displayNameMap.put(u._displayName, u);
            _codeMap.put(u._code, u);
        }

    }

    private final Type _type;
    private final long _multiplier;
    private final String _displayName;
    private final String _code;

    private UnitUtil(Type pType, long pMultiplier, String pDisplayName, String pCode) {
        this._type = pType;
        this._multiplier = pMultiplier;
        this._displayName = pDisplayName;
        this._code = pCode;
    }

    public static double convert(double pNumber, UnitUtil pFrom, UnitUtil pTo) {
        if (!pFrom.hasSameTypeAs(pTo)) {
            throw new IllegalArgumentException(String.format("Can't convert from %s to %s", pFrom, pTo));
        } else if (pNumber == 0.0D) {
            return 0.0D;
        } else {
            return pFrom.isRaw() ? pNumber : pNumber * (double) pFrom._multiplier / (double) pTo._multiplier;
        }
    }

    public static UnitUtil fromString(String pDisplayName) {
        if (StrUtil.isBlank(pDisplayName)) {
            return RAW;
        } else {
            UnitUtil units = (UnitUtil) _displayNameMap.get(pDisplayName);
            if (units != null) {
                return units;
            } else {
                throw new IllegalArgumentException("Invalid Units name: " + pDisplayName);
            }
        }
    }

    public static UnitUtil findByCode(String pCode) {
        UnitUtil units = (UnitUtil) _codeMap.get(pCode);
        if (units != null) {
            return units;
        } else {
            throw new IllegalArgumentException("Invalid Units code: " + pCode);
        }
    }

    public Type getType() {
        return this._type;
    }

    public double convertTo(double pNumber, UnitUtil pTargetUnits) {
        return convert(pNumber, this, pTargetUnits);
    }

    public String getCode() {
        return this._code;
    }

    public boolean hasSameTypeAs(UnitUtil pThat) {
        return this._type == pThat._type;
    }

    public boolean isRaw() {
        return this == RAW;
    }

    public long times(long pMult) {
        return this._multiplier * pMult;
    }

    @Override
    public String toString() {
        return this._displayName;
    }

    public static enum Type {
        TIME,
        DATA,
        REQUEST,
        GIGABYTE_TIME,
        RAW;

        private Type() {
        }
    }
}
