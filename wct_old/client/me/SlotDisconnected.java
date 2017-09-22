package p455w0rd.wct.client.me;

import appeng.container.slot.AppEngSlot;
import appeng.items.misc.ItemEncodedPattern;
import appeng.util.Platform;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class SlotDisconnected extends AppEngSlot {

	private final ClientDCInternalInv mySlot;

	public SlotDisconnected(final ClientDCInternalInv me, final int which, final int x, final int y) {
		super(me.getInventory(), which, x, y);
		mySlot = me;
	}

	@Override
	public boolean isItemValid(final ItemStack par1ItemStack) {
		return false;
	}

	@Override
	public void putStack(final ItemStack par1ItemStack) {

	}

	@Override
	public boolean canTakeStack(final EntityPlayer par1EntityPlayer) {
		return false;
	}

	@Override
	public ItemStack getDisplayStack() {
		if (Platform.isClient()) {
			final ItemStack is = super.getStack();
			if (is != null && is.getItem() instanceof ItemEncodedPattern) {
				final ItemEncodedPattern iep = (ItemEncodedPattern) is.getItem();
				final ItemStack out = iep.getOutput(is);
				if (out != null) {
					return out;
				}
			}
		}
		return super.getStack();
	}

	@Override
	public boolean getHasStack() {
		return getStack() != null;
	}

	@Override
	public int getSlotStackLimit() {
		return 0;
	}

	@Override
	public ItemStack decrStackSize(final int par1) {
		return null;
	}

	public ClientDCInternalInv getSlot() {
		return mySlot;
	}
}