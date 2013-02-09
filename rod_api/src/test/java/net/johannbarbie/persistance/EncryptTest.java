package net.johannbarbie.persistance;

import java.net.URLDecoder;

import junit.framework.Assert;
import net.johannbarbie.persistance.resources.AbstractResource;

import org.junit.Test;

public class EncryptTest {

	@Test
	public void testEnc() throws Exception {
		String msg = "the Message";
		String enc = AbstractResource.ENCRYPT(msg);
		String dec = AbstractResource.DECRYPT(enc);
		Assert.assertEquals(msg, dec);
	}
	
	@Test
	public void testUnescape() throws Exception{
		String msg = "%5BFBGAME%5D%20Closed%20Beta%20User%20List";
		String rv = URLDecoder.decode(msg,"UTF-8");
		Assert.assertEquals("[FBGAME] Closed Beta User List", rv);
	}

}
