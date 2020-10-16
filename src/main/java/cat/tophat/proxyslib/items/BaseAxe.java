package cat.tophat.proxyslib.items;

import net.minecraft.item.ItemAxe;

/**
 * Will likely be removed for 1.14/1.15
 */
@Deprecated
public class BaseAxe extends ItemAxe {

	public BaseAxe(ToolMaterial material) {
		super(material, material.getAttackDamage(), 0.0F);
	}
}
