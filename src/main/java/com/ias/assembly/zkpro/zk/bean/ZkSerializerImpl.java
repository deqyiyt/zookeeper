package com.ias.assembly.zkpro.zk.bean;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.springframework.stereotype.Service;

@Service
public class ZkSerializerImpl implements ZkSerializer {

	public byte[] serialize(Object data) throws ZkMarshallingError {
		return data.toString().getBytes();
	}

	public Object deserialize(byte[] bytes) throws ZkMarshallingError {
		return new String(bytes);
	}

}
