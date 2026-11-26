package com.tyzsskills.integration.kubejs;


import com.tyzsskills.Tyzsskills;
import com.tyzsskills.api.TyzsSkillsAPI;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.plugin.ClassFilter;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.function.Supplier;

@ApiStatus.Internal
public class TyzsKubePlugin implements KubeJSPlugin {

    public static final EventGroup GROUP = EventGroup.of("TyzsSkillsEvents");
    private static final String EVENTS_PACKAGE = "com.tyzsskills.api.events.";
    private boolean initialized = false;

    @Override
    @SuppressWarnings("unchecked")
    public void registerEvents(EventGroupRegistry registry) {
        if(!initialized) {
            initialized = true;

            var scanData = ModList.get().getModFileById(Tyzsskills.MODID).getFile().getScanResult();

            for(var classData : scanData.getClasses()) {
                var className = classData.clazz().getClassName();
                if(!className.startsWith(EVENTS_PACKAGE)) continue;

                try {
                    var rawClass = Class.forName(className);
                    if(Event.class.isAssignableFrom(rawClass) && !Modifier.isAbstract(rawClass.getModifiers())) {
                        registerNeoEvent((Class<? extends Event>) rawClass);
                    }
                }
                catch (ClassNotFoundException e) {
                    Tyzsskills.LOGGER.error("Failed to bind KubeJS event for {}", className, e);
                }
            }
        }
        registry.register(GROUP);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Event> void registerNeoEvent(Class<T> eventClass) {
        var jsName = toJsEventName(eventClass);
        var isModBus = IModBusEvent.class.isAssignableFrom(eventClass);
        var isClientPkg = eventClass.getPackageName().contains(".client");
        var isCancellable = ICancellableEvent.class.isAssignableFrom(eventClass);

        Supplier<Class<? extends KubeEvent>> typeSupplier = () -> (Class) GenericKubeEvent.class;
        EventHandler handler;
        ScriptType targetScriptType;

        if(isModBus) {
            handler = GROUP.startup(jsName, typeSupplier);
            targetScriptType = ScriptType.STARTUP;
        }
        else if(isClientPkg) {
            handler = GROUP.client(jsName, typeSupplier);
            targetScriptType = ScriptType.CLIENT;
        }
        else {
            handler = GROUP.server(jsName, typeSupplier);
            targetScriptType = ScriptType.SERVER;
        }

        if(isCancellable) handler.hasResult();
        if (isClientPkg && !FMLEnvironment.dist.isClient()) return;

        IEventBus bus = isModBus ? ModList.get().getModContainerById(Tyzsskills.MODID).orElseThrow().getEventBus() : NeoForge.EVENT_BUS;
        if(bus == null) return;

        bus.addListener(eventClass, neoEvent -> {
            if(!handler.hasListeners()) return;

            var result = handler.post(targetScriptType, new GenericKubeEvent<>(neoEvent));
            if (neoEvent instanceof ICancellableEvent cancellable) {
                result.applyCancel(cancellable);
            }
        });
    }

    private static String toJsEventName(Class<?> eventClass) {
        var binarySimple = eventClass.getName().substring(eventClass.getPackageName().length() + 1);
        var parts = binarySimple.split("\\$");
        var base = parts[0];

        if(base.endsWith("Event")) base = base.substring(0, base.length() - "Event".length());
        if(base.startsWith("TyzsSkills")) base = base.substring("TyzsSkills".length());

        var combined = parts.length > 1 ? base + parts[1] : base;
        return combined.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
    }

    @Override
    public void registerBindings(BindingRegistry bindings) {bindings.add("TyzsSkills", TyzsSkillsAPI.class);}
    @Override
    public void registerClasses(ClassFilter filter) {
        filter.allow("com.tyzsskills.api");
    }
}
