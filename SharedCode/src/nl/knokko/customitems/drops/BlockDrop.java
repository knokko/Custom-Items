package nl.knokko.customitems.drops;

import nl.knokko.customitems.encoding.DropEncoding;
import nl.knokko.customitems.item.ItemSetBase;
import nl.knokko.customitems.trouble.UnknownEncodingException;
import nl.knokko.util.bits.BitInput;
import nl.knokko.util.bits.BitOutput;

public class BlockDrop {
	
	public static BlockDrop load(BitInput input, ItemSetBase set) throws UnknownEncodingException {
		byte encoding = input.readByte();
		if (encoding == DropEncoding.Block.ENCODING1)
			return load1(input, set);
		else
			throw new UnknownEncodingException("BlockDrop", encoding);
	}
	
	private static BlockDrop load1(BitInput input, ItemSetBase set) {
		return new BlockDrop(BlockType.getByOrdinal(input.readInt()), Drop.load1(input, set));
	}
	
	private BlockType block;
	
	private Drop drop;
	
	public BlockDrop(BlockType block, Drop drop) {
		this.block = block;
		this.drop = drop;
	}
	
	@Override
	public String toString() {
		return drop + " for block " + block;
	}
	
	public void save(BitOutput output) {
		save1(output);
	}
	
	protected void save1(BitOutput output) {
		output.addByte(DropEncoding.Block.ENCODING1);
		output.addInt(block.ordinal());
		drop.save1(output);
	}
	
	public BlockType getBlock() {
		return block;
	}
	
	public Drop getDrop() {
		return drop;
	}
	
	public void setBlock(BlockType newBlock) {
		block = newBlock;
	}
	
	public void setDrop(Drop newDrop) {
		drop = newDrop;
	}
}
