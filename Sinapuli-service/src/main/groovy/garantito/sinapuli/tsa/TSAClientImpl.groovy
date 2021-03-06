package garantito.sinapuli.tsa

import org.bouncycastle.asn1.*
import org.bouncycastle.asn1.cmp.*
import org.bouncycastle.operator.*
import org.bouncycastle.tsp.*


public class TSAClientImpl implements TSAClient {
  String tsaURL = "http://localhost:5050/timestamp"
  String tsaUsername
  String tsaPassword

  public TSAClientImpl() {
  }

  public TSAClientImpl(String url, String username, String password) {
    this.tsaURL = url
    this.tsaUsername = username
    this.tsaPassword = password
  }

  @Override
  public byte[] getToken(byte[] imprint) {
    byte[] respBytes = null

    def algoFinder = new DefaultDigestAlgorithmIdentifierFinder()
    def algoIdentifier = algoFinder.find("SHA-256")

    // Setup the time stamp request
    def tsqGenerator = new TimeStampRequestGenerator()
    tsqGenerator.certReq = true
    //tsqGenerator.reqPolicy = new ASN1ObjectIdentifier("1.3.6.1.4.1.13762.3")
    def nonce = BigInteger.valueOf(System.currentTimeMillis())
    def request = tsqGenerator.generate(algoIdentifier.objectId, imprint, nonce)
    byte[] requestBytes = request.encoded

    // Call the communications layer
    respBytes = getTSAResponse(requestBytes)

    // Handle the TSA response
    def response = new TimeStampResponse(respBytes)

    // validate communication level attributes (RFC 3161 PKIStatus)
    response.validate(request)

    def failure = response.failInfo
    int value = (failure == null) ? 0 : failure.intValue()
    if (value != 0) {
      throw new IOException("TSA failure: ${value} (${response.statusString})")
    }

    // extract just the time stamp token (removes communication status info)
    def tsToken = response.timeStampToken
    if (tsToken == null) {
      throw new IOException("TSA failed to return token: ${response.status} (${response.statusString})")
    }
    def tsTokenInfo = tsToken.timeStampInfo
    byte[] encoded = tsToken.encoded

    encoded
  }

  def getTSAResponse(byte[] requestBytes) {
    // Setup the TSA connection
    def tsaConnection = new URL(tsaURL).openConnection()
    tsaConnection.doInput = true
    tsaConnection.doOutput = true
    tsaConnection.useCaches = false

    tsaConnection.setRequestProperty("Content-Type", "application/timestamp-query")
    //tsaConnection.setRequestProperty("Content-Transfer-Encoding", "base64")
    tsaConnection.setRequestProperty("Content-Transfer-Encoding", "binary")

    if (tsaUsername != null && tsaUsername != "") {
      String userPassword = tsaUsername + ":" + tsaPassword
      tsaConnection.setRequestProperty("Authorization", "Basic " +
          userPassword.bytes.encodeBase64().toString())
    }
    def out = tsaConnection.outputStream
    out << requestBytes
    out.close()

    // Get TSA response as a byte array
    def inp = tsaConnection.inputStream
    def baos = new ByteArrayOutputStream()
    baos << inp
    byte[] respBytes = baos.toByteArray()

    def encoding = tsaConnection.getContentEncoding()

    if (encoding != null && encoding.equalsIgnoreCase("base64")) {
      respBytes = new String(respBytes).decodeBase64()
    }

    respBytes
  }
}

