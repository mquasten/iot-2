package de.mq.iot2.user.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Security;
import java.util.Collection;
import java.util.Optional;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.BeanUtils;

import de.mq.iot2.support.IdUtil;
import de.mq.iot2.support.RandomTestUtil;
import de.mq.iot2.user.User;

class UserImplTest {

	private static final String NEW_PASSWORD = "rotB=µ0*j+µ0*Ɛ0*dE/dt";
	private static final String ALGORITHM = "MD5";
	private static final String NAME = RandomTestUtil.randomString();
	private static final String PASSWORD = RandomTestUtil.randomString();
	private static final long ID = RandomTestUtil.randomLong();

	@Test
	void create() {
		final User user = new UserImpl(ID, NAME, PASSWORD, Optional.of(ALGORITHM));
		
		final String passwordHash = DigestUtils.md5Hex(PASSWORD.getBytes());
		assertEquals(NAME, user.name());
		assertEquals(passwordHash, user.encodedPassword());
		assertEquals(IdUtil.id(ID), IdUtil.getId(user));
		assertTrue(user.algorithm().isPresent());
		assertEquals(ALGORITHM, user.algorithm().get());
		assertEquals(passwordHash+"-"+ ALGORITHM, user.encodedPasswordAndAlgorithm());
	}
	
	@Test
	void createRandomId() {
		final User user = new UserImpl(NAME, PASSWORD, Optional.of(ALGORITHM));
		
		assertEquals(NAME, user.name());
		String passwordHash = DigestUtils.md5Hex(PASSWORD.getBytes());
		assertEquals(passwordHash, user.encodedPassword());
		assertNotNull(IdUtil.getId(user));
		assertTrue(user.algorithm().isPresent());
		assertEquals(ALGORITHM, user.algorithm().get());
		assertEquals(passwordHash+"-"+ ALGORITHM, user.encodedPasswordAndAlgorithm());
	}
	
	@Test
	void createRandomWithoutAlgorithm() {
		final User user = new UserImpl(NAME, PASSWORD, Optional.empty());
		
		final String passwordHash = Hex.encodeHexString(PASSWORD.getBytes());
		assertEquals( passwordHash, user.encodedPassword());
		assertNotNull(IdUtil.getId(user));
		assertTrue(user.algorithm().isEmpty());
		assertEquals(passwordHash, user.encodedPasswordAndAlgorithm());
	}
	
	@Test
	void assingPassword() {
		final User user = new UserImpl(NAME, PASSWORD, Optional.of(ALGORITHM));
		
		user.assingPassword(NEW_PASSWORD);
		
		
		final String passwordHash = Hex.encodeHexString(NEW_PASSWORD.getBytes());
		assertEquals( passwordHash, user.encodedPassword());
		assertNotNull(IdUtil.getId(user));
		assertTrue(user.algorithm().isEmpty());
		assertEquals(passwordHash, user.encodedPasswordAndAlgorithm());
	}
	
	@ParameterizedTest
	@MethodSource("algorithms")
	void assingPassword(final String algorithm) {
		final User user = new UserImpl(NAME, PASSWORD, Optional.empty());
		
		user.assingPassword(NEW_PASSWORD, algorithm);
		
		final String passwordHash = new DigestUtils(algorithm).digestAsHex(NEW_PASSWORD);
		assertEquals( passwordHash, user.encodedPassword());
		assertNotNull(IdUtil.getId(user));
		assertEquals(algorithm, user.algorithm().get());
		assertEquals(passwordHash+"-"+ algorithm, user.encodedPasswordAndAlgorithm());
	}
	
	static Collection<String> algorithms() {
		return Security.getAlgorithms("MessageDigest");
	}
	
	@Test
	void hash() {
		final User user = new UserImpl(NAME, PASSWORD, Optional.empty());
		assertEquals(NAME.hashCode(), user.hashCode());
	}
	
	@Test
	void hashNameNull() {
		final User user = BeanUtils.instantiateClass(UserImpl.class);
		assertEquals(System.identityHashCode(user) , user.hashCode());
	}
	
	@Test
	void equals() {
		final User valid = new UserImpl(NAME, PASSWORD, Optional.empty());
		final User invalid = BeanUtils.instantiateClass(UserImpl.class);
		assertTrue(valid.equals(new UserImpl(NAME, PASSWORD, Optional.empty())));
		assertFalse(valid.equals(new UserImpl(RandomTestUtil.randomString(), PASSWORD, Optional.empty())));
		assertFalse(valid.equals(invalid));
		assertFalse(invalid.equals(valid));
		assertTrue(invalid.equals(invalid));
		assertFalse(valid.equals(new Object()));
	}

}
