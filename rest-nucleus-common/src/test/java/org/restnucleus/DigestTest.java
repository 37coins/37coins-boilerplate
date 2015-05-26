package org.restnucleus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.restnucleus.filter.DigestFilter;

import java.math.BigDecimal;

public class DigestTest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Example {
        private String email;
        private BigDecimal amount;

        public String getEmail() {
            return email;
        }

        public Example setEmail(String email) {
            this.email = email;
            return this;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public Example setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
    }

    /**
     * small numbers not in scientific notation
     */
    @Test
    public void testDigest() throws Exception {
        Example example = new Example().setEmail("test@mail.com").setAmount(new BigDecimal("0.0000002"));
        String reqValue = new ObjectMapper().writeValueAsString(example);
        String reqSig = DigestFilter.calculateSignature("https://example.com/charge/token", DigestFilter.parseJson(reqValue.getBytes()), "password");
        Assert.assertEquals("AfQFWpzS9eYx9TaeJ3L7n2sJ1bE+87qXFeZlPNxPeJ0=", reqSig);
    }

    /**
     * have empty string and null strings in the list
     */
    @Test
    public void testDigestEmpty() throws Exception {
        Example example = new Example().setEmail("").setAmount(new BigDecimal("2"));
        String reqValue = new ObjectMapper().writeValueAsString(example);
        String reqSig = DigestFilter.calculateSignature("https://example.com/charge/token", DigestFilter.parseJson(reqValue.getBytes()), "password");
        Assert.assertEquals("ttNDQTvmEEpExMBxX6zVM9HgnmiAL4WORyuk2gNX6SE=", reqSig);
    }
}
