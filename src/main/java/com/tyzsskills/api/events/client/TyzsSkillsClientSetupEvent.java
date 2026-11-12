package com.tyzsskills.api.events.client;

import com.tyzsskills.api.interfaces.ITyzsSkillsClientRegistration;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class TyzsSkillsClientSetupEvent extends Event implements IModBusEvent {
    private final ITyzsSkillsClientRegistration wrapper;

    public TyzsSkillsClientSetupEvent(ITyzsSkillsClientRegistration wrapper) {
        this.wrapper = wrapper;
    }

    public ITyzsSkillsClientRegistration wrapper(){return wrapper;}
}
