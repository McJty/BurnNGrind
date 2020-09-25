package mcjty.burnngrind.items;

import mcjty.burnngrind.setup.Registration;
import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.key;

public class BasicSpeedUpgrade extends Item implements ITooltipSettings, IUpgrade {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.burnngrind.shiftmessage"))
            .infoShift(header());

    public BasicSpeedUpgrade() {
        super(Registration.createStandardProperties());
    }

    @Override
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        super.addInformation(itemStack, world, list, flags);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flags);
    }

    @Override
    public int getFactor() {
        return 150;
    }
}
