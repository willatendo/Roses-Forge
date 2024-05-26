package willatendo.roses.data;

import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import willatendo.roses.data.loot.RosesBlockLootSubProvider;
import willatendo.roses.server.util.RosesUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = RosesUtils.ID)
public class RosesData {
    @SubscribeEvent
    public static void gatherDataEvent(GatherDataEvent gatherDataEvent) {
        DataGenerator dataGenerator = gatherDataEvent.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        ExistingFileHelper existingFileHelper = gatherDataEvent.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> registries = gatherDataEvent.getLookupProvider();

        dataGenerator.addProvider(gatherDataEvent.includeClient(), new RosesItemModelProvider(packOutput, RosesUtils.ID, existingFileHelper));
        dataGenerator.addProvider(gatherDataEvent.includeClient(), new RosesBlockStateProvider(packOutput, RosesUtils.ID, existingFileHelper));
        dataGenerator.addProvider(gatherDataEvent.includeClient(), new RosesSoundDefinitionsProvider(packOutput, RosesUtils.ID, existingFileHelper));
        dataGenerator.addProvider(gatherDataEvent.includeClient(), new RosesLanguageProvider(packOutput, RosesUtils.ID, "en_us"));

        dataGenerator.addProvider(gatherDataEvent.includeServer(), new RosesBuiltinProvider(packOutput, registries, RosesUtils.ID));
        dataGenerator.addProvider(gatherDataEvent.includeServer(), new RosesRecipeProvider(packOutput));
        dataGenerator.addProvider(gatherDataEvent.includeServer(), new LootTableProvider(packOutput, Set.of(), List.of(new LootTableProvider.SubProviderEntry(RosesBlockLootSubProvider::new, LootContextParamSets.BLOCK))) {
            @Override
            protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
            }
        });
        dataGenerator.addProvider(gatherDataEvent.includeServer(), new RosesDataMapProvider(packOutput, registries));
        RosesBlockTagsProvider rosesBlockTagsProvider = new RosesBlockTagsProvider(packOutput, registries, RosesUtils.ID, existingFileHelper);
        dataGenerator.addProvider(gatherDataEvent.includeServer(), rosesBlockTagsProvider);
        dataGenerator.addProvider(gatherDataEvent.includeServer(), new RosesItemTagsProvider(packOutput, registries, rosesBlockTagsProvider.contentsGetter(), RosesUtils.ID, existingFileHelper));
        dataGenerator.addProvider(gatherDataEvent.includeServer(), new RosesBiomeTagsProvider(packOutput, registries, RosesUtils.ID, existingFileHelper));
        dataGenerator.addProvider(gatherDataEvent.includeServer(), new RosesGameEventTagsProvider(packOutput, registries, RosesUtils.ID, existingFileHelper));
        dataGenerator.addProvider(gatherDataEvent.includeServer(), new PackMetadataGenerator(packOutput).add(PackMetadataSection.TYPE, new PackMetadataSection(RosesUtils.translation("resourcePack", "description"), DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES), Optional.empty())));
    }
}
