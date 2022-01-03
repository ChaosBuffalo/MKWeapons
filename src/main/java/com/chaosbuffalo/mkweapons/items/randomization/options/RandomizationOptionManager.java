package com.chaosbuffalo.mkweapons.items.randomization.options;

import com.mojang.serialization.Dynamic;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class RandomizationOptionManager {

    public static final Map<ResourceLocation, Supplier<IRandomizationOption>> OPTION_DESERIALIZERS =
            new HashMap<>();

    public static void addOptionDeserializer(ResourceLocation type,
                                             Supplier<IRandomizationOption> deserializer){
        OPTION_DESERIALIZERS.put(type, deserializer);
    }

    static {
        addOptionDeserializer(AttributeOption.NAME, AttributeOption::new);
        addOptionDeserializer(ItemEffectAttributeOption.NAME, ItemEffectAttributeOption::new);
    }

    public static <D> IRandomizationOption deserializeOption(Dynamic<D> dynamic){
        ResourceLocation type = BaseRandomizationOption.readType(dynamic);
        IRandomizationOption option = OPTION_DESERIALIZERS.get(type).get();
        if (option != null){
            option.deserialize(dynamic);
        }
        return option;
    }
}
