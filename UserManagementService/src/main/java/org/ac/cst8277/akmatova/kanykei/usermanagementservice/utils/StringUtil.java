package org.ac.cst8277.akmatova.kanykei.usermanagementservice.utils;

import org.apache.commons.codec.binary.Hex;

public final class StringUtil {
    public static String getUuidStringFromBytes(byte[] bytes) {
        return Hex.encodeHexString(bytes).replaceFirst(
                "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                "$1-$2-$3-$4-$5");
    }
}
