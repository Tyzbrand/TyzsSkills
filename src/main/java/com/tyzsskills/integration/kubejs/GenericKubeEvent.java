package com.tyzsskills.integration.kubejs;

import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.type.TypeInfo;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public record GenericKubeEvent<T extends Event> (T neoEvent) implements KubeEvent, CustomJavaToJsWrapper {
    @Override
    public Scriptable convertJavaToJs(Context cx, Scriptable scope, TypeInfo staticType) {
        return new WrappedNeoEventObject(scope, this, cx);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> RuntimeException rethrow(Throwable e) throws E {throw (E) e;}

    private static final class WrappedNeoEventObject extends NativeJavaObject {

        WrappedNeoEventObject(Scriptable scope, GenericKubeEvent<?> parent, Context cx) {

            super(scope, parent.neoEvent(), TypeInfo.of(parent.neoEvent().getClass()), cx);

            addCustomFunction("cancel", TypeInfo.OBJECT, ctx -> {
                if (parent.neoEvent() instanceof ICancellableEvent cancellable) {
                    cancellable.setCanceled(true);
                }
                try {return parent.cancel(ctx);}
                catch (EventExit exit) {throw rethrow(exit);}
            });
        }
    }
}
