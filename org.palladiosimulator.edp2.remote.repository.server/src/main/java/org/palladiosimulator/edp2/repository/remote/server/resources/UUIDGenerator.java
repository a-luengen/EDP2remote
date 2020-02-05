package org.palladiosimulator.edp2.repository.remote.server.resources;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class UUIDGenerator {

	private final Encoder encoder;
	
	public UUIDGenerator() {
		this.encoder = Base64.getUrlEncoder();
	}
	
	public String generateOptimizedRandomUUID() {
		UUID uuid = UUID.randomUUID();
		
		byte[] src = ByteBuffer.wrap(new byte[16])
				.putLong(uuid.getMostSignificantBits())
				.putLong(uuid.getLeastSignificantBits())
				.array();
		encoder.encodeToString(src).substring(0,  22);
		return "";
	}
	
	public String generateRandomUUID() {
		return UUID.randomUUID().toString();
	}
}
