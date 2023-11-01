package pt.tecnico.supplier;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

import com.google.type.Money;

import io.grpc.stub.StreamObserver;
import pt.tecnico.supplier.domain.Supplier;
import pt.tecnico.supplier.grpc.*;

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.Key;

public class SupplierServiceImpl extends SupplierGrpc.SupplierImplBase {
	private static byte[] digestAndCipher(byte[] bytes, Key key) throws Exception {
		// get a message digest object using the specified algorithm
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		// calculate the digest and print it out
		messageDigest.update(bytes);
		byte[] digest = messageDigest.digest();
		System.out.println("Digest:");
		System.out.println(printHexBinary(digest));

		// get an AES cipher object
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherDigest = cipher.doFinal(digest);

		System.out.println("Cyphered Digest:");
		System.out.println(printHexBinary(cipherDigest));

		return cipherDigest;
	}

	public static Key readKey(String resourcePath) throws Exception {
		System.out.println("Reading key from resource " + resourcePath + " ...");

		InputStream fis = Thread.currentThread()
			.getContextClassLoader()
			.getResourceAsStream(resourcePath);
		byte[] encoded = new byte[fis.available()];
		fis.read(encoded);
		fis.close();

		System.out.println("Key:");
		System.out.println(printHexBinary(encoded));
		SecretKeySpec keySpec = new SecretKeySpec(encoded, "AES");

		return keySpec;
	}


	/**
	 * Set flag to true to print debug messages. The flag can be set using the
	 * -Ddebug command line option.
	 */
	private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

	/** Helper method to print debug messages. */
	private static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}

	/** Domain object. */
	final private Supplier supplier = Supplier.getInstance();

	/** Constructor */
	public SupplierServiceImpl() {
		debug("Loading demo data...");
		supplier.demoData();
	}

	/** Helper method to convert domain product to message product. */
	private Product buildProductFromProduct(pt.tecnico.supplier.domain.Product p) {
		Product.Builder productBuilder = Product.newBuilder();
		productBuilder.setIdentifier(p.getId());
		productBuilder.setDescription(p.getDescription());
		productBuilder.setQuantity(p.getQuantity());
		productBuilder.setDiscount(p.getDiscount());

		Money.Builder moneyBuilder = Money.newBuilder();
		moneyBuilder.setCurrencyCode("EUR").setUnits(p.getPrice());
		productBuilder.setPrice(moneyBuilder.build());

		return productBuilder.build();
	}

	@Override
	public void listProducts(ProductsRequest request, StreamObserver<SignedResponse> responseObserver) {
		debug("listProducts called");

		debug("Received request:");
		debug(request.toString());
		debug("in binary hexadecimals:");
		byte[] requestBinary = request.toByteArray();
		debug(String.format("%d bytes%n", requestBinary.length));

		// build response
		ProductsResponse.Builder responseBuilder = ProductsResponse.newBuilder();
		responseBuilder.setSupplierIdentifier(supplier.getId());
		for (String pid : supplier.getProductsIDs()) {
			pt.tecnico.supplier.domain.Product p = supplier.getProduct(pid);
			Product product = buildProductFromProduct(p);
			responseBuilder.addProduct(product);
		}
		ProductsResponse response = responseBuilder.build();

		try {
			byte[] digest = digestAndCipher(response.toByteArray(), SupplierServiceImpl.readKey("secret.key"));
			Signature sig = Signature.newBuilder()
					.setValue(com.google.protobuf.ByteString.copyFrom(digest))
					.setTimestamp(System.currentTimeMillis())
					.build();

			SignedResponse res = SignedResponse.newBuilder()
					.setResponse(response)
					.setSignature(sig)
					.build();

			responseObserver.onNext(res);
			responseObserver.onCompleted();

		} catch (Exception exp) {
			System.out.println("Dei uma exceção, que foi:");
			System.out.println(exp.toString());
			// Linda, não quero saber!
		}
	}

}
