package org.restnucleus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Test;
import org.restnucleus.dao.Example;
import org.restnucleus.filter.DigestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DigestTest {

    /**
     * 
     * small numbers not in scientific notation
     * 
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     * @throws IOException
     */

    @Test
    public void testDigest() throws NoSuchAlgorithmException,
            UnsupportedEncodingException, IOException {
        Example example = new Example().setEmail("test@mail.com").setAmount(new BigDecimal("0.0000002"));
        example.setCreationTime(null);
        String reqValue = new ObjectMapper().writeValueAsString(example);
        String reqSig = DigestFilter.calculateSignature("https://example.com/charge/token",DigestFilter.parseJson(reqValue.getBytes()), "password");
        Assert.assertEquals("AfQFWpzS9eYx9TaeJ3L7n2sJ1bE+87qXFeZlPNxPeJ0=",reqSig);
    }
    

    /**
     * have empty string and null strings in the list
     * 
     */
    @Test
    public void testDigestEmpty() throws Exception {
        Example example = new Example().setEmail("").setAmount(new BigDecimal("2"));
        example.setCreationTime(null);
        String reqValue = new ObjectMapper().writeValueAsString(example);
        String reqSig = DigestFilter.calculateSignature("https://example.com/charge/token",DigestFilter.parseJson(reqValue.getBytes()), "password");
        Assert.assertEquals("ttNDQTvmEEpExMBxX6zVM9HgnmiAL4WORyuk2gNX6SE=",reqSig);
    }

}
