package org.palladiosimulator.edp2.repository.remote.server.util;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class UUIDConverter {


	public static String getHexFromBase64(final String base64) {
		String temp = "";
		if(base64.startsWith("_")) {
			temp = base64.substring(1, base64.length());
		}
		temp += "==";
	    byte[] bytes = Base64.decodeBase64(temp.getBytes());
	    ByteBuffer bb = ByteBuffer.wrap(bytes);
	    UUID uuid = new UUID(bb.getLong(), bb.getLong());
	    return uuid.toString();
	}
	
	public static String getBase64FromHex(final String uuidStr) {
	    UUID uuid = UUID.fromString(uuidStr);
	    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	    bb.putLong(uuid.getMostSignificantBits());
	    bb.putLong(uuid.getLeastSignificantBits());
	    return "_" + Base64.encodeBase64URLSafeString(bb.array());
	}
	
}
