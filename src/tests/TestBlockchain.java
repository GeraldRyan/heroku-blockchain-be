package tests;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;

import org.eclipse.persistence.jpa.jpql.Assert.AssertException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import exceptions.BlocksInChainInvalidException;
import exceptions.ChainTooShortException;
import exceptions.GenesisBlockInvalidException;
import privblock.gerald.ryan.entity.Block;
import privblock.gerald.ryan.entity.Blockchain;

public class TestBlockchain {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testBlockchainInstance() {
		Blockchain blockchain = new Blockchain();
		assertTrue(blockchain instanceof Blockchain);
	}

	@Test
	public void testAddBlock() throws NoSuchAlgorithmException {
		Blockchain blockchain = Blockchain.createBlockchainInstance("test");
		String[] testData = { "foo", "bar" };
		blockchain.add_block(testData);
		assertTrue(blockchain.getChain().get(blockchain.getChain().size() - 1).getData() == testData); // assertEquals
																										// appears to be
																										// deprecated
	}

	@Test
	public void testIsValidChain() throws NoSuchAlgorithmException {
		Blockchain blockchain = Blockchain.createBlockchainInstance("Test");
		for (int i = 0; i < 5; i++) {
			blockchain.add_block(new String[] { "foo", "bar" });
		}
		// Test code implicitly with no exception (WARNING, NOT SURE THIS IS PROPERLY
		// SET UP YET AS TO GIVE US ASSURANCE)
	}

//	@Test
//	public void testIsValidChainBadGenesis() {
//		// maybe I can mutate state somehow in one field (e.g. nonce) just to wreck things for testing. 
//		assertThrows(GenesisBlockInvalidException.class, ()->{
//			System.out.println("Implement Me");
//		});
//	}

	@Test
	public void testReplaceChain() throws NoSuchAlgorithmException, ChainTooShortException, GenesisBlockInvalidException, BlocksInChainInvalidException {
		Blockchain blockchain5 = Blockchain.createBlockchainInstance("Test");
		for (int i = 0; i < 5; i++) {
			blockchain5.add_block(new String[] { "foo", "bar" });
		}
		Blockchain blockchain = Blockchain.createBlockchainInstance("veryoriginal");
		assertEquals(1, blockchain.getLength_of_chain());
		blockchain.replace_chain(blockchain5);
		assertEquals(6, blockchain.getLength_of_chain()); // redundant with line below.
		assertTrue(blockchain.getChain().equals(blockchain5.getChain()));
	}

	@Test
	public void testReplaceChainShorter() throws NoSuchAlgorithmException, ChainTooShortException {
		Blockchain blockchain5 = Blockchain.createBlockchainInstance("Test");
		for (int i = 0; i < 5; i++) {
			blockchain5.add_block(new String[] { "foo", "bar" });
		}
		Blockchain blockchain1 = Blockchain.createBlockchainInstance("veryoriginal");
		assertThrows(ChainTooShortException.class, () -> {
			blockchain5.replace_chain(blockchain1);
		});
	}

}
