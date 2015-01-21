package com.thend.friendship.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ByteSerializer {
        
        private static Logger logger = LoggerFactory.getLogger(ByteSerializer.class);

        /**
         * 反序列化
         * @param bytes
         * @return
         */
        @SuppressWarnings("unchecked")
        public static <T> T deserialize(byte[] bytes) {

                if (isEmpty(bytes)) {
                        return null;
                }

                try {
                        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
                        try {
                                ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
                                try {
                                        return (T)objectInputStream.readObject();
                                }
                                catch (ClassNotFoundException ex) {
                                        throw new Exception("Failed to deserialize object type", ex);
                                }
                        }
                        catch (Throwable ex) {
                                throw new Exception("Failed to deserialize", ex);
                        }
                } catch (Exception e) {
                        logger.error("Failed to deserialize",e);
                }
                return null;
        }

        public static boolean isEmpty(byte[] data) {
                return (data == null || data.length == 0);
        }

        /**
         * 序列化
         * @param object
         * @return
         */
        public static byte[] serialize(Object object) {

                byte[] result = null;

                if (object == null) {
                        return new byte[0];
                }
                try {
                        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
                        try  {
                                if (!(object instanceof Serializable)) {
                                        throw new IllegalArgumentException(ByteSerializer.class.getSimpleName() + " requires a Serializable payload " +
                                                        "but received an object of type [" + object.getClass().getName() + "]");
                                }
                                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                                objectOutputStream.writeObject(object);
                                objectOutputStream.flush();
                                result =  byteStream.toByteArray();
                        }
                        catch (Throwable ex) {
                                throw new Exception("Failed to serialize", ex);
                        }
                } catch (Exception ex) {
                        logger.error("Failed to serialize",ex);
                }
                return result;
        }
}
