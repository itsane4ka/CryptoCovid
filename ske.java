
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

public class ske {
	private SecretKeySpec secretKey;
	private Cipher cipher;

	public ske(String secret, int length, String algorithm)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
		byte[] key = new byte[length];
		key = fixSecret(secret, length);
		this.secretKey = new SecretKeySpec(key, algorithm);
		this.cipher = Cipher.getInstance(algorithm);
	}

	private byte[] fixSecret(String s, int length) throws UnsupportedEncodingException {
		if (s.length() < length) {
			int missingLength = length - s.length();
			for (int i = 0; i < missingLength; i++) {
				s += " ";
			}
		}
		return s.substring(0, length).getBytes("UTF-8");
	}

	public void encryptFile(File f)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		//System.out.println("Encrypting file: " + f.getName());
		this.cipher.init(Cipher.ENCRYPT_MODE, this.secretKey);
		this.writeToFile(f);
	}

	public void decryptFile(File f)
			throws InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		//System.out.println("Decrypting file: " + f.getName());
		this.cipher.init(Cipher.DECRYPT_MODE, this.secretKey);
		this.writeToFile(f);
	}

	public void writeToFile(File f) throws IOException, IllegalBlockSizeException, BadPaddingException {
		FileInputStream in = new FileInputStream(f);
		byte[] input = new byte[(int) f.length()];
		in.read(input);

		FileOutputStream out = new FileOutputStream(f);
		byte[] output = this.cipher.doFinal(input);
		out.write(output);

		out.flush();
		out.close();
		in.close();
	}

	public static void main(String[] args) {
		File dir = new File(JOptionPane.showInputDialog(null,"Виберіть папку-","dir"));
		File[] filelist = dir.listFiles();

		ske ske1;
		try {
			ske1 = new ske(JOptionPane.showInputDialog(null,"Ключ-"), 16, "AES");
			int choice = -2;
			while (choice != -1) {
				int fg1 = 3, fg2 = 3;
				String[] options = { "Кодувати", "Розкодувати", "Вихід" };
				choice = JOptionPane.showOptionDialog(null, "Виберіть дію", "Кодування файлів", 0,
						JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

				switch (choice) {
				case 0:
					
					Arrays.asList(filelist).forEach(file -> {  
						try {
							ske1.encryptFile(file);
						} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
								| IOException e) {
							JOptionPane.showMessageDialog(null, "Couldn't encrypt " + file.getName() + ": " + e.getMessage());
							fg1=0;
						}
					});
					if (fg1!=0){
						JOptionPane.showMessageDialog(null,"Files encrypted successfully");
					}
					break;
				case 1:
					Arrays.asList(filelist).forEach(file -> {
						try {
							ske1.decryptFile(file);
						} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
								| IOException e) {
							JOptionPane.showMessageDialog(null,"Couldn't decrypt " + file.getName() + ": " + e.getMessage());
							fg2=0;
						}
					});
					if (fg2!=0){
						JOptionPane.showMessageDialog(null, "Files decrypted successfully");
					{
					break;
				default:
					choice = -1;
					break;
				}
			}
		} catch (UnsupportedEncodingException ex) {
			JOptionPane.showMessageDialog(null, "Couldn't create key: " + ex.getMessage());
		} catch (NoSuchAlgorithmException || NoSuchPaddingException e) {
			JOptionPane.showMessageDialog(null,e.getMessage());
		}
	}
}
