package com.promcteam.enigma;

import java.util.Iterator;
import java.util.Map;

import com.promcteam.risecore.legacy.util.DeserializationWorker;
import com.promcteam.risecore.legacy.util.SerializationBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import org.apache.commons.lang3.Validate;

@SerializableAs("Enigma_ItemCommand")
public class ItemCommand implements ConfigurationSerializable {
    public static final int         TPS = 20;
    private final       CommandType commandType;
    private final       String      command;
    private final       int         delay;

    public ItemCommand(final CommandType commandType, final String command, final int delay) {
        this.commandType = commandType;
        this.command = command;
        this.delay = delay;
    }

    public ItemCommand(final Map<String, Object> map) {
        final DeserializationWorker w = DeserializationWorker.start(map);
        this.delay = w.getInt("delay", 0);
        this.commandType = w.getEnum("as", CommandType.CONSOLE);
        this.command = w.getString("cmd");
        Validate.notEmpty(this.command, "Command can't be empty! " + this);
    }

    public void invoke(final CommandSender target, final Iterator<ItemCommand> next) {
        final Runnable action = () -> {
            this.commandType.invoke(target, this.command);
            if ((next != null) && next.hasNext()) {
                next.next().invoke(target, next);
            }
        };
        if (this.delay == 0) {
            action.run();
            return;
        }
        Enigma.runTaskLater(action, this.delay);
    }

    public static void invoke(final CommandSender target, final Iterable<ItemCommand> commands) {
        final Iterator<ItemCommand> it = commands.iterator();
        if (!it.hasNext()) {
            return;
        }
        it.next().invoke(target, it);
    }

    @Override
    public Map<String, Object> serialize() {
        return SerializationBuilder.start(3)
                .append("delay", this.delay)
                .append("as", this.commandType)
                .append("cmd", this.command)
                .build();
    }

    @Override
    public String toString() {
        return new org.apache.commons.lang3.builder.ToStringBuilder(this,
                org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE).appendSuper(super.toString())
                .append("commandType", this.commandType)
                .append("command", this.command)
                .append("delay", this.delay)
                .toString();
    }
}
