package de.triology.universeadm;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.UUID;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;

/**
 *
 * @author Sebastian Sdorra <sebastian.sdorra@triology.de>
 */
public final class Cipher
{

  private Cipher()
  {
  }

  /**
   * aes cipher for en/decryption *
   */
  private static final AesCipherService AES = new AesCipherService();

  /**
   * charset for string byte conversion *
   */
  private static final Charset UTF8 = Charsets.UTF_8;

  /**
   * secret key
   */
  private static final byte[] KEY = new byte[]
  {
    0x61, 0x65, 0x64, 0x30, 0x35, 0x36, 0x38, 0x38, 0x2D, 0x37, 0x34, 0x36, 
    0x66, 0x2D, 0x34, 0x38
  };

  /** 
   * default key size for key generation 
   **/
  private static final int DEFAULT_KEYSIZE = 128;

  /**
   * Cli interface to generate keys and to encrypt data.
   *
   * @param args cli arguments
   */
  public static void main(String[] args)
  {
    int rc = executeCliCommand(System.out, System.err, args);
    if (rc > 0){
      System.exit(rc);
    }
  }
  
  /**
   * Execute an cli command.
   * 
   * @param out output stream for command
   * @param err error stream for command
   * @param args command arguments
   * 
   * @return return code of the command
   */
  @VisibleForTesting
  static int executeCliCommand(PrintStream out, PrintStream err, String... args)
  {
    int result = 0;
    if (args.length == 0)
    {
      err.println("usage cipher [createkey|encrypt]");
      result = 1;
    }
    else if ("createkey".equalsIgnoreCase(args[0]))
    {
      int keylength = DEFAULT_KEYSIZE;
      if (args.length >= 2)
      {
        keylength = Integer.parseInt(args[1].trim());
      }
      createNewKey(out, keylength / 8);
    }
    else if ("encrypt".equalsIgnoreCase(args[0]))
    {
      if (args.length >= 2)
      {
        out.println(encrypt(args[1]));
      }
      else
      {
        err.println("usage chipher encrypt plaintext");
        result = 2;
      }
    }
    else
    {
      err.println("unknown sub command");
      result = 3;
    }
    return result;
  }

  /**
   * Creates a new secret key for the cipher. The key will be written to stdout.
   */
  private static void createNewKey(PrintStream out, int length)
  {
    byte[] bytes = UUID.randomUUID().toString().getBytes(UTF8);
    int l = Math.min(length, bytes.length);
    for (int i = 0; i < l; i++)
    {
      out.printf("0x%02X", bytes[i]);
      if ((i + 1) < l)
      {
        out.print(", ");
      }
      if ((i + 1) % 12 == 0)
      {
        out.println();
      }
    }
    out.println();
  }

  /**
   * Encrypts the given plain text.
   *
   * @param plaintext text to encrypt
   * @return encrypted text
   */
  public static String encrypt(String plaintext)
  {
    return AES.encrypt(plaintext.getBytes(UTF8), KEY).toBase64();
  }

  /**
   * Decrypts the given text.
   *
   * @param enc encrypted text
   * @return decrypted text
   */
  public static String decrypt(String enc)
  {
    return new String(AES.decrypt(Base64.decode(enc), KEY).getBytes(), UTF8);
  }

}
