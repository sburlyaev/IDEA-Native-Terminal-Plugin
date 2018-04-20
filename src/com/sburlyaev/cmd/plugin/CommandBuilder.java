package com.sburlyaev.cmd.plugin;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.externalSystem.service.execution.NotSupportedException;
import com.sburlyaev.cmd.plugin.model.Command;
import com.sburlyaev.cmd.plugin.model.Environment;
import com.sburlyaev.cmd.plugin.model.OperationSystem;
import com.sburlyaev.cmd.plugin.model.Terminal;

public class CommandBuilder {

    private static final Logger log = Logger.getInstance(CommandBuilder.class);

    public static Command createCommand(Environment env, String projectBaseDir, String favoriteTerminal) {
        OperationSystem os = env.getOs();
        StringBuilder builder = new StringBuilder();

        String command = favoriteTerminal == null
                ? os.getDefaultTerminal().getCommand()
                : favoriteTerminal;
        Terminal terminal = Terminal.fromString(command);
        log.info("Favorite terminal is [" + favoriteTerminal + "] and using [" + terminal + "]");

        switch (os) {
            case WINDOWS:

                switch (terminal) {
                    case COMMAND_PROMPT:
                        builder.append("cmd /c \"start ")
                                .append(command)
                                .append(" /K \"cd /d ")
                                .append(projectBaseDir)
                                .append("\"");
                        break;
                    case POWER_SHELL:
                        builder.append("cmd /c start ")
                                .append(command)
                                .append(" -NoExit")
                                .append(" -Command")
                                .append(" \"Set-Location '")
                                .append(projectBaseDir)
                                .append("'\"");
                        break;
                    default:
                        builder.append("cmd /c \"start ")
                                .append(command)
                                .append(" ")
                                .append(projectBaseDir)
                                .append("\"");
                        break;
                }
                break;

            case LINUX:

                switch (terminal) {
                    case GNOME_TERMINAL:
                        builder.append(command)
                                .append(" --working-directory=")
                                .append(projectBaseDir);
                        break;
                    default:
                        builder.append(command)
                                .append(" ")
                                .append(projectBaseDir);
                        break;
                }
                break;

            case MAC_OS:
                builder.append("open ")
                        .append(projectBaseDir)
                        .append(" -a ")
                        .append(command);
                break;

            default:
                throw new NotSupportedException("The environment is not supported: " + os);
        }
        return new Command(builder.toString());
    }
}
