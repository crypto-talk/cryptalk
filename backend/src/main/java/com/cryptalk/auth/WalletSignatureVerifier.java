package com.cryptalk.auth;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

@Component
public class WalletSignatureVerifier {
    public boolean verify(String address, String message, String signature) {
        try {
            byte[] bytes = Numeric.hexStringToByteArray(signature);
            if (bytes.length != 65) return false;
            byte v = bytes[64];
            if (v < 27) v += 27;
            Sign.SignatureData data = new Sign.SignatureData(v, Arrays.copyOfRange(bytes, 0, 32), Arrays.copyOfRange(bytes, 32, 64));
            BigInteger key = Sign.signedPrefixedMessageToKey(message.getBytes(StandardCharsets.UTF_8), data);
            return ("0x" + Keys.getAddress(key)).equalsIgnoreCase(address);
        } catch (Exception ignored) {
            return false;
        }
    }
}
