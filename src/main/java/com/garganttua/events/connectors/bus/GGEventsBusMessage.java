package com.garganttua.events.connectors.bus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GGEventsBusMessage {

	private String toDataflowUuid;
	private byte[] value;

	public byte[] getBytes() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.toDataflowUuid);
		sb.append(":");
		sb.append(new String(this.value));
		return sb.toString().getBytes();
	}
	
	public static GGEventsBusMessage fromBytes(byte[] bytes) {
		String s = new String(bytes);
		String[] splits = s.split(":", 2);
		GGEventsBusMessage message = new GGEventsBusMessage(splits[0], splits[1].getBytes());
		return message;
	}
}
