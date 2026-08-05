package com.cryptalk.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

class WalletSignatureVerifierTest {
    private final WalletSignatureVerifier verifier = new WalletSignatureVerifier();

    @Test
    void verifiesEthereumPersonalSignSignature() throws Exception {
        ECKeyPair keyPair = Keys.createEcKeyPair();
        Credentials credentials = Credentials.create(keyPair);
        String message = "CrypTalk login nonce";
        Sign.SignatureData signed = Sign.signPrefixedMessage(message.getBytes(StandardCharsets.UTF_8), keyPair);
        String signature = Numeric.toHexStringNoPrefix(signed.getR())
            + Numeric.toHexStringNoPrefix(signed.getS())
            + Numeric.toHexStringNoPrefix(new byte[]{signed.getV()[0]});

        assertThat(verifier.verify(credentials.getAddress(), message, "0x" + signature)).isTrue();
        assertThat(verifier.verify(credentials.getAddress(), message + " altered", "0x" + signature)).isFalse();
    }
}
