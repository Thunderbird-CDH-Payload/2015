package com.example.aschere.cdhprototype;

/**
 * Created by aschere on 11/21/2015.
 * This is an enum of negotiationCodes,
 * byte arrays we send to the Arduino
 * to indicate certain instructions.
 * For instance, sending a negotiationCode.GET_FUN_ID should cause the receiving arduino to send its fun id.
 * ARDUINO team, decide the codes.
 */
public enum negotiationCode
{
	//note: all should be in byte[] format
	SENDING_START(new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01}),
	SENDING_END(new byte[] {(byte)0x00, (byte)0x00, (byte)0x01, (byte)0x00}),
	RECEIVING(new byte[] {(byte)0x00, (byte)0x01, (byte)0x00, (byte)0x00}),
	GET_FUN_ID(new byte[] {(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00}),
	GET_ORDERS(new byte[] {(byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00});

	private byte[] code;
	negotiationCode(byte[] enumCode)
	{
		this.code = enumCode;
	}

	public byte[] code()
	{
		return this.code;
	}
}
