package org.palladiosimulator.edp2.repository.remote.server.util;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class UUIDConverter {

	public static String getBase64FromHexUuid(final String uuid) throws DecoderException {
		// remove "-"
		String result = uuid.replace("-", "");
		
		String newBase64 = new String(Base64.encodeBase64(result.getBytes()));
		
		return newBase64;
	}
	
	public static String getHexfromBase64(final String base64Id) {
		
		
		byte[] decoded64 = Base64.decodeBase64(base64Id);
		
		return Hex.encodeHexString(decoded64);
	}
	
	public static String getUuidFromBase64(final String base64) {
		String temp = base64.replace("_", "");
		temp += "=";
	    byte[] bytes = Base64.decodeBase64(temp.getBytes());
	    ByteBuffer bb = ByteBuffer.wrap(bytes);
	    UUID uuid = new UUID(bb.getLong(), bb.getLong());
	    return uuid.toString();
	}
	
	public static String getBase64FromUuid(final String uuidStr) {
	    UUID uuid = UUID.fromString(uuidStr);
	    ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
	    bb.putLong(uuid.getMostSignificantBits());
	    bb.putLong(uuid.getLeastSignificantBits());
	    return "_" + Base64.encodeBase64URLSafeString(bb.array());
	}
	
}