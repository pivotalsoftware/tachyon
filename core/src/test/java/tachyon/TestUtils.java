/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package tachyon;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import tachyon.client.OutStream;
import tachyon.client.TachyonFile;
import tachyon.client.TachyonFS;
import tachyon.client.WriteType;
import tachyon.thrift.ClientFileInfo;

public final class TestUtils {
  /**
   * Create a simple file with <code>len</code> bytes.
   * 
   * @param tfs
   * @param fileName
   * @param op
   * @param len
   * @return created file id.
   * @throws IOException
   */
  public static int createByteFile(TachyonFS tfs, String fileName, WriteType op, int len)
      throws IOException {
    return createByteFile(tfs, new TachyonURI(fileName), op, len);
  }

  /**
   * Create a simple file with <code>len</code> bytes.
   * 
   * @param tfs
   * @param fileURI
   * @param op
   * @param len
   * @return
   * @throws IOException
   */
  public static int createByteFile(TachyonFS tfs, TachyonURI fileURI, WriteType op, int len)
      throws IOException {
    int fileId = tfs.createFile(fileURI);
    TachyonFile file = tfs.getFile(fileId);
    OutStream os = file.getOutStream(op);

    for (int k = 0; k < len; k ++) {
      os.write((byte) k);
    }
    os.close();

    return fileId;
  }

  /**
   * Create a simple file with <code>len</code> bytes.
   * 
   * @param tfs
   * @param fileName
   * @param op
   * @param len
   * @param blockCapacityByte
   * @return created file id.
   * @throws IOException
   */
  public static int createByteFile(TachyonFS tfs, String fileName, WriteType op, int len,
      long blockCapacityByte) throws IOException {
    int fileId = tfs.createFile(new TachyonURI(fileName), blockCapacityByte);
    TachyonFile file = tfs.getFile(fileId);
    OutStream os = file.getOutStream(op);

    for (int k = 0; k < len; k ++) {
      os.write((byte) k);
    }
    os.close();

    return fileId;
  }

  public static byte[] getIncreasingByteArray(int len) {
    return getIncreasingByteArray(0, len);
  }

  public static byte[] getIncreasingByteArray(int start, int len) {
    byte[] ret = new byte[len];
    for (int k = 0; k < len; k ++) {
      ret[k] = (byte) (k + start);
    }
    return ret;
  }

  public static boolean equalIncreasingByteArray(int len, byte[] arr) {
    return equalIncreasingByteArray(0, len, arr);
  }

  public static boolean equalIncreasingByteArray(int start, int len, byte[] arr) {
    if (arr == null || arr.length < len) {
      return false;
    }
    for (int k = 0; k < len; k ++) {
      if (arr[k] != (byte) (start + k)) {
        return false;
      }
    }
    return true;
  }

  public static ByteBuffer getIncreasingByteBuffer(int len) {
    return getIncreasingByteBuffer(0, len);
  }

  public static ByteBuffer getIncreasingByteBuffer(int start, int len) {
    return ByteBuffer.wrap(getIncreasingByteArray(start, len));
  }

  public static ByteBuffer getIncreasingIntBuffer(int len) {
    ByteBuffer ret = ByteBuffer.allocate(len * 4);
    for (int k = 0; k < len; k ++) {
      ret.putInt(k);
    }
    ret.flip();
    return ret;
  }

  public static List<String> listFiles(TachyonFS tfs, String path) throws IOException {
    List<ClientFileInfo> infos = tfs.listStatus(new TachyonURI(path));
    List<String> res = new ArrayList<String>();
    for (ClientFileInfo info : infos) {
      res.add(info.getPath());

      if (info.isFolder) {
        res.addAll(listFiles(tfs, info.getPath()));
      }
    }

    return res;
  }

  /**
   * Creates a unique path based off the caller.
   */
  public static final String uniqPath() {
    StackTraceElement caller = new Throwable().getStackTrace()[1];
    long time = System.nanoTime();
    return "/" + caller.getClassName() + "/" + caller.getMethodName() + "/" + time;
  }
}
