package com.tyzsskills.api.events;

import com.tyzsskills.api.interfaces.ITyzsSkillsCommonRegistration;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

public class TyzsSkillsCommonSetupEvent extends Event implements IModBusEvent {

    private final ITyzsSkillsCommonRegistration wrapper;

    public TyzsSkillsCommonSetupEvent(ITyzsSkillsCommonRegistration wrapper) {
        this.wrapper = wrapper;
    }

    public ITyzsSkillsCommonRegistration wrapper(){return wrapper;}
}
