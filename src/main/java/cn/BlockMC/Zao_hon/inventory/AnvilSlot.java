package cn.BlockMC.Zao_hon.inventory;

public enum AnvilSlot {
	INPUT_0(0), INPUT_1(1), OUPUT_2(2);

	private int slot;

	AnvilSlot(int slot) {
		this.slot = slot;
	}

	public int getSlot() {
		return this.slot;
	}

	public static AnvilSlot getBySlot(int slot) {
		AnvilSlot[] slots = AnvilSlot.values();
		int j = slots.length;
		for (int i = 0; i < j; i++) {
			AnvilSlot anvilslot = slots[i];

			if (anvilslot.getSlot() == slot)
				return anvilslot;
		}
		return null;
	}
}
