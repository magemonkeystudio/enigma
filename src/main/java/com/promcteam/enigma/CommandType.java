package com.promcteam.enigma;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public enum CommandType {
    PLAYER {
        @Override
        public void invoke(final CommandSender user, final String command) {
            Bukkit.dispatchCommand(user, StringUtils.replace(command, "{player}", user.getName()));
        }
    },
    OP {
        @Override
        public void invoke(final CommandSender user, final String command) {
            final boolean isOp = user.isOp();
            try {
                if (!isOp) // don't op if he had op
                {
                    user.setOp(true);
                }
                Bukkit.dispatchCommand(user, StringUtils.replace(command, "{player}", user.getName()));
                if (!isOp) // don't de-op if he had op
                {
                    user.setOp(false);
                }
            } finally // for sure... shit happens
            {
                if (!isOp) {
                    user.setOp(false);
                }
            }
        }
    },
    CONSOLE {
        @Override
        public void invoke(final CommandSender user, final String command) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), StringUtils.replace(command, "{player}", user.getName()));
        }
    };

    public abstract void invoke(CommandSender user, String command);
}
