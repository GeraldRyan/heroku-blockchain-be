package privblock.gerald.ryan.entity;

//@GeneratedValue(strategy=GenerationType.AUTO)  Consider using this later under ID to auto increment
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.eclipse.persistence.indirection.ValueHolderInterface;

import com.google.gson.Gson;

import exceptions.BlocksInChainInvalidException;
import exceptions.ChainTooShortException;
import exceptions.GenesisBlockInvalidException;
import privblock.gerald.ryan.entity.Block;

/**
 * 
 * @author Gerald Ryan Blockchain class of blockchain app. Blockchain class.
 *         Instantiate blockchain with a name as string
 *
 *
 */
@Entity
@Table(name = "blockchain")
public class Blockchain {
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	int id;
	@Column // (unique = true) doesn't need to be unique but why not
	String instance_name;
	long date_created;
	long date_last_modified;
	int length_of_chain;
	@OneToMany(targetEntity = Block.class, cascade = CascadeType.PERSIST)
	@JoinTable(name = "BlocksByChain")
	List<Block> chain; // The chain itself

	/**
	 * Constructor function, initializes an ArrayList of Blocks with a valid genesis
	 * block. Future blocks are to be mined.
	 */
	public Blockchain(String name) {
		this.instance_name = name;
		this.date_created = new Date().getTime();
		this.chain = new ArrayList<Block>();
		this.chain.add(Block.genesis_block());
		this.length_of_chain = 1;
	}

	public Blockchain() {
//		this.chain = new ArrayList<Block>();
//		this.chain.add(Block.genesis_block());
	}

	public static Blockchain createBlockchainInstance(String name) {
		return new Blockchain(name);
	}

//	private ValueHolderInterface chainValueHolder;
//
//	// Use this get/set pair when configuring your Mapping
//	public void setChainValueHolder(ValueHolderInterface value) {
//		this.chainValueHolder = value;
//	}
//
//	public ValueHolderInterface getChainValueHolder() {
//		return this.chainValueHolder;
//	}

////	 Your application uses these methods to interact with Addresses
//	public void setChain(ArrayList<Block> chain) {
//		this.chainValueHolder.setValue(chain);
//	}
//
//	public ArrayList<Block> getChain() {
//		return (ArrayList<Block>) this.chainValueHolder.getValue();
//	}

	public Block add_block(String[] data) throws NoSuchAlgorithmException {
		Block new_block = Block.mine_block(this.chain.get(this.chain.size() - 1), data);
		this.chain.add(new_block);
		this.length_of_chain++;
		this.date_last_modified = new Date().getTime();
		return new_block;
	}
	
	public Block add_block(String dataScalar) throws NoSuchAlgorithmException {
		Block new_block = Block.mine_block(this.chain.get(this.chain.size() - 1), dataScalar);
		this.chain.add(new_block);
		this.length_of_chain++;
		this.date_last_modified = new Date().getTime();
		return new_block;
	}

	/**
	 * Replace the local chain with the incoming chain if the following apply: - the
	 * incoming chain is longer than the local one - the incoming chain is formatted
	 * properly
	 * 
	 * @param chain
	 * @throws NoSuchAlgorithmException
	 * @throws ChainTooShortException
	 * @throws BlocksInChainInvalidException
	 * @throws GenesisBlockInvalidException
	 */
	public void replace_chain(Blockchain other_blockchain) throws NoSuchAlgorithmException, ChainTooShortException,
			GenesisBlockInvalidException, BlocksInChainInvalidException {
		// TODO how will I implement this? Different localhosts will have different
		// chains?
		if (other_blockchain.chain.size() <= this.chain.size()) {
			throw new ChainTooShortException("Chain too short to replace");
		}
		if (!Blockchain.is_valid_chain(other_blockchain)) {
			System.out.println("Cannot replace chain. The incoming chain is invalid");
			return;
		}

		try {
			Blockchain.is_valid_chain(other_blockchain);
			this.chain = other_blockchain.chain;
			this.length_of_chain = other_blockchain.length_of_chain;
			this.date_last_modified = new Date().getTime();
			System.out.println("Chain replaced with valid longer chain");
		} catch (GenesisBlockInvalidException e) {
			System.out.println(e);
		} catch (BlocksInChainInvalidException e) {
			System.out.println(e);
		}

//		else {
//			try {
//				Blockchain.is_valid_chain(other_blockchain);
//			} catch (Exception e) {
//				throw new // exception
//				// block of code to handle errors
//			}
//		}
	}

	/**
	 * Validate the incoming chain. Enforce the following rules: - the chain must
	 * start with the genesis block - blocks must be formatted correctly
	 * 
	 * @param blockchain
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws GenesisBlockInvalidException
	 * @throws BlocksInChainInvalidException
	 */
	public static boolean is_valid_chain(Blockchain blockchain)
			throws NoSuchAlgorithmException, GenesisBlockInvalidException, BlocksInChainInvalidException {
		if (!blockchain.chain.get(0).equals(Block.genesis_block())) {
			System.out.println("The genesis block must be valid");
			throw new GenesisBlockInvalidException("Genesis Block is invalid");
		}
		for (int i = 1; i < blockchain.chain.size(); i++) {
			Block current_block = blockchain.chain.get(i);
			Block last_block = blockchain.chain.get(i - 1);
			if (!Block.is_valid_block(last_block, current_block)) {
//				System.out.println("At least one of the blocks in the chain is not valid");
				throw new BlocksInChainInvalidException("At least one of the blocks in the chain is not valid");
			}
		}
		return true;
	}

	public String toStringConsole() {

		return String.format("%5s %15s %15s %15s %15s", id, instance_name, date_created, date_last_modified,
				length_of_chain, "length", "content");
//		return "Blockchain: " + this.chain;
	}

	public String toStringBroadcastChain() {
		String string_to_return = "";
		for (Block b : chain) {
			string_to_return += b.toStringWebAPI();
		}
		return string_to_return;
	}
	
	/*
	 * Uses GSON library to serialize as json string
	 * Trim is not necessary. My json formatters on chrome were not working with this so I thought it might be necessary. 
	 */
	public String toJSONtheChain() {
		return new Gson().toJson(chain).trim();
	}
	
	/*
	 * Helper method for getting last block (peeking)
	 */
	public Block getLastBlock() {
		return this.getChain().get(getLength_of_chain()-1);
	}

	public int getId() {
		return id;
	}

	public String getInstance_name() {
		return instance_name;
	}

	public long getDate_created() {
		return date_created;
	}

	public long getDate_last_modified() {
		return date_last_modified;
	}

	public int getLength_of_chain() {
		return length_of_chain;
	}

	public List<Block> getChain() {
		return chain;
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		Blockchain blockchain = new Blockchain("Bitcoin 2");
//		System.out.println(blockchain);
		blockchain.add_block(new String[] { "Shakespeare", "wrote", "it" });
		System.out.println(blockchain);
	}

}
